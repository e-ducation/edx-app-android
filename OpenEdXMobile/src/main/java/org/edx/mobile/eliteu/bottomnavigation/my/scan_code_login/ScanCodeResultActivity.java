package org.edx.mobile.eliteu.bottomnavigation.my.scan_code_login;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.gyf.immersionbar.ImmersionBar;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.util.NetworkUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScanCodeResultActivity extends BaseFragmentActivity {

    TextView confimLoginBtn;
    TextView cancleLoginBtn;
    @Inject
    EliteApi eliteApi;
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_result);
        initStatusBar();
        cancleLoginBtn = findViewById(R.id.cancle_login);
        confimLoginBtn = findViewById(R.id.confim_login);
        cancleLoginBtn.setOnClickListener(v -> onBackPressed());

        confimLoginBtn.setOnClickListener(v -> confimOnclick());

        findViewById(R.id.down_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void confimOnclick() {
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog("", getString(R.string.reset_no_network_message));
            return;
        }
        eliteApi.requestScanSuccess(getIntent().getStringExtra("url") + "&confirm=true")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpResponseBean -> {
                    if (httpResponseBean.getCode() == 200) {
                        Toast.makeText(ScanCodeResultActivity.this, R.string.scan_login_success, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ScanCodeResultActivity.this, R.string.scan_login_fail, Toast.LENGTH_LONG).show();
                    }
                    onBackPressed();
                }, throwable -> throwable.printStackTrace());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(0, R.anim.slide_out_bottom);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_bottom);

    }

    private void initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true);
        mImmersionBar.init();
    }

}
