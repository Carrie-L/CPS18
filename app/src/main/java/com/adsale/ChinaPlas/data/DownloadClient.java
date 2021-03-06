package com.adsale.ChinaPlas.data;

import android.os.Environment;

import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Carrie on 2017/9/21.
 */

public interface DownloadClient {

    @GET
    Observable<ResponseBody> download(@Url String url);


    @GET("{fileName}")
    Observable<Response<ResponseBody>> downloadFile(@Path("fileName") String fileName);

    @GET(NetWorkHelper.DOWN_WEBCONTENT_URL)
    Observable<Response<ResponseBody>> downWebContent(@Path("fileName") String fileName);

    @GET(NetWorkHelper.DOWN_EVENT_URL)
    Observable<ResponseBody> downloadEventPage(@Path("fileName") String fileName);

    @GET(NetWorkHelper.DOWN_MAP_URL)
    Observable<ResponseBody> downloadMap(@Path("fileName") String fileName);


    /*  ``````````````````````````````  内容更新中心  `````````````````````````````````````  */
    @GET
    Observable<Response<ResponseBody>> downExhibitorCSVs(@Url String url);

    @GET
    Observable<Response<ResponseBody>> downUrls(@Url String url);

    /**
     * 断点下载接口 breakpoint download
     */
    @Streaming  /* 大文件需要加入这个判断，防止下载过程中写入到内存中 */
    @GET
    Observable<Response<ResponseBody>> bpDownload(@Header("RANGE") String start, @Url String url);

    @Streaming  /* 大文件需要加入这个判断，防止下载过程中写入到内存中 */
    @GET
    Observable<Response<ResponseBody>> largeDownload(@Url String url);

    @Streaming  /* 大文件需要加入这个判断，防止下载过程中写入到内存中 */
    @GET
    Observable<ResponseBody> largeDownload2(@Url String url);

//    @Streaming  /* 大文件需要加入这个判断，防止下载过程中写入到内存中 */

     /*  ``````````````````````````````  订阅电子快讯  `````````````````````````````````````  */

    /**
     * in SubscribeActivity
     *
     * @param lang eng||trad||simp
     */
    @POST(NetWorkHelper.Subscribe_LAST_URL)
    Observable<Response<ResponseBody>> onSubscribe(@Query("lang") String lang, @Body RequestBody requestBody);


    /*  ``````````````````````````````  预登记  `````````````````````````````````````  */
    @POST(NetWorkHelper.REGISTER_CHARGE)
    Observable<ResponseBody> getRegCharge(@Body RequestBody body);

    @GET
    Observable<ResponseBody> downConfirmImg(@Url String url);

    @POST(NetWorkHelper.REGISTER_CONFIRM_PAY)
    Observable<ResponseBody> confirmPay(@Body RequestBody body);

    @POST(NetWorkHelper.REGISTER_EMAIL_DATA)
    Observable<EmailVisitorData> regGetData(@Body RequestBody body);

 /*  ``````````````````````````````    `````````````````````````````````````  */

}
