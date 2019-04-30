package org.edx.mobile.eliteu.article;

import java.io.Serializable;
import java.util.List;

public class ArticleTagBean implements Serializable {


    /**
     * meta : {"total_count":20}
     * items : [{"name":"用户故事"},{"name":"逍遥说"},{"name":"时政新闻"},{"name":"抗癌药"},{"name":"tag_01"},{"name":"tag_02"},{"name":"tag_03"},{"name":"tag_04"},{"name":"tag_05"},{"name":"tag_06"},{"name":"tag_07"},{"name":"tag_08"},{"name":"tag_09"},{"name":"哈哈哈哈"},{"name":"逍遥说 时政新闻"},{"name":"tag_01 tag_02"},{"name":"逍遥说逍遥说"},{"name":"mlds"},{"name":"一二三四五六七八九十iiiiiiiiii"},{"name":"tag_111"}]
     */

    private MetaBean meta;
    private List<ItemsBean> items;

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class MetaBean {
        /**
         * total_count : 20
         */

        private int total_count;

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }
    }

    public static class ItemsBean {
        /**
         * name : 用户故事
         */

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
