package org.dj.myokhttp.interceptor;

import com.google.gson.Gson;

import org.dj.myokhttp.header.DJHeaders;
import org.dj.myokhttp.utils.DJConstant;
import org.dj.myokhttp.utils.DJParamsUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * 作者：DuJianBo on 2017/11/28 15:55
 * 邮箱：jianbo_du@foxmail.com
 */

public class DJUrlParamsInterceptor implements Interceptor {

    private Gson gson = new Gson();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        if(DJHeaders.SCP.equals(oldRequest.header(DJHeaders.HEADER_TYPE_URL_PARAM))) {
            return processScpRequest(chain);
        }

        if(DJHeaders.MALL.equals(oldRequest.header(DJHeaders.HEADER_TYPE_URL_PARAM))) {
            return processMallRequest(chain);
        }
        return chain.proceed(oldRequest);
    }

    private Response processMallRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        //生成url拼接参数
        String checkStr = "";
        long nonce = System.currentTimeMillis();
        String sign = DJParamsUtils.getSign(null, nonce);

        // 添加新的参数
        HttpUrl.Builder httpUrl = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter("nonce", String.valueOf(nonce));

        for (String key : DJConstant.mallUrlParams.keySet()) {
            httpUrl.addQueryParameter(key, String.valueOf(DJConstant.mallUrlParams.get(key)));
        }

        if (oldRequest.body() instanceof FormBody) {
            HashMap<String, Object> rootMap = new HashMap<>();
            FormBody oldFormBody = (FormBody) oldRequest.body();
            int size = oldFormBody.size();
            for (int i = 0; i < size; i++) {
                rootMap.put(oldFormBody.name(i), oldFormBody.value(i));
            }
            //生成url拼接参数
            checkStr = DJParamsUtils.getCheckStr(gson.toJson(rootMap));
            sign = DJParamsUtils.getSign(checkStr, nonce);
            // 添加新的参数
            httpUrl.addQueryParameter("checkStr", checkStr);
        } else if (!(oldRequest.body() instanceof MultipartBody)) {
            Buffer buffer = new Buffer();
            oldRequest.body().writeTo(buffer);
            String oldParamsJson = buffer.readUtf8();
            //生成url拼接参数
            checkStr = DJParamsUtils.getCheckStr(oldParamsJson);
            sign = DJParamsUtils.getSign(checkStr, nonce);
            // 添加新的参数
            httpUrl.addQueryParameter("checkStr", checkStr);
        }

        httpUrl.addQueryParameter("s", sign);

        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .url(httpUrl.build())
                .removeHeader(DJHeaders.HEADER_TYPE_URL_PARAM)
                .build();
        return chain.proceed(newRequest);
    }

    private Response processScpRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .removeHeader(DJHeaders.HEADER_TYPE_URL_PARAM)
                .build();
        return chain.proceed(newRequest);
    }
}
