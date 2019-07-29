package org.edx.mobile.eliteu.bindmobile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
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
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.event.BindMobileSuccessEvent;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.util.CannotCancelDialogFragment;
import org.edx.mobile.eliteu.util.InputValidationUtil;
import org.edx.mobile.eliteu.util.RxBus;
import org.edx.mobile.http.HttpStatus;
import org.edx.mobile.user.Account;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.SoftKeyboardUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import kotlin.Unit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BindMobileFragment extends BaseFragment {

    private EditText mobileEt;
    private EditText verificationEt;
    private TextView verificationBtn;
    private TextView submitBtn;
    private TextView mobileAreaCodeTv;
    private TextView bind_mobile_tv;
    private View progress;

    //获取验证码按钮倒计时秒数
    private static final int COUNT_DOWN_TIME = 60;

    private CompositeDisposable mCompositeDisposable;

    @Inject
    private EliteApi eliteApi;
    @Inject
    AccountPrefs accountPrefs;
    boolean isChangeBind =false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bind_mobile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bind_mobile_tv = view.findViewById(R.id.bind_mobile_tv);
        Account account = accountPrefs.getAccount();
        if (TextUtils.isEmpty(account.getPhone())) {
            getActivity().setTitle(R.string.bind_mobile);
            bind_mobile_tv.setVisibility(View.GONE);
            isChangeBind = false;
        } else {
            getActivity().setTitle(R.string.change_bind_mobile);
            bind_mobile_tv.setVisibility(View.VISIBLE);
            bind_mobile_tv.setText(getString(R.string.current_bind_mobile)+account.getPhone());
            isChangeBind = true;
        }
        mCompositeDisposable = new CompositeDisposable();
        mobileAreaCodeTv = view.findViewById(R.id.mobile_area_code);
        mobileEt = view.findViewById(R.id.mobile_et);
        verificationEt = view.findViewById(R.id.verification_et);
        verificationBtn = view.findViewById(R.id.get_verification_btn);
        submitBtn = view.findViewById(R.id.submit_btn);
        if (isChangeBind){
            submitBtn.setText(R.string.change_bind_mobile);
        }else {
            submitBtn.setText(R.string.label_submit);
        }
        progress = view.findViewById(R.id.progress);
        //TODO 手机区号暂固定为+86，应由服务器获取
        mobileAreaCodeTv.setText("+86");
        RxTextView.textChanges(mobileEt).map(new Function<CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence phone) {
                return InputValidationUtil.isValidPhone(phone.toString());
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                verificationBtn.setEnabled(aBoolean);
                verificationBtn.setTextColor(aBoolean ? ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_can_click) : ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_cannot_click));
                initGetVerificationBtn();
            }
        });

        Observable.combineLatest(RxTextView.textChanges(mobileEt), RxTextView.textChanges(verificationEt), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(@NonNull CharSequence phone, @NonNull CharSequence verification) throws Exception {
                return InputValidationUtil.isValidPhone(phone.toString()) && verification.length() == 6;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                submitBtn.setEnabled(aBoolean);
                initSubmitBtn(mobileEt.getText().toString(), verificationEt.getText().toString());
            }
        });
    }

    /**
     * 初始化获取验证码按钮
     */
    private void initGetVerificationBtn() {
        RxView.clicks(verificationBtn).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        if (InputValidationUtil.isValidPhone(mobileEt.getText().toString())) {
                            //符合手机格式，请求获取验证码接口
                            requestServerSendVerificationCode(mobileEt.getText().toString());
                        } else {
                            showAlertDialog(getString(R.string.phone_number_format_not_correct));
                        }
                    }
                });
    }

    private void initSubmitBtn(final String mobile, final String verificationCode) {
        RxView.clicks(submitBtn).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        requestServerBindingMobile(mobile, verificationCode);
                    }
                });

    }

    /**
     * 网络请求服务器获取验证码
     *
     * @param phone
     */
    private void requestServerSendVerificationCode(String phone) {
        if (!NetworkUtil.isConnected(getActivity())) {
            showAlertDialog(getString(R.string.reset_no_network_message));
            return;
        }
        verificationBtn.setEnabled(false);
        verificationBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_cannot_click));

        SoftKeyboardUtil.hide(getActivity());
        eliteApi.sendCodeBindingPhone(phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            startCountDown();
                            Toast.makeText(getActivity(), R.string.verification_code_has_send, Toast.LENGTH_LONG).show();
                        } else {
                            verificationBtn.setEnabled(true);
                            verificationBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_can_click));

                            try {
                                String errorMsg = response.errorBody().string().replace("\"", "");
                                if (response.code() == HttpStatus.BAD_REQUEST) {
                                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_verification_code_error, Toast.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        verificationBtn.setEnabled(true);
                        verificationBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_can_click));
                        Toast.makeText(getActivity(), R.string.get_verification_code_error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 网络请求，提交绑定手机
     *
     * @param mobile
     * @param verificationCode
     */
    private void requestServerBindingMobile(final String mobile, String verificationCode) {
        if (!NetworkUtil.isConnected(getActivity())) {
            showAlertDialog(getString(R.string.reset_no_network_message));
            return;
        }
        showSubmitProgress();
        SoftKeyboardUtil.hide(getActivity());
        eliteApi.bindingPhone(mobile, verificationCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            //绑定手机成功
                            hideSubmitProgress();
                            showAlertDialog(getString(R.string.binding_success), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().finish();
                                    final AccountPrefs accountPrefs = new AccountPrefs(getActivity());
                                    Account account = accountPrefs.getAccount();
                                    account.setPhone(mobile);
                                    accountPrefs.storeAccount(account);
                                    RxBus.getDefault().post(new BindMobileSuccessEvent(mobile));
                                }
                            });
                        } else {
                            hideSubmitProgress();
                            try {
                                String errorMsg = response.errorBody().string().replace("\"", "");
                                if (response.code() == HttpStatus.BAD_REQUEST) {
                                    showAlertDialog(errorMsg);
                                } else {
                                    showAlertDialog(getString(R.string.binding_fail));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        hideSubmitProgress();
                        showAlertDialog(getString(R.string.binding_fail));
                    }
                });
    }

    /**
     * 增加Disposable
     *
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    /**
     * 按钮开始倒数
     */
    private void startCountDown() {
        Disposable disposable = Flowable.intervalRange(0, COUNT_DOWN_TIME + 1, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        verificationBtn.setText((COUNT_DOWN_TIME - aLong) + getString(R.string.verification_code_countdown));
                        verificationBtn.setEnabled(false);
                        verificationBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_cannot_click));

                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //倒计时完毕置为可点击状态
                        verificationBtn.setEnabled(true);
                        verificationBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_can_click));
                        verificationBtn.setText(R.string.verification_code_btn_str);
                    }
                })
                .subscribe();

        addDisposable(disposable);

    }

    private void showSubmitProgress() {
        progress.setVisibility(View.VISIBLE);
        if (isChangeBind){
            submitBtn.setText(getString(R.string.change_bind_mobile) + "...");
        }else {
            submitBtn.setText(getString(R.string.label_submit) + "...");
        }
    }

    private void hideSubmitProgress() {
        progress.setVisibility(View.GONE);
        if (isChangeBind){
            submitBtn.setText(R.string.change_bind_mobile);
        }else {
            submitBtn.setText(R.string.label_submit);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity销毁的时候清除所有请求
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
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
