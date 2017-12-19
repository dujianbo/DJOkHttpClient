package org.dj.myokhttp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 作者：DuJianBo on 2016/10/17 18:12
 * 邮箱：jianbo_du@foxmail.com
 */
public final class DJAppUtils {

    private DJAppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的 版本号
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 获取版本号(内部识别号)
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static String IMSI;//手机标识码
    /**
     * 获取手机唯一的标识
     *
     * @param context
     * @return
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceSN(Context context) {
        if (null == IMSI) {
            //同步代码块用的锁是单例的字节码文件对象，且只能用这个锁
            synchronized (DJAppUtils.class) {
                if (null == IMSI) {
                    try {
                        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        //1优先：不同的手机设备返回IMEI,MEID或者ESN码
                        if(tm != null) {
                            IMSI = tm.getDeviceId();
                            if (TextUtils.isEmpty(IMSI)) {
                                //2.sim serial number
                                IMSI = tm.getSimSerialNumber();//对于cdma设备，返回的是一个空值！
                                if (TextUtils.isEmpty(IMSI)) {
                                    //3.IMSI = SubscriberId
                                    IMSI = tm.getSubscriberId();
                                    if (TextUtils.isEmpty(IMSI)) {
                                        //4.android_id
                                        IMSI = Settings.System.getString
                                                (context.getContentResolver(),
                                                        Settings.Secure.ANDROID_ID);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        IMSI = "";
                    }
                }
            }
        }

        //最后还是没获取到,自己生成一个
        if (TextUtils.isEmpty(IMSI)) {
            IMSI = getDeviceID(context);
        }
        return IMSI;
    }

    /**
     * 比较版本号
     *
     * @param versionServer
     * @param versionLocal
     * @return 0, 相同, 服务器版本大返回1。服务器版本小返回-1
     */
    public static int compareVersion(String versionServer, String versionLocal) {

        if (TextUtils.isEmpty(versionServer) || TextUtils.isEmpty(versionLocal)) {
            return 0;
        }
        if (TextUtils.isEmpty(versionServer.trim()) || TextUtils.isEmpty(versionLocal.trim())) {
            return 0;
        }
        if (versionLocal.equals(versionServer)) {
            return 0;
        }

        String[] version1Array = versionServer.split("\\.");
        String[] version2Array = versionLocal.split("\\.");

        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;

        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }

        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }

            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    /**
     *  设备ID：deviceID 在安装APP后生成GUID
     */
    public static String getDeviceID(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String deviceId = sp.getString("deviceId", null);
        if (TextUtils.isEmpty(deviceId)) {
            //得到时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            String timeStr = dateFormat.format(new Date());
            String md5Hex = DigestUtils.md5(timeStr);
            deviceId = "A" + md5Hex;

            SharedPreferences.Editor edit = sp.edit();
            edit.putString("deviceId", deviceId);
            edit.apply();
        }
        return deviceId;
    }
}
