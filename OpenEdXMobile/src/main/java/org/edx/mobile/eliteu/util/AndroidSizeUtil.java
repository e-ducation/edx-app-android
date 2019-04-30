package org.edx.mobile.eliteu.util;



import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class AndroidSizeUtil {
    @SuppressWarnings("static-access")
    public static float dp2Px(Context context, float value) {
        if (context == null) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return typedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }

    @SuppressWarnings("static-access")
    public static int dp2Px(Context context, int value) {
        if (context == null) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float numfloat= typedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
        int numint=(int)numfloat;
        return numint;
    }


    @SuppressWarnings("static-access")
    public static float sp2Px(Context context, float value) {
        TypedValue typedValue = new TypedValue();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return typedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics);
    }

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}