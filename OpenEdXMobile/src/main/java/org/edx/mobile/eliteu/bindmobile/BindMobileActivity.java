package org.edx.mobile.eliteu.bindmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;

public class BindMobileActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.bind_mobile));
    }

    @Override
    public Fragment getFirstFragment() {
        return new BindMobileFragment();
    }

}
