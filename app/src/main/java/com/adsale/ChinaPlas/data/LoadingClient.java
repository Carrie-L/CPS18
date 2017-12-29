package com.adsale.ChinaPlas.data;


import com.adsale.ChinaPlas.utils.NetWorkHelper;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Carrie on 2017/9/8.
 * Loading加载页的 Retrofit + RxJava
 */

public interface LoadingClient {

    @Headers(NetWorkHelper.HEADER_SOAP_ACTION_SCANFILES)
    @POST(NetWorkHelper.GetFolderScanFilesJson)
    Observable<Response<ResponseBody>> getScanFile(@Body RequestBody requestBody);

    @Headers(NetWorkHelper.HEADER_SOAP_ACTION_MASTER)
    @POST(NetWorkHelper.GetMasterV2)
    Observable<Response<ResponseBody>> getMaster(@Body RequestBody requestBody);

    //public static final String BASE_DOWN_URL = "https://eform.adsale.com.hk/AppCPS17/AppFiles/";
    @GET(NetWorkHelper.DOWN_TXT_URL)
    Observable<Response<ResponseBody>> downTxt(@Path("fileName") String fileName);

    @GET(NetWorkHelper.DOWN_WEBCONTENT_URL)
    Observable<Response<ResponseBody>> downWebContent(@Path("fileName") String fileName);

    @GET
    Observable<Response<ResponseBody>> downIcons(@Url String url);

    @GET
    Observable<ResponseBody> download(@Url String url);


}
