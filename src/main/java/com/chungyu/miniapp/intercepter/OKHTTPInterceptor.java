package com.chungyu.miniapp.intercepter;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @Author Vincentxin
 * @Date 2019/10/24
 */
public class OKHTTPInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request().newBuilder()

                .addHeader("Connection","close").build();

        return chain.proceed(request);
    }
}
