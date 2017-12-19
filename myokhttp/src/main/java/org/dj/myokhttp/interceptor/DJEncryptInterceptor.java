package org.dj.myokhttp.interceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.dj.myokhttp.header.DJHeaders;
import org.dj.myokhttp.utils.DJConstant;
import org.dj.myokhttp.utils.DJContentType;
import org.dj.myokhttp.utils.DJParamsUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


/**
 * 作者：DuJianBo on 2017/11/29 11:45
 * 邮箱：jianbo_du@foxmail.com
 */

public class DJEncryptInterceptor implements Interceptor {

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        if(DJHeaders.SCP.equals(oldRequest.header(DJHeaders.HEADER_TYPE_ENCRYPT))) {
            return processScpRequest(chain);
        }

        if(DJHeaders.MALL.equals(oldRequest.header(DJHeaders.HEADER_TYPE_ENCRYPT))) {
            return processMallRequest(chain);
        }
        return chain.proceed(oldRequest);
    }

    private Response processMallRequest(Chain chain) throws IOException {
        try {
            Request oldRequest = chain.request();
            Request.Builder builder = oldRequest.newBuilder();
            //GET请求则使用HttpUrl.Builder来构建
            if ("GET".equalsIgnoreCase(oldRequest.method())) {
                // TODO get请求加密
            } else if ("POST".equalsIgnoreCase(oldRequest.method())) {
                //如果原请求是表单请求
                if (oldRequest.body() instanceof FormBody) {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    HashMap<String, Object> map = new HashMap<>();
                    FormBody oldFormBody = (FormBody) oldRequest.body();
                    int size = oldFormBody.size();
                    for (int i = 0; i < size; i++) {
                        map.put(oldFormBody.name(i), oldFormBody.value(i));
                    }
                    map.putAll(DJConstant.mallBodyParams);
                    HashMap<String, Object> hashMap = DJParamsUtils.makeMallEncryptBody(gson.toJson(map));
                    for (String key : hashMap.keySet()) {
                        formBodyBuilder.add(key, String.valueOf(hashMap.get(key)));
                    }
                    builder.post(formBodyBuilder.build());
                } else {
                    //buffer流
                    if(!(oldRequest.body() instanceof MultipartBody)) {
                        Buffer buffer = new Buffer();
                        oldRequest.body().writeTo(buffer);
                        String oldParamsJson = buffer.readUtf8();
                        HashMap<String, Object> hashMap = DJParamsUtils.makeMallEncryptBody(oldParamsJson);
                        String newJsonParams = gson.toJson(hashMap);  //转化成json字符串
                        builder.post(RequestBody.create(DJContentType.JSON, newJsonParams)).build();
                    }
                }
            }

            // 新的请求
            Request newRequest = builder
                    .removeHeader(DJHeaders.HEADER_TYPE_ENCRYPT)
                    .build();
            return chain.proceed(newRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response processScpRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .removeHeader(DJHeaders.HEADER_TYPE_ENCRYPT)
                .build();
        return chain.proceed(newRequest);
    }
}
