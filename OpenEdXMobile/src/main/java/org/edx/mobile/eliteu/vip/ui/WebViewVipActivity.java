package org.edx.mobile.eliteu.vip.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;
import org.edx.mobile.eliteu.util.RxBus;
import org.edx.mobile.event.MoveToDiscoveryTabEvent;
import org.edx.mobile.social.ThirdPartyLoginConstants;
import org.edx.mobile.util.Config;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WebViewVipActivity extends BaseSingleFragmentActivity {

    @Inject
    Config config;
    private CompositeDisposable mCompositeDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.vip_page_title);
        mCompositeDisposable = new CompositeDisposable();
        initDiscoveryTabEvent();
    }

    @Override
    public Fragment getFirstFragment() {
        WebViewVipFragment webViewVipFragment = new WebViewVipFragment();
        webViewVipFragment.setArguments(WebViewVipFragment.makeArguments(config.getApiHostURL() + ThirdPartyLoginConstants.VIP_URL,
                "javascript", true));
        return webViewVipFragment;
    }


    private void initDiscoveryTabEvent() {
        Disposable disposable = RxBus.getDefault().toObservable(MoveToDiscoveryTabEvent.class).subscribe(moveToDiscoveryTabEvent -> {
            if (!isDestroyed()) {
                finish();
            }
        });
        addDisposable(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }
}
