package org.dj.myokhttp.interceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.dj.myokhttp.header.DJHeaders;
import org.dj.myokhttp.utils.DJConstant;
import org.dj.myokhttp.utils.DJContentType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


/**
 * 作者：DuJianBo on 2017/11/28 14:46
 * 邮箱：jianbo_du@foxmail.com
 */

public class DJBodyParamsInterceptor implements Interceptor {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    new TypeToken<HashMap<String, Object>>() {}.getType(),
                    new JsonDeserializer<HashMap<String, Object>>() {
                        @Override
                        public HashMap<String, Object> deserialize(
                                JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            JsonObject jsonObject = json.getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                hashMap.put(entry.getKey(), entry.getValue());
                            }
                            return hashMap;
                        }
                    }).create();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        if(DJHeaders.SCP.equals(oldRequest.header(DJHeaders.HEADER_TYPE_BODY_PARAM))) {
            return processScpRequest(chain);
        }

        if(DJHeaders.MALL.equals(oldRequest.header(DJHeaders.HEADER_TYPE_BODY_PARAM))) {
            return processMallRequest(chain);
        }
        return chain.proceed(oldRequest);
    }

    private Response processMallRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder builder = oldRequest.newBuilder();
        //GET请求则使用HttpUrl.Builder来构建
        if ("GET".equalsIgnoreCase(oldRequest.method())) {
            HttpUrl.Builder httpUrlBuilder = oldRequest.url().newBuilder();
            for (String key : DJConstant.mallBodyParams.keySet()) {
                httpUrlBuilder.addQueryParameter(key, String.valueOf(DJConstant.mallBodyParams.get(key)));
            }
            builder.url(httpUrlBuilder.build());
        } else if ("POST".equalsIgnoreCase(oldRequest.method())) {
            //如果原请求是表单请求
            if (oldRequest.body() instanceof FormBody) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                for (String key : DJConstant.mallBodyParams.keySet()) {
                    formBodyBuilder.add(key, String.valueOf(DJConstant.mallBodyParams.get(key)));
                }
                FormBody oldFormBody = (FormBody) oldRequest.body();
                int size = oldFormBody.size();
                for (int i = 0; i < size; i++) {
                    formBodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i));
                }

                builder.post(formBodyBuilder.build());
            } else {
                //buffer流
                if(!(oldRequest.body() instanceof MultipartBody)) {
                    Buffer buffer = new Buffer();
                    oldRequest.body().writeTo(buffer);
                    String oldParamsJson = buffer.readUtf8();
                    Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
                    HashMap<String, Object> rootMap = gson.fromJson(oldParamsJson, type);  //原始参数
                    for (String key : DJConstant.mallBodyParams.keySet()) {
                        rootMap.put(key, DJConstant.mallBodyParams.get(key));//重新组装
                    }
                    String newJsonParams = gson.toJson(rootMap);  //转化成json字符串
                    builder.post(RequestBody.create(DJContentType.JSON, newJsonParams)).build();
                }
            }
        }

        // 新的请求
        Request newRequest = builder
                .removeHeader(DJHeaders.HEADER_TYPE_BODY_PARAM)
                .build();
        return chain.proceed(newRequest);
    }

    private Response processScpRequest(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .removeHeader(DJHeaders.HEADER_TYPE_BODY_PARAM)
                .build();
        return chain.proceed(newRequest);
    }
}
