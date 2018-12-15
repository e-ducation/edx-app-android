package org.edx.mobile.eliteu.vip.adapter;

import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.util.PriceUtil;
import org.edx.mobile.eliteu.vip.bean.VipBean;

import java.util.List;

public class VipAdapter extends BaseQuickAdapter<VipBean, BaseViewHolder> {

    public VipAdapter(@Nullable List<VipBean> data) {
        super(R.layout.vip_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipBean item) {

        RelativeLayout rootView = helper.getView(R.id.root_view);

        helper.setText(R.id.tv_member_label, item.getName());

        TextView tv_original_price = helper.getView(R.id.tv_original_price);
        tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 删除线
        tv_original_price.getPaint().setAntiAlias(true);// 抗锯齿
        tv_original_price.setText(item.getSuggested_price());

        TextView tv_current_price = helper.getView(R.id.tv_current_price);
        tv_current_price.setText(PriceUtil.OtherPriceFormat("￥", item.getPrice()));

        helper.setVisible(R.id.is_recommend, item.isIs_recommended());

        if (item.isSelect()) {
            rootView.setBackgroundResource(R.drawable.vip_item_select_border);
            tv_current_price.setTextColor(ContextCompat.getColor(mContext, R.color.vip_current_price_selecct_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rootView.setElevation(5);
            }
        } else {
            rootView.setBackgroundResource(R.drawable.vip_item_unselect_border);
            tv_current_price.setTextColor(ContextCompat.getColor(mContext, R.color.vip_current_price_unselecct_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rootView.setElevation(0);
            }
        }
    }

    public void changeBg(int position) {
        for (VipBean bean : mData) {
            bean.setSelect(false);
        }
        mData.get(position).setSelect(true);
        notifyDataSetChanged();
    }

}
