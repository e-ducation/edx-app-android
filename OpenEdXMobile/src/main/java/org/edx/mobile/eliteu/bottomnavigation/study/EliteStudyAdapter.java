package org.edx.mobile.eliteu.bottomnavigation.study;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.harvard.HarvardStatusUtil;
import org.edx.mobile.eliteu.util.AccountPrefs;
import org.edx.mobile.eliteu.util.CourseUtil;
import org.edx.mobile.eliteu.wight.RoundedProgressBar;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.util.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class EliteStudyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<EnrolledCoursesResponse> mDataList;

    private Config mConfig;

    private static final int TYPE_EMPTY = 2;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 0;

    AccountPrefs mAccountPrefs;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            if (mDataList.size() == 0) {
                return TYPE_EMPTY;
            } else {
                return TYPE_ITEM;

            }
        }
    }

    public EliteStudyAdapter(Context context, Config config) {
        this.mContext = context;
        this.mConfig = config;
        mAccountPrefs = new AccountPrefs(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_eliteu_study, parent, false));
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.eliteu_study_recyclerview_header_view, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_study_course_is_empty, parent, false);
            return new EmptyViewViewHolder(emptyView);
        }
        return null;


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).setData(mDataList.get(position - 1), mContext, mConfig);
        } else if (holder instanceof HeaderViewHolder) {
            HarvardStatusUtil.initButton(((HeaderViewHolder) holder).harvard_manager_layout, ((HeaderViewHolder) holder).harvard_time, mConfig, mContext);
        } else if (holder instanceof EmptyViewViewHolder) {
            RxView.clicks((((EmptyViewViewHolder) holder).add_course_btn)).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(unit -> mOnItemClickListener.onEmptyClick());
        }
    }

    @Override
    public int getItemCount() {
        int begin = 1;
        int empty = 1;
        if (mDataList.size() == 0) {
            return begin + empty;
        }
        return mDataList.size() + begin;
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView harvard_time;
        FrameLayout harvard_manager_layout;
        TextView listTitle;

        public HeaderViewHolder(View convertView) {
            super(convertView);
            harvard_time = convertView.findViewById(R.id.harvard_time);
            harvard_manager_layout = convertView.findViewById(R.id.harvard_manager_layout);
            listTitle = convertView.findViewById(R.id.list_title);
            listTitle.getPaint().setFakeBoldText(true);
        }

    }


    public static class EmptyViewViewHolder extends RecyclerView.ViewHolder {

        TextView add_course_btn;

        public EmptyViewViewHolder(View convertView) {
            super(convertView);
            add_course_btn = convertView.findViewById(R.id.add_course_btn);
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView course_image;
        TextView course_name;
        RelativeLayout vip_expired_layout;
        FrameLayout rootView;
        RoundedProgressBar progressBar;
        TextView progress_tv;


        public ItemViewHolder(View itemView) {
            super(itemView);
            course_image = itemView.findViewById(R.id.course_image);
            course_name = itemView.findViewById(R.id.course_name);
            vip_expired_layout = itemView.findViewById(R.id.vip_expired_layout);
            rootView = itemView.findViewById(R.id.root_view);
            progressBar = itemView.findViewById(R.id.progressbar);
            progress_tv = itemView.findViewById(R.id.progress_tv);
        }

        public void setData(EnrolledCoursesResponse enrolledCoursesResponse, Context context, Config config) {
            if (enrolledCoursesResponse.getCourse().getCourse_image(config.getApiHostURL()) != null) {

                Glide.with(context).load(enrolledCoursesResponse.getCourse().getCourse_image(config.getApiHostURL()))
                        .placeholder(R.drawable.placeholder_course_card_image)
                        .into(this.course_image);
            } else {
                Glide.with(context).load(R.drawable.placeholder_course_card_image)
                        .placeholder(R.drawable.placeholder_course_card_image)
                        .into(this.course_image);
            }

            this.course_name.setText(enrolledCoursesResponse.getCourse().getName());
            this.vip_expired_layout.setVisibility(CourseUtil.courseCanView(enrolledCoursesResponse) ? View.GONE : View.VISIBLE);
            RxView.clicks(rootView).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(unit -> mOnItemClickListener.onItemClick(enrolledCoursesResponse));

            if (enrolledCoursesResponse.getCourse().getProgress() == null) {
                return;
            }
            double progress = enrolledCoursesResponse.getCourse().getProgress().getTotal_grade();
            int progress_int = (int) (progress * 100.00);

            progress_tv.setText(progress_int + "%");
            progressBar.setProgress(progress_int);
            if (enrolledCoursesResponse.getCourse().getProgress().isIs_pass()) {
                progressBar.setColorProgress("#8cc34a");
            } else {
                progressBar.setColorProgress("#4788c7");
            }

        }

    }

    public void notifyDataSetChanged(List<EnrolledCoursesResponse> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(EnrolledCoursesResponse enrolledCoursesResponse);

        void onEmptyClick();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;

}