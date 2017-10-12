package com.bsmwireless.common.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.network.converters.LatLngFlagConverter;
import com.bsmwireless.data.network.converters.MalfunctionConverter;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import app.bsmuniversal.com.BuildConfig;
import app.bsmuniversal.com.R;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

@Module
public final class NetworkModule {

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ELDEvent.LatLngFlag.class, new LatLngFlagConverter())
                .registerTypeAdapter(Malfunction.class, new MalfunctionConverter())
                .create();
    }

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
    HttpClientManager provideHttpClientManager(HttpLoggingInterceptor logger, TokenManager tokenManager,
                                               PreferencesManager preferencesManager, CookieJar cookieJar,
                                               AccountManager accountManager, Context context) {
        InputStream inStream = context.getResources().openRawResource(R.raw.bsm_keystore);
        return new HttpClientManager(logger, tokenManager, preferencesManager, cookieJar, accountManager, inStream);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(HttpClientManager clientManager, Gson gson) {
        return new Retrofit.Builder()
                .client(clientManager.getClient())
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
