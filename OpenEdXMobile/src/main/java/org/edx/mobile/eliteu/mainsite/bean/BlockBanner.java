package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;
import java.util.List;

public class BlockBanner implements Serializable {


    /**
     * banners : [{"link":"http://www.eliteu.xyz/professors/","mobile_image":"/media/original_images/banner1.png","image":"/media/original_images/banner2x.png"},{"link":"http://www.eliteu.xyz/vip","mobile_image":"/media/original_images/banner2.png","image":"/media/original_images/banner12x_zuizhong.png"},{"link":"http://www.eliteu.xyz/all_courses","mobile_image":"/media/original_images/banner3.png","image":"/media/original_images/banner32x_zuizhong.png"}]
     * loop_time : 3000
     */

    private int loop_time;
    private List<BannersBean> banners;

    public int getLoop_time() {
        return loop_time;
    }

    public void setLoop_time(int loop_time) {
        this.loop_time = loop_time;
    }

    public List<BannersBean> getBanners() {
        return banners;
    }

    public void setBanners(List<BannersBean> banners) {
        this.banners = banners;
    }

    public static class BannersBean {
        /**
         * link : http://www.eliteu.xyz/professors/
         * mobile_image : /media/original_images/banner1.png
         * image : /media/original_images/banner2x.png
         */

        private String link;
        private String mobile_image;
        private String image;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getMobile_image() {
            return mobile_image;
        }

        public void setMobile_image(String mobile_image) {
            this.mobile_image = mobile_image;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
