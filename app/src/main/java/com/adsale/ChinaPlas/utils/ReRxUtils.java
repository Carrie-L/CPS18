package com.adsale.ChinaPlas.utils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.helper.ProgressCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Carrie on 2017/9/21.
 */

public class ReRxUtils {

    public static <T> T setupRxtrofit(Class<T> cls, String baseUrl) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(App.mOkHttpClient).build();
        return retrofit.create(cls);
    }

    public static <T,E> T setupRxtrofitProgress(Class<T> cls, String baseUrl, ProgressCallback callback,E entity,long length) {
        Gson gson = new GsonBuilder().setLenient().create();
        DownloadProgressInterceptor<E> interceptor = new DownloadProgressInterceptor<>(callback,entity,length);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(cls);
    }


    public static <T,E> T setupRxtrofitProgress(Class<T> cls, String baseUrl, ProgressCallback callback,E entity) {
        Gson gson = new GsonBuilder().setLenient().create();
        DownloadProgressInterceptor<E> interceptor = new DownloadProgressInterceptor<>(callback,entity);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(cls);
    }
}
