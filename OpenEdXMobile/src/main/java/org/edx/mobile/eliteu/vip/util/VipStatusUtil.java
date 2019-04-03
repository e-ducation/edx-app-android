package org.edx.mobile.eliteu.vip.util;

import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.event.VipRemainDayUpdateEvent;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.util.RxBus;

import io.reactivex.functions.Consumer;

public class VipStatusUtil {

    private static volatile VipStatusUtil instance = null;

    private VipStatusUtil() {

    }

    public static VipStatusUtil getInstance() {
        if (instance == null) {
            synchronized (VipStatusUtil.class) {
                if (instance == null) {
                    instance = new VipStatusUtil();
                }
            }
        }
        return instance;
    }


    public void initVipButton(final BaseFragmentActivity activity, final TextView textView) {
        final AccountPrefs accountPrefs = new AccountPrefs(activity);
        if (accountPrefs.getAccount().getVip_status() == 1) {
            textView.setText(activity.getString(R.string.vip_not_buy));
        } else if (accountPrefs.getAccount().getVip_status() == 3) {
            textView.setText(activity.getString(R.string.vip_expired));
        } else if (accountPrefs.getAccount().getVip_status() == 2) {
            String day = activity.getResources().getQuantityString(R.plurals.vip_time_day, accountPrefs.getAccount().getVip_remain_days(), accountPrefs.getAccount().getVip_remain_days());
            textView.setText(day);
        }

        RxBus.getDefault().toObservable(VipRemainDayUpdateEvent.class).subscribe(new Consumer<VipRemainDayUpdateEvent>() {
            @Override
            public void accept(VipRemainDayUpdateEvent vipRemainDayUpdateEvent) throws Exception {
                String day = activity.getResources().getQuantityString(R.plurals.vip_time_day, vipRemainDayUpdateEvent.getVip_remain_day(), vipRemainDayUpdateEvent.getVip_remain_day());
                textView.setText(day);
            }
        });
    }

}
