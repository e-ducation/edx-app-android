package org.edx.mobile.eliteu.resetpassword;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.api.HttpResponseBean;
import org.edx.mobile.eliteu.util.CannotCancelDialogFragment;
import org.edx.mobile.http.callback.Callback;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.SoftKeyboardUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import kotlin.Unit;

public class ResetPasswordFragment extends BaseFragment {

    TextInputEditText oldPasswordEt;
    TextInputEditText newPasswordEt;
    TextInputEditText doubleNewPasswordEt;
    TextView submitBtn;
    View progress;
    @Inject
    private EliteApi eliteApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        oldPasswordEt = view.findViewById(R.id.old_password_et);
        newPasswordEt = view.findViewById(R.id.new_password_et);
        doubleNewPasswordEt = view.findViewById(R.id.double_new_password_et);
        progress = view.findViewById(R.id.progress);
        submitBtn = view.findViewById(R.id.submit_btn);
        Observable.combineLatest(RxTextView.textChanges(oldPasswordEt), RxTextView.textChanges(newPasswordEt), RxTextView.textChanges(doubleNewPasswordEt), new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence oldpassword, CharSequence newpassword, CharSequence doublenewpassword) throws Exception {
                return oldpassword.length() >= 6 && newpassword.length() >= 6 && doublenewpassword.length() >= 6;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                submitBtn.setEnabled(aBoolean);
                initSubmitBtn(oldPasswordEt.getText().toString(), newPasswordEt.getText().toString(), doubleNewPasswordEt.getText().toString());
            }
        });
    }

    private void initSubmitBtn(String oldpassword, String newpassword, String doublenewpassword) {
        RxView.clicks(submitBtn).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        requestServerResetPassword(oldpassword, newpassword, doublenewpassword);
                    }
                });
    }

    private void requestServerResetPassword(String oldpassword, String newpassword, String doublenewpassword) {
        if (!newpassword.equals(doublenewpassword)){
            showAlertDialog(getString(R.string.new_password_different));
            return;
        }
        if (!NetworkUtil.isConnected(getActivity())) {
            showAlertDialog(getString(R.string.reset_no_network_message));
            return;
        }
        showSubmitProgress();
        SoftKeyboardUtil.hide(getActivity());
        eliteApi.resetPassword(oldpassword, newpassword, doublenewpassword)
                .enqueue(new Callback<HttpResponseBean>() {
                    @Override
                    protected void onResponse(@NonNull HttpResponseBean responseBody) {
                        hideSubmitProgress();
                        if (responseBody.getCode() == 200) {
                            showAlertDialog(getString(R.string.password_reset_success), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().finish();
                                }
                            });
                        } else if (responseBody.getCode() == 201) {
                            showAlertDialog(getString(R.string.new_password_different));
                        } else if (responseBody.getCode() == 202) {
                            showAlertDialog(getString(R.string.password_format_error));
                        } else if (responseBody.getCode() == 203) {
                            showAlertDialog(getString(R.string.old_password_error));
                        } else if (responseBody.getCode() == 400) {
                            showAlertDialog(getString(R.string.reset_password_error));
                        } else {
                            showAlertDialog(getString(R.string.error_unknown));
                        }
                    }

                    @Override
                    protected void onFailure(@NonNull Throwable error) {
                        super.onFailure(error);
                        error.printStackTrace();
                        hideSubmitProgress();
                        showAlertDialog(getString(R.string.reset_password_error));
                    }
                });
    }

    private void showSubmitProgress() {
        progress.setVisibility(View.VISIBLE);
        submitBtn.setText(getString(R.string.label_submit) + "...");
    }

    private void hideSubmitProgress() {
        progress.setVisibility(View.GONE);
        submitBtn.setText(R.string.label_submit);
    }

    private void showAlertDialog(@NonNull String message) {
        BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) getActivity();
        baseFragmentActivity.showAlertDialog("", message);
    }

    private void showAlertDialog(@NonNull String message, @Nullable DialogInterface.OnClickListener onPositiveClick) {
        BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) getActivity();
        if (baseFragmentActivity.isInForeground()) {
            //使用点击返回键不消失的对话框
            CannotCancelDialogFragment.newInstance("", message, getString(R.string.label_ok), onPositiveClick, null, null)
                    .show(getActivity().getSupportFragmentManager(), null);
        }
    }
}
