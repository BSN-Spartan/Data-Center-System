package com.spartan.dc.core.util.okhttp;

import com.alibaba.fastjson.JSON;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LogInterceptor implements Interceptor {
    private static Logger log = LoggerFactory.getLogger(LogInterceptor.class);
    private String TAG = super.getClass().getSimpleName();


    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get request information, here you can add headers information if needed
        Request request = chain.request();
        log.info("{}[request]{}:", TAG, request.toString());
        log.debug("{}[request-headers]{}:", TAG, request.headers().toString());
        log.debug("{}[request-headers]{}:", TAG, JSON.toJSONString(request));
        /* Record request time */
        long startNs = System.nanoTime();
        /* Send request, get response */
        Response response = chain.proceed(request);

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        /* Print request time */
        log.info("{}[Time consumed]:{}", TAG, tookMs + "ms");
        /* Use the response to get headers(), you can update the local cookie*/
        log.debug("{}[response-code]{}:", TAG, response.code());
        log.debug("{}[response-headers]{}:", TAG, response.headers().toString());
        return response;
    }

    private String readRequestBody(Request oriReq) {
        if (oriReq.body() == null) {
            return "";
        }
        Request request = oriReq.newBuilder().build();
        Buffer buffer = new Buffer();
        try {
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}