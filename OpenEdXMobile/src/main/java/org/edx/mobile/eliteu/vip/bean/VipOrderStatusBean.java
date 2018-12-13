package org.edx.mobile.eliteu.vip.bean;

import java.io.Serializable;

public class VipOrderStatusBean implements Serializable {

    private int status;//1: 等待支付, 2: 已完成, 3: 已取消, 4: 已退款, 0: 查询失败

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VipOrderStatusBean{" +
                "status=" + status +
                '}';
    }
}
