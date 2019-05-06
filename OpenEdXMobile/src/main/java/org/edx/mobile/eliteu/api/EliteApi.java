package org.edx.mobile.eliteu.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.eliteu.article.ArticleBean;
import org.edx.mobile.eliteu.article.ArticleTagBean;
import org.edx.mobile.eliteu.mainsite.bean.MainSiteBlockHttpResponse;
import org.edx.mobile.eliteu.professor.ProfessorBean;
import org.edx.mobile.eliteu.professor.ProfessorsDetailBean;
import org.edx.mobile.eliteu.mainsite.bean.PageHttpResult;
import org.edx.mobile.eliteu.util.BaseHttpResult;
import org.edx.mobile.eliteu.vip.bean.AliPayReqBean;
import org.edx.mobile.eliteu.vip.bean.WeChatReqBean;
import org.edx.mobile.eliteu.vip.bean.VipBean;
import org.edx.mobile.eliteu.vip.bean.VipOrderStatusBean;
import org.edx.mobile.eliteu.vip.bean.VipPersonInfo;
import org.edx.mobile.model.Page;
import org.edx.mobile.module.prefs.UserPrefs;
import org.edx.mobile.util.Config;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;


@Singleton
public class EliteApi {

    @Inject
    protected Config config;

    @NonNull
    private final EliteService eliteService;

    @NonNull
    private final UserPrefs userPrefs;

    public static final int PAGE_SIZE = 10;

    @Inject
    public EliteApi(@NonNull EliteService eliteService, @NonNull UserPrefs userPrefs) {
        this.eliteService = eliteService;
        this.userPrefs = userPrefs;
    }

    /**
     * @return Vip套餐列表
     */
    @NonNull
    public Observable<Page<VipBean>> getVipPackages() {
        return eliteService.getVipPackages();
    }

    /**
     * @return Vip个人信息
     */
    @NonNull
    public Observable<VipPersonInfo> getVipPersonInfo() {
        return eliteService.getVipPersonInfo();
    }

    /**
     * @return Vip购买获取支付宝请求参数
     */
    @NonNull
    public Observable<AliPayReqBean> getVipAliPayReqBean(int package_id) {
        return eliteService.getVipAliPayReqBean(package_id);
    }

    /**
     * @return Vip购买获取微信请求参数
     */
    @NonNull
    public Observable<WeChatReqBean> getVipWeChatPayReqBean(int package_id) {
        return eliteService.getVipWeChatPayReqBean(package_id);
    }

    /**
     * @return Vip购买订单查询
     */
    public Observable<BaseHttpResult<VipOrderStatusBean>> getVipOrderStstus(String id) {
        return eliteService.getVipOrderStstus(id);
    }

    /**
     * @return 绑定手机发送验证码
     */
    public Call<ResponseBody> sendCodeBindingPhone(String phone) {
        return eliteService.sendCodeBindingPhone(phone);
    }

    /**
     * @return 绑定手机验证验证码
     */
    public Call<ResponseBody> bindingPhone(String phone, String code) {
        return eliteService.bindingPhone(phone, code);
    }

    /**
     * @return 重置密码
     */
    public Call<HttpResponseBean> resetPassword(String oldpassword, String newpassword, String doublenewpassword) {
        return eliteService.resetPassword(oldpassword, newpassword, doublenewpassword);
    }

    /**
     * @return 获取教授列表
     */
    public Observable<PageHttpResult<ProfessorBean>> getProfessorList(int page) {
        return eliteService.getProfessorList(PAGE_SIZE, page);
    }

    /**
     * @return 获取教授详情
     */
    public Observable<ProfessorsDetailBean> getProfessorDetail(int professor_id) {
        return eliteService.getProfessorDetail(professor_id);
    }

    /**
     * @return 获取文章列表
     */
    public Observable<PageHttpResult<ArticleBean>> getArticleList(int page, String order, String tags) {
        String fields = "tags,author_image,article_datetime,article_cover_app,liked_count,description,author_name";
        String type = "home.ArticlePage";
        if (TextUtils.isEmpty(tags)) {
            return eliteService.getArticleListWithOutTags(fields, page, PAGE_SIZE, type, order);
        } else {
            return eliteService.getArticleListWithTags(fields, page, PAGE_SIZE, type, "-article_datetime", tags);
        }
    }

    /**
     * @return 获取文章标签
     */
    public Observable<ArticleTagBean> getArticleTags() {
        String fields = "_,name";
        return eliteService.getArticleTags(fields);
    }

    /**
     * @return 获取首页数据
     */
    public Observable<MainSiteBlockHttpResponse> getMainSiteBlock() {
        String html_path = "/";
        return eliteService.getMainSiteBlock(html_path);
    }

    /**
     * @return 获取课程详情
     */
    public Call<CourseDetail> getCourseDetail(String courseId, String username) {
        return eliteService.getCourseDetail(courseId, username);
    }

}
