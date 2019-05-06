package org.edx.mobile.eliteu.custonwebview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.event.EnrolledInCourseEvent;
import org.edx.mobile.event.NetworkConnectivityChangeEvent;
import org.edx.mobile.logger.Logger;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.UiUtil;
import org.edx.mobile.view.OfflineSupportUtils;

import de.greenrobot.event.EventBus;
import roboguice.inject.InjectView;

/**
 * Provides a webview which authenticates the user before loading a page,
 * Javascript can also be passed in arguments for evaluation.
 */
public class EliteuCustomWebViewFragment extends BaseFragment {
    protected final Logger logger = new Logger(getClass().getName());
    public static final String ARG_URL = "ARG_URL";
    public static final String ARG_JAVASCRIPT = "ARG_JAVASCRIPT";
    public static final String ARG_IS_MANUALLY_RELOADABLE = "ARG_IS_MANUALLY_RELOADABLE";

    @InjectView(R.id.auth_webview)
    protected EliteuCustomWebView authWebView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.loading_indicator)
    private ProgressBar progressWheel;

    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    private boolean refreshOnResume = false;

    public static Bundle makeArguments(@NonNull String url, @Nullable String javascript, boolean isManuallyReloadable) {
        final Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putBoolean(ARG_IS_MANUALLY_RELOADABLE, isManuallyReloadable);
        if (!TextUtils.isEmpty(javascript)) {
            args.putString(ARG_JAVASCRIPT, javascript);
        }
        return args;
    }

    public static Fragment newInstance(@NonNull String url) {
        return newInstance(url, null);
    }

    public static Fragment newInstance(@NonNull String url, @Nullable String javascript) {
        final Fragment fragment = new EliteuCustomWebViewFragment();
        fragment.setArguments(makeArguments(url, javascript, false));
        return fragment;
    }

    public static Fragment newInstance(@NonNull String url, @Nullable String javascript, boolean isManuallyReloadable) {
        final Fragment fragment = new EliteuCustomWebViewFragment();
        fragment.setArguments(makeArguments(url, javascript, isManuallyReloadable));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eliteu_custom_webview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Disable the SwipeRefreshLayout by-default to allow the subclasses to provide a proper implementation for it
        swipeContainer.setEnabled(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            final String url = getArguments().getString(ARG_URL);
            final String javascript = getArguments().getString(ARG_JAVASCRIPT);
            final boolean isManuallyReloadable = getArguments().getBoolean(ARG_IS_MANUALLY_RELOADABLE);

            authWebView.initWebView(getActivity(), false, isManuallyReloadable);
            authWebView.loadUrlWithJavascript(true, url, javascript);
        }


        authWebView.getWebViewClient().setPageStatusListener(new CustomURLInterceptorWebViewClient.IPageStatusListener() {
            @Override
            public void onPageStarted() {
            }

            @Override
            public void onPageFinished() {
                swipeContainer.setRefreshing(false);
                tryEnablingSwipeContainer();
            }

            @Override
            public void onPageLoadError(WebView view, int errorCode, String description, String failingUrl) {
                onPageFinished();
            }

            @Override
            public void onPageLoadError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse, boolean isMainRequestFailure) {
                onPageFinished();
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
        authWebView.onResume();
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

    @Override
    public void onPause() {
        super.onPause();
        authWebView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        authWebView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        authWebView.onDestroyView();
    }
}
