package org.edx.mobile.eliteu.professor;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.StandardCharsets;
import org.edx.mobile.util.WebViewUtil;
import org.edx.mobile.view.custom.EdxWebView;
import org.edx.mobile.view.custom.IconProgressBar;
import org.edx.mobile.view.custom.URLInterceptorWebViewClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfessorDetailFragment extends BaseFragment {


    private FullScreenErrorNotification errorNotification;
    private IconProgressBar mIconProgressBar;
    private EdxWebView mEdxWebView;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    private EliteApi eliteApi;
    @Inject
    private Config config;

    public static Fragment newInstance(int professor_id) {
        final ProfessorDetailFragment fragment = new ProfessorDetailFragment();
        fragment.setArguments(createArguments(professor_id));
        return fragment;
    }

    @NonNull
    @VisibleForTesting
    public static Bundle createArguments(@NonNull int professor_id) {
        final Bundle bundle = new Bundle();
        bundle.putInt("professor_id", professor_id);
        return bundle;
    }

    @NonNull
    private int getProfessorId() {
        return getArguments().getInt("professor_id", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_detail, container, false);
        initView(view);
        return view;

    }

    private void initView(View view) {
        mCompositeDisposable = new CompositeDisposable();
        mIconProgressBar = view.findViewById(R.id.loading_indicator);
        mEdxWebView = view.findViewById(R.id.professor_detail_webview);
        mEdxWebView.setVisibility(View.GONE);
        errorNotification = new FullScreenErrorNotification(mEdxWebView);
        loadData();
    }

    private void loadData() {
        // 请求服务器加载数据。
        if (!NetworkUtil.isConnected(getActivity())) {
            showNetwordisNotConnected();
            return;
        }
        mIconProgressBar.setVisibility(View.VISIBLE);

        Disposable disposable = eliteApi.getProfessorDetail(getProfessorId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(professorsDetailBean -> {
                    errorNotification.hideError();
                    populateAboutThisCourse(professorsDetailBean.getProfessor_info());
                }, throwable -> {
                    mIconProgressBar.setVisibility(View.GONE);
                    showError(throwable);
                });
        addDisposable(disposable);

    }

    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity销毁的时候清除所有请求
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    private void showNetwordisNotConnected() {
        errorNotification.showError(getResources().getString(R.string.reset_no_network_message),
                FontAwesomeIcons.fa_wifi, R.string.lbl_reload, v -> loadData());
    }

    private void showError(Throwable throwable) {
        errorNotification.showError(getActivity(), throwable, R.string.lbl_reload,
                v -> {
                    if (NetworkUtil.isConnected(getContext())) {
                        loadData();
                    }
                });
    }

    protected final Logger logger = new Logger(getClass().getName());


    private void populateAboutThisCourse(String overview) {
        URLInterceptorWebViewClient client = new URLInterceptorWebViewClient(
                getActivity(), mEdxWebView);
        client.setAllLinksAsExternal(true);

        StringBuilder buff = WebViewUtil.getIntialWebviewBuffer(getActivity(), logger);

        buff.append("<body>");
        buff.append("<div class=\"header\">");

        String css = "<div class=\"my-theme-course-about professor-detail\">";
        buff.append(css);
        buff.append(overview);
        String style = "</div><link rel=\"stylesheet\" type=\"text/css\" href=\"//oss.elitemba.cn/web_static/css/overview.css\" />";
        buff.append(style);
        buff.append("</div>");
        buff.append("</body>");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mEdxWebView.setWebContentsDebuggingEnabled(true);
        }
        mEdxWebView.clearCache(true);
        mEdxWebView.loadDataWithBaseURL(config.getApiHostURL(), buff.toString(), "text/html", StandardCharsets.UTF_8.name(), null);
        client.setPageStatusListener(new URLInterceptorWebViewClient.IPageStatusListener() {

            @Override
            public void onPageStarted() {

            }

            @Override
            public void onPageFinished() {

            }

            @Override
            public void onPageLoadError(WebView view, int errorCode, String description, String failingUrl) {

            }

            @Override
            public void onPageLoadError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse, boolean isMainRequestFailure) {

            }

            @Override
            public void onPageLoadProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    mIconProgressBar.setVisibility(View.GONE);
                    mEdxWebView.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
