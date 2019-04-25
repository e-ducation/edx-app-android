package org.edx.mobile.eliteu.professor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.wight.SwipeRecyclerViewLoadMoreView;
import org.edx.mobile.http.notifications.FullScreenErrorNotification;
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

public class ProfessorListFragment extends OfflineSupportBaseFragment {

    SwipeRecyclerView mRecyclerView;
    SwipeRefreshLayout mRefreshLayout;
    private ProfessorAdapter mAdapter;
    private List<ProfessorBean> mDataList;
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
    int page = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_professor_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mCompositeDisposable = new CompositeDisposable();
        isFirstRequest = true;
        mDataList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getActivity(), R.color.mian_site_divider_color)));
        mRecyclerView.setOnItemClickListener(mItemClickListener); // RecyclerView Item点击监听。

//      mRecyclerView.useDefaultLoadMore();

        mLoadMoreView = new SwipeRecyclerViewLoadMoreView(getContext());
        mRecyclerView.addFooterView(mLoadMoreView);
        mRecyclerView.setLoadMoreView(mLoadMoreView);
        mRecyclerView.setLoadMoreListener(mLoadMoreListener);

        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        UiUtil.setSwipeRefreshLayoutColors(mRefreshLayout);

        mIconProgressBar = view.findViewById(R.id.loading_indicator);
        errorNotification = new FullScreenErrorNotification(mRefreshLayout);

        mAdapter = new ProfessorAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

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
            Intent intent = new Intent(getActivity(), ProfessorDetailActivity.class);
            intent.putExtra("professor_name", getString(R.string.webview_title));
            intent.putExtra("professor_id", mDataList.get(position).getId());
            startActivity(intent);
//            router.showCustomWebviewActivity(getActivity(),config.getApiHostURL()+"/professors/"+mDataList.get(position).getId()+"/",getString(R.string.webview_title));
        }
    };


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

        // 请求服务器加载数据。
        if (!NetworkUtil.isConnected(getActivity()) && isFirstRequest) {
            showNetwordisNotConnected(isLoadMore);
            return;
        }

        if (isFirstRequest) {
            mIconProgressBar.setVisibility(View.VISIBLE);
        }
        Disposable disposable = eliteApi.getProfessorList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(professorBeanTempHttpResult -> {
                    if (page == 1) {
                        mDataList.clear();
                    }

                    mDataList.addAll(professorBeanTempHttpResult.getResults());
                    mAdapter.notifyDataSetChanged(mDataList);
                    mRefreshLayout.setRefreshing(false);

                    if (isFirstRequest) {
                        mIconProgressBar.setVisibility(View.GONE);
                        isFirstRequest = false;
                    }
                    if (TextUtils.isEmpty(professorBeanTempHttpResult.getNext())) {
                        mRecyclerView.loadMoreFinish(false, false);
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
                    mRefreshLayout.setRefreshing(false);
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
