package org.edx.mobile.eliteu.mainsite.bean;

import org.edx.mobile.eliteu.mainsite.bean.BaseMainSiteBlockBean;

import java.io.Serializable;
import java.util.List;

public class MainSiteBlockHttpResponse implements Serializable {

    private int id;
    private List<BaseMainSiteBlockBean> body;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BaseMainSiteBlockBean> getBody() {
        return body;
    }

    public void setBody(List<BaseMainSiteBlockBean> body) {
        this.body = body;
    }
}
