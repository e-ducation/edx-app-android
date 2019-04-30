package org.edx.mobile.eliteu.mainsite.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainSiteSingleArticleAdapter extends RecyclerView.Adapter<MainSiteSingleArticleAdapter.ViewHolder> {

    private Context mContext;

    private List<BlockStory.StoryBean> mDataList;

    Config config;

    Router router;

    public MainSiteSingleArticleAdapter(Context mContext, Config config,Router router) {
        this.mContext = mContext;
        this.config = config;
        this.router = router;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.main_site_single_item_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position), mContext,config,router);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView story_photo;
        TextView story_title;
        TextView story_content;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_title = itemView.findViewById(R.id.story_title);
            story_content = itemView.findViewById(R.id.story_content);
            rootView = itemView.findViewById(R.id.root_view);

        }

        public void setData(BlockStory.StoryBean storyBean, Context context, Config config, Router router) {

            if (storyBean.getStory_photo() != null && storyBean.getStory_photo().size() != 0) {
                Glide.with(context).load(config.getApiHostURL()+storyBean.getStory_photo().get(0))
                        .placeholder(R.drawable.main_site_story_single_default_img)
                        .error(R.drawable.main_site_story_single_default_img)
                        .into(this.story_photo);
            } else {
                Glide.with(context).load(R.drawable.main_site_story_single_default_img)
                        .into(this.story_photo);
            }


            this.story_title.setText(storyBean.getStory_title());
            this.story_content.setText(storyBean.getStory_content());
            RxView.clicks(rootView).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(unit -> router.showCustomWebviewActivity((FragmentActivity) context, storyBean.getStory_link(), context.getString(R.string.webview_title)));
        }
    }

    public void setData(List<BlockStory.StoryBean> dataList) {
        this.mDataList = dataList;
    }

}