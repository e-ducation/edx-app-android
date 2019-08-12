package org.edx.mobile.eliteu.bottomnavigation.course;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.model.api.StartType;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.DateUtil;
import org.edx.mobile.util.ResourceUtil;
import org.edx.mobile.util.images.CourseCardUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EliteCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<CourseDetail> mDataList;

    private Config mConfig;

    private static final int TYPE_EMPTY = 2;

    private static final int TYPE_ITEM = 1;

    public EliteCourseAdapter(Context mContext, Config config) {
        this.mContext = mContext;
        this.mConfig = config;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_eliteu_find_course, parent, false));
        } else if (viewType == TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_find_eliteu_course_is_empty, parent, false);
            return new EmptyViewViewHolder(emptyView);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).setData(mDataList.get(position), mContext, mConfig);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return mDataList.size() == 0 ? 1 : mDataList.size();
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView course_image;
        TextView course_name;
        TextView course_status;
        TextView course_professor;
        RelativeLayout rootview;

        public ItemViewHolder(View itemView) {
            super(itemView);
            course_image = itemView.findViewById(R.id.course_image);
            course_name = itemView.findViewById(R.id.course_name);
            course_status = itemView.findViewById(R.id.course_status);
            course_professor = itemView.findViewById(R.id.course_professor);
            rootview = itemView.findViewById(R.id.root_view);
        }

        public void setData(CourseDetail courseDetail, Context context, Config config) {
            if (courseDetail.media.course_image != null) {

                Glide.with(context).load(courseDetail.media.course_image.getUri(config.getApiHostURL())).error(R.drawable.placeholder_course_card_image)
                        .into(this.course_image);
            } else {
                Glide.with(context).load(R.drawable.placeholder_course_card_image)
                        .into(this.course_image);
            }

            this.course_name.setText(courseDetail.name);
            this.course_professor.setText(context.getString(R.string.professor) + " " + courseDetail.professor_name);
            this.course_status.setText(getformattedDate(courseDetail.start, courseDetail.start_type, courseDetail.start_display, context));
            RxView.clicks(this.rootview).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(unit -> mOnItemClickListener.onItemClick(courseDetail));
        }

        public String getformattedDate(String start, StartType start_type, String start_display, Context context) {
            String formattedDate;

            if (CourseCardUtils.isDatePassed(new Date(), start)) {
                formattedDate = context.getString(R.string.starting_class);
            } else {
                if (start_type == StartType.TIMESTAMP && !TextUtils.isEmpty(start)) {
                    final Date startDate = DateUtil.convertToDate(start);
                    formattedDate = ResourceUtil.getFormattedString(context.getResources(), R.string
                            .label_starting, "date", DateUtil.formatDateWithNoYear(startDate.getTime())).toString();
                } else if (start_type == StartType.STRING && !TextUtils.isEmpty(start_display)) {
                    formattedDate = ResourceUtil.getFormattedString(context.getResources(), R.string
                            .label_starting, "date", DateUtil.formatDateWithNoYear(DateUtil.convertToDate(start).getTime())).toString();
                } else {
                    formattedDate = ResourceUtil.getFormattedString(context.getResources(), R.string
                            .label_starting, "date", DateUtil.formatDateWithNoYear(DateUtil.convertToDate(start).getTime())).toString();
                }

            }
            return formattedDate;
        }
    }

    public static class EmptyViewViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewViewHolder(View convertView) {
            super(convertView);
        }

    }

    public void notifyDataSetChanged(List<CourseDetail> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.size() == 0) {
            return TYPE_EMPTY;
        } else {
            return TYPE_ITEM;
        }
    }

    public interface OnItemClickListener {

        void onItemClick(CourseDetail courseDetail);

    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private static OnItemClickListener mOnItemClickListener;

}