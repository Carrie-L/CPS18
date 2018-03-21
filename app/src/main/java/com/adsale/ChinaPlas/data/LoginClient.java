package com.adsale.ChinaPlas.data;


import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Carrie on 2017/8/9.
 */

public interface LoginClient {
    @POST("Membership/{lang}/MyExhibitor.aspx?istest=321")
    Observable<Response<ResponseBody>> loginRx(@Path("lang") String lang, @Body RequestBody body);

    @POST(NetWorkHelper.REGISTER_EMAIL_DATA)
    Observable<EmailVisitorData> regGetData(@Body RequestBody body);

    @GET
    Observable<Response<ResponseBody>> downImg(@Url String url);

    @POST("info/CartSync.aspx?vf=581")
    Observable<Response<ResponseBody>> sync(@Body RequestBody body);
}
