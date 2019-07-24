package org.edx.mobile.eliteu.bottomnavigation.course;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import org.edx.mobile.R;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.util.BaseLazyLoadFragment;
import org.edx.mobile.eliteu.wight.SwipeRecyclerViewLoadMoreView;
import org.edx.mobile.http.callback.Callback;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
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

@SuppressLint("ValidFragment")
public class FindCourseListFragment extends BaseLazyLoadFragment {

    private int course_type_id;
    protected boolean isVisible;

    SwipeRecyclerView mRecyclerView;
    SwipeRefreshLayout mRefreshLayout;
    private EliteCourseAdapter mAdapter;
    private List<CourseDetail> mDataList;
    private FullScreenErrorNotification errorNotification;
    private IconProgressBar mIconProgressBar;
    private boolean isFirstRequest = false;
    private CompositeDisposable mCompositeDisposable;
    SwipeRecyclerViewLoadMoreView mLoadMoreView;


    @Inject
    private EliteApi eliteApi;
    @Inject
    private Router router;
    @Inject
    private Config config;
    @Inject
    private LoginPrefs loginPrefs;
    int page = 1;


    public static Fragment newInstance(int course_type_id) {
        FindCourseListFragment fragment = new FindCourseListFragment(course_type_id);
        return fragment;
    }

    @SuppressLint("ValidFragment")
    public FindCourseListFragment(int course_type_id) {
        this.course_type_id = course_type_id;

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_find_course_list;
    }

    @Override
    protected void initView(View view) {

        mCompositeDisposable = new CompositeDisposable();
        isFirstRequest = true;
        mDataList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getActivity(), R.color.mian_site_divider_color)));
        mRecyclerView.setOnItemClickListener(mItemClickListener); // RecyclerView Item点击监听。

        mLoadMoreView = new SwipeRecyclerViewLoadMoreView(getContext());
        mRecyclerView.addFooterView(mLoadMoreView);
        mRecyclerView.setLoadMoreView(mLoadMoreView);
        mRecyclerView.setLoadMoreListener(mLoadMoreListener);

        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        UiUtil.setSwipeRefreshLayoutColors(mRefreshLayout);

        mIconProgressBar = view.findViewById(R.id.loading_indicator);
        errorNotification = new FullScreenErrorNotification(mRefreshLayout);

        mAdapter = new EliteCourseAdapter(getActivity(), config);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void loadData() {
        loadData(false);
    }


    //下拉刷新
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = () -> {
        page = 1;
        loadData(false);
    };

    /**
     * Item点击监听。
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            if (mDataList.size() == 0) {
                return;
            }
            goToCourseDetail(mDataList.get(position).course_id);
        }
    };

    private void goToCourseDetail(String course_id) {

        eliteApi.getCourseDetail(course_id, loginPrefs.getUsername()).enqueue(new Callback<CourseDetail>() {
            @Override
            protected void onResponse(@NonNull CourseDetail responseBody) {
                if (getActivity().isDestroyed()) {
                    return;
                }
                if (responseBody != null) {
                    router.showCourseDetail(getActivity(), responseBody);
                } else {
                    Toast.makeText(getActivity(), R.string.error_unknown, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFailure(@NonNull Throwable error) {
                super.onFailure(error);
                if (getActivity().isDestroyed()) {
                    return;
                }
                error.printStackTrace();
                Toast.makeText(getActivity(), R.string.error_unknown, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 加载更多。
     */
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener = () -> {
        page = page + 1;
        loadData(true);
    };


    /**
     * 加载数据。
     */
    private void loadData(boolean isLoadMore) {
        if (mRecyclerView.getFooterCount() == 0) {
            mRecyclerView.addFooterView(mLoadMoreView);
        }

        if (!NetworkUtil.isConnected(getActivity()) && isFirstRequest) {
            showNetwordisNotConnected(isLoadMore);
            return;
        }

        if (isFirstRequest) {
            mIconProgressBar.setVisibility(View.VISIBLE);
        }
        Disposable disposable = eliteApi.getCourseSearchByTypeId(page, this.course_type_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                    if (page == 1) {
                        mDataList.clear();
                    }

                    mDataList.addAll(result.getResults());
                    mAdapter.notifyDataSetChanged(mDataList);
                    mRefreshLayout.setRefreshing(false);

                    if (isFirstRequest) {
                        mIconProgressBar.setVisibility(View.GONE);
                        isFirstRequest = false;
                    }
                    if (!result.hasNext()) {
                        mRecyclerView.loadMoreFinish(false, false);
                        mRecyclerView.removeFooterView(mLoadMoreView);
                    } else {
                        mRecyclerView.loadMoreFinish(false, true);
                    }
                    errorNotification.hideError();

                }, throwable -> {
                    throwable.printStackTrace();
                    if (isFirstRequest) {
                        mIconProgressBar.setVisibility(View.GONE);
                        showError(throwable, isLoadMore);
                        isFirstRequest = false;
                    }
                    if (isLoadMore) {
                        mRecyclerView.loadMoreError(0, throwable.getMessage());
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
    protected boolean isShowingFullScreenError() {
        return errorNotification != null && errorNotification.isShowing();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirstRequest = false;
        //在activity销毁的时候清除所有请求
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.clear();
        }
    }

    private void showNetwordisNotConnected(boolean isLoadMore) {
        errorNotification.showError(getResources().getString(R.string.reset_no_network_message),
                FontAwesomeIcons.fa_wifi, R.string.lbl_reload, v -> loadData(isLoadMore));
    }

    private void showError(Throwable throwable, boolean isLoadMore) {
        errorNotification.showError(getActivity(), throwable, R.string.lbl_reload,
                v -> {
                    if (NetworkUtil.isConnected(getContext())) {
                        loadData(isLoadMore);
                    }
                });
    }


}
