package org.edx.mobile.eliteu.vip.bean;

import java.io.Serializable;

public class VipBean implements Serializable {

    private int id;
    private String name;
    private int month;
    private String price;//价格
    private String suggested_price;//建议价格(原价)
    private boolean is_recommended;//是否推荐
    private boolean isSelect = false;//是否被选中

    public VipBean(int id, String name, int month, String price, String suggested_price, boolean is_recommended) {
        this.id = id;
        this.name = name;
        this.month = month;
        this.price = price;
        this.suggested_price = suggested_price;
        this.is_recommended = is_recommended;
    }

    public VipBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSuggested_price() {
        return suggested_price;
    }

    public void setSuggested_price(String suggested_price) {
        this.suggested_price = suggested_price;
    }

    public boolean isIs_recommended() {
        return is_recommended;
    }

    public void setIs_recommended(boolean is_recommended) {
        this.is_recommended = is_recommended;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "VipBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", month=" + month +
                ", price='" + price + '\'' +
                ", suggested_price='" + suggested_price + '\'' +
                ", is_recommended=" + is_recommended +
                ", isSelect=" + isSelect +
                '}';
    }
}
