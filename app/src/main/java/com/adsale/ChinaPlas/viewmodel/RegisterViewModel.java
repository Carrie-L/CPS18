package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import io.reactivex.Observable;

import static com.adsale.ChinaPlas.R.id.rl_registed;

/**
 * Created by Carrie on 2017/8/10.
 */

public class RegisterViewModel {
    private static final String TAG="RegisterViewModel";
    private Context mContext;

    public final ObservableBoolean isLoginOrReged = new ObservableBoolean(false);
    private final int mLanguage;


    public RegisterViewModel(Context mContext) {
        this.mContext = mContext;
        mLanguage = AppUtil.getCurLanguage(mContext);
    }

    public void showWebView(WebView webView){
        String registerUrl = AppUtil.getName(mLanguage, NetWorkHelper.Register_TW_URL, NetWorkHelper.Register_EN_URL, NetWorkHelper.Register_CN_URL);;
        webView.loadUrl(registerUrl);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {


                return true;
            }



        });



    }

    public void showPicView(){

    }

    public void showPic(){

    }

    public void reset(){
        AppUtil.putLogout();
        showWebView();
    }



}
