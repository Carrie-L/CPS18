package com.adsale.ChinaPlas.utils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

}
