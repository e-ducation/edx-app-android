package org.edx.mobile.http.interceptor;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AcceptLanguageHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header("Accept-Language", Locale.getDefault().getLanguage())
                .build());
    }

}
