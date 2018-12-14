package org.edx.mobile.eliteu.util;

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

}
