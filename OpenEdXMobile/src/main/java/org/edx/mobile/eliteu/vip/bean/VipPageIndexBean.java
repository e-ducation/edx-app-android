package org.edx.mobile.eliteu.vip.bean;

import java.util.List;

public class VipPageIndexBean {
    private VipPersonInfo vipPersonInfo;
    private List<VipBean> vipBeans;

    public VipPageIndexBean(VipPersonInfo vipPersonInfo, List<VipBean> vipBeans) {
        this.vipPersonInfo = vipPersonInfo;
        this.vipBeans = vipBeans;
    }

    public VipPersonInfo getVipPersonInfo() {
        return vipPersonInfo;
    }

    public void setVipPersonInfo(VipPersonInfo vipPersonInfo) {
        this.vipPersonInfo = vipPersonInfo;
    }

    public List<VipBean> getVipBeans() {
        return vipBeans;
    }

    public void setVipBeans(List<VipBean> vipBeans) {
        this.vipBeans = vipBeans;
    }

    @Override
    public String toString() {
        return "VipPageIndexBean{" +
                "vipPersonInfo=" + vipPersonInfo +
                ", vipBeans=" + vipBeans +
                '}';
    }
}
