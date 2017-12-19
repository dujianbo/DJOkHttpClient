package org.dj.myokhttp.interceptor;

import org.dj.myokhttp.header.DJHeaders;
import org.dj.myokhttp.utils.DJConstant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：DuJianBo on 2017/11/28 11:18
 * 邮箱：jianbo_du@foxmail.com
 */

public class DJHeadersInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        if(DJHeaders.SCP.equals(oldRequest.header(DJHeaders.HEADER_TYPE_HEADER))) {
            return processScpRequest(chain);
        }

        if(DJHeaders.MALL.equals(oldRequest.header(DJHeaders.HEADER_TYPE_HEADER))) {
            return processMallRequest(chain);
        }

        return chain.proceed(oldRequest);
    }

    private Response processMallRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .removeHeader(DJHeaders.HEADER_TYPE_HEADER)
                .addHeader("DS-Client-Info", DJConstant.DEVICES_INFO_STR)
                .build();
        return chain.proceed(newRequest);
    }

    private Response processScpRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .removeHeader(DJHeaders.HEADER_TYPE_HEADER)
                .addHeader("User-Agent", DJConstant.SCP_DEVICES_INFO_STR)
//                .addHeader("Accept-Encoding", "gzip")
                .addHeader("api-version", DJConstant.VERSION_NUM_SCP)
                .build();
        return chain.proceed(newRequest);
    }
}
