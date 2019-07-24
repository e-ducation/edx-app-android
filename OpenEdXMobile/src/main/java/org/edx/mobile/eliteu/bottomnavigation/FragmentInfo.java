package org.edx.mobile.eliteu.bottomnavigation;

import android.support.v4.app.Fragment;

import java.io.Serializable;

public class FragmentInfo implements Serializable {


    private String title;
    private Class fragment;
    private Fragment mfragment;

    public FragmentInfo(String title, Fragment mfragment) {
        this.title = title;
        this.mfragment = mfragment;
    }

    public Fragment getMfragment() {
        return mfragment;
    }

    public void setMfragment(Fragment mfragment) {
        this.mfragment = mfragment;
    }

    public FragmentInfo(String title, Class fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }
}