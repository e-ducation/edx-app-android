package org.edx.mobile.eliteu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * 设备程序检测工具类
 */
public class DeviceProgramDetectionUtil {

    public static final String WECHAT_PACKAGE_INFO = "com.tencent.mm";

    private static boolean booleanInstall(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                System.out.println(pinfo.get(i).packageName);
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测微信是否安装
     * @param context
     * @return
     */
    public static boolean booleanWechatInstall(Context context) {
        return booleanInstall(context, WECHAT_PACKAGE_INFO);
    }
}
