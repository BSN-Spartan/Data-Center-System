package com.spartan.dc.core.util.common;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author xingjie
 */
public class OkHttpUtils {

    public static OkHttpClient client = null;

    public final static long CONNECTION_TIMEOUT = 5;

    public final static long READ_TIMEOUT = 20L;

    public final static long WRITE_TIMEOUT = 10L;

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }


    public static String doGet(String host, String path, Map<String, String> headers, Map<String, String> params) throws IOException {
        StringBuffer url = new StringBuffer(host + (path == null ? "" : path));
        if (params != null) {
            url.append("?");
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry) iterator.next();
                url.append(e.getKey()).append("=").append(e.getValue() + "&");
            }
            url = new StringBuffer(url.substring(0, url.length() - 1));
        }

        Request.Builder builder = new Request.Builder();
        buildHeader(builder, headers);
        Request request = builder.url(String.valueOf(url)).build();
        Response response = client.newCall(request).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }


    public static String doPost(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        FormBody.Builder formbody = new FormBody.Builder();
        if (null != params) {
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry) iterator.next();
                formbody.add(elem.getKey(), elem.getValue());
            }
        }
        RequestBody body = formbody.build();
        Request.Builder builder = new Request.Builder();
        buildHeader(builder, headers);
        Request request = builder.url(url).post(body).build();
        Response response = client.newCall(request).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }

    public static String doPost(String url, Map<String, String> headers, String json) throws IOException {
        RequestBody body = FormBody.create(MEDIA_TYPE_JSON, json);
        Request.Builder builder = new Request.Builder();
        buildHeader(builder, headers);
        Request request = builder.url(url).post(body).build();
        Response response = client.newCall(request).execute();
        String responseStr = response.body() == null ? "" : response.body().string();
        return responseStr;
    }


    public static String postJSON(final String url, Map<String, String> headerMap, String json) throws IOException {
        return doPost(url, headerMap, json);
    }


    private static void buildHeader(Request.Builder builder, Map<String, String> headers) {
        if (Objects.nonNull(headers) && headers.size() > 0) {
            headers.forEach((k, v) -> {
                if (Objects.nonNull(k) && Objects.nonNull(v)) {
                    builder.addHeader(k, v);
                }
            });
        }
    }

}