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
 * Created by Carrie on 2017/9/21.
 */

public interface DownloadClient {


    @GET("{fileName}")
    Observable<Response<ResponseBody>> downloadFile(@Path("fileName") String fileName);


    /*  ``````````````````````````````  内容更新中心  `````````````````````````````````````  */
    @GET
    Observable<Response<ResponseBody>> downExhibitorCSVs(@Url String url);

}
