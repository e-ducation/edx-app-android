package org.edx.mobile.eliteu.mainsite.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.mainsite.bean.BlockStory;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            return new MultipleViewHolder(mLayoutInflater.inflate(R.layout.main_site_multiple_item_story, parent, false));
        } else if (viewType == TYPE_SINGLE) {
            return new SingleViewHolder(mLayoutInflater.inflate(R.layout.main_site_single_story_layout, null, false), mContext);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            MultipleViewHolder multipleViewHolder = (MultipleViewHolder) holder;
            multipleViewHolder.setData(mDataList.get(position), mContext, config);
            RxView.clicks(multipleViewHolder.rootView).
                    throttleFirst(1, TimeUnit.SECONDS).subscribe(unit -> router.showCustomWebviewActivity((FragmentActivity) mContext, mDataList.get(position).getStory_link(), mContext.getString(R.string.webview_title)));
        } else if (position == 1) {
            SingleViewHolder singleViewHolder = (SingleViewHolder) holder;
            MainSiteSingleArticleAdapter mainSiteSingleArticleAdapter = new MainSiteSingleArticleAdapter(mContext, config, router);
            List<BlockStory.StoryBean> list = new ArrayList<>();
            list.clear();
            for (BlockStory.StoryBean storyBean : mDataList) {
                if (storyBean.getStory_photo().size() > 1) {
                    continue;
                }
                list.add(storyBean);
            }
            mainSiteSingleArticleAdapter.setData(list);
            singleViewHolder.recyclerView.setAdapter(mainSiteSingleArticleAdapter);
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

    static class SingleViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public SingleViewHolder(View itemView, Context context) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            recyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(context, R.color.professor_divider_color)));
            DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            Drawable drawable = context.getResources().getDrawable(R.drawable.main_site_recycler_view_divider);
            decoration.setDrawable(drawable);
            recyclerView.addItemDecoration(decoration);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    static class MultipleViewHolder extends RecyclerView.ViewHolder {

        ImageView story_photo1;
        ImageView story_photo2;
        ImageView story_photo3;
        TextView story_title;
        View rootView;

        public MultipleViewHolder(View itemView) {
            super(itemView);
            story_photo1 = itemView.findViewById(R.id.story_photo1);
            story_photo2 = itemView.findViewById(R.id.story_photo2);
            story_photo3 = itemView.findViewById(R.id.story_photo3);
            story_title = itemView.findViewById(R.id.story_title);
            rootView = itemView.findViewById(R.id.root_view);
        }

        public void setData(BlockStory.StoryBean storyBean, Context context, Config config) {
            List<String> list = storyBean.getStory_photo();
            if (list.size() == 3) {
                Glide.with(context).load(config.getApiHostURL() + list.get(0)).placeholder(R.drawable.main_site_multiple_story_default_1).error(R.drawable.main_site_multiple_story_default_1).into(story_photo1);
                Glide.with(context).load(config.getApiHostURL() + list.get(1)).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo2);
                Glide.with(context).load(config.getApiHostURL() + list.get(2)).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo3);
            } else {
                Glide.with(context).load(R.drawable.main_site_multiple_story_default_1).placeholder(R.drawable.main_site_multiple_story_default_1).error(R.drawable.main_site_multiple_story_default_1).into(story_photo1);
                Glide.with(context).load(R.drawable.main_site_multiple_story_default_2_3).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo2);
                Glide.with(context).load(R.drawable.main_site_multiple_story_default_2_3).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo3);
            }
            story_title.setText(storyBean.getStory_title());
        }
    }

    public void setData(List<BlockStory.StoryBean> dataList) {
        this.mDataList = dataList;
    }

}