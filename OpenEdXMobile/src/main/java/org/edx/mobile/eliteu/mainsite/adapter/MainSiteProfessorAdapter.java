package org.edx.mobile.eliteu.mainsite.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding3.view.RxView;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.mainsite.bean.BlockProfessor;
import org.edx.mobile.eliteu.professor.ProfessorDetailActivity;
import org.edx.mobile.util.Config;
import org.edx.mobile.view.Router;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainSiteProfessorAdapter extends RecyclerView.Adapter<MainSiteProfessorAdapter.ViewHolder> {

    private Context mContext;

    private List<BlockProfessor.ProfessorBean> mDataList;

    Config config;

    Router router;

    public MainSiteProfessorAdapter(Context mContext, Config config, Router router) {
        this.mContext = mContext;
        this.config = config;
        this.router = router;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mait_site_item_professor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position), mContext, config, router);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_img;
        TextView tvName;
        TextView tvDesc;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.professor_image);
            tvName = itemView.findViewById(R.id.professor_name);
            tvDesc = itemView.findViewById(R.id.professor_desc);
            rootView = itemView.findViewById(R.id.root_view);

        }

        public void setData(BlockProfessor.ProfessorBean professorBean, Context context, Config config, Router router) {
            Glide.with(context).load(config.getApiHostURL() + professorBean.getProfessor_pic()).placeholder(R.drawable.main_site_professor_default_img).error(R.drawable.main_site_professor_default_img)
                    .into(this.iv_img);
            this.tvName.setText(professorBean.getName());
            this.tvDesc.setText(Html.fromHtml(professorBean.getContent()));
            RxView.clicks(this.rootView).
                    throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(unit -> {
                        if (!TextUtils.isEmpty(professorBean.getProfessor_link())) {
                            if (professorBean.getProfessor_link().contains("/professors/")) {
                                String professor_id = professorBean.getProfessor_link().substring(professorBean.getProfessor_link().lastIndexOf("/professors/") + "/professors/".length(), professorBean.getProfessor_link().lastIndexOf("/"));
                                if (!TextUtils.isEmpty(professor_id)) {
                                    Intent intent = new Intent(context, ProfessorDetailActivity.class);
                                    intent.putExtra("professor_name", context.getString(R.string.webview_title));
                                    intent.putExtra("professor_id", Integer.parseInt(professor_id));
                                    context.startActivity(intent);
                                }
                            }
                        }
                    });
        }
    }

    public void setData(List<BlockProfessor.ProfessorBean> dataList) {
        this.mDataList = dataList;
    }

}