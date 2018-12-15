package org.edx.mobile.eliteu.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import java.text.DecimalFormat;

public class PriceUtil {

    /**
     * @param other ￥
     * @param price 价格
     * @return 将￥12.00 小数点后面的两位小数字体变小
     */
    public static SpannableStringBuilder OtherPriceFormat(Object other, Object price) {
        String price_str = roundByScale(price, 2);
        String other_str = other.toString();
        String allstr = other_str + price_str;
        SpannableStringBuilder style = new SpannableStringBuilder(allstr);
        style.setSpan(new RelativeSizeSpan(0.8f), allstr.length() - 2, allstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //0.5f表示默认字体大小的一半
        return style;
    }

    public static String roundByScale(Object vObj, int scale) {
        if (vObj == null) {
            return "";
        }
        double v = Double.parseDouble(vObj.toString());
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }

}
