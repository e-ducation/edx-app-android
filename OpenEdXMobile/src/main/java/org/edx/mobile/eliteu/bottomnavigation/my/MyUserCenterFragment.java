package org.edx.mobile.eliteu.bottomnavigation.my;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.inject.Inject;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.vip.ui.VipActivity;
import org.edx.mobile.eliteu.vip.util.VipStatusUtil;
import org.edx.mobile.event.AccountDataLoadedEvent;
import org.edx.mobile.event.ProfilePhotoUpdatedEvent;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.user.Account;
import org.edx.mobile.user.ProfileImage;
import org.edx.mobile.user.UserAPI;
import org.edx.mobile.util.AppStoreUtils;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.view.Router;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class MyUserCenterFragment extends BaseFragment {

    CircleImageView profileImageView;
    RelativeLayout editProfileLayout;
    TextView usernameTv;
    RelativeLayout layoutVip;
    TextView vipTv;//VIP
    RelativeLayout layoutAccountManager;
    RelativeLayout layoutScanCodeLogin;
    RelativeLayout layoutFeedback;
    RelativeLayout layoutGiveUsPraise;
    RelativeLayout layoutAboutus;
    RelativeLayout layoutSetting;

    private boolean loadFinish = false;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    private EliteApi eliteApi;
    @Inject
    private Config config;
    @Inject
    private LoginPrefs loginPrefs;
    @Inject
    private Router router;
    @Inject
    private AccountPrefs accountPrefs;
    @Inject
    UserAPI userAPI;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_user_center, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        return view;
    }

    public void initView(View view) {
        profileImageView = view.findViewById(R.id.profile_image);
        editProfileLayout = view.findViewById(R.id.edit_profile_layout);
        usernameTv = view.findViewById(R.id.username);

        layoutVip = view.findViewById(R.id.layout_vip);
        vipTv = view.findViewById(R.id.vip_tv);
        layoutAccountManager = view.findViewById(R.id.layout_account_manager);
        layoutScanCodeLogin = view.findViewById(R.id.layout_scan_code_login);
        layoutFeedback = view.findViewById(R.id.layout_feedback);
        layoutGiveUsPraise = view.findViewById(R.id.layout_giveuspraise);
        layoutAboutus = view.findViewById(R.id.layout_aboutus);
        layoutSetting = view.findViewById(R.id.layout_setting);

        mCompositeDisposable = new CompositeDisposable();
        if (!NetworkUtil.isConnected(getActivity())) {
            loadData();
        }
    }

    public void loadData() {
        if (loadFinish == true) {
            return;
        }
        Account account = accountPrefs.getAccount();
        if (account == null) {
            return;
        }

        loadProfileImage(account.getProfileImage(), profileImageView);

        //用户名
        usernameTv.setText(account.getUsername());

        //编辑个人信息布局点击
        RxView.clicks(editProfileLayout).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showEditProfileInfo(getActivity(), account.getUsername()));

        //Vip布局点击
        RxView.clicks(layoutVip).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showVip(getActivity(), VipActivity.VIP_SELECT_ID));

        VipStatusUtil.getInstance().initVipButton((BaseFragmentActivity) getActivity(), vipTv);

        //设置布局点击
        RxView.clicks(layoutSetting).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showSettings(getActivity()));

        RxView.clicks(layoutAccountManager).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showAccountManager(getActivity()));

        RxView.clicks(layoutGiveUsPraise).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> AppStoreUtils.openAppInAppStore(getActivity()));

        RxView.clicks(layoutAboutus).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showAboutUs(getActivity()));

        RxView.clicks(layoutFeedback).throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showFeedback(getActivity(),account.getUsername()));

        loadFinish = true;

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
        loadFinish = false;
        EventBus.getDefault().unregister(this);

    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull AccountDataLoadedEvent event) {
        if (!config.isUserProfilesEnabled()) {
            return;
        }
        final Account account = event.getAccount();
        accountPrefs.storeAccount(account);
        loadData();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull ProfilePhotoUpdatedEvent event) {
        if (!config.isUserProfilesEnabled()) {
            return;
        }

        if (null == event.getUri()) {
            Glide.with(getActivity())
                    .load(R.drawable.profile_photo_placeholder)
                    .into(profileImageView);
        } else {
            Glide.with(getActivity())
                    .load(event.getUri())
                    .placeholder(R.drawable.profile_photo_placeholder)
                    .dontAnimate().error(R.drawable.profile_photo_placeholder)
                    .into(profileImageView);
        }
    }

    private void loadProfileImage(@NonNull ProfileImage profileImage, @NonNull ImageView imageView) {
        if (profileImage.hasImage()) {
            Glide.with(this)
                    .load(profileImage.getImageUrlMedium())
                    .placeholder(R.drawable.profile_photo_placeholder)
                    .dontAnimate().error(R.drawable.profile_photo_placeholder)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.profile_photo_placeholder)
                    .into(imageView);
        }
    }
}
