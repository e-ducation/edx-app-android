package org.edx.mobile.eliteu.vip.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.edx.mobile.base.BaseSingleFragmentActivity;


public class VipActivity extends BaseSingleFragmentActivity {

    public static final String VIP_SELECT_ID = "VIP_SELECT_ID";

    public static Intent newIntent(@NonNull Context context, @NonNull String id) {
        return new Intent(context, VipActivity.class)
                .putExtra(VIP_SELECT_ID, id);
    }

    @Override
    public Fragment getFirstFragment() {
        return VipFragment.newInstance(getIntent().getStringExtra(VIP_SELECT_ID));
    }

}
