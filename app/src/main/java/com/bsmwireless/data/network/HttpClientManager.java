package com.bsmwireless.data.network;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.bsmwireless.common.Constants.CONNECTION_TIMEOUT;
import static com.bsmwireless.common.Constants.READ_TIMEOUT;

public class HttpClientManager {
    private String mDriverId;
    private String mOrg;
    private String mCluster;
    private String mToken;

    private OkHttpClient mClient;

    public HttpClientManager(HttpLoggingInterceptor logger, Cache cache, CookieJar cookieJar) {
        Interceptor auth = chain -> {
            Request request = chain.request();

            if (mToken != null && mOrg != null && mCluster != null && mDriverId != null) {
                request = request.newBuilder()
                        .header("X-Driver", mDriverId)
                        .header("X-Org", mOrg)
                        .header("X-Cluster", mCluster)
                        .header("X-Token", mToken)
                        .build();
            }

            return chain.proceed(request);
        };

        mClient = new OkHttpClient.Builder()
                .addInterceptor(auth)
                .addInterceptor(logger)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .cache(cache)
                .build();
    }

    public void setHeaders(String driverId, String org, String cluster, String token) {
        mDriverId = driverId;
        mOrg = org;
        mCluster = cluster;
        mToken = token;
    }

    public OkHttpClient getClient() {
        return mClient;
    }
}
