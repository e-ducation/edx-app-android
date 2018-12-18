package org.edx.mobile.eliteu.event;

public class BindMobileSuccessEvent {

    private String phone;

    public BindMobileSuccessEvent(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

}
