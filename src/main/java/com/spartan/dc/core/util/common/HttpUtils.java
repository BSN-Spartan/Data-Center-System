package com.spartan.dc.core.util.common;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author rjx
 * @date 2022/6/16 14:19
 */
public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);


    private static final int TIME_OUT = 5000;


    private static final String HTTPS = "https";

    /**
     * Content-Type
     */
    private static final String CONTENT_TYPE = "Content-Type";


    private static final String FORM_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";


    private static final String JSON_TYPE = "application/json;charset=UTF-8";


    public static Response get(String url) {
        return get(url, null);
    }


    public static Response get(String url, Map<String, String> headers) {
        if (null == url || url.isEmpty()) {
            throw new RuntimeException("The request URL is blank");
        }


        if (url.startsWith(HTTPS)) {
            getTrust();
        }
        Connection connection = Jsoup.connect(url);
        connection.method(Method.GET);
        connection.timeout(TIME_OUT);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        connection.maxBodySize(0);

        if (null != headers) {
            connection.headers(headers);
        }

        Response response = null;
        try {
            response = connection.execute();
        } catch (IOException e) {
            log.error("Failed to execute a GET request. Procedure，url: {}", url, e);
        }
        return response;
    }


    public static Response post(String url, String params) {
        return doPostRequest(url, null, params);
    }


    public static Response post(String url, Map<String, String> headers, String params) {
        return doPostRequest(url, headers, params);
    }


    public static Response post(String url, Map<String, String> paramMap) {
        return doPostRequest(url, null, paramMap, null);
    }


    public static Response post(Map<String, String> headers, String url, Map<String, String> paramMap) {
        return doPostRequest(url, headers, paramMap, null);
    }


    public static Response post(String url, Map<String, String> paramMap, Map<String, File> fileMap) {
        return doPostRequest(url, null, paramMap, fileMap);
    }


    public static Response post(String url, Map<String, String> headers, Map<String, String> paramMap, Map<String, File> fileMap) {
        return doPostRequest(url, headers, paramMap, fileMap);
    }


    private static Response doPostRequest(String url, Map<String, String> headers, String jsonParams) {
        if (null == url || url.isEmpty()) {
            throw new RuntimeException("The request URL is blank");
        }


        if (url.startsWith(HTTPS)) {
            getTrust();
        }

        Connection connection = Jsoup.connect(url);
        connection.method(Method.POST);
        connection.timeout(TIME_OUT);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        connection.maxBodySize(0);

        if (null != headers) {
            connection.headers(headers);
        }

        connection.header(CONTENT_TYPE, JSON_TYPE);
        connection.requestBody(jsonParams);

        Response response = null;
        try {
            response = connection.execute();
        } catch (IOException e) {
            log.error("Failed to execute POST request, URL: {}, parameter: {}", url, jsonParams, e);
        }
        return response;
    }


    private static Response doPostRequest(String url, Map<String, String> headers, Map<String, String> paramMap, Map<String, File> fileMap) {
        if (null == url || url.isEmpty()) {
            throw new RuntimeException("The request URL is blank");
        }


        if (url.startsWith(HTTPS)) {
            getTrust();
        }

        Connection connection = Jsoup.connect(url);
        connection.method(Method.POST);
        connection.timeout(TIME_OUT);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        connection.maxBodySize(0);

        if (null != headers) {
            connection.headers(headers);
        }


        List<InputStream> inputStreamList = null;
        Response response = null;
        try {


            if (null != fileMap && !fileMap.isEmpty()) {
                inputStreamList = new ArrayList<>();
                InputStream in = null;
                File file = null;
                for (Entry<String, File> e : fileMap.entrySet()) {
                    file = e.getValue();
                    in = new FileInputStream(file);
                    inputStreamList.add(in);
                    connection.data(e.getKey(), file.getName(), in);
                }
            } else {

                connection.header(CONTENT_TYPE, FORM_TYPE);
            }


            if (null != paramMap && !paramMap.isEmpty()) {
                connection.data(paramMap);
            }

            response = connection.execute();
        } catch (Exception e) {
            log.error("Failed to execute POST request, URL: {}, parameter: {}", url, paramMap.toString(), e);
        }


        finally {
            closeStream(inputStreamList);
        }

        return response;
    }

    /**
     *
     *
     * @param streamList
     */
    private static void closeStream(List<? extends Closeable> streamList) {
        if (null != streamList) {
            for (Closeable stream : streamList) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void getTrust() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            log.error("Get the server trust exception", e);
        }
    }


}