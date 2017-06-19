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
    private String mId;
    private String mDomain;
    private String mToken;

    private OkHttpClient mClient;

    public HttpClientManager(HttpLoggingInterceptor logger, Cache cache, CookieJar cookieJar) {
        Interceptor auth = chain -> {
            Request request = chain.request();

            if (mToken != null && mDomain != null && mId != null) {
                request = request.newBuilder()
                        .header("X-Domain", mDomain)
                        .header("X-DriveId", mId)
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

    public void setHeaders(String id, String domain, String token) {
        mId = id;
        mDomain = domain;
        mToken = token;
    }

    public OkHttpClient getClient() {
        return mClient;
    }
}
