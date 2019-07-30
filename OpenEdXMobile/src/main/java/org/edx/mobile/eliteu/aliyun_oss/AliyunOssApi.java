package org.edx.mobile.eliteu.aliyun_oss;


import org.edx.mobile.social.ThirdPartyLoginConstants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class AliyunOssApi {

    private static final int DEFAULT_CONNECT_TIMEOUT = 300;//连接超时时间，单位s
    private static final int DEFAULT_READ_TIMEOUT = 300;//读超时时间，单位s
    private static final int DEFAULT_WRITE_TIMEOUT = 300;//写超时时间，单位s

    private AliyunOssService mService;

    private AliyunOssApi() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(httpLoggingInterceptor);
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://bss.eliteu.cn/aliyun_oss/")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mService = retrofit.create(AliyunOssService.class);
    }

    private static volatile AliyunOssApi instance;

    public static AliyunOssApi getIstance() {
        if (instance == null) {
            synchronized (AliyunOssApi.class) {
                if (instance == null) {
                    instance = new AliyunOssApi();
                }
            }
        }
        return instance;
    }

    public Observable<AliyunStsBean> getAliyunOss() {
        return mService.getAliyunOss(ThirdPartyLoginConstants.ALIYUN_OSS_ACCESS_KEY_ID, ThirdPartyLoginConstants.ALIYUN_OSS_ACCESS_KEY_SECRET);
    }

}
