package org.edx.mobile.eliteu.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;

/**
 * @author: laibinzhi
 * @date: 2020-03-12 17:56
 * @github: https://github.com/laibinzhi
 * @blog: https://www.laibinzhi.top/
 */
public class DeleteMyAccountActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.delete_account));
    }

    @Override
    public Fragment getFirstFragment() {
        return new DeleteMyAccountFragment();
    }
}
