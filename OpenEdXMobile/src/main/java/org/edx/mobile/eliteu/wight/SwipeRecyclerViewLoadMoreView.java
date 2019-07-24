package org.edx.mobile.eliteu.wight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.edx.mobile.R;

public class SwipeRecyclerViewLoadMoreView extends LinearLayout implements SwipeRecyclerView.LoadMoreView, View.OnClickListener {

    private ProgressBar mProgressBar;
    private TextView mTvMessage;
    private RelativeLayout mTvMessage2;

    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener;

    public SwipeRecyclerViewLoadMoreView(Context context) {
        this(context, null);
    }

    public SwipeRecyclerViewLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setGravity(Gravity.CENTER);
        setVisibility(GONE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int minHeight = (int)(displayMetrics.density * 60 + 0.5);
        setMinimumHeight(minHeight);

        inflate(getContext(), R.layout.support_recycler_view_load_more, this);
        mProgressBar = findViewById(R.id.progress_bar);
        mTvMessage = findViewById(R.id.tv_load_more_message);
        mTvMessage2 = findViewById(R.id.tv_load_more_layout);
        setOnClickListener(this);
    }

    @Override
    public void onLoading() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage2.setVisibility(GONE);
        mTvMessage.setText(R.string.support_recycler_load_more_message);
    }

    @Override
    public void onLoadFinish(boolean dataEmpty, boolean hasMore) {
        if (!hasMore) {
            setVisibility(VISIBLE);

            if (dataEmpty) {
                mProgressBar.setVisibility(GONE);
                mTvMessage.setVisibility(VISIBLE);
                mTvMessage2.setVisibility(GONE);

                mTvMessage.setText(R.string.support_recycler_data_empty);
            } else {
                mProgressBar.setVisibility(GONE);
                mTvMessage.setVisibility(GONE);
                mTvMessage2.setVisibility(GONE);
            }
        } else {
            setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onWaitToLoadMore(SwipeRecyclerView.LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;

        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage2.setVisibility(GONE);
        mTvMessage.setText(R.string.support_recycler_click_load_more);
    }

    @Override
    public void onLoadError(int errorCode, String errorMessage) {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage2.setVisibility(GONE);
        mTvMessage.setText(TextUtils.isEmpty(errorMessage)
                ? getContext().getString(R.string.support_recycler_load_error)
                : errorMessage);
    }

    @Override
    public void onClick(View v) {
        if (mLoadMoreListener != null) mLoadMoreListener.onLoadMore();
    }

}
