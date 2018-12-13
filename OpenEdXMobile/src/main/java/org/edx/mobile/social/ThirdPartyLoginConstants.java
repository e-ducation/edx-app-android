package org.edx.mobile.social;

import com.lbz.login.config.IThirdPartyConfig;

public class ThirdPartyLoginConstants {

    public static String QQ_APP_ID = "";

    public static String WECHAT_APP_ID = "";

    public static String WECHAT_SECRETID = "";

    public static String WEIBO_APP_ID = "";

    public static String WEIBO_REDIRECTURL = "";

    public static String VIP_URL = "/vip?device=android";

    public static String getBackendByType(int type) {
        switch (type) {
            case IThirdPartyConfig.TYPE_QQ:
                return "qq";
            case IThirdPartyConfig.TYPE_WX:
                return "weixin";
            case IThirdPartyConfig.TYPE_WB:
                return "weibo";
            default:
                return "";
        }
    }

}
