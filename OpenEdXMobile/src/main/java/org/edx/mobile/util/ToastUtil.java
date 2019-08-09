package org.edx.mobile.util;


import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.edx.mobile.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ToastUtil {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static Toast makeText(Context context, @StringRes int resId, @Duration int duration)
            throws Resources.NotFoundException {
        Toast toast = Toast.makeText(context, resId, duration);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.custom_toast_bg);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(context,R.color.white));
        return toast;
    }


    public static Toast makeText(Context context, CharSequence charSequence, @Duration int duration) {
        Toast toast = Toast.makeText(context, charSequence, duration);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.custom_toast_bg);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(context,R.color.white));
        return toast;
    }

}
