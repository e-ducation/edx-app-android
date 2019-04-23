package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;
import java.util.List;

public class BlockRecommendCourse implements Serializable {


    private String title;
    private List<MainSiteCourseBean> courses;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MainSiteCourseBean> getCourses() {
        return courses;
    }

    public void setCourses(List<MainSiteCourseBean> courses) {
        this.courses = courses;
    }
}
