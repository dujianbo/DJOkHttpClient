package org.dj.myokhttp.utils;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：DuJianBo on 2017/11/27 17:01
 * 邮箱：jianbo_du@foxmail.com
 */

public final class DJConstant {

    public static final int DEFAULT_CONNECTION_TIME_OUT = 30;
    public static final int DEFAULT_READ_TIME_OUT = 30;
    public static final int DEFAULT_WRITE_TIME_OUT = 60;

    static final String APP_SECRET_KEY_1 = "a63bab826e87f1276c26ae9feedd1622";
    static final String APP_SECRET_KEY_2 = "2a3037d7e82e8dfbb3307d3f183813c2";

    static final String APP_KEY = "47d88cb55d16528a5c634db4463fc27f";
    static final Integer AUTHVER = 1;
    static String TOKEN;

    /**
     * 设备信息字符串 订货宝请求规则使用
     */
    public static String DEVICES_INFO_STR;

    /**
     * scp 接口规则设备信息
     */
    public static String SCP_DEVICES_INFO_STR;

    public static Map<String, Object> mallBodyParams;
    public static Map<String, Object> mallUrlParams;
    public static String VERSION_NUM_SCP = "1.0";

    private DJConstant() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(Context context) {
        TOKEN = DigestUtils.md5(DJAppUtils.getDeviceID(context));
        initInfo(context);
        initMallBodyParams(context);
        initMallUrlParams(context);
    }

    private static void initMallBodyParams(Context context) {
        if(mallBodyParams == null) {
            synchronized (DJConstant.class) {
                if(mallBodyParams == null) {
                    mallBodyParams = new HashMap<>();
                    mallBodyParams.put("terminal", 3);
                    mallBodyParams.put("plat", 3);
                    mallBodyParams.put("version", DJAppUtils.getVersionName(context));
                    mallBodyParams.put("equipment", "a_" + DJAppUtils.getDeviceSN(context));
                }
            }
        }
    }

    private static void initMallUrlParams(Context context) {
        if(mallUrlParams == null) {
            synchronized (DJConstant.class) {
                if(mallUrlParams == null) {
                    mallUrlParams = new HashMap<>();
                    mallUrlParams.put("appkey", APP_KEY);
                    mallUrlParams.put("tk", TOKEN);
                    mallUrlParams.put("auth_ver", AUTHVER);
                }
            }
        }
    }

    private static void initInfo(Context context) {
        DEVICES_INFO_STR = "OS=Android&Version=" + Build.VERSION.RELEASE + "&Model=" + Build.MANUFACTURER + " " + Build.MODEL;
        SCP_DEVICES_INFO_STR = "ds365_distributionstation_android/" + DJAppUtils.getVersionName(context)
                + "/" + DJAppUtils.getVersionCode(context)
                + "|Device/" + Build.MODEL
                + "|System/" + Build.VERSION.RELEASE
                + "|IMEI/" + DJAppUtils.getDeviceSN(context);
    }
}
