package org.edx.mobile.eliteu.professor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import org.edx.mobile.R;

import java.util.List;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ViewHolder> {

    private Context mContext;

    private List<ProfessorBean> mDataList;

    public ProfessorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_professor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_img;
        TextView tvName;
        TextView tvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.professor_image);
            tvName = itemView.findViewById(R.id.professor_name);
            tvDesc = itemView.findViewById(R.id.professor_desc);

        }

        public void setData(ProfessorBean professorBean, Context context) {
            Glide.with(context).load(professorBean.getAvatar())
                    .into(this.iv_img);
            this.tvName.setText(professorBean.getName());
            this.tvDesc.setText(Html.fromHtml(professorBean.getDescription()));
        }
    }

    public void notifyDataSetChanged(List<ProfessorBean> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

}