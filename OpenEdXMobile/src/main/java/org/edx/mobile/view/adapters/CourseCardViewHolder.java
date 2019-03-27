package org.edx.mobile.view.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.edx.mobile.R;
import org.edx.mobile.model.api.CourseEntry;
import org.edx.mobile.util.images.ImageUtils;
import org.edx.mobile.util.images.TopAnchorFillWidthTransformation;

public class CourseCardViewHolder extends BaseListAdapter.BaseViewHolder {

    @LayoutRes
    public static int LAYOUT = R.layout.row_course_list;

    private final ImageView courseImage;
    private final TextView courseTitle;
    private final TextView courseDetails;
    private final View newCourseContent;
    private final View vipExpiredLayout;
    private final View vipExpiredButton;

    public CourseCardViewHolder(View convertView) {
        this.courseTitle = (TextView) convertView
                .findViewById(R.id.course_name);
        this.courseDetails = (TextView) convertView
                .findViewById(R.id.course_details);
        this.courseImage = (ImageView) convertView
                .findViewById(R.id.course_image);
        this.newCourseContent = convertView
                .findViewById(R.id.new_course_content_layout);
        this.vipExpiredLayout = convertView.findViewById(R.id.vip_expired_layout);
        this.vipExpiredButton = convertView.findViewById(R.id.vip_expired_btn);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // On pre-lollipop devices CardView doesn't support the clipping for round corners
            // Overlay foreground image is used as a hack for it.
            convertView.findViewById(R.id.view_foreground_overlay).setVisibility(View.VISIBLE);
        }
    }

    public void setCourseTitle(@NonNull String title) {
        courseTitle.setText(title);
    }

    public void setCourseImage(@NonNull String imageUrl) {
        final Context context = courseImage.getContext();
        if (ImageUtils.isValidContextForGlide(context)) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_course_card_image)
                    .transform(new TopAnchorFillWidthTransformation(context))
                    .into(courseImage);
        }
    }

    public void setHasUpdates(@NonNull CourseEntry courseData, @NonNull View.OnClickListener listener) {
        courseDetails.setVisibility(View.GONE);
        newCourseContent.setVisibility(View.VISIBLE);
        newCourseContent.setTag(courseData);
        newCourseContent.setOnClickListener(listener);
    }

    public void setDetails(@NonNull String date) {
        newCourseContent.setVisibility(View.GONE);
        if (TextUtils.isEmpty(date)) {
            courseDetails.setVisibility(View.GONE);
        } else {
            courseDetails.setVisibility(View.VISIBLE);
            courseDetails.setText(date);
        }
    }

    public void setVipExpiredLayoutVisable(boolean show) {
        vipExpiredLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
