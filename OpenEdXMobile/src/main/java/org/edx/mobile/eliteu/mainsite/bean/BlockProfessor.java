package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;
import java.util.List;

public class BlockProfessor implements Serializable {


    /**
     * professor : [{"professor_degree":["美国宾夕法尼亚大学沃顿商学院 管理科学 博士"],"professor_link":"https://www.elitemba.cn/professors/1/","content":"现任美国长岛大学商学院管理系主任，终身教授，从事收益管理、服务业的供应链和物流等多个领域的研究20余年。","name":"萧柏春","professor_pic":"/media/original_images/xiaobaichun.png"},{"professor_degree":["美国卡内基梅隆大学 制造与自动化管理 博士"],"professor_link":"https://www.elitemba.cn/professors/2/","content":"现任美国长岛大学商学院终身教授，主要研究方向包括车辆调度、供应链的生产、库存管理和电子商务中的收益管理。","name":"杨威","professor_pic":"/media/original_images/yangwei.png"},{"professor_degree":["美国芝加哥大学 法律 博士"],"professor_link":"https://www.elitemba.cn/professors/7/","content":"凯洛格商学院家族企业中心并为创始人，著有《企业家不是天生的》、《企业家的成功颠覆创变真经》等畅销全球的书籍。","name":"谢洛德","professor_pic":"/media/original_images/xieluode.png"},{"professor_degree":["美国波士顿学院 经济学 博士"],"professor_link":"https://www.elitemba.cn/professors/14/","content":"戴维森学院弗朗提斯·W·约翰斯顿经济学院教授，曾戴维森学院院长和教务处副处长的职位，拥有15年连续担任经济学系系主任的经验。","name":"克拉克·罗斯","professor_pic":"/media/original_images/luosi.png"}]
     * professor_image : /media/original_images/Tui_Jian_Jiao_Shou_banner2x.png
     * professor_link : http://www.eliteu.xyz/professors/
     * title : 专业MBA教授
     */

    private String professor_image;
    private String professor_link;
    private String title;
    private List<ProfessorBean> professor;

    public String getProfessor_image() {
        return professor_image;
    }

    public void setProfessor_image(String professor_image) {
        this.professor_image = professor_image;
    }

    public String getProfessor_link() {
        return professor_link;
    }

    public void setProfessor_link(String professor_link) {
        this.professor_link = professor_link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProfessorBean> getProfessor() {
        return professor;
    }

    public void setProfessor(List<ProfessorBean> professor) {
        this.professor = professor;
    }

    public static class ProfessorBean {
        /**
         * professor_degree : ["美国宾夕法尼亚大学沃顿商学院 管理科学 博士"]
         * professor_link : https://www.elitemba.cn/professors/1/
         * content : 现任美国长岛大学商学院管理系主任，终身教授，从事收益管理、服务业的供应链和物流等多个领域的研究20余年。
         * name : 萧柏春
         * professor_pic : /media/original_images/xiaobaichun.png
         */

        private String professor_link;
        private String content;
        private String name;
        private String professor_pic;
        private List<String> professor_degree;

        public String getProfessor_link() {
            return professor_link;
        }

        public void setProfessor_link(String professor_link) {
            this.professor_link = professor_link;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfessor_pic() {
            return professor_pic;
        }

        public void setProfessor_pic(String professor_pic) {
            this.professor_pic = professor_pic;
        }

        public List<String> getProfessor_degree() {
            return professor_degree;
        }

        public void setProfessor_degree(List<String> professor_degree) {
            this.professor_degree = professor_degree;
        }
    }
}
