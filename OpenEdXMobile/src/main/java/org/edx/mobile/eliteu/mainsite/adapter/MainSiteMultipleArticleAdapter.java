package org.edx.mobile.eliteu.mainsite.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainSiteMultipleArticleAdapter extends RecyclerView.Adapter<MainSiteMultipleArticleAdapter.ViewHolder> {

    private Context mContext;

    private List<BlockStory.StoryBean> mDataList;

    Config config;

    Router router;

    public MainSiteMultipleArticleAdapter(Context mContext, Config config, Router router) {
        this.mContext = mContext;
        this.config = config;
        this.router = router;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.main_site_multiple_item_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position), mContext, config,router);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView story_photo1;
        ImageView story_photo2;
        ImageView story_photo3;
        TextView story_title;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            story_photo1 = itemView.findViewById(R.id.story_photo1);
            story_photo2 = itemView.findViewById(R.id.story_photo2);
            story_photo3 = itemView.findViewById(R.id.story_photo3);
            story_title = itemView.findViewById(R.id.story_title);
            rootView = itemView.findViewById(R.id.root_view);

        }

        public void setData(BlockStory.StoryBean storyBean, Context context, Config config,Router router) {
            List<String> list = storyBean.getStory_photo();
            int size = list.size();
            for (int i = 0; i < (3 - size); i++) {
                list.add("");
            }
            if (TextUtils.isEmpty(list.get(0))) {
                Glide.with(context).load(R.drawable.main_site_multiple_story_default_1).placeholder(R.drawable.main_site_multiple_story_default_1).error(R.drawable.main_site_multiple_story_default_1).into(story_photo1);
            } else {
                Glide.with(context).load(config.getApiHostURL() + list.get(0)).placeholder(R.drawable.main_site_multiple_story_default_1).error(R.drawable.main_site_multiple_story_default_1).into(story_photo1);
            }

            if (TextUtils.isEmpty(list.get(1))) {
                Glide.with(context).load(R.drawable.main_site_multiple_story_default_2_3).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo2);
            } else {
                Glide.with(context).load(config.getApiHostURL() + list.get(1)).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo2);
            }

            if (TextUtils.isEmpty(list.get(2))) {
                Glide.with(context).load(R.drawable.main_site_multiple_story_default_2_3).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo3);
            } else {
                Glide.with(context).load(config.getApiHostURL() + list.get(2)).placeholder(R.drawable.main_site_multiple_story_default_2_3).error(R.drawable.main_site_multiple_story_default_2_3).into(story_photo3);
            }

            story_title.setText(storyBean.getStory_title());
            RxView.clicks(rootView).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(unit -> router.showCustomWebviewActivity((FragmentActivity) context, storyBean.getStory_link(), context.getString(R.string.webview_title)));
        }
    }

    public void setData(List<BlockStory.StoryBean> dataList) {
        this.mDataList = dataList;
    }

}