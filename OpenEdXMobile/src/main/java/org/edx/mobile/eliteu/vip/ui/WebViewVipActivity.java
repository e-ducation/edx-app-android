package org.edx.mobile.eliteu.vip.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;
import org.edx.mobile.social.ThirdPartyLoginConstants;
import org.edx.mobile.util.Config;

public class WebViewVipActivity extends BaseSingleFragmentActivity {

    @Inject
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.vip_page_title);
    }

    @Override
    public Fragment getFirstFragment() {
        WebViewVipFragment webViewVipFragment = new WebViewVipFragment();
        webViewVipFragment.setArguments(WebViewVipFragment.makeArguments(config.getApiHostURL() + ThirdPartyLoginConstants.VIP_URL,
                "javascript", true));
        return webViewVipFragment;
    }
}
