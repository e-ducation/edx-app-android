package org.edx.mobile.eliteu.bottomnavigation.my.scan_code_login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

import com.gyf.immersionbar.ImmersionBar;
import com.king.zxing.CaptureActivity;
import com.king.zxing.ViewfinderView;

import org.edx.mobile.R;

public class ScanCodeLoginActivity extends CaptureActivity {

    private ImmersionBar mImmersionBar;

    SurfaceView surfaceView;

    ViewfinderView viewfinderView;

    @Override
    public int getLayoutId() {
        return R.layout.scan_code_login_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        surfaceView = findViewById(R.id.surfaceView);
        viewfinderView = findViewById(R.id.viewfinderView);
        findViewById(R.id.ivLeft).setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true);
        mImmersionBar.init();
    }

}
