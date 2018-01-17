package com.adsale.ChinaPlas.utils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.helper.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Carrie on 2018/1/15.
 */

public class DownloadProgressInterceptor<T> implements Interceptor {

    private T entity;
    private ProgressCallback callback;

    public DownloadProgressInterceptor(ProgressCallback callback, T t) {
        this.callback = callback;
        entity = t;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // if you want to add headers
//        Request original = chain.request();
//        Request.Builder requestBuilder = original.newBuilder()
//                .header("Range","bytes=10000000-21984951");
//        Request request = requestBuilder.build();

        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), callback, entity))
                .build();
    }
}
