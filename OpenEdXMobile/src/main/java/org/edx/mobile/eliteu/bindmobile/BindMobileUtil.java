package org.edx.mobile.eliteu.bindmobile;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.TextView;


import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.event.BindMobileSuccessEvent;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.util.CannotCancelDialogFragment;
import org.edx.mobile.eliteu.util.RxBus;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.user.Account;
import org.edx.mobile.view.Router;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.functions.Consumer;

public class BindMobileUtil {

    private static volatile BindMobileUtil instance = null;

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String BIND_MOBILE_SP_KEY = "bind_mobile_dialog_last_show_time";

    private BindMobileUtil() {

    }

    public static BindMobileUtil getInstance() {
        if (instance == null) {
            synchronized (BindMobileUtil.class) {
                if (instance == null) {
                    instance = new BindMobileUtil();
                }
            }
        }
        return instance;
    }

    public void checkAccountMobile(BaseFragmentActivity activity) {
        final AccountPrefs accountPrefs = new AccountPrefs(activity);
        Account account = accountPrefs.getAccount();
        if (account != null) {
            checkAccountMobile(account, activity);
        }
    }

    public void checkAccountMobile(Account account, BaseFragmentActivity activity) {
        if (!activity.isInForeground()) {
            return;
        }
        if (TextUtils.isEmpty(account.getPhone())) {
            final PrefManager bindMobilePrefManager = new PrefManager(
                    activity, PrefManager.Pref.LOGIN);
            //格式 2018-12-17
            String lastShowTime = bindMobilePrefManager.getString(BIND_MOBILE_SP_KEY);
            String nowTime = new SimpleDateFormat(TIME_FORMAT).format(new Date());
            if (lastShowTime == null || !TextUtils.equals(lastShowTime, nowTime)) {
                showDialog(bindMobilePrefManager, activity, nowTime);
            }
        }
    }

    private void showDialog(PrefManager bindMobilePrefs, final BaseFragmentActivity activity, String nowTime) {
        bindMobilePrefs.put(BIND_MOBILE_SP_KEY, nowTime);
        CannotCancelDialogFragment.newInstance(activity.getString(R.string.bind_mobie_dialog_title), activity.getString(R.string.bind_mobie_dialog_message), activity.getString(R.string.bind_mobie_dialog_positiveText), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Router().showBindMobile(activity);
            }
        }, activity.getString(R.string.bind_mobie_dialog_negativeText), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show(activity.getSupportFragmentManager(), null);
    }

    public void initBindMobileButton(final BaseFragmentActivity activity, final TextView textView) {
        final AccountPrefs accountPrefs = new AccountPrefs(activity);
        if (TextUtils.isEmpty(accountPrefs.getAccount().getPhone())) {
            textView.setText(R.string.not_bind_mobile);
        } else {
            textView.setText(activity.getString(R.string.already_bind_mobile) + " " + accountPrefs.getAccount().getPhone());
        }
        RxBus.getDefault().toObservable(BindMobileSuccessEvent.class).subscribe(new Consumer<BindMobileSuccessEvent>() {
            @Override
            public void accept(BindMobileSuccessEvent bindMobileSuccessEvent) throws Exception {
                textView.setText(activity.getString(R.string.already_bind_mobile) + " " + bindMobileSuccessEvent.getPhone());
            }
        });
    }
}
