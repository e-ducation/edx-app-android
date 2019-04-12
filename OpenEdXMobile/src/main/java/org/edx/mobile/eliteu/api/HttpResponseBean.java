package org.edx.mobile.eliteu.api;

import java.io.Serializable;

public class HttpResponseBean implements Serializable{

    private String msg;
    private int code;
    private boolean success;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "HttpResponseBean{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", success=" + success +
                '}';
    }
}
