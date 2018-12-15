package org.edx.mobile.eliteu.vip.bean;



import com.lbz.pay.wechat.WeChatReqParam;

import java.io.Serializable;

public class WeChatReqBean implements Serializable {

    private String order_id;

    private WeChatReqParam wechat_request;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public WeChatReqParam getWechat_request() {
        return wechat_request;
    }

    public void setWechat_request(WeChatReqParam wechat_request) {
        this.wechat_request = wechat_request;
    }

    @Override
    public String toString() {
        return "WeChatReqBean{" +
                "order_id='" + order_id + '\'' +
                ", wechat_request=" + wechat_request +
                '}';
    }
}
