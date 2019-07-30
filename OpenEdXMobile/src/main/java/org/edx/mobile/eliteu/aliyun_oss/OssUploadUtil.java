package org.edx.mobile.eliteu.aliyun_oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import org.edx.mobile.social.ThirdPartyLoginConstants;
import org.edx.mobile.util.Config;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OssUploadUtil {

    /**
     * 上传client
     */
    OSS oss;
    /**
     * 上传次数
     */
    int number;
    /**
     * 成功上传(本地文件名作为key,阿里云地址为value)
     */
    Map<Integer, Object> success = new HashMap<>();
    /**
     * 失败上传(返回失败文件的本地地址)
     */
    List<String> failure = new ArrayList<>();
    /**
     * 上传回调
     */
    UploadListener uploadListener;
    /**
     * 上传任务列表
     */
    List<OSSAsyncTask> ossAsyncTasks = new ArrayList<>();

    Context mContext;

    String mUser_id;

    /**
     * 构造函数
     */
    public OssUploadUtil(Context context, final AliyunStsBean aliyunStsBean, String user_id) {
        mContext = context;
        mUser_id = user_id;
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(aliyunStsBean.getCredentials().getAccessKeyId(), aliyunStsBean.getCredentials().getAccessKeySecret(), aliyunStsBean.getCredentials().getSecurityToken());
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        oss = new OSSClient(context, ThirdPartyLoginConstants.ALIYUN_OSS_ENDPOINT, credentialProvider, conf);
        OSSLog.enableLog();
    }

    private String getUploadPath() {
        String date = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        String date_and_random = date + String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        String API_HOST_URL = new Config(mContext).getApiHostURL();
        if (API_HOST_URL.contains("xyz")) {
            return "beta/elitemba/" + mUser_id + "/" + date_and_random;
        } else {
            return "prod/elitemba/" + mUser_id + "/" + date_and_random;
        }
    }

    /**
     * 添加上传任务
     *
     * @param paths
     * @param listener
     */
    public void setDatas(final List<String> paths, UploadListener listener) {
        this.uploadListener = listener;
        ossAsyncTasks.clear();
        number = 0;
        success.clear();
        failure.clear();
        for (final String path : paths) {
            final File file = new File(path);
            /**
             * 阿里云上文件名称
             */
            final String objectKey = getUploadPath();

            PutObjectRequest put = new PutObjectRequest(ThirdPartyLoginConstants.ALIYUN_OSS_BUCKET, objectKey, path);
            put.setCRC64(OSSRequest.CRC64Config.YES);
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", ThirdPartyLoginConstants.ALIYUN_OSS_CALLBACkURL);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "{\"filePath\":${x:filePath},\"image_sequence\":${x:image_sequence}}");
                }
            });
            put.setCallbackVars(new HashMap<String, String>() {
                {
                    put("x:filePath", objectKey);
                    put("x:image_sequence", String.valueOf(paths.indexOf(path)));
                }
            });
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest putObjectRequest, long currentSize, long totalSize) {

                }
            });
            /**
             * 上传任务
             */
            OSSAsyncTask task;
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {

                    try {
                        if (putObjectResult.getStatusCode() == 200) {
                            JSONObject json = new JSONObject(putObjectResult.getServerCallbackReturnBody().toString());
                            String code = json.getString("code");
                            if (code.equals("200")) {
                                JSONObject dataJsonObj = json.getJSONObject("data");
                                if (dataJsonObj != null) {
                                    String domain = dataJsonObj.getString("domain");
                                    String fid = dataJsonObj.getString("fid");
                                    String image_sequence = dataJsonObj.getString("image_sequence");
                                    number++;
                                    success.put(Integer.parseInt(image_sequence), domain + fid);
                                    if (number == paths.size()) {
                                        uploadListener.onUploadComplete(success, failure);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    number++;
                    failure.add(path);
                    if (number == paths.size()) {
                        uploadListener.onUploadComplete(success, failure);
                    }
                }
            });
            /**
             * 添加到上传记录
             */
            ossAsyncTasks.add(task);
        }
    }


    public void cancleTasks() {
        for (OSSAsyncTask task : ossAsyncTasks) {
            if (task.isCompleted()) {

            } else {
                task.cancel();
            }
        }
    }


    public interface UploadListener {
        /**
         * 上传完成
         *
         * @param success
         * @param failure
         */
        void onUploadComplete(Map<Integer, Object> success, List<String> failure);
    }

}
