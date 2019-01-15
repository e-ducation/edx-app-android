package org.edx.mobile.eliteu.vip.bean;

import java.io.Serializable;

public class VipPersonInfo implements Serializable {

    private String start_at;//开通时间
    private String expired_at;//过期时间
    private boolean is_vip;
    private String vip_remain_days;//vip剩余天数
    private String vip_pass_days;//vip已经开通天数
    private String vip_expired_days;//vip过期天数
    private String last_start_at;

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public String getVip_remain_days() {
        return vip_remain_days;
    }

    public void setVip_remain_days(String vip_remain_days) {
        this.vip_remain_days = vip_remain_days;
    }

    public String getVip_pass_days() {
        return vip_pass_days;
    }

    public void setVip_pass_days(String vip_pass_days) {
        this.vip_pass_days = vip_pass_days;
    }

    public String getVip_expired_days() {
        return vip_expired_days;
    }

    public void setVip_expired_days(String vip_expired_days) {
        this.vip_expired_days = vip_expired_days;
    }

    public String getLast_start_at() {
        return last_start_at;
    }

    public void setLast_start_at(String last_start_at) {
        this.last_start_at = last_start_at;
    }
}
