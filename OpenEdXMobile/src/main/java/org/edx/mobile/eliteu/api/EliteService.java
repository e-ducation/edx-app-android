package org.edx.mobile.eliteu.api;

import android.support.annotation.NonNull;

import com.google.inject.Inject;

import org.edx.mobile.course.CourseDetail;
import org.edx.mobile.eliteu.article.ArticleBean;
import org.edx.mobile.eliteu.article.ArticleTagBean;
import org.edx.mobile.eliteu.bottomnavigation.course.CourseSubjectBean;
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
import org.edx.mobile.http.provider.RetrofitProvider;
import org.edx.mobile.model.Page;
import org.edx.mobile.model.api.EnrolledCoursesResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface EliteService {

    class Provider implements com.google.inject.Provider<EliteService> {
        @Inject
        RetrofitProvider retrofitProvider;

        @Override
        public EliteService get() {
            return retrofitProvider.getWithOfflineCache().create(EliteService.class);
        }
    }

    @GET("/api/v1/mobile/vip/package/")
    Observable<Page<VipBean>> getVipPackages();

    @GET("/api/v1/mobile/vip/info/")
    Observable<VipPersonInfo> getVipPersonInfo();

    @NonNull
    @FormUrlEncoded
    @POST("/api/v1/mobile/vip/pay/alipay/paying/")
    Observable<AliPayReqBean> getVipAliPayReqBean(@Field("package_id") int package_id);

    @NonNull
    @FormUrlEncoded
    @POST("/api/v1/mobile/vip/pay/wechat/paying/")
    Observable<WeChatReqBean> getVipWeChatPayReqBean(@Field("package_id") int package_id);

    @GET("/api/v1/vip/order/{id}")
    Observable<BaseHttpResult<VipOrderStatusBean>> getVipOrderStstus(@Path("id") String id);

    @NonNull
    @FormUrlEncoded
    @POST("/api/user/v1/accounts/send_code_binding_phone/")
    Call<ResponseBody> sendCodeBindingPhone(@Field("phone") String phone);

    @NonNull
    @FormUrlEncoded
    @POST("/api/user/v1/accounts/binding_phone/")
    Call<ResponseBody> bindingPhone(@Field("phone") String phone, @Field("code") String code);

    @NonNull
    @FormUrlEncoded
    @POST("/api/user/v1/accounts/elite_password_reset/")
    Call<HttpResponseBean> resetPassword(@Field("old_password") String old_password, @Field("new_password1") String new_password1, @Field("new_password2") String new_password2);

    @GET("/api/v1/professors/")
    Observable<PageHttpResult<ProfessorBean>> getProfessorList(@Query("page_size") int page_size, @Query("page") int page);

    @GET("/api/v1/professors/{professor_id}")
    Observable<ProfessorsDetailBean> getProfessorDetail(@Path("professor_id") int professor_id);

    @GET("/api/v2/app/")
    Observable<PageHttpResult<ArticleBean>> getArticleListWithTags(@Query("fields") String fields,
                                                                   @Query("page") int page,
                                                                   @Query("page_size") int page_size,
                                                                   @Query("type") String type,
                                                                   @Query("order") String order,
                                                                   @Query("tags") String tags
    );

    @GET("/api/v2/app/")
    Observable<PageHttpResult<ArticleBean>> getArticleListWithOutTags(@Query("fields") String fields,
                                                                      @Query("page") int page,
                                                                      @Query("page_size") int page_size,
                                                                      @Query("type") String type,
                                                                      @Query("order") String order

    );

    @GET("/api/v2/tags/")
    Observable<ArticleTagBean> getArticleTags(@Query("fields") String fields);

    @GET("/api/v2/pages/find/")
    Observable<MainSiteBlockHttpResponse> getMainSiteBlock(@Query("html_path") String html_path);

    @GET("/api/v1/mobile/courses/{course_id}")
    Call<CourseDetail> getCourseDetail(@Path("course_id") final String courseId,
                                       @Query("username") final String username);

    @GET("/elitemba/api/v1/course_types/")
    Observable<PageHttpResult<CourseSubjectBean>> getCourseSubject(@Query("page_size") final int page_size);

    @GET("/elitemba/api/v1/courses_search/")
    Observable<Page<CourseDetail>> getCourseSearchByTypeId(@Query("page") final int page_index,
                                                           @Query("page_size") final int page_size,
                                                           @Query("course_type_id") final int course_type_id);

    @GET("/elitemba/api/v1/courses_search/")
    Observable<Page<CourseDetail>> getCourseSearchByKeyWord(@Query("page") final int page_index,
                                                            @Query("page_size") final int page_size,
                                                            @Query("search_name") final String search_name);

    @GET("/elitemba/api/v1/courses_search/")
    Observable<Page<CourseDetail>> getCourseSearchAll(@Query("page") final int page_index,
                                                            @Query("page_size") final int page_size);

    @GET("/api/v1/mobile/users/{username}/course_enrollments")
    Observable<List<EnrolledCoursesResponse>> getEnrolledCourses(@Path("username") final String username,
                                                                 @Query("org") final String org);

    @NonNull
    @FormUrlEncoded
    @POST("/user_feeback/")
    Observable<HttpResponseBean> submitFeedback(@Field("image_url") String image_url,
                                      @Field("username") String username,
                                      @Field("content") String content,
                                      @Field("contact") String contact);

    @GET
    Observable<HttpResponseBean> requestScanSuccess(@Url String url);

}
