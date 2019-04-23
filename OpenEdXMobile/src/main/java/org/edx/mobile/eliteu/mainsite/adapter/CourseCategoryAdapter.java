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
import org.edx.mobile.eliteu.mainsite.bean.BlockCourseCategory.CategorieslistBean;
import org.edx.mobile.view.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class CourseCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CategorieslistBean> mDataList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Integer> mIconList;
    private Router router;

    public CourseCategoryAdapter(List<CategorieslistBean> list, Context context, Router router) {
        this.mDataList = list;
        this.mContext = context;
        this.router = router;
        this.mLayoutInflater = LayoutInflater.from(context);
        mIconList = new ArrayList<>();
        initIconList();
    }

    private void initIconList() {
        if (mIconList != null) {
            mIconList.clear();
            mIconList.add(R.drawable.course_category_icon_1);
            mIconList.add(R.drawable.course_category_icon_2);
            mIconList.add(R.drawable.course_category_icon_3);
            mIconList.add(R.drawable.course_category_icon_4);
            mIconList.add(R.drawable.course_category_icon_5);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.main_site_course_category_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Glide.with(mContext).load(mIconList.get(position)).placeholder(R.drawable.course_category_default_img).error(R.drawable.course_category_default_img).into(itemViewHolder.imageView);
            itemViewHolder.textView.setText(mDataList.get(position).getCategories_name());
            RxView.clicks(itemViewHolder.rootView).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Unit>() {
                @Override
                public void accept(Unit unit) throws Exception {
                    router.showCustomWebviewActivity((FragmentActivity) mContext, mDataList.get(position).getCategories_link(), mContext.getString(R.string.webview_title));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        View rootView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
            imageView = itemView.findViewById(R.id.image);
            rootView = itemView.findViewById(R.id.root_view);
        }
    }

}
