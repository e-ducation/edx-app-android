package org.edx.mobile.eliteu.account;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.core.IEdxEnvironment;
import org.edx.mobile.databinding.FragmentEliteuAccountBinding;
import org.edx.mobile.eliteu.bindmobile.BindMobileUtil;
import org.edx.mobile.eliteu.vip.ui.VipActivity;
import org.edx.mobile.eliteu.vip.util.VipStatusUtil;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.Router;

public class EliteUAccountFragment extends BaseFragment {
    private static final String TAG = EliteUAccountFragment.class.getCanonicalName();
    private FragmentEliteuAccountBinding binding;

    @Inject
    private Config config;

    @Inject
    private IEdxEnvironment environment;

    @Inject
    private LoginPrefs loginPrefs;

    @Inject
    private Router router;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_eliteu_account, container, false);

        binding.layoutVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                router.showVip(getContext(), VipActivity.VIP_SELECT_ID);
            }
        });
        VipStatusUtil.getInstance().initVipButton((BaseFragmentActivity) getActivity(), binding.vipTv);
        BindMobileUtil.getInstance().initBindMobileButton((BaseFragmentActivity) getActivity(), binding.bindingMobileTv);
        binding.layoutMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                environment.getRouter().showBindMobile(getActivity());
            }
        });

        binding.layoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                environment.getRouter().showResetPassword(getActivity());
            }
        });

        binding.layoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                environment.getRouter().showSettings(getActivity());
            }
        });

        binding.layoutFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                environment.getRouter().showFeedbackScreen(getActivity(), getString(R.string.email_subject));
            }
        });

        binding.tvVersionNo.setText(String.format("%s %s %s", getString(R.string.label_version),
                BuildConfig.VERSION_NAME, environment.getConfig().getEnvironmentDisplayName()));

        return binding.getRoot();
    }
}
