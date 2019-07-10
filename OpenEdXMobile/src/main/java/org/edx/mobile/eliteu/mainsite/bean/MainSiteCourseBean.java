package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;

public class MainSiteCourseBean implements Serializable {

    private String description;
    private String link;
    private String title;
    private String image;
    private String publicity_page_url;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPublicity_page_url() {
        return publicity_page_url;
    }

    public void setPublicity_page_url(String publicity_page_url) {
        this.publicity_page_url = publicity_page_url;
    }
}
