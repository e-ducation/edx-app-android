package org.edx.mobile.eliteu.resetpassword;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;

public class ResetPasswordActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.reset_password_title));
    }

    @Override
    public Fragment getFirstFragment() {
        return new ResetPasswordFragment();
    }

}
