package org.edx.mobile.eliteu.vip.bean;


import java.io.Serializable;

public class AliPayReqBean implements Serializable{

    private String order_id;

    private String alipay_request;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAlipay_request() {
        return alipay_request;
    }

    public void setAlipay_request(String alipay_request) {
        this.alipay_request = alipay_request;
    }
}
