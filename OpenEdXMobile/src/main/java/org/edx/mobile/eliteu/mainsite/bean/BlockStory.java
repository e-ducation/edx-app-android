package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;
import java.util.List;

public class BlockStory implements Serializable {


    /**
     * propaganda_link : http://abc2.eliteu.xyz/user_story
     * propaganda_image : /media/original_images/Yong_Hu_Gu_Shi_banner.png
     * title : 用户故事
     * story : [{"story_photo":["/media/original_images/340px_160px.png","/media/original_images/170x90.png","/media/original_images/170x90.png"],"story_content":"故事内容故事内容故事内容故事内容","photo_location":"left","story_title":"我用三个月时间学完EliteMBA《职场新人营  销》实践课，月薪从三千到一万，哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈","story_link":"http://abc2.eliteu.xyz/user_story/gushi01/"},{"story_photo":["/media/original_images/290x160.png"],"story_content":"通过学习经济学，可以了解市场经济的发展与趋势，可以在市场不稳定时，给公司提供合理的建议，度过瓶颈期。通过学习经济学，可以了解市场经济的发展与趋势，可以在市场不稳定时，给公司提供合理的建议，度过瓶颈期。","photo_location":"center","story_title":"学习分享：快速找到你的学习路径 学习分享：快速找到你的学习路径学习分享：快速找到你的学习路径学习分享：快速找到你的学习路径学习分享：快速找到你的学习路径","story_link":"http://abc2.eliteu.xyz/user_story/articl01/"},{"story_photo":["/media/original_images/290x160.png"],"story_content":"我用三个月时间学完EliteMBA《职场新人营  销》实践课，月薪从三千到一万；我用三个月时间学完EliteMBA《职场新人营  销》实践课，月薪从三千到一万","photo_location":"center","story_title":"学习分享：快速找到你的学习路径","story_link":"http://abc2.eliteu.xyz/user_story/articl04/"}]
     */

    private String propaganda_link;
    private String propaganda_image;
    private String title;
    private List<StoryBean> story;

    public String getPropaganda_link() {
        return propaganda_link;
    }

    public void setPropaganda_link(String propaganda_link) {
        this.propaganda_link = propaganda_link;
    }

    public String getPropaganda_image() {
        return propaganda_image;
    }

    public void setPropaganda_image(String propaganda_image) {
        this.propaganda_image = propaganda_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<StoryBean> getStory() {
        return story;
    }

    public void setStory(List<StoryBean> story) {
        this.story = story;
    }

    public static class StoryBean {
        /**
         * story_photo : ["/media/original_images/340px_160px.png","/media/original_images/170x90.png","/media/original_images/170x90.png"]
         * story_content : 故事内容故事内容故事内容故事内容
         * photo_location : left
         * story_title : 我用三个月时间学完EliteMBA《职场新人营  销》实践课，月薪从三千到一万，哈哈哈哈哈哈哈哈哈啊哈哈哈哈哈哈哈
         * story_link : http://abc2.eliteu.xyz/user_story/gushi01/
         */

        private String story_content;
        private String photo_location;
        private String story_title;
        private String story_link;
        private List<String> story_photo;

        public String getStory_content() {
            return story_content;
        }

        public void setStory_content(String story_content) {
            this.story_content = story_content;
        }

        public String getPhoto_location() {
            return photo_location;
        }

        public void setPhoto_location(String photo_location) {
            this.photo_location = photo_location;
        }

        public String getStory_title() {
            return story_title;
        }

        public void setStory_title(String story_title) {
            this.story_title = story_title;
        }

        public String getStory_link() {
            return story_link;
        }

        public void setStory_link(String story_link) {
            this.story_link = story_link;
        }

        public List<String> getStory_photo() {
            return story_photo;
        }

        public void setStory_photo(List<String> story_photo) {
            this.story_photo = story_photo;
        }
    }
}
