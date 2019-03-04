package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityWebViewBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

/**
 * Created by new on 2016/10/13.
 * <p/>
 * <p>只顯示本地html，不縮放</>
 * <p>Url(Constant.WEB_URL)|MainIcon</p>
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

    public final ObservableBoolean isBarShow = new ObservableBoolean(true);

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
        mWebUrl = addPrefix(mWebUrl);
        initWebView();
    }

    private String addPrefix(String url) {
        if (url != null && url.toLowerCase().startsWith("www")) {
            url = "http://".concat(url);
        }
        return url;
    }

    private void initWebView() {
        rl = binding.rlWebcontent;
        progressBar = binding.progressBar1;
        webView = binding.webView;
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
                url = addPrefix(url);
                view.loadUrl(url);

                LogUtil.i(TAG, "shouldOverrideUrlLoading: URL = " + url);


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

                if (url.contains("mychinaplas")) {
                    LogUtil.i(TAG, "url = " + url);
                    view.evaluateJavascript("document.getElementById('MID').value", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogUtil.e(TAG, "shouldOverrideUrlLoading_onReceiveValue:MID=" + value + "," + value.length());
                            LogUtil.i(TAG, " login success? " + !TextUtils.isEmpty(value));
                        }
                    });
                    view.evaluateJavascript("document.getElementById('AppToken').value", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            LogUtil.e(TAG, "shouldOverrideUrlLoading_onReceiveValue:AppToken=" + value);
                            if (!TextUtils.isEmpty(value) && !value.equals("null")) {
                                AppUtil.setLogin(true);
                                LogUtil.i(TAG, "登录成功");

                                // 如果是从主界面MyChinaplas点击跳转而来，则关闭这个页面，显示用户小工具；
                                if (getIntent().getBooleanExtra("MyTool", false)) {
                                    Intent intent = new Intent(WebViewActivity.this, UserInfoActivity.class);
                                    startActivity(intent);
                                    overridePendingTransPad();
                                    finish();
                                }else if(getIntent().getBooleanExtra("ExhibitorInfo", false)){
                                    overridePendingTransPad();
                                    finish();
                                }

                            } else {
                                AppUtil.setLogin(false);
                                LogUtil.i(TAG, "未登录");
                            }
                        }
                    });

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
        overridePendingTransPad();
    }

    @Override
    public void back() {
        LogUtil.i(TAG, "back()");

        if (getIntent().getBooleanExtra("MyCPS", false) && !AppUtil.isLogin()) {
            Intent intent = new Intent(this, AppUtil.isPadDevice(getApplicationContext()) ? PadMainActivity.class : MainActivity.class);
            startActivity(intent);
            overridePendingTransPad();
        }

        super.back();
        LogUtil.i(TAG, "back1()");
    }

    @Override
    public void onBackPressed() {
        LogUtil.i(TAG, "onBackPressed()");
        super.onBackPressed();
        LogUtil.i(TAG, "onBackPressed1()");
    }
}
