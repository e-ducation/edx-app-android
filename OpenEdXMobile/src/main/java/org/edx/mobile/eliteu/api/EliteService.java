package org.edx.mobile.eliteu.api;

import android.support.annotation.NonNull;

import com.google.inject.Inject;

import org.edx.mobile.eliteu.util.BaseHttpResult;
import org.edx.mobile.eliteu.vip.bean.AliPayReqBean;
import org.edx.mobile.eliteu.vip.bean.WeChatReqBean;
import org.edx.mobile.eliteu.vip.bean.VipBean;
import org.edx.mobile.eliteu.vip.bean.VipOrderStatusBean;
import org.edx.mobile.eliteu.vip.bean.VipPersonInfo;
import org.edx.mobile.http.provider.RetrofitProvider;
import org.edx.mobile.model.Page;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

}
