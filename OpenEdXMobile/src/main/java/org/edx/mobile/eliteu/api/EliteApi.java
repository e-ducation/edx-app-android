package org.edx.mobile.eliteu.api;

import android.support.annotation.NonNull;

import com.google.inject.Inject;
import com.google.inject.Singleton;

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
        return eliteService.resetPassword(oldpassword, newpassword,doublenewpassword);
    }
}
