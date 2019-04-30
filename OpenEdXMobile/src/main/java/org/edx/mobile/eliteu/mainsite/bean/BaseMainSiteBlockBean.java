package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;

public class BaseMainSiteBlockBean implements Serializable {

    private String type;
    private Object value;
    private String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
