package org.edx.mobile.whatsnew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.gyf.immersionbar.ImmersionBar;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.base.BaseAppActivity;
import org.edx.mobile.base.MainApplication;
import org.edx.mobile.module.prefs.PrefManager;

public class WhatsNewActivity extends BaseAppActivity {

    private ImmersionBar mImmersionBar;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, WhatsNewActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);
        initStatusBar();


        Fragment singleFragment = new WhatsNewFragment();

        // This activity will only ever hold this lone fragment, so we
        // can afford to retain the instance during activity recreation
        singleFragment.setRetainInstance(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, singleFragment, null);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();

        final PrefManager.AppInfoPrefManager appPrefs = new PrefManager.AppInfoPrefManager(MainApplication.application);
        appPrefs.setWhatsNewShown(BuildConfig.VERSION_NAME);
    }


    private void initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
                .statusBarColor(R.color.edx_brand_primary_base)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true);  //透明状态栏，不写默认透明色
        mImmersionBar.init();
    }
}
