package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;
import java.util.List;

public class BlockCourseCategory implements Serializable {


    private List<CategorieslistBean> categorieslist;

    public List<CategorieslistBean> getCategorieslist() {
        return categorieslist;
    }

    public void setCategorieslist(List<CategorieslistBean> categorieslist) {
        this.categorieslist = categorieslist;
    }

    public static class CategorieslistBean {
        /**
         * categories_link : http://www.elitemba.cn
         * categories_name : 创新与创业
         */

        private String categories_link;
        private String categories_name;

        public String getCategories_link() {
            return categories_link;
        }

        public void setCategories_link(String categories_link) {
            this.categories_link = categories_link;
        }

        public String getCategories_name() {
            return categories_name;
        }

        public void setCategories_name(String categories_name) {
            this.categories_name = categories_name;
        }
    }
}
