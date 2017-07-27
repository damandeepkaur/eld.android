package com.bsmwireless.data.network;

import android.os.Build;

import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.PreferencesManager;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.CONNECTION_TIMEOUT;
import static com.bsmwireless.common.Constants.READ_TIMEOUT;

public class HttpClientManager {
    private OkHttpClient mClient;

    public HttpClientManager(HttpLoggingInterceptor logger, TokenManager tokenManager, PreferencesManager preferencesManager, CookieJar cookieJar) {
        Interceptor auth = chain -> {
            Request request = chain.request();

            String accountName = preferencesManager.getAccountName();
            String driver = tokenManager.getDriver(accountName);
            String org = tokenManager.getOrg(accountName);
            String cluster = tokenManager.getCluster(accountName);
            String token = tokenManager.getToken(accountName);
            int boxId = preferencesManager.getBoxId();

            if (token != null && org != null && cluster != null && driver != null) {
                Request.Builder builder = request.newBuilder()
                        .header("X-Driver", driver)
                        .header("X-Org", org)
                        .header("X-Cluster", cluster)
                        .header("X-Token", token)
                        .header("X-Box", String.valueOf(boxId));
                request = builder.build();
            }

            return chain.proceed(request);
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(auth)
                .addInterceptor(logger)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .cookieJar(cookieJar);

        enableTLS(builder);

        mClient = builder.build();
    }

    public OkHttpClient getClient() {
        return mClient;
    }

    private X509TrustManager getDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            // The system has no TLS
            throw new AssertionError();
        }
    }

    private void enableTLS(OkHttpClient.Builder builder) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 20) {
            try {
                SSLContext sc = SSLContext.getInstance(TLS12SocketFactory.TLS_V12);
                sc.init(null, null, null);
                builder.sslSocketFactory(new TLS12SocketFactory(sc.getSocketFactory()), getDefaultTrustManager());

                ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(connectionSpec);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                builder.connectionSpecs(specs);
            } catch (Exception exc) {
                Timber.e(exc);
            }
        }
    }
}
