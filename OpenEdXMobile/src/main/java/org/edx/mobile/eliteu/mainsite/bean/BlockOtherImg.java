package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;

public class BlockOtherImg implements Serializable {


    /**
     * fill_the_screen_or_nor : false
     * img_for_MOBILE : /media/original_images/h5Jia_Ru_Hui_Yuan_.png
     * link : http://www.eliteu.xyz/vip
     * img_for_pc : /media/original_images/Cheng_Wei_Hui_Yuan_2x_WfbBaDj.png
     * title : 加入会员
     */

    private boolean fill_the_screen_or_nor;
    private String img_for_MOBILE;
    private String link;
    private String img_for_pc;
    private String title;

    public boolean isFill_the_screen_or_nor() {
        return fill_the_screen_or_nor;
    }

    public void setFill_the_screen_or_nor(boolean fill_the_screen_or_nor) {
        this.fill_the_screen_or_nor = fill_the_screen_or_nor;
    }

    public String getImg_for_MOBILE() {
        return img_for_MOBILE;
    }

    public void setImg_for_MOBILE(String img_for_MOBILE) {
        this.img_for_MOBILE = img_for_MOBILE;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg_for_pc() {
        return img_for_pc;
    }

    public void setImg_for_pc(String img_for_pc) {
        this.img_for_pc = img_for_pc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
