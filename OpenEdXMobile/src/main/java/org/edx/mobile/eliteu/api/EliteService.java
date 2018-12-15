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

}
