package org.edx.mobile.eliteu.bottomnavigation.my.accountmanager;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.bindmobile.BindMobileUtil;
import org.edx.mobile.view.Router;

import java.util.concurrent.TimeUnit;

public class AccountManagerActivity extends BaseFragmentActivity {

    RelativeLayout layoutBindMobile;
    RelativeLayout layoutResetPassword;
    TextView bindMobileTv;
    AppBarLayout appBarLayout;
    @Inject
    Router router;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        super.setToolbarAsActionBar();
        setTitle(getString(R.string.account_manager));
        initUi();
    }

    private void initUi() {
        layoutBindMobile = findViewById(R.id.layout_bind_mobile);
        layoutResetPassword = findViewById(R.id.layout_reset_password);
        bindMobileTv = findViewById(R.id.binding_mobile_tv);
        appBarLayout = findViewById(R.id.appbar);
        BindMobileUtil.getInstance().initBindMobileButton(this, bindMobileTv);
        RxView.clicks(layoutBindMobile).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showBindMobile(this));
        RxView.clicks(layoutResetPassword).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showResetPassword(this));
    }

}
