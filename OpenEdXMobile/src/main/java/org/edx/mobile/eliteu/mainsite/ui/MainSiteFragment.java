package org.edx.mobile.eliteu.mainsite.ui;

import android.databinding.DataBindingUtil;

import org.edx.mobile.databinding.FragmentMainSiteBinding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.mainsite.adapter.MainSiteAdapter;
import org.edx.mobile.eliteu.mainsite.bean.BaseMainSiteBlockBean;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.UiUtil;
import org.edx.mobile.view.OfflineSupportBaseFragment;
import org.edx.mobile.view.Router;
import org.edx.mobile.view.custom.IconProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainSiteFragment extends OfflineSupportBaseFragment {


    FragmentMainSiteBinding binding;
    public static List<String> images = new ArrayList<>();
    @Inject
    private EliteApi eliteApi;
    @Inject
    private Router router;
    @Inject
    private Config config;
    @Inject
    LoginPrefs loginPrefs;
    private IconProgressBar mIconProgressBar;

    private FullScreenErrorNotification errorNotification;
    private CompositeDisposable mCompositeDisposable;
    MainSiteAdapter mMainSiteAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_site, container, false);
        initView();
        loadData(false);
        return binding.getRoot();
    }

    private void initView() {
        mIconProgressBar = binding.getRoot().findViewById(R.id.loading_indicator);
        errorNotification = new FullScreenErrorNotification(binding.refreshLayout);
        mCompositeDisposable = new CompositeDisposable();
        UiUtil.setSwipeRefreshLayoutColors(binding.refreshLayout);
        binding.refreshLayout.setOnRefreshListener(mRefreshListener);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    //下拉刷新
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = () -> loadData(true);

    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    private void showNetwordisNotConnected(boolean reflush) {
        errorNotification.showError(getResources().getString(R.string.reset_no_network_message),
                FontAwesomeIcons.fa_wifi, R.string.lbl_reload, v -> loadData(reflush));
    }

    private void showError(Throwable throwable, boolean reflush) {
        errorNotification.showError(getActivity(), throwable, R.string.lbl_reload,
                v -> {
                    if (NetworkUtil.isConnected(getActivity())) {
                        loadData(reflush);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity销毁的时候清除所有请求
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    private void loadData(boolean reflush) {
        // 请求服务器加载数据。
        if (!NetworkUtil.isConnected(getActivity()) && reflush == false) {
            showNetwordisNotConnected(reflush);
            return;
        }
        if (!reflush) {
            mIconProgressBar.setVisibility(View.VISIBLE);
        }
        Disposable disposable = eliteApi.getMainSiteBlock()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mainSiteBlockBean -> {
                    errorNotification.hideError();
                    mIconProgressBar.setVisibility(View.GONE);
                    binding.refreshLayout.setRefreshing(false);
                    binding.refreshLayout.setVisibility(View.VISIBLE);
                    setUpMainSite(mainSiteBlockBean.getBody());
                }, throwable -> {
                    throwable.printStackTrace();
                    mIconProgressBar.setVisibility(View.GONE);
                    binding.refreshLayout.setRefreshing(false);
                    showError(throwable, reflush);
                });
        addDisposable(disposable);
    }

    private void setUpMainSite(List<BaseMainSiteBlockBean> body) {
        mMainSiteAdapter = new MainSiteAdapter(getActivity(), router, config, eliteApi, loginPrefs.getUsername());
        mMainSiteAdapter.setData(body);
        binding.recyclerView.setAdapter(mMainSiteAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMainSiteAdapter != null) {
            if (mMainSiteAdapter.getBanner() != null) {
                mMainSiteAdapter.getBanner().startAutoPlay();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMainSiteAdapter != null) {
            if (mMainSiteAdapter.getBanner() != null) {
                mMainSiteAdapter.getBanner().stopAutoPlay();
            }
        }
    }

    @Override
    protected boolean isShowingFullScreenError() {
        return false;
    }
}
