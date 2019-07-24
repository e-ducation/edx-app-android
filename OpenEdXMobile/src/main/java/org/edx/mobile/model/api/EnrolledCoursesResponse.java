package org.edx.mobile.model.api;

import android.text.TextUtils;

import org.edx.mobile.interfaces.SectionItemInterface;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EnrolledCoursesResponse implements SectionItemInterface {

    private String auditAccessExpires;
    private String created;
    private String mode;
    private boolean is_active;
    private CourseEntry course;
    private boolean isDiscussionBlackedOut = false;

    private CertificateModel certificate;

    // derived fields (doesn't come in server response)
    public int videoCount;
    public long size;
    public boolean is_vip;//是否是vip
    public boolean is_normal_enroll;//是否单课购买
    public Progress progress;

    public String getAuditAccessExpires() {
        return auditAccessExpires;
    }

    public void setAuditAccessExpires(String auditAccessExpires) {
        this.auditAccessExpires = auditAccessExpires;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public CourseEntry getCourse() {
        return course;
    }

    public void setCourse(CourseEntry course) {
        this.course = course;
    }

    @Override
    public boolean isChapter() {
        return false;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public String toString() {
        return course.getName();
    }

    @Override
    public boolean isCourse() {
        return true;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public boolean isDownload() {
        return false;
    }

    public String getVideoCountReadable() {
        return String.format("%d %s", videoCount, (videoCount==1 ? "Video" : "Videos"));
    }

    public String getCertificateURL() {
        return this.certificate == null ? null : this.certificate.certificateURL;
    }

    public boolean isCertificateEarned() {
        return this.certificate != null && !TextUtils.isEmpty(this.certificate.certificateURL);

    }

    public boolean isDiscussionBlackedOut() {
        return isDiscussionBlackedOut;
    }

    public void setDiscussionBlackedOut(boolean discussionBlackedOut) {
        isDiscussionBlackedOut = discussionBlackedOut;
    }

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public boolean isIs_normal_enroll() {
        return is_normal_enroll;
    }

    public void setIs_normal_enroll(boolean is_normal_enroll) {
        this.is_normal_enroll = is_normal_enroll;
    }

   public static class Progress implements Serializable{
        float total_grade;
        boolean is_pass;

       public float getTotal_grade() {
           return total_grade;
       }

       public void setTotal_grade(float total_grade) {
           this.total_grade = total_grade;
       }

       public boolean isIs_pass() {
            return is_pass;
        }

        public void setIs_pass(boolean is_pass) {
            this.is_pass = is_pass;
        }
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
