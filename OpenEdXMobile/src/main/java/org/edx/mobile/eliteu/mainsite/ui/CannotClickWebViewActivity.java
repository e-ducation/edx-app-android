package org.edx.mobile.eliteu.mainsite.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.inject.Inject;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.http.callback.Callback;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.util.AppConstants;
import org.edx.mobile.util.ToastUtil;
import org.edx.mobile.view.Router;
import org.edx.mobile.view.custom.EdxWebView;

import java.util.concurrent.TimeUnit;

public class CannotClickWebViewActivity extends BaseFragmentActivity {

    public static final String COURSE_ID = "COURSE_ID";

    @Inject
    private EliteApi eliteApi;
    @Inject
    private LoginPrefs loginPrefs;
    @Inject
    private Router router;


    long shouldOverrideUrlLoadingTime = -1;
    private static final long FRESHNESS_INTERVAL = TimeUnit.SECONDS.toMillis(2);


    private static final String ARG_URL = "url";
    private static final String ARG_TITLE = "title";


    EdxWebView webView;

    public static Intent newIntent(@NonNull Context context, @NonNull String url, @Nullable String title, @NonNull String course_id) {
        return new Intent(context, CannotClickWebViewActivity.class)
                .putExtra(ARG_URL, url)
                .putExtra(COURSE_ID, course_id)
                .putExtra(ARG_TITLE, title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        super.setToolbarAsActionBar();

        final ProgressBar progress = findViewById(R.id.loading_indicator);
        progress.setVisibility(View.GONE);

        webView = findViewById(R.id.webView);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > AppConstants.PAGE_LOAD_THRESHOLD)
                    progress.setVisibility(View.GONE);
            }


        });


        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.GONE);
                view.getSettings().setJavaScriptEnabled(true);


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progress.setVisibility(View.GONE);
            }


            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (request.getUrl().toString().equals(view.getUrl())) {
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isshouldOverrideUrlLoadingExpired()) {
                    gotoCourseDetail();

                }

                return true;

            }

        });

        webView.loadUrl(getIntent().getStringExtra(ARG_URL));

        final String title = getIntent().getStringExtra(ARG_TITLE);
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(title);
        }

    }

    private void gotoCourseDetail() {
        shouldOverrideUrlLoadingTime = System.currentTimeMillis() + FRESHNESS_INTERVAL;
        eliteApi.getCourseDetail(getIntent().getStringExtra(COURSE_ID), loginPrefs.getUsername()).enqueue(new Callback<CourseDetail>() {
            @Override
            protected void onResponse(@NonNull CourseDetail responseBody) {
                if (responseBody != null) {
                    router.showCourseDetail(CannotClickWebViewActivity.this, responseBody);
                } else {
                    ToastUtil.makeText(CannotClickWebViewActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFailure(@NonNull Throwable error) {
                super.onFailure(error);
                error.printStackTrace();
                ToastUtil.makeText(CannotClickWebViewActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        webView.destroy();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        shouldOverrideUrlLoadingTime = -1;
    }

    public boolean isshouldOverrideUrlLoadingExpired() {
        return shouldOverrideUrlLoadingTime < System.currentTimeMillis();
    }
}
