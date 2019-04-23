package org.edx.mobile.eliteu.article;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.edx.mobile.R;
import org.edx.mobile.util.Config;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context mContext;

    private List<ArticleBean> mDataList;

    private Config mConfig;

    public ArticleAdapter(Context mContext, Config config) {
        this.mContext = mContext;
        this.mConfig = config;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position), mContext, mConfig);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView article_image;
        TextView article_title;
        TextView article_time;
        TextView article_like_num;
        TextView article_tags;

        public ViewHolder(View itemView) {
            super(itemView);
            article_image = itemView.findViewById(R.id.article_image);
            article_title = itemView.findViewById(R.id.article_title);
            article_time = itemView.findViewById(R.id.article_time);
            article_like_num = itemView.findViewById(R.id.article_like_num);
            article_tags = itemView.findViewById(R.id.article_tags);

        }

        public void setData(ArticleBean articleBean, Context context, Config config) {
            if (articleBean.getArticle_cover_app() != null) {

                if (articleBean.getArticle_cover_app().getMeta() != null) {
                    if (articleBean.getArticle_cover_app().getMeta().getDownload_url() != null) {
                        Glide.with(context).load(config.getApiHostURL() + articleBean.getArticle_cover_app().getMeta().getDownload_url())
                                .placeholder(R.drawable.article_default_img)
                                .into(this.article_image);
                    }
                }
            } else {
                Glide.with(context).load(R.drawable.article_default_img)
                        .into(this.article_image);
            }


            this.article_title.setText(articleBean.getTitle());
            this.article_time.setText(articleBean.getArticle_datetime());
            this.article_like_num.setText(String.valueOf(articleBean.getLiked_count()));
            StringBuilder stringBuilder = new StringBuilder();
            for (String tag : articleBean.getTags()) {
                stringBuilder.append(tag + "  ");
            }
            this.article_tags.setText(stringBuilder.toString());
        }
    }

    public void notifyDataSetChanged(List<ArticleBean> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

}