package com.adsale.ChinaPlas.data;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Carrie on 2017/8/9.
 */

public interface GitHubClient {

    @POST("Membership/{lang}/MyExhibitor.aspx?istest=321")
    Call<String> preLogin(@Path("lang") String lang, @Body FormBody body);


}
