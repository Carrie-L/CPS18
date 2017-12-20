package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.utils.NetWorkHelper;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Carrie on 2017/9/21.
 *
 */

public interface DownloadClient {


    @GET("{fileName}")
    Observable<Response<ResponseBody>> downloadFile(@Path("fileName") String fileName);


    /*  ``````````````````````````````  内容更新中心  `````````````````````````````````````  */
    @GET
    Observable<Response<ResponseBody>> downExhibitorCSVs(@Url String url);

    @GET
    Observable<Response<ResponseBody>> downUrls(@Url String url);


     /*  ``````````````````````````````  订阅电子快讯  `````````````````````````````````````  */
    /**
     * in SubscribeActivity
     * @param lang eng||trad||simp
     */
    @POST(NetWorkHelper.Subscribe_LAST_URL)
    Observable<Response<ResponseBody>> onSubscribe(@Query("lang") String lang, @Body RequestBody requestBody);


    /*  ``````````````````````````````  预登记  `````````````````````````````````````  */
    @POST(NetWorkHelper.REGISTER_CHARGE)
    Observable<ResponseBody> getRegCharge(@Body RequestBody body);

    @GET
    Observable<ResponseBody> downConfirmImg(@Url String url);

 /*  ``````````````````````````````    `````````````````````````````````````  */

}
