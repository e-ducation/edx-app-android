package org.edx.mobile.eliteu.event;

public class VipRemainDayUpdateEvent {

    private int vip_remain_day;

    public VipRemainDayUpdateEvent(int vip_remain_day) {
        this.vip_remain_day = vip_remain_day;
    }

    public int getVip_remain_day() {
        return vip_remain_day;
    }

}
