package org.edx.mobile.eliteu.vip.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.lbz.pay.PayAPI;
import com.lbz.pay.alipay.AliPayReq;
import com.lbz.pay.alipay.AliPayReqParam;
import com.lbz.pay.wechat.WeChatPayReq;
import com.lbz.pay.wechat.WeChatReqParam;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.util.DeviceProgramDetectionUtil;
import org.edx.mobile.eliteu.util.RxBus;
import org.edx.mobile.eliteu.vip.bean.WeChatReqBean;
import org.edx.mobile.eliteu.event.PayResultEvent;
import org.edx.mobile.eliteu.vip.bean.VipOrderStatusBean;
import org.edx.mobile.eliteu.util.BaseHttpResult;
import org.edx.mobile.eliteu.util.DateUtil;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.vip.adapter.VipAdapter;
import org.edx.mobile.eliteu.vip.bean.AliPayReqBean;
import org.edx.mobile.eliteu.vip.bean.VipBean;
import org.edx.mobile.eliteu.vip.bean.VipPageIndexBean;
import org.edx.mobile.eliteu.vip.bean.VipPersonInfo;
import org.edx.mobile.eliteu.wight.SelectPayPopupWindow;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.model.Page;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.view.OfflineSupportBaseFragment;
import org.edx.mobile.view.custom.IconProgressBar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VipFragment extends OfflineSupportBaseFragment {

    private RecyclerView mRecyclerView;
    private View mParentView;
    private VipAdapter mVipAdapter;
    private List<VipBean> mDatas;
    private IconProgressBar mIconProgressBar;
    private FullScreenErrorNotification errorNotification;
    private CompositeDisposable mCompositeDisposable;
    private ProgressDialog mCheckOrderStatusLoadingDialog;
    private SelectPayPopupWindow selectPayPopupWindow;
    private String mOrderId;
    @Inject
    private EliteApi eliteApi;

    public static Fragment newInstance(String vip_select_id) {
        final VipFragment fragment = new VipFragment();
        fragment.setArguments(createArguments(vip_select_id));
        return fragment;
    }

    @NonNull
    @VisibleForTesting
    public static Bundle createArguments(@NonNull String vip_select_id) {
        final Bundle bundle = new Bundle();
        bundle.putString(VipActivity.VIP_SELECT_ID, vip_select_id);
        return bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vip, container, false);
        initView(view);
        initData();
        initWeChatPayCallBack();
        return view;
    }

    private void initView(View view) {
        mIconProgressBar = view.findViewById(R.id.loading_indicator);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mParentView = view.findViewById(R.id.parent_view);
        errorNotification = new FullScreenErrorNotification(mRecyclerView);
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mCompositeDisposable = new CompositeDisposable();
    }

    private void initData() {
        if (!NetworkUtil.isConnected(getActivity())) {
            showNetwordisNotConnected();
            return;
        }
        mIconProgressBar.setVisibility(View.VISIBLE);
        Observable<VipPersonInfo> vipPersonInfoObservable = eliteApi.getVipPersonInfo();
        Observable<Page<VipBean>> vipPackagesObservable = eliteApi.getVipPackages();

        Disposable disposable = Observable.zip(vipPersonInfoObservable, vipPackagesObservable, new BiFunction<VipPersonInfo, Page<VipBean>, VipPageIndexBean>() {
            @Override
            public VipPageIndexBean apply(VipPersonInfo vipPersonResult, Page<VipBean> vipBeanResult) throws Exception {
                VipPageIndexBean indexBean = new VipPageIndexBean(vipPersonResult, vipBeanResult.getResults());
                return indexBean;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VipPageIndexBean>() {
                    @Override
                    public void accept(VipPageIndexBean vipPageIndexBean) throws Exception {
                        VipPersonInfo vipPersonInfo = vipPageIndexBean.getVipPersonInfo();
                        mDatas = vipPageIndexBean.getVipBeans();
                        setUpSelectVip();
                        initAdapter();
                        setUpHeader(vipPersonInfo);
                        mIconProgressBar.setVisibility(View.GONE);
                        errorNotification.hideError();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mIconProgressBar.setVisibility(View.GONE);
                        showError(throwable);
                    }
                });
        addDisposable(disposable);
    }

    /**
     * @return 获取选中的套餐ID
     */
    @NonNull
    private String getVipSelectId() {
        return getArguments().getString(VipActivity.VIP_SELECT_ID);
    }

    /**
     * 根据设置套餐列表哪个高亮
     * 当setUpSelectVip()的值和VipActivity.VIP_SELECT_ID相等时，表示这个页面不是在H5页面进来的
     */
    private void setUpSelectVip() {
        if (getVipSelectId().equals(VipActivity.VIP_SELECT_ID)) {
            mDatas.get(0).setSelect(true);
            return;
        }
        for (VipBean vipBean : mDatas) {
            if (String.valueOf(vipBean.getId()).equals(getVipSelectId())) {
                vipBean.setSelect(true);
            }
        }
    }

    private void initAdapter() {
        mVipAdapter = new VipAdapter(mDatas);
        mVipAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                mVipAdapter.changeBg(position);
                selectPayPopupWindow = new SelectPayPopupWindow.Builder().with(getActivity())
                        .setPrice(mDatas.get(position).getPrice())
                        .setParentView(mParentView)
                        .create()
                        .show();
                selectPayPopupWindow.setOnAliPayListener(new SelectPayPopupWindow.ISelectPayWayListener() {
                    @Override
                    public void payByAliPay() {
                        new RxPermissions(getActivity())
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean granted) throws Exception {
                                        if (granted) {
                                            selectAliPay(mDatas.get(position).getId());
                                        }
                                    }
                                });
                    }

                    @Override
                    public void payByWeChat() {
                        selectWeChatPay(mDatas.get(position).getId());
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mVipAdapter);
    }

    /**
     * 根据返回来的vip个人信息设置头部
     *
     * @param vipPersonInfo Vip个人信息
     */
    private void setUpHeader(VipPersonInfo vipPersonInfo) {
        if (vipPersonInfo.isIs_vip()) {
            //已开通
            View already_opened_header = LayoutInflater.from(getActivity()).inflate(R.layout.vip_already_opened_header, null, false);
            if (already_opened_header != null) {
                TextView tv_date_of_expiry = already_opened_header.findViewById(R.id.tv_date_of_expiry);
                TextView tv_begin_time = already_opened_header.findViewById(R.id.tv_begin_time);
                TextView tv_remaining_time = already_opened_header.findViewById(R.id.tv_remaining_time);

                //到期日期
                tv_date_of_expiry.setText(String.format(getResources().getString(R.string.vip_data_of_expiry), DateUtil.formatVipInfoDate(vipPersonInfo.getExpired_at())));
                //开通时间
                tv_begin_time.setText(String.format(getResources().getString(R.string.vip_data_of_begin), DateUtil.formatVipInfoDate(vipPersonInfo.getStart_at())));
                //已经开通天数
                String has_passed_day = String.valueOf(vipPersonInfo.getVip_pass_days());
                //剩余天数
                String remain_days = String.valueOf(vipPersonInfo.getVip_remain_days());
                String remaining_time = String.format(getResources().getString(R.string.vip_remainint_time), has_passed_day, remain_days);

                int pixelSize = getResources().getDimensionPixelSize(R.dimen.dp24);
                int index = remaining_time.indexOf(String.valueOf(remain_days));
                ColorStateList colors = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.vip_current_price_unselecct_color));
                TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, 0, pixelSize, colors, null);
                SpannableStringBuilder spanBuilder = new SpannableStringBuilder(remaining_time);
                spanBuilder.setSpan(textAppearanceSpan, index, index + remain_days.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                tv_remaining_time.setText(spanBuilder);
                mVipAdapter.addHeaderView(already_opened_header);
            }
        } else {
            if (TextUtils.isEmpty(vipPersonInfo.getStart_at())) {
                //未开通
                View nonactivated_header = LayoutInflater.from(getActivity()).inflate(R.layout.vip_nonactivated_header, null, false);
                mVipAdapter.addHeaderView(nonactivated_header);
            } else {
                //已过期expired
                View expired_header = LayoutInflater.from(getActivity()).inflate(R.layout.vip_expired_header, null, false);
                if (expired_header != null) {
                    TextView tv_expired_time = expired_header.findViewById(R.id.tv_expired_time);
                    TextView tv_last_opening_time = expired_header.findViewById(R.id.tv_last_opening_time);
                    TextView tv_date_of_expiry = expired_header.findViewById(R.id.tv_date_of_expiry);

                    //过期时间
                    tv_date_of_expiry.setText(String.format(getResources().getString(R.string.vip_data_of_expiry), DateUtil.formatVipInfoDate(vipPersonInfo.getStart_at())));
                    //上次开通时间
                    tv_last_opening_time.setText(String.format(getResources().getString(R.string.vip_data_of_last_open), DateUtil.formatVipInfoDate(vipPersonInfo.getExpired_at())));

                    //过期时间
                    String expired_time = vipPersonInfo.getVip_expired_days();
                    String remaining_time = String.format(getResources().getString(R.string.vip_expired_time), expired_time);

                    int pixelSize = getResources().getDimensionPixelSize(R.dimen.dp24);
                    int index = remaining_time.indexOf(String.valueOf(expired_time));
                    ColorStateList colors = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.vip_current_price_selecct_color));
                    TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, 0, pixelSize, colors, null);
                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder(remaining_time);
                    spanBuilder.setSpan(textAppearanceSpan, index, index + expired_time.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_expired_time.setText(spanBuilder);
                    mVipAdapter.addHeaderView(expired_header);
                }
            }
        }

    }

    private void showNetwordisNotConnected() {
        errorNotification.showError(getResources().getString(R.string.reset_no_network_message),
                FontAwesomeIcons.fa_wifi, R.string.lbl_reload, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
    }

    private void showError(Throwable throwable) {
        errorNotification.showError(getActivity(), throwable, R.string.lbl_reload,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkUtil.isConnected(getContext())) {
                            initData();
                        }
                    }
                });
    }

    @Override
    protected boolean isShowingFullScreenError() {
        return errorNotification != null && errorNotification.isShowing();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity销毁的时候清除所有请求
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
        if (selectPayPopupWindow != null) {
            selectPayPopupWindow = null;
        }
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
     * 通过接口获取微信签名信息并调起支付
     */
    private void selectWeChatPay(int package_id) {
        if (!DeviceProgramDetectionUtil.booleanWechatInstall(getActivity())) {
            Toast.makeText(getActivity(), R.string.wechat_not_install, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtil.isConnected(getActivity())) {
            return;
        }
        selectPayPopupWindow.showProgress();
        final Disposable disposable = eliteApi.getVipWeChatPayReqBean(package_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeChatReqBean>() {
                    @Override
                    public void accept(WeChatReqBean weChatReqBean) throws Exception {
                        if (weChatReqBean != null) {

                            WeChatReqParam weChatReqParam = weChatReqBean.getWechat_request();
                            mOrderId = weChatReqBean.getOrder_id();
                            if (weChatReqParam != null) {
                                WeChatPayReq weChatPayReq = new WeChatPayReq.Builder().with(getActivity())
                                        .setWeChatReqParam(weChatReqParam)
                                        .create();
                                PayAPI.getInstance().sendPayRequestWithOutKey(weChatPayReq);
                            } else {
                                selectPayPopupWindow.hideProgress();
                            }
                        } else {
                            selectPayPopupWindow.hideProgress();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        selectPayPopupWindow.hideProgress();
                    }
                });
        addDisposable(disposable);
        selectPayPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                disposable.dispose();
            }
        });
    }

    /**
     * 通过接口获取支付宝签名信息并调起支付
     */
    private void selectAliPay(int package_id) {
        selectPayPopupWindow.showProgress();
        final Disposable disposable = eliteApi.getVipAliPayReqBean(package_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AliPayReqBean>() {
                    @Override
                    public void accept(final AliPayReqBean aliPayReqBean) throws Exception {
                        if (aliPayReqBean != null) {
                            mOrderId = aliPayReqBean.getOrder_id();
                            AliPayReqParam aliPayReqParam = new AliPayReqParam(aliPayReqBean.getAlipay_request());
                            if (aliPayReqParam != null) {
                                AliPayReq aliPayReq = new AliPayReq.Builder().with(getActivity())
                                        .setAliPayReqParam(aliPayReqParam)
                                        .create()
                                        .setOnAliPayListener(new AliPayReq.OnAliPayListener() {
                                            @Override
                                            public void onPaySuccess(String resultInfo) {
                                                selectPayPopupWindow.hideProgress();
                                                selectPayPopupWindow.dismiss();
                                                checkOrderStatus(mOrderId);
                                            }

                                            @Override
                                            public void onPayFailure(String resultInfo) {
                                                Toast.makeText(getActivity(), R.string.pay_fail, Toast.LENGTH_SHORT).show();
                                                selectPayPopupWindow.hideProgress();

                                            }

                                            @Override
                                            public void onPayConfirmimg(String resultInfo) {
                                                Toast.makeText(getActivity(), R.string.pay_confirming, Toast.LENGTH_SHORT).show();
                                                selectPayPopupWindow.hideProgress();

                                            }

                                            @Override
                                            public void onPayCancel(String resultInfo) {
                                                Toast.makeText(getActivity(), R.string.pay_cancle, Toast.LENGTH_SHORT).show();
                                                selectPayPopupWindow.hideProgress();

                                            }
                                        });
                                PayAPI.getInstance().sendPayRequestByOrderInfo(aliPayReq);
                            } else {
                                selectPayPopupWindow.hideProgress();
                            }
                        } else {
                            selectPayPopupWindow.hideProgress();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        selectPayPopupWindow.hideProgress();
                    }
                });
        addDisposable(disposable);
        selectPayPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                disposable.dispose();
            }
        });
    }

    private void showCheckOrderStatusLoadingDialog() {
        if (mCheckOrderStatusLoadingDialog == null) {
            mCheckOrderStatusLoadingDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            mCheckOrderStatusLoadingDialog.setMessage(getString(R.string.progress_dialog_message));
            mCheckOrderStatusLoadingDialog.setCancelable(false);
            mCheckOrderStatusLoadingDialog.setCanceledOnTouchOutside(false);
        }
        mCheckOrderStatusLoadingDialog.show();
    }

    private void hideCheckOrderStatusLoadingDialog() {
        if (mCheckOrderStatusLoadingDialog != null) {
            if (mCheckOrderStatusLoadingDialog.isShowing()) {
                mCheckOrderStatusLoadingDialog.dismiss();
            }
        }
    }

    private void showPaySuccessDialog() {
        BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) getActivity();
        if (baseFragmentActivity.isInForeground()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.vip_order_check_success)
                    .setCancelable(false)
                    .create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    initData();
                }
            });
            alertDialog.show();
        }
    }

    Disposable orderReqDisposable;
    int mRequestTimes = 0;
    int REPEAT_TIMES = 3;

    private void checkOrderStatus(String id) {
        mRequestTimes = 0;
        showCheckOrderStatusLoadingDialog();
        orderReqDisposable = eliteApi.getVipOrderStstus(id)
                .delay(3, TimeUnit.SECONDS, true)       // 设置delayError为true，表示出现错误的时候也需要延迟3s进行通知，达到无论是请求正常还是请求失败，都是3s后重新订阅，即重新请求。
                .subscribeOn(Schedulers.io())
                .repeat(REPEAT_TIMES)   // repeat保证请求成功后能够重新订阅。
                .retry(REPEAT_TIMES)    // retry保证请求失败后能重新订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseHttpResult<VipOrderStatusBean>>() {
                    @Override
                    public void accept(BaseHttpResult<VipOrderStatusBean> result) throws Exception {
                        if (result.getData().getStatus() == 2) {
                            orderReqDisposable.dispose();
                            hideCheckOrderStatusLoadingDialog();
                            showPaySuccessDialog();
                        } else {
                            mRequestTimes++;
                            if (mRequestTimes == REPEAT_TIMES) {
                                orderReqDisposable.dispose();
                                hideCheckOrderStatusLoadingDialog();
                                Toast.makeText(getActivity(), R.string.check_order_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideCheckOrderStatusLoadingDialog();
                    }
                });
        addDisposable(mCompositeDisposable);
    }

    /**
     * 微信支付回调
     */
    private void initWeChatPayCallBack() {
        Disposable disposable = RxBus.getDefault().toObservable(PayResultEvent.class)
                .subscribe(new Consumer<PayResultEvent>() {
                    @Override
                    public void accept(PayResultEvent payResultEvent) throws Exception {
                        if (payResultEvent.getType() == PayResultEvent.PAY_SUCCESS) {
                            selectPayPopupWindow.hideProgress();
                            selectPayPopupWindow.dismiss();
                            checkOrderStatus(mOrderId);
                        } else if (payResultEvent.getType() == PayResultEvent.PAY_FAIL) {
                            Toast.makeText(getActivity(), R.string.pay_fail, Toast.LENGTH_SHORT).show();
                            selectPayPopupWindow.hideProgress();
                        } else if (payResultEvent.getType() == PayResultEvent.PAY_CANCEL) {
                            Toast.makeText(getActivity(), R.string.pay_cancle, Toast.LENGTH_SHORT).show();
                            selectPayPopupWindow.hideProgress();
                        }
                    }
                });
        addDisposable(disposable);
    }
}
