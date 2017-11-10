package com.adsale.ChinaPlas.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.databinding.ActivityWebViewBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.File;

/**
 * Created by new on 2016/10/13.
 * <p/>
 * <p>只顯示本地html，不縮放</>
 * <p>WebUrl|MainIcon</p>
 * title/WebUrl/baiduTJ
 */
public class WebViewActivity extends BaseActivity {
    private final String TAG = "WebViewActivity";
    private String oDeviceType;
    private WebView webView;
    private WebSettings settings;
    private ProgressBar progressBar;
    private RelativeLayout rl;
    private Intent gIntent;
    private String mWebUrl;
    protected ImageView back;
    protected ImageView go;
    protected ImageView out;
    private ActivityWebViewBinding binding;

    @Override
    protected void initView() {
        binding = ActivityWebViewBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();

        gIntent = getIntent();
        barTitle.set(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initData() {
        mWebUrl = gIntent.getStringExtra(Constant.WEB_URL);
        initWebView();
    }

    private void initWebView() {
        rl=binding.rlWebcontent;
        progressBar=binding.progressBar1;
        webView=new WebView(getApplicationContext());
        rl.addView(webView);
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); //如果访问的页面中有Javascript，则WebView必须设置支持Javascript
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true); //支持缩放
        settings.setBuiltInZoomControls(true); //支持手势缩放
        settings.setDisplayZoomControls(false); //是否显示缩放按钮

        // >= 19(SDK4.4)启动硬件加速，否则启动软件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            settings.setLoadsImagesAutomatically(false);
        }

        settings.setUseWideViewPort(true); //将图片调整到适合WebView的大小
        settings.setLoadWithOverviewMode(true); //自适应屏幕
        settings.setDomStorageEnabled(true);
        settings.setSaveFormData(true);
        settings.setSupportMultipleWindows(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT); //优先使用缓存

        webView.setHorizontalScrollbarOverlay(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER); // 取消WebView中滚动或拖动到顶部、底部时的阴影
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // 取消滚动条白边效果
        webView.requestFocus();

        webView.loadUrl(mWebUrl);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setLayerType(View.LAYER_TYPE_NONE, null);
                progressBar.setVisibility(View.GONE);
                if (!settings.getLoadsImagesAutomatically()) {
                    settings.setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (progressBar.getVisibility() == View.GONE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

    }


    @Override
    public void onDestroy() {
        rl.removeAllViews();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        rl = null;
        super.onDestroy();
    }

    @Override
    public void back() {
//        if(gIntent.getStringExtra("Type")!=null&&gIntent.getStringExtra("Type").equals(Constant.PUSH_INTENT)){
//            Intent intent=new Intent(getApplicationContext(),MenuActivity.class);
//            startActivity(intent);
//            SystemMethod.animBack(this);
//        }
        super.back();
    }

    public void onBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    public void onGo() {
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    public void onOut() {
        LogUtil.i(TAG, "mWebUrl=" + mWebUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebUrl));
        startActivity(intent);
    }
}
