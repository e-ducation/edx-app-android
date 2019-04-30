package org.edx.mobile.eliteu.mainsite.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.mainsite.bean.BlockStory;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.Router;

import java.util.ArrayList;
import java.util.List;

public class MainSiteStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<BlockStory.StoryBean> mDataList;

    Config config;

    Router router;

    LayoutInflater mLayoutInflater;

    public static final int TYPE_MULTIPLE = 1;
    public static final int TYPE_SINGLE = 2;


    public MainSiteStoryAdapter(Context mContext, Config config, Router router) {
        this.mContext = mContext;
        this.config = config;
        this.router = router;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_MULTIPLE) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.temple_recyclerview, parent, false), mContext, TYPE_MULTIPLE);
        } else if (viewType == TYPE_SINGLE) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.temple_recyclerview, parent, false), mContext, TYPE_SINGLE);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {

            ViewHolder viewHolder = (ViewHolder) holder;
            MainSiteMultipleArticleAdapter mainSiteSingleArticleAdapter = new MainSiteMultipleArticleAdapter(mContext, config, router);
            List<BlockStory.StoryBean> list = new ArrayList<>();
            list.clear();
            for (BlockStory.StoryBean storyBean : mDataList) {
                if (storyBean.getPhoto_location().equals("left")) {
                    list.add(storyBean);
                }
            }
            mainSiteSingleArticleAdapter.setData(list);
            viewHolder.recyclerView.setAdapter(mainSiteSingleArticleAdapter);
        } else if (position == 1) {
            ViewHolder viewHolder = (ViewHolder) holder;
            MainSiteSingleArticleAdapter mainSiteSingleArticleAdapter = new MainSiteSingleArticleAdapter(mContext, config, router);
            List<BlockStory.StoryBean> list = new ArrayList<>();
            list.clear();
            for (BlockStory.StoryBean storyBean : mDataList) {
                if (storyBean.getPhoto_location().equals("center")) {
                    list.add(storyBean);
                }
            }
            mainSiteSingleArticleAdapter.setData(list);
            viewHolder.recyclerView.setAdapter(mainSiteSingleArticleAdapter);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_MULTIPLE;
        } else if (position == 1) {
            return TYPE_SINGLE;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public ViewHolder(View itemView, Context context, int type) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(context, R.color.professor_divider_color)));
            if (type == TYPE_MULTIPLE) {
                recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(context, R.color.mian_site_divider_color)));
            } else {
                DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                Drawable drawable = context.getResources().getDrawable(R.drawable.main_site_recycler_view_divider);
                decoration.setDrawable(drawable);
                recyclerView.addItemDecoration(decoration);
            }

            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    public void setData(List<BlockStory.StoryBean> dataList) {
        this.mDataList = dataList;
    }

}