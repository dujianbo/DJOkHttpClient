package org.dj.myokhttp;

import android.content.Context;

import org.dj.myokhttp.interceptor.DJBodyParamsInterceptor;
import org.dj.myokhttp.interceptor.DJEncryptInterceptor;
import org.dj.myokhttp.interceptor.DJHeadersInterceptor;
import org.dj.myokhttp.interceptor.DJUrlParamsInterceptor;
import org.dj.myokhttp.utils.DJConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 作者：DuJianBo on 2017/11/27 13:34
 * 邮箱：jianbo_du@foxmail.com
 */

public final class DJOkHttpClient {

    private static OkHttpClient client;

    private DJOkHttpClient(){}

    public static OkHttpClient getClient(Context context) {
        if (client == null) {
            synchronized (DJOkHttpClient.class) {
                if (client == null) {
                    DJConstant.init(context);
                    client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .addInterceptor(new DJHeadersInterceptor())
                            .addInterceptor(new DJBodyParamsInterceptor())
                            .addInterceptor(new DJEncryptInterceptor())
                            .addInterceptor(new DJUrlParamsInterceptor())
                            .connectTimeout(DJConstant.DEFAULT_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(DJConstant.DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(DJConstant.DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return client;
    }
}
