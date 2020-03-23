package org.edx.mobile.eliteu.account;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import org.edx.mobile.R;
import org.edx.mobile.core.IEdxEnvironment;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.api.HttpResponseBean;
import org.edx.mobile.eliteu.util.UserAgreementUtil;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.util.SoftKeyboardUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

/**
 * @author: laibinzhi
 * @date: 2020-03-13 09:44
 * @github: https://github.com/laibinzhi
 * @blog: https://www.laibinzhi.top/
 */
@SuppressLint("ValidFragment")
public class DeleteAccountConfirmDialogFragment extends DialogFragment {

    EditText passwordEt;
    TextView okBtn;
    TextView cancleBtn;
    private CompositeDisposable mCompositeDisposable;

    private EliteApi eliteApi;

    private LoginPrefs loginPrefs;

    private IEdxEnvironment mIEdxEnvironment;

    @SuppressLint("ValidFragment")
    public DeleteAccountConfirmDialogFragment(EliteApi eliteApi,IEdxEnvironment iEdxEnvironment) {
        this.eliteApi = eliteApi;
        this.mIEdxEnvironment = iEdxEnvironment;
        loginPrefs =new LoginPrefs(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_fragment_delete_account_confirm, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false; //默认返回 false
                }
            }
        });
        initView(view);
        return view;
    }

    private void initView(View view) {
        mCompositeDisposable = new CompositeDisposable();

        passwordEt = view.findViewById(R.id.password_et);
        okBtn = view.findViewById(R.id.ok_btn);
        cancleBtn = view.findViewById(R.id.cancle_btn);

        RxTextView.textChanges(passwordEt).map(new Function<CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence password) {
                return password.length() > 0;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                okBtn.setEnabled(aBoolean);
                okBtn.setTextColor(aBoolean ? ContextCompat.getColor(getActivity(), R.color.confirm_delete_account_enable_color) : ContextCompat.getColor(getActivity(), R.color.confirm_delete_account_disenable_color));
            }
        });


        RxView.clicks(okBtn).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {

                doDeleteTask();
            }
        });

        RxView.clicks(cancleBtn).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                dismiss();
            }
        });
    }

    private void doDeleteTask() {
        Log.e("13123213", "doDeleteTask");
        okBtn.setEnabled(false);
        SoftKeyboardUtil.hide(getActivity());
        Disposable disposable = eliteApi.deletAccount(loginPrefs.getUsername(),passwordEt.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HttpResponseBean>() {
                    @Override
                    public void accept(HttpResponseBean httpResponseBean) throws Exception {
                        okBtn.setEnabled(true);
                        Log.e("132313","httpResponseBean="+httpResponseBean.toString());
                        if (httpResponseBean.getCode() ==204){
//                            Toast.makeText(getActivity(),"注销成功",Toast.LENGTH_SHORT).show();
                            mIEdxEnvironment.getRouter().performManualLogout(getActivity(),
                                    mIEdxEnvironment.getAnalyticsRegistry(), mIEdxEnvironment.getNotificationDelegate());
                        }else if (httpResponseBean.getCode() ==  403){
                            showToast(getString(R.string.password_error));
                        }else if (httpResponseBean.getCode() == 400){
                            showToast(getString(R.string.password_can_not_yanzhneg));
                        }else if (httpResponseBean.getCode() == 404){
                            showToast(getString(R.string.user_not_exit));
                        }else {
                            showToast(getString(R.string.delete_account_fail));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        okBtn.setEnabled(true);
                    }
                });

        addDisposable(disposable);

    }


    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    private void showToast(String msg){
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
