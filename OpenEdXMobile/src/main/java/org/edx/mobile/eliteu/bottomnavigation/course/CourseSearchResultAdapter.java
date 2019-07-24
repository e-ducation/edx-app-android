package org.edx.mobile.eliteu.bottomnavigation.course;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.course.CourseDetail;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class CourseSearchResultAdapter extends RecyclerView.Adapter<CourseSearchResultAdapter.ViewHolder> {

    private Context mContext;

    private List<CourseDetail> mDataList;

    public CourseSearchResultAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_course_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position));

        RxView.clicks(holder.tv_result)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(unit -> {
                    onItemClickListener.onItemClick(mDataList.get(position).course_id);
                });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_result;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_result = itemView.findViewById(R.id.result);
        }

        public void setData(CourseDetail courseDetail) {
            tv_result.setText(courseDetail.name);
        }

    }

    public void notifyDataSetChanged(List<CourseDetail> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String result);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

}