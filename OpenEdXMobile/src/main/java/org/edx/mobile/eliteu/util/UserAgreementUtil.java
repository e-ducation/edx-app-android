package org.edx.mobile.eliteu.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.util.TextUtils;

import java.io.Serializable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: laibinzhi
 * @date: 2019-12-10 11:46
 * @github: https://github.com/laibinzhi
 * @blog: https://www.laibinzhi.top/
 */
public class UserAgreementUtil {

    private volatile static UserAgreementUtil instance;

    private Context mContext;

    PrefManager mPrefManager;

    EliteApi mEliteApi;

    public static final String USER_AGREEMENT_VERSION_SP_KEY = "user_agreement_verison";

    private UserAgreementUtil(EliteApi eliteApi) {
        this.mEliteApi = eliteApi;
        mPrefManager = new PrefManager(mContext, PrefManager.Pref.USER_AGREEMENT_VERSION);
    }

    public static UserAgreementUtil newInstance(EliteApi eliteApi) {

        if (instance == null) {
            synchronized (UserAgreementUtil.class) {
                if (instance == null) {
                    instance = new UserAgreementUtil(eliteApi);
                }

            }
        }

        return instance;
    }


    public void check(Context context) {
        this.mContext = context;

        mEliteApi.requestUserAgreementVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(version -> {
                    if (version.isNotify()){
                        String localVersion = mPrefManager.getString(USER_AGREEMENT_VERSION_SP_KEY);
                        String netVersion = version.getVersion();
                        if (android.text.TextUtils.isEmpty(localVersion)) {
                            showDialog(mPrefManager, mContext,netVersion);
                        } else {
                            if (!localVersion.equals(netVersion)) {
                                showDialog(mPrefManager, mContext,netVersion);
                            } else {
                            }
                        }
                    }else {
                    }
                }, throwable -> throwable.printStackTrace());
    }


    private void showDialog(PrefManager prefManager, final Context context,String version) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.user_agreement_dialog_title);
        builder.setMessage(TextUtils.generateLicenseText2(context.getResources(), R.string.user_agreement_dialog));
        builder.setPositiveButton(R.string.user_agreement_dialog_yes_btn, (dialog, which) -> prefManager.put(USER_AGREEMENT_VERSION_SP_KEY,version));
        builder.setNegativeButton(R.string.user_agreement_dialog_no_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);//设置对话框外部没有焦点
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            } else {
                return false; //默认返回 false
            }
        });
        dialog.show();
        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    public static class UserAgreementVersion implements Serializable{

        String version;

        boolean notify;

        public boolean isNotify() {
            return notify;
        }

        public void setNotify(boolean notify) {
            this.notify = notify;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

}
