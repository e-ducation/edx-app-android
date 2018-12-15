package org.edx.mobile.eliteu.vip.ui;


import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.event.EnrolledInCourseEvent;
import org.edx.mobile.event.NetworkConnectivityChangeEvent;
import org.edx.mobile.social.ThirdPartyLoginConstants;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.ResourceUtil;
import org.edx.mobile.util.UiUtil;
import org.edx.mobile.util.images.ShareUtils;
import org.edx.mobile.view.AuthenticatedWebViewFragment;
import org.edx.mobile.view.OfflineSupportUtils;
import org.edx.mobile.view.custom.URLInterceptorWebViewClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.reactivex.functions.Consumer;
import roboguice.inject.InjectView;

public class WebViewVipFragment extends AuthenticatedWebViewFragment {

    @InjectView(R.id.fab)
    protected FloatingActionButton fab;

    @InjectView(R.id.loading_indicator)
    private ProgressBar progressWheel;

    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;

    private boolean refreshOnResume = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authenticated_webview_with_fab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RxView.clicks(fab).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        shareVip();
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            authWebView.getWebView().setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY && scrollY > 0) {
                        fab.hide();
                    }
                    if (scrollY < oldScrollY) {
                        fab.show();
                    }
                }
            });
        }
        authWebView.getWebViewClient().setPageStatusListener(new URLInterceptorWebViewClient.IPageStatusListener() {
            @Override
            public void onPageStarted() {

            }

            @Override
            public void onPageFinished() {
                swipeContainer.setRefreshing(false);
                tryEnablingSwipeContainer();
                fab.show();
            }

            @Override
            public void onPageLoadError(WebView view, int errorCode, String description, String failingUrl) {
                swipeContainer.setRefreshing(false);
                tryEnablingSwipeContainer();
            }

            @Override
            public void onPageLoadError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse, boolean isMainRequestFailure) {
                swipeContainer.setRefreshing(false);
                tryEnablingSwipeContainer();
            }

            @Override
            public void onPageLoadProgressChanged(WebView webView, int progress) {

            }
        });

        tryEnablingSwipeContainer();
        UiUtil.setSwipeRefreshLayoutColors(swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // We already have spinner inside the WebView, so we don't need the SwipeRefreshLayout's spinner
                swipeContainer.setEnabled(false);
                authWebView.loadUrl(true, authWebView.getWebView().getUrl());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        SwipeRefreshLayout intercepts and acts upon the scroll even when its child layout hasn't
        scrolled to its top, which leads to refresh logic happening and spinner appearing mid-scroll.
        With the following logic, we are forcing the SwipeRefreshLayout to use the scroll only when
        the underlying WebView has scrolled to its top.
        More info can be found on this SO question: https://stackoverflow.com/q/24658428/1402616
         */
        swipeContainer.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (!tryEnablingSwipeContainer())
                            swipeContainer.setEnabled(false);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        swipeContainer.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshOnResume) {
            refreshOnResume = false;
            // Swipe refresh shouldn't work while the page is refreshing
            swipeContainer.setEnabled(false);
            authWebView.loadUrl(true, authWebView.getWebView().getUrl());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        OfflineSupportUtils.setUserVisibleHint(getActivity(), isVisibleToUser,
                authWebView != null && authWebView.isShowingError());
    }

    @SuppressWarnings("unused")
    public void onEvent(NetworkConnectivityChangeEvent event) {
        if (getActivity() != null) {
            if (!tryEnablingSwipeContainer()) {
                //Disable swipe functionality and hide the loading view
                swipeContainer.setEnabled(false);
                swipeContainer.setRefreshing(false);
            }
            OfflineSupportUtils.onNetworkConnectivityChangeEvent(getActivity(), getUserVisibleHint(), authWebView.isShowingError());
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(EnrolledInCourseEvent event) {
        refreshOnResume = true;
    }

    @Override
    protected void onRevisit() {
        tryEnablingSwipeContainer();
        OfflineSupportUtils.onRevisit(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Tries enabling the {@link #swipeContainer} if certain conditions are met and tells the caller
     * if it was enabled or not.
     *
     * @return <code>true</code> if {@link #swipeContainer} was enabled, <code>false</code> otherwise.
     */
    private boolean tryEnablingSwipeContainer() {
        if (getActivity() != null) {
            if (NetworkUtil.isConnected(getActivity())
                    && !authWebView.isShowingError()
                    && progressWheel.getVisibility() != View.VISIBLE
                    && authWebView.getWebView().getScrollY() == 0) {
                swipeContainer.setEnabled(true);
                return true;
            }
        }
        return false;
    }

    /**
     * vip分享页面
     */
    private void shareVip() {
        final Map<String, CharSequence> shareTextParams = new HashMap<>();
        shareTextParams.put("platform_name", getString(R.string.platform_name));
        shareTextParams.put("vip_url", ThirdPartyLoginConstants.VIP_URL);
        final String shareText = ResourceUtil.getFormattedString(getResources(), R.string.share_vip_message, shareTextParams).toString();
        ShareUtils.showShareMenu(getActivity(), ShareUtils.newShareIntent(shareText), getActivity().findViewById(R.id.menu_item_account), new ShareUtils.ShareMenuItemListener() {
            @Override
            public void onMenuItemClick(@NonNull ComponentName componentName, @NonNull ShareUtils.ShareType shareType) {
                final Intent intent = ShareUtils.newShareIntent(shareText);
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });
    }

}
