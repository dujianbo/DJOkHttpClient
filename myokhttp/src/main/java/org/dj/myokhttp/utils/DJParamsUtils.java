package org.dj.myokhttp.utils;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 作者：DuJianBo on 2017/11/28 16:05
 * 邮箱：jianbo_du@foxmail.com
 */

public class DJParamsUtils {

    private DJParamsUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取签名
     *
     * @param checkStr
     * @param nonce
     * @return
     */
    public static String getSign(String checkStr, long nonce) {
        Map<String, Object> signParams = new TreeMap<>();
        signParams.put("appkey", DJConstant.APP_KEY);
        signParams.put("tk", DJConstant.TOKEN);
        signParams.put("nonce", nonce);
        signParams.put("auth_ver", DJConstant.AUTHVER);
        if(!TextUtils.isEmpty(checkStr))
            signParams.put("checkStr", checkStr);

        StringBuilder paramsStr = new StringBuilder();
        Set<Map.Entry<String, Object>> paramsEntry = signParams.entrySet();
        for (Map.Entry<String, Object> entry : paramsEntry) {
            paramsStr.append(entry.getKey()).append(entry.getValue() == null ? "-" : entry.getValue());
        }
        paramsStr.append(DJConstant.APP_SECRET_KEY_1);

        return DigestUtils.md5(paramsStr.toString());
    }

    /**
     * 根据时间，请求体，生成加密串
     *
     * @param createTimeStr
     * @param requestBodyStr
     * @return
     * @throws Exception
     */
    public static String makeMsg(String createTimeStr, String requestBodyStr) throws Exception {
        long l = Long.parseLong(createTimeStr)+1;
        byte[] iv = String.valueOf(l).substring(2).getBytes();
        return DigestUtils.encode(encryption(DJConstant.APP_SECRET_KEY_2.getBytes(), iv, requestBodyStr.getBytes()));
    }

    /**
     * 完整性校验str
     *
     * @param body
     * @return
     */
    public static String getCheckStr(String body) {
        int len = body.length();
        if (len > 50) body = body.substring(0, 50);
        String str = body + DJConstant.APP_KEY;
        return DigestUtils.md5(str);
    }

    /**
     * 构建加密请求体HashMap
     *
     * @param requestBodyStr
     * @return
     * @throws Exception
     */
    public static HashMap<String, Object> makeMallEncryptBody(String requestBodyStr) throws Exception {
        //生成post体json
        String createTimeStr = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        HashMap<String, Object> map = new HashMap<>();
        map.put("createTime", Long.parseLong(createTimeStr));
        map.put("msg", makeMsg(createTimeStr, requestBodyStr));
        return map;
    }

    /**
     * 加密
     *
     * @param key     密匙
     * @param iv      IV向量(nonce前八位)
     * @param message 加密
     * @return
     */
    private static byte[] encryption(byte[] key, byte[] iv, byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
        SecretKey secretKey = skf.generateSecret(new DESedeKeySpec(key));
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(message);
    }
}
