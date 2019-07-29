package org.edx.mobile.eliteu.bottomnavigation.my;


import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;

public class AboutUsActivity extends BaseFragmentActivity {

    TextView aboutUsTv;
    TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        super.setToolbarAsActionBar();
        setTitle(getString(R.string.aboutus));
        aboutUsTv = findViewById(R.id.about_us_tv);
        versionTv = findViewById(R.id.version_tv);
        versionTv.setText(String.format("V " + BuildConfig.VERSION_NAME));
        aboutUsTv.setText(Html.fromHtml(getString(R.string.about_us_text)));
    }
}
