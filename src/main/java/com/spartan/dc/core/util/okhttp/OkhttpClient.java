package com.spartan.dc.core.util.okhttp;

import okhttp3.*;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class OkhttpClient {

    /**
     * Original OkHttpClient, globally kept unique, thus ensuring performance overhead
     */
    private OkHttpClient mOkHttpClient = null;

    /**
     * Configuration options for httpClient {@link HttpClientConfig}
     */
    private HttpClientConfig config;

    /**
     * Construct method
     */
    public OkhttpClient() {
        this(new HttpClientConfig());
    }

    /**
     * Construct method
     *
     * @param config {@link HttpClientConfig}
     */
    public OkhttpClient(HttpClientConfig config) {
        if (config == null) {
            config = new HttpClientConfig();
        }
        this.setConfig(config);
    }

    public void setConfig(HttpClientConfig config) {
        if (config == null) {
            return;
        }
        this.config = config;
        resetHttpClient();
    }

    public HttpClientConfig getConfig() {
        return config;
    }

    /**
     * Reconfiguration HttpClient
     *
     */
    public synchronized void resetHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // Set timeout connection time
                .connectTimeout(config.getTimeoutConnect(), TimeUnit.MILLISECONDS)
                // Set write timeout time
                .writeTimeout(config.getTimeoutWrite(), TimeUnit.MILLISECONDS)
                // Set read timeout time
                .readTimeout(config.getTimeoutRead(), TimeUnit.MILLISECONDS)
                // Connection pool
                .connectionPool(new ConnectionPool(
                        config.getMaxIdleConnections(),
                        config.getKeepAliveDuration(),
                        config.getKeepAliveTimeUnit()));
        if (config.getInterceptors() != null) {
            for (Interceptor interceptor : config.getInterceptors()) {
                if (interceptor == null) {
                    continue;
                }
                builder.addInterceptor(interceptor);
            }
        }
        if (config.getNetworkInterceptors() != null) {
            for (Interceptor interceptor : config.getNetworkInterceptors()) {
                if (interceptor == null) {
                    continue;
                }
                builder.addNetworkInterceptor(interceptor);
            }
        }
        if (config.getExecutorService() != null) {
            builder.dispatcher(new Dispatcher(config.getExecutorService()));
        }


        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        // Support HTTPS requests, skip certificate validation
        builder.sslSocketFactory(createSSLSocketFactory(trustAllCerts), (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        mOkHttpClient = builder.build();
    }

    /**
     * Generate secure socket factory for certificate skipping for https requests
     *
     * @return SSLSocketFactory
     */
    private SSLSocketFactory createSSLSocketFactory(TrustManager[] trustManagers) {
        SSLSocketFactory ssfFactory = null;
        try {
            //"SSLv2Hello", "SSLv3", "TLSv1"
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagers, new SecureRandom());
            ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }


    public synchronized OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public synchronized Call newCall(Request request){
        return mOkHttpClient.newCall(request);
    }
}