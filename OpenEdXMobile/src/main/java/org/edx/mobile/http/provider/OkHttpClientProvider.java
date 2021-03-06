package org.edx.mobile.http.provider;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.http.interceptor.AcceptLanguageHeaderInterceptor;
import org.edx.mobile.http.authenticator.OauthRefreshTokenAuthenticator;
import org.edx.mobile.http.interceptor.CustomCacheQueryInterceptor;
import org.edx.mobile.http.interceptor.NewVersionBroadcastInterceptor;
import org.edx.mobile.http.interceptor.NoCacheHeaderStrippingInterceptor;
import org.edx.mobile.http.interceptor.OauthHeaderRequestInterceptor;
import org.edx.mobile.http.interceptor.StaleIfErrorHandlingInterceptor;
import org.edx.mobile.http.interceptor.StaleIfErrorInterceptor;
import org.edx.mobile.http.interceptor.UserAgentInterceptor;
import org.edx.mobile.http.util.Tls12SocketFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public interface OkHttpClientProvider extends Provider<OkHttpClient> {
    @NonNull
    OkHttpClient get();

    @NonNull
    OkHttpClient getWithOfflineCache();

    @NonNull
    OkHttpClient getNonOAuthBased();


    @Singleton
    class Impl implements OkHttpClientProvider {
        private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB

        private static final int FLAG_IS_OAUTH_BASED = 1;
        private static final int USES_OFFLINE_CACHE = 1 << 1;

        final static int READ_TIMEOUT = 100;

        final static int CONNECT_TIMEOUT = 60;

        final static int WRITE_TIMEOUT = 60;

        @Inject
        private Context context;

        private final OkHttpClient[] clients = new OkHttpClient[1 << 2];

        @NonNull
        @Override
        public OkHttpClient get() {
            return get(true, false);
        }

        @NonNull
        @Override
        public OkHttpClient getWithOfflineCache() {
            return get(true, true);
        }

        @NonNull
        @Override
        public OkHttpClient getNonOAuthBased() {
            return get(false, false);
        }

        @NonNull
        private synchronized OkHttpClient get(boolean isOAuthBased, boolean usesOfflineCache) {
            final int index = (isOAuthBased ? FLAG_IS_OAUTH_BASED : 0) |
                    (usesOfflineCache ? USES_OFFLINE_CACHE : 0);
            OkHttpClient client = clients[index];
            if (client == null) {
                final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                //读取超时
                builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
                //连接超时
                builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
                //写入超时
                builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
                //自定义连接池最大空闲连接数和等待时间大小，否则默认最大5个空闲连接
                builder.connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES));

                List<Interceptor> interceptors = builder.interceptors();
                if (usesOfflineCache) {
                    final File cacheDirectory = new File(context.getFilesDir(), "http-cache");
                    if (!cacheDirectory.exists()) {
                        cacheDirectory.mkdirs();
                    }
                    final Cache cache = new Cache(cacheDirectory, cacheSize);
                    builder.cache(cache);
                    interceptors.add(new StaleIfErrorInterceptor());
                    interceptors.add(new StaleIfErrorHandlingInterceptor());
                    interceptors.add(new CustomCacheQueryInterceptor(context));
                    builder.networkInterceptors().add(new NoCacheHeaderStrippingInterceptor());
                }
                interceptors.add(new UserAgentInterceptor(
                        System.getProperty("http.agent") + " " +
                                context.getString(R.string.user_agent_app_name) + "/" +
                                BuildConfig.APPLICATION_ID + "/" +
                                BuildConfig.VERSION_NAME));
                if (isOAuthBased) {
                    interceptors.add(new OauthHeaderRequestInterceptor(context));
                }
                interceptors.add(new NewVersionBroadcastInterceptor());
                interceptors.add(new AcceptLanguageHeaderInterceptor());
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    interceptors.add(loggingInterceptor);
                }
                builder.authenticator(new OauthRefreshTokenAuthenticator(context));
                // Enable TLS 1.2 support
                client = Tls12SocketFactory.enableTls12OnPreLollipop(builder).build();
                clients[index] = client;
            }
            return client;
        }
    }
}
