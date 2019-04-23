package org.edx.mobile.eliteu.custonwebview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.edx.mobile.base.BaseSingleFragmentActivity;

public class CustomWebViewActivity extends BaseSingleFragmentActivity {

    private static final String ARG_URL = "url";
    private static final String ARG_TITLE = "title";

    public static Intent newIntent(@NonNull Context context, @NonNull String url, @NonNull String title) {
        return new Intent(context, CustomWebViewActivity.class)
                .putExtra(ARG_URL, url)
                .putExtra(ARG_TITLE, title);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String title = getIntent().getStringExtra(ARG_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    @Override
    public Fragment getFirstFragment() {
        return EliteuCustomWebViewFragment.newInstance(getIntent().getStringExtra(ARG_URL),"javascript");
    }
}
