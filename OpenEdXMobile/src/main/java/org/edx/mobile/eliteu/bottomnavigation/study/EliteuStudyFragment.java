package org.edx.mobile.eliteu.bottomnavigation.study;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.bottomnavigation.BottomNavigationMainDashboardActivity;
import org.edx.mobile.eliteu.util.CourseUtil;
import org.edx.mobile.eliteu.vip.ui.VipActivity;
import org.edx.mobile.eliteu.wight.SwipeRecyclerViewLoadMoreView;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.NetworkUtil;
import org.edx.mobile.util.UiUtil;
import org.edx.mobile.view.Router;
import org.edx.mobile.view.custom.IconProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class EliteuStudyFragment extends BaseFragment {


    private FullScreenErrorNotification errorNotification;
    private IconProgressBar mIconProgressBar;
    private CompositeDisposable mCompositeDisposable;
    EliteStudyAdapter eliteStudyAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mRefreshLayout;

    private List<EnrolledCoursesResponse> mDataList;

    private boolean isFirstRequest = false;
    SwipeRecyclerViewLoadMoreView mLoadMoreView;

    @Inject
    private EliteApi eliteApi;
    @Inject
    private Config config;
    @Inject
    private LoginPrefs loginPrefs;
    @Inject
    private Router router;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eliteu_study, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mCompositeDisposable = new CompositeDisposable();
        isFirstRequest = true;
        mDataList = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreView = new SwipeRecyclerViewLoadMoreView(getContext());

        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        UiUtil.setSwipeRefreshLayoutColors(mRefreshLayout);

        mIconProgressBar = view.findViewById(R.id.loading_indicator);
        errorNotification = new FullScreenErrorNotification(mRefreshLayout);


        eliteStudyAdapter = new EliteStudyAdapter(getActivity(), config);
        mRecyclerView.setAdapter(eliteStudyAdapter);
        eliteStudyAdapter.setOnItemClickListener(new EliteStudyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(EnrolledCoursesResponse enrolledCoursesResponse) {
                if (!CourseUtil.courseCanView(enrolledCoursesResponse)) {
                    router.showVip(getContext(), VipActivity.VIP_SELECT_ID);
                    return;
                }

                if (enrolledCoursesResponse != null) {
                    router.showCourseDashboardTabs(getActivity(), enrolledCoursesResponse, false);
                }

            }

            @Override
            public void onEmptyClick() {
                BottomNavigationMainDashboardActivity activity = (BottomNavigationMainDashboardActivity) getActivity();
                activity.mBottomNavigationBar.selectTab(1);
            }
        });

        loadData();

    }


    //下拉刷新
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = () -> {
        loadData();
    };


    private void loadData() {
        if (!NetworkUtil.isConnected(getActivity()) && isFirstRequest) {
            showNetwordisNotConnected();
            return;
        }

        if (isFirstRequest) {
            mIconProgressBar.setVisibility(View.VISIBLE);
        }
        Disposable disposable = eliteApi.getEnrolledCourses(loginPrefs.getUsername(), config.getOrganizationCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    mDataList.clear();
                    mDataList.addAll(result);

                    eliteStudyAdapter.notifyDataSetChanged(result);
                    mRefreshLayout.setRefreshing(false);

                    if (isFirstRequest) {
                        mIconProgressBar.setVisibility(View.GONE);
                        isFirstRequest = false;
                    }

                    errorNotification.hideError();

                }, throwable -> {
                    throwable.printStackTrace();

                    if (isFirstRequest) {
                        mIconProgressBar.setVisibility(View.GONE);
                        showError(throwable);
                        isFirstRequest = false;
                    }


                });
        addDisposable(disposable);
    }


    public void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.add(disposable);
        }
    }

    private void showNetwordisNotConnected() {
        errorNotification.showError(getResources().getString(R.string.reset_no_network_message),
                FontAwesomeIcons.fa_wifi, R.string.lbl_reload, v -> loadData());
    }

    private void showError(Throwable throwable) {
        errorNotification.showError(getActivity(), throwable, R.string.lbl_reload,
                v -> {
                    if (NetworkUtil.isConnected(getActivity())) {
                        loadData();
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }


}
