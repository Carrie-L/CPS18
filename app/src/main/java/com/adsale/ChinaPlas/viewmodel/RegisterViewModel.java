package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

import static com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H;


/**
 * todo webView无法点击
 * <p>
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
        mLanguage = App.mLanguage.get();
    }

    public void start(WebView wv, ImageView iv, ProgressBar pb) {
        mWebView = wv;
        mImageView = iv;
        mProgressBar = pb;
        isLoginOrReged.set(AppUtil.isLogin());
        if (isLoginOrReged.get()) {
            showPicView();
        } else {
            showWebView();
        }
    }

    private void showWebView() {
        String registerUrl = AppUtil.getName(mLanguage, NetWorkHelper.Register_TW_URL, NetWorkHelper.Register_EN_URL, NetWorkHelper.Register_CN_URL);

        mWebView.loadUrl(registerUrl);
        mWebView.setWebViewClient(new MyWebClient());

//        new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                mProgressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                /* https://www.chinaplasonline.com/CPS18/Mobile/Home/lang-simp/QuickPreRegPay.aspx?guid=94BA407AFE4B465AA5F67FF962501897&device=mobileapp&RegSource=MOB */
//                LogUtil.i(TAG, "shouldOverrideUrlLoading: URL=" + url);
//                if (url.contains("https://epayment.adsale-marketing.com.cn/vreg/PayMent/PayAPPjump")) {
//                    LogUtil.i(TAG, "shouldOverrideUrlLoading: PayAPPjump");
//                }
//
//                if (url.contains("QuickPreRegPay")) {
//                    LogUtil.i(TAG, "shouldOverrideUrlLoading: QuickPreRegPay");
//
//                }
//
////                document.getElementById('p_payMethod').value;//取出支付方式的js代码
////                document.getElementById('g_guid').value;取出guid的代码
////                document.getElementById('p_lang').value;语言
////                document.getElementById('p_image').value;确认信图片的名称
//
//                if (!url.contains("PayAPPjump")) {
//                    view.loadUrl(url);
//                }
//
//
//                return true;
//            }
//
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//                LogUtil.i(TAG, "onLoadResource:" + url);
//                if (url.contains("PayAPPjump")) {
//                    return;
//                }
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                LogUtil.i(TAG, "onPageFinished:" + url);
//                mProgressBar.setVisibility(View.GONE);
//
//                if (url.contains("https://epayment.adsale-marketing.com.cn/vreg/PayMent/PayAPPjump")) {
//                    view.addJavascriptInterface(new RegJSInterface(mContext), "Reg");
//                    view.loadUrl("javascript:function() { " +
//                            "var method = document.getElementById('p_payMethod').value; " +
//                            "var guid = document.getElementById('g_guid').value; " +
//                            "var lang = document.getElementById('p_lang').value; " +
//                            "var image = document.getElementById('p_image').value; " +
//                            "Reg.sendData(method,guid,lang,image); } function();");
//
//                    if (Build.VERSION.SDK_INT >= 19) {
//
////                        view.loadUrl("javascript: (function() {return document.getElementById('g_guid').value;}) ();" );
//                        view.evaluateJavascript("document.getElementById('g_guid').value;", new ValueCallback<String>() {
//                            @Override
//                            public void onReceiveValue(String value) {
//                                LogUtil.i(TAG, "onReceiveValue:" + value);
//                            }
//                        });
//                    }
//
//
////                    try {
////                        new Thread(){
////
////                        }.start();
////                        Document document = Jsoup.connect("https://www.chinaplasonline.com/CPS18/Mobile/Home/lang-simp/QuickPreRegPay.aspx?guid=CD63302175E84185A7AE81E9565354B2&device=mobileapp&RegSource=MOB").get();
////                        Element oElement = document.getElementById("'p_payMethod'");
////                        String data = oElement.data();
////                        LogUtil.i(TAG, "data=" + data);
////
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
//                }
//
//
//            }
//        };


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

    class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            super.shouldOverrideUrlLoading(view, url);
            LogUtil.i(TAG, "shouldOverrideUrlLoading:" + url);
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            LogUtil.i(TAG, "shouldInterceptRequest:" + url);

            if (url.equals("https://epayment.adsale-marketing.com.cn/vreg/PayMent/PayAPPjump")) {
                LogUtil.i(TAG, "shouldInterceptRequest: PayAPPjump " );
                if (Build.VERSION.SDK_INT >= 19) {

//                        view.loadUrl("javascript: (function() {return document.getElementById('g_guid').value;}) ();" );
                    view.evaluateJavascript("document.getElementById('p_payMethod').value;", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogUtil.i(TAG, "onReceiveValue:" + value);
                        }
                    });
                }
            }

            if (url.equals("https://epayment.adsale-marketing.com.cn/vreg/PayMent/PayAPPjump")) {

            }


            return super.shouldInterceptRequest(view, url);
        }
    }

    public class RegJSInterface {
        Context mContext;
        String data;

        RegJSInterface(Context ctx) {
            this.mContext = ctx;
        }

        @JavascriptInterface
        public void sendData(String method, String guid, String lang, String image) {
            //Get the string value to process
            this.data = method;

            LogUtil.e(TAG, "method=" + method);
            LogUtil.e(TAG, "guid=" + guid);
            LogUtil.e(TAG, "lang=" + lang);
            LogUtil.e(TAG, "image=" + image);

        }
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
                        final boolean isSaveSuccess = AppUtil.saveFileOutput(mContext, Constant.REG_PNG, bytes);
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
                        if (aBoolean) {
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

    private void showPicView() {
        try {
            FileInputStream fis = mContext.openFileInput(Constant.REG_PNG);
            mImageView.setImageBitmap(BitmapFactory.decodeStream(fis));
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            //如果图片不存在，下载 [登录时保存的regImageName]
        }
    }

    public void reset() {
        AppUtil.putLogout();
        isLoginOrReged.set(false);
        showWebView();
    }

    public void addToCalendar() {
        LogUtil.i(TAG, "addToCalendar");
    }


}
