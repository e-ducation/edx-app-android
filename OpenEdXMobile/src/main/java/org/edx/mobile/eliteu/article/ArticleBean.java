package org.edx.mobile.eliteu.article;

import java.io.Serializable;
import java.util.List;

public class ArticleBean implements Serializable{


    /**
     * id : 16
     * meta : {"html_url":"http://abc2.eliteu.xyz/user_story/articl01/"}
     * title : 我用三个月时间学完EliteMBA《职场新人营销》实践课程
     * tags : ["soda","用户故事","逍遥说"]
     * author_image : null
     * author_name : 萧教授
     * article_datetime : 2019-04-08 11:00
     * article_cover_app : {"id":146,"meta":{"type":"wagtailimages.Image","detail_url":"http://abc2.eliteu.xyz/api/v2/images/146/","download_url":"/media/original_images/article_covera_app.png"},"title":"app默认图片"}
     * description : 一寸山河一寸血，一抔热土一抔魂。近代以来，在争取民族独立、人民解放的道路上，无数革命先辈前仆后继、顽强奋斗，用鲜血和生命谱写出一部部气壮山河的英雄史诗，铸就了永不褪色的精神丰碑。
     * liked_count : 6
     */

    private int id;
    private MetaBean meta;
    private String title;
    private Object author_image;
    private String author_name;
    private String article_datetime;
    private ArticleCoverAppBean article_cover_app;
    private String description;
    private int liked_count;
    private List<String> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(Object author_image) {
        this.author_image = author_image;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getArticle_datetime() {
        return article_datetime;
    }

    public void setArticle_datetime(String article_datetime) {
        this.article_datetime = article_datetime;
    }

    public ArticleCoverAppBean getArticle_cover_app() {
        return article_cover_app;
    }

    public void setArticle_cover_app(ArticleCoverAppBean article_cover_app) {
        this.article_cover_app = article_cover_app;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLiked_count() {
        return liked_count;
    }

    public void setLiked_count(int liked_count) {
        this.liked_count = liked_count;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public static class MetaBean {
        /**
         * html_url : http://abc2.eliteu.xyz/user_story/articl01/
         */

        private String html_url;

        public String getHtml_url() {
            return html_url;
        }

        public void setHtml_url(String html_url) {
            this.html_url = html_url;
        }
    }

    public static class ArticleCoverAppBean {
        /**
         * id : 146
         * meta : {"type":"wagtailimages.Image","detail_url":"http://abc2.eliteu.xyz/api/v2/images/146/","download_url":"/media/original_images/article_covera_app.png"}
         * title : app默认图片
         */

        private int id;
        private MetaBeanX meta;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public MetaBeanX getMeta() {
            return meta;
        }

        public void setMeta(MetaBeanX meta) {
            this.meta = meta;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public static class MetaBeanX {
            /**
             * type : wagtailimages.Image
             * detail_url : http://abc2.eliteu.xyz/api/v2/images/146/
             * download_url : /media/original_images/article_covera_app.png
             */

            private String type;
            private String detail_url;
            private String download_url;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDetail_url() {
                return detail_url;
            }

            public void setDetail_url(String detail_url) {
                this.detail_url = detail_url;
            }

            public String getDownload_url() {
                return download_url;
            }

            public void setDownload_url(String download_url) {
                this.download_url = download_url;
            }

            @Override
            public String toString() {
                return "MetaBeanX{" +
                        "type='" + type + '\'' +
                        ", detail_url='" + detail_url + '\'' +
                        ", download_url='" + download_url + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ArticleCoverAppBean{" +
                    "id=" + id +
                    ", meta=" + meta +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ArticleBean{" +
                "id=" + id +
                ", meta=" + meta +
                ", title='" + title + '\'' +
                ", author_image=" + author_image +
                ", author_name='" + author_name + '\'' +
                ", article_datetime='" + article_datetime + '\'' +
                ", article_cover_app=" + article_cover_app +
                ", description='" + description + '\'' +
                ", liked_count=" + liked_count +
                ", tags=" + tags +
                '}';
    }
}
