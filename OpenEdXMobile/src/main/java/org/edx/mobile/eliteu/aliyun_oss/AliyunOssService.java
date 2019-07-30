package org.edx.mobile.eliteu.aliyun_oss;



import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AliyunOssService {

    @GET("/aliyun_oss/get_upload_sts/")
    Observable<AliyunStsBean> getAliyunOss(@Query("access_key_id") String access_key_id,
                                           @Query("access_key_secret") String access_key_secret);
}
