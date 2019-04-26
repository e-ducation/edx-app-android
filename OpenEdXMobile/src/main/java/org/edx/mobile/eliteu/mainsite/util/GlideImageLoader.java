package org.edx.mobile.eliteu.mainsite.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

import org.edx.mobile.R;
import org.edx.mobile.eliteu.wight.GlidePlaceholderDrawable;

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).placeholder(new GlidePlaceholderDrawable(context.getResources(), R.drawable.banner_default_img)).error(R.drawable.banner_default_img).into(imageView);
    }
}
