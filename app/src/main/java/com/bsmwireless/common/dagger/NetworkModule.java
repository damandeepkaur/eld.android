package com.bsmwireless.common.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.ErrorHandlingFactory;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import app.bsmuniversal.com.BuildConfig;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

@Module
public class NetworkModule {
    @Singleton
    @Provides
    HttpLoggingInterceptor provideInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(BuildConfig.USE_LOG ? BODY : NONE);
        return httpLoggingInterceptor;
    }

    @Singleton
    @Provides
    CookieJar provideCookieJar() {
        return new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<>();
            }
        };
    }

    @Singleton
    @Provides
    HttpClientManager provideHttpClientManager(HttpLoggingInterceptor logger, TokenManager tokenManager, PreferencesManager preferencesManager, CookieJar cookieJar) {
        return new HttpClientManager(logger, tokenManager, preferencesManager, cookieJar);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(HttpClientManager clientManager) {
        return new Retrofit.Builder()
                .client(clientManager.getClient())
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(ErrorHandlingFactory.create())
                .build();
    }

    @Provides
    @Singleton
    ServiceApi provideServiceApi(Retrofit retrofit) {
        return retrofit.create(ServiceApi.class);
    }

    @Provides
    @Singleton
    public NtpClientManager provideNtpClientManager() {
        return new NtpClientManager();
    }
}