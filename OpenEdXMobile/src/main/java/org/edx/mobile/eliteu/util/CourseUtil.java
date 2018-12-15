package org.edx.mobile.eliteu.util;

import android.content.Context;

import org.edx.mobile.R;
import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.model.api.EnrolledCoursesResponse;

public class CourseUtil {

    /**
     * @param enrolledCoursesResponse
     * @return 我的课程列表中是否能查看和点击课程
     */
    public static boolean courseCanView(EnrolledCoursesResponse enrolledCoursesResponse) {
        if (enrolledCoursesResponse == null) {
            return false;
        }
        boolean is_normal_enroll = enrolledCoursesResponse.isIs_normal_enroll();//是否是单课购买
        boolean is_get_certificate = enrolledCoursesResponse.isCertificateEarned();//是否获得证书
        boolean is_vip = enrolledCoursesResponse.isIs_vip();//当前是否是vip

        if (is_normal_enroll) {
            //单课购买
            return true;
        } else {
            if (is_get_certificate) {
                //取得证书
                return true;
            } else {
                if (is_vip) {
                    //vip正常
                    return true;
                } else {
                    //vip过期
                    return false;
                }
            }
        }

    }

    public static String getEnrollButtonString(CourseDetail courseDetail, Context context) {
        String enrollButtonStr;
        if (courseDetail.has_cert) {
            enrollButtonStr = context.getString(R.string.view_course_button_text);
        } else {
            if (courseDetail.is_enroll) {
                if (courseDetail.is_normal_enroll) {
                    enrollButtonStr = context.getString(R.string.view_course_button_text);
                } else {
                    if (courseDetail.is_vip) {
                        enrollButtonStr = context.getString(R.string.view_course_button_text);
                    } else {
                        enrollButtonStr = context.getString(R.string.enroll_now_button_text);
                    }
                }
            } else {
                enrollButtonStr = context.getString(R.string.enroll_now_button_text);
            }
        }

        return enrollButtonStr;
    }

    public static final int CLICK_OPEN_COURSE = 1001;//查看课程
    public static final int CLICK_VIP_ENROLL = 1002;//vip购买
    public static final int CLICK_NORMAL_ENROLL = 1003;//单课购买
    public static final int CLICK_VIP_DIALOG = 1004;//VIP课程但是is_vip为false

    public static int getEnrollButtonOnClickEvent(CourseDetail courseDetail) {
        int event = 0;
        if (courseDetail.has_cert) {
            event = CLICK_OPEN_COURSE;
        } else {
            if (courseDetail.is_enroll) {
                if (courseDetail.is_normal_enroll) {
                    event = CLICK_OPEN_COURSE;
                } else {
                    if (courseDetail.is_vip) {
                        event = CLICK_OPEN_COURSE;
                    } else {
                        event = CLICK_VIP_DIALOG;
                    }
                }
            } else {
                if (!courseDetail.is_subscribe_pay) {
                    if (courseDetail.is_vip) {
                        event = CLICK_VIP_ENROLL;
                    } else {
                        event = CLICK_VIP_DIALOG;
                    }
                } else {
                    event = CLICK_NORMAL_ENROLL;
                }
            }
        }
        return event;
    }

}
