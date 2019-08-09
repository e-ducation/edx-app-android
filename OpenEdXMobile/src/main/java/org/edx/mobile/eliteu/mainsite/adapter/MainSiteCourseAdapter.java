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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.eliteu.api.EliteApi;
import org.edx.mobile.eliteu.mainsite.bean.MainSiteCourseBean;
import org.edx.mobile.eliteu.mainsite.ui.CannotClickWebViewActivity;
import org.edx.mobile.http.callback.Callback;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.ToastUtil;
import org.edx.mobile.view.Router;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class MainSiteCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MainSiteCourseBean> mDataList;
    private Builder mBuilder;

    public static final int TYPE_URL = 1;
    public static final int TYPE_COURSE = 2;

    public MainSiteCourseAdapter(Builder builder) {
        this.mBuilder = builder;
    }

    public void setData(List<MainSiteCourseBean> list) {
        this.mDataList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mBuilder.mContext).inflate(mBuilder.layoutId, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Glide.with(mBuilder.mContext).load(mBuilder.mConfig.getApiHostURL() + mDataList.get(position).getImage()).placeholder(R.drawable.main_site_course_default_img).error(R.drawable.main_site_course_default_img).into(itemViewHolder.courseImage);
            itemViewHolder.course_name.setText(mDataList.get(position).getTitle());
            RxView.clicks(itemViewHolder.whole_item_layout).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {
                            if (mBuilder.type == MainSiteCourseAdapter.TYPE_URL) {
                                mBuilder.router.showCustomWebviewActivity((FragmentActivity) mBuilder.mContext, mDataList.get(position).getLink(), mBuilder.mContext.getString(R.string.webview_title));
                            } else if (mBuilder.type == MainSiteCourseAdapter.TYPE_COURSE) {
                                if (!TextUtils.isEmpty(mDataList.get(position).getPublicity_page_url())) {
                                    mBuilder.mContext.startActivity(CannotClickWebViewActivity.newIntent(mBuilder.mContext, mDataList.get(position).getPublicity_page_url() + "?device=android", mBuilder.mContext.getString(R.string.popular_recommendation), mDataList.get(position).getLink()));
                                } else {
                                    if (!TextUtils.isEmpty(mDataList.get(position).getLink())) {
                                        itemViewHolder.whole_item_layout.setEnabled(false);
                                        mBuilder.eliteApi.getCourseDetail(mDataList.get(position).getLink(), mBuilder.username).enqueue(new Callback<CourseDetail>() {
                                            @Override
                                            protected void onResponse(@NonNull CourseDetail responseBody) {
                                                if (responseBody != null) {
                                                    mBuilder.router.showCourseDetail(mBuilder.mContext, responseBody);
                                                } else {
                                                    ToastUtil.makeText(mBuilder.mContext, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                                                }
                                                itemViewHolder.whole_item_layout.setEnabled(true);
                                            }

                                            @Override
                                            protected void onFailure(@NonNull Throwable error) {
                                                super.onFailure(error);
                                                error.printStackTrace();
                                                ToastUtil.makeText(mBuilder.mContext, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                                                itemViewHolder.whole_item_layout.setEnabled(true);
                                            }
                                        });
                                    }
                                }


                            }

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView courseImage;

        TextView course_name;

        RelativeLayout whole_item_layout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.iamge);
            course_name = itemView.findViewById(R.id.course_name);
            whole_item_layout = itemView.findViewById(R.id.whole_item_layout);
        }

    }


    public static class Builder {


        private int layoutId = R.layout.main_site_course_recyler_horizontal_item;

        private Context mContext;

        private Config mConfig;

        private Router router;

        private int type;

        private EliteApi eliteApi;

        private String username;

        public Builder layout(int resId) {
            this.layoutId = resId;
            return this;
        }

        public Builder config(Config config) {
            this.mConfig = config;
            return this;
        }

        public MainSiteCourseAdapter build() {
            return new MainSiteCourseAdapter(this);
        }

        public Builder conetxt(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder router(Router router) {
            this.router = router;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder eliteapi(EliteApi eliteApi) {
            this.eliteApi = eliteApi;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

    }

    public static Builder builder() {

        return new Builder();

    }

}
