package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * todo webView无法点击
 *
 * Created by Carrie on 2017/8/10.
 */

public class RegisterViewModel {
    private static final String TAG = "RegisterViewModel";
    private Context mContext;
    private WebView mWebView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public final ObservableBoolean isLoginOrReged = new ObservableBoolean(false);
    private final int mLanguage;


    public RegisterViewModel(Context mContext) {
        this.mContext = mContext;
        mLanguage = AppUtil.getCurLanguage();
    }

    public void start(WebView wv, ImageView iv,ProgressBar pb){
        mWebView=wv;
        mImageView=iv;
        mProgressBar=pb;
    }

    public void showWebView() {
        String registerUrl = AppUtil.getName(mLanguage, NetWorkHelper.Register_TW_URL, NetWorkHelper.Register_EN_URL, NetWorkHelper.Register_CN_URL);

        mWebView.loadUrl(registerUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                    mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("appsave")) {
                    LogUtil.i(TAG,"appsave: URL="+url);
                    downPic(url);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (mProgressBar.getVisibility() == View.GONE) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


    }

    private void downPic(String url) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(NetWorkHelper.BASE_URL_CPS)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(httpClient.build()).build();
        final LoginClient client = retrofit.create(LoginClient.class);
        client.downImg(url)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        //保存图片
                        byte[] bytes = responseBodyResponse.body().bytes();
                        final boolean isSaveSuccess = FileUtil.saveToMemory(App.memoryFileDir, "reg.png", bytes);
                        LogUtil.i(TAG, "isSaveSuccess=" + isSaveSuccess);
                        return isSaveSuccess;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        if(aBoolean){
                            showPicView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    public void showPicView() {
        isLoginOrReged.set(true);
        showPic();
    }

    private void showPic() {
        String imgPath = App.memoryFileDir+"/reg.png";
        if(!new File(imgPath).exists()){
            //如果图片不存在，下载 [登录时保存的regImageName]
        }

        mImageView.setImageURI(Uri.fromFile(new File(imgPath)));
    }

    public void reset() {
        AppUtil.putLogout();
        isLoginOrReged.set(false);
        showWebView();
    }

    public void addToCalendar(){
        LogUtil.i(TAG,"addToCalendar");
    }


}
