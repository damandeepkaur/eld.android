package com.bsmwireless.data.network;

import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.PreferencesManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
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

public final class HttpClientManager {

    private OkHttpClient mClient;

    public HttpClientManager(HttpLoggingInterceptor logger, TokenManager tokenManager,
                             PreferencesManager preferencesManager, CookieJar cookieJar,
                             AccountManager accountManager, InputStream certificate) {
        Interceptor auth = chain -> {
            Request request = chain.request();

            String driver = request.header(Config.HEADER_DRIVER);
            String token = request.header(Config.HEADER_TOKEN);
            String cluster = request.header(Config.HEADER_CLUSTER);
            String org = request.header(Config.HEADER_ORG);

            String accountName = accountManager.getCurrentUserAccountName();

            org = org == null ? tokenManager.getOrg(accountName) : org;
            cluster = cluster == null ? tokenManager.getCluster(accountName) : cluster;
            driver = driver == null ? tokenManager.getDriver(accountName) : driver;
            token = token == null ? tokenManager.getToken(accountName) : token;

            int boxId = preferencesManager.getBoxId();

            if (token != null && org != null && cluster != null && driver != null) {
                Request.Builder builder = request.newBuilder()
                        .header(Config.HEADER_DRIVER, driver)
                        .header(Config.HEADER_ORG, org)
                        .header(Config.HEADER_CLUSTER, cluster)
                        .header(Config.HEADER_TOKEN, token)
                        .header(Config.HEADER_BOX, String.valueOf(boxId));
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

        configureTLS(builder, certificate);
        mClient = builder.build();
    }

    private Certificate configureCertificate(InputStream certificate) {
        Certificate cert = null;
        InputStream certBufferStream = null;
        FileInputStream certStream = null;
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance(Config.CERTIFICATE_TYPE);
            certBufferStream = new BufferedInputStream(certificate);
            cert = certFactory.generateCertificate(certBufferStream);
        } catch (CertificateException e) {
            Timber.e(e);
        } finally {
            if (certBufferStream != null) {
                try {
                    certBufferStream.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
            if (certStream != null) {
                try {
                    certStream.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
        return cert;
    }

    private KeyStore configureKeystore(Certificate cert) {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cert);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            Timber.e(e);
        }
        return keyStore;
    }

    private TrustManager[] configureTrustManagers(KeyStore keyStore) {
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf;
        TrustManager[] trustManagers = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            trustManagers = tmf.getTrustManagers();
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            Timber.e(e);
        }
        return trustManagers;
    }

    private SSLContext configureContext(TrustManager[] trustManagers) {
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Timber.e(e);
        }
        return context;
    }

    private boolean configureTLS(OkHttpClient.Builder builder, InputStream certificate) {
        Certificate cert = configureCertificate(certificate);
        if (cert == null) {
            return false;
        }

        KeyStore keyStore = configureKeystore(cert);
        if (keyStore == null) {
            return false;
        }

        TrustManager[] trustManagers = configureTrustManagers(keyStore);
        if (trustManagers == null || trustManagers.length == 0) {
            return false;
        }

        SSLContext sslContext = configureContext(trustManagers);
        if (sslContext == null) {
            return false;
        }

        if (!(trustManagers[0] instanceof X509TrustManager)) {
            return false;
        }

        builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
        ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .build();

        List<ConnectionSpec> specs = new ArrayList<>();
        specs.add(connectionSpec);
        specs.add(ConnectionSpec.COMPATIBLE_TLS);
        specs.add(ConnectionSpec.CLEARTEXT);

        builder.connectionSpecs(specs);
        return true;
    }

    public OkHttpClient getClient() {
        return mClient;
    }

    private static final class Config {

        private static final String HEADER_DRIVER = "X-Driver";
        private static final String HEADER_TOKEN = "X-Token";
        private static final String HEADER_CLUSTER = "X-Cluster";
        private static final String HEADER_ORG = "X-Org";
        private static final String HEADER_BOX = "X-Box";

        private static final String CERTIFICATE_TYPE = "X.509";
        private static final String CERTIFICATE_KEYSTORE_PATH = "bsm_keystore.crt";
    }
}
