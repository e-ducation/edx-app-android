package org.edx.mobile.eliteu.mainsite.bean;

import java.io.Serializable;
import java.util.List;

public class BlockSeriesCourse implements Serializable{


    /**
     * series : [{"description":"职场瓶颈难以突破？学习纯正美国MBA课程，轻松进阶精英管理层。","link":"http://www.eliteu.xyz/tongyongguanli","title":"通用管理系列课","image":"/media/original_images/Tong_Yong_Guan_Li_Ke_Cheng_2x.png"},{"description":"国内零售管理名师亲授如何让策略落地，实现\u201c零缺陷管理\u201d。","link":"http://www.eliteu.xyz/lingshouguanli","title":"零售管理系列课","image":"/media/original_images/Ling_Shou_Guan_Li_Ke_Cheng_2x.png"},{"description":"大健康产业时代，医药连锁实现持续增长的关键点是什么？解密精细化管理的通用密码。","link":"http://www.eliteu.xyz/yiyaoliansuoguanli","title":"医药连锁管理系列课","image":"/media/original_images/Yi_Yao_Guan_Li_Ke_Cheng_2x.png"}]
     * title : 精选系列课
     */

    private String title;
    private List<MainSiteCourseBean> series;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MainSiteCourseBean> getSeries() {
        return series;
    }

    public void setSeries(List<MainSiteCourseBean> series) {
        this.series = series;
    }

}
