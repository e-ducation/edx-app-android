package org.edx.mobile.eliteu.bottomnavigation.my.editprofile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;
import org.edx.mobile.module.analytics.Analytics;

public class EditProfileInfoActivity extends BaseSingleFragmentActivity {

    public static final String EXTRA_USERNAME = "username";

    public static Intent newIntent(@NonNull Context context, @NonNull String username) {
        return new Intent(context, EditProfileInfoActivity.class)
                .putExtra(EXTRA_USERNAME, username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.person_info);
        environment.getAnalyticsRegistry().trackScreenView(Analytics.Screens.PROFILE_EDIT);
    }

    @Override
    public Fragment getFirstFragment() {
        final Fragment fragment = new EditProfileInfoFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }
}
