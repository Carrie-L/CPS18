package com.adsale.ChinaPlas.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.File;

/**
 * must intent data: [Constant.WEB_URL]
 * [Url] : e.g.:【"ConcurrentEvent/001"】,内存中的文件夹名称需与asset目录下的一致，最后不需要[/]
 */
public class WebContentActivity extends BaseActivity {

    private WebView webView;
    private String mIntentUrl;
    private Intent mIntent;
    private WebSettings settings;

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_web_content, mBaseFrameLayout, true);
        webView = findViewById(R.id.web_content_view);
        TAG = "WebContentActivity";

        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void initData() {
        final Intent intent = getIntent();
        mIntentUrl = intent.getStringExtra("Url");
        LogUtil.i(TAG, "mIntentUrl=" + mIntentUrl);

        if ((mIntentUrl.toLowerCase().startsWith("http") && !checkImageUrl(mIntentUrl)) || mIntentUrl.toLowerCase().startsWith("web:")) {
            loadWebUrl();
        } else {
            loadLocalHtml(getHtmName());
        }

        setWebViewClient();

    }

    private boolean checkImageUrl(String url) {
        return url.endsWith("jpg") || url.endsWith("png");
    }

    private void loadLocalHtml(String htmlName) {
        StringBuilder sb = new StringBuilder();
        if (new File(App.rootDir.concat(mIntentUrl)).exists()) {
            sb.append("file://").append(App.rootDir).append(mIntentUrl).append("/").append(htmlName);
        } else {
            sb.append("file:///android_asset/").append(mIntentUrl).append("/").append(htmlName);
        }
        webView.loadUrl(sb.toString());
        LogUtil.i(TAG, "loadLocalHtml= " + sb.toString());

        if(mIntentUrl.contains("FloorPlan")){ //平面总览图可以缩放
            settings.setSupportZoom(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
        }
    }

    private void loadWebUrl() {
        LogUtil.i(TAG, "-- loadWebUrl --");
        webView.loadUrl(mIntentUrl);
    }

    private String getHtmName() {
        return AppUtil.getName("TC.htm", "EN.htm", "SC.htm");
    }

    private String mUrl;

    private void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mUrl = url;
                LogUtil.i(TAG, "shouldOverrideUrlLoading: url= " + url);

                if (checkImageUrl(url)) {
                    toImageAty();
                } else if (startsWith("prereg://")) {
                    toRegisterAty();
                } else if (startsWith("web")) {
                    web();
                    return true;
                } else if (startsWith("http")) {
                    http();
                } else if (startsWith("mailto")) {
                    mailTo();
                    return true;
                } else if (startsWith("tel:")) {
                    callPhone();
                    return true;
                } else if (url.startsWith("floor://")) { // 平面图总览 —— 点击楼层，显示相应html
                    loadLocalHtml(url.replace("floor://", ""));
                    return true;
                } else if (url.startsWith("hall://")) {
                    mIntent = new Intent(getApplicationContext(), FloorDetailActivity.class);
                    mIntent.putExtra("HALL", url.replace("hall://", ""));
                }
                intent();
                return true;
            }
        });
    }

    private boolean startsWith(String abc) {
        return mUrl.toLowerCase().startsWith(abc);
    }

    private void toImageAty() {
        mIntent = new Intent(WebContentActivity.this, ImageActivity.class);
        mIntent.putExtra("url", mUrl);
        mIntent.putExtra("title", barTitle.get());
    }

    private void toRegisterAty() {
        mIntent = new Intent(WebContentActivity.this, RegisterActivity.class);
    }

    private void web() {
        mUrl = mUrl.replace("web://", "http://");
        LogUtil.i(TAG, "startsWith(\"web_————url=" + mUrl);
        Uri uri = Uri.parse(mUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void http() {
        mIntent = new Intent(WebContentActivity.this, WebViewActivity.class);
        mIntent.putExtra(Constant.WEB_URL, mUrl);
//                    mIntent.putExtra("title", gTitle);
    }

    private void mailTo() {
        mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.setData(Uri.parse(mUrl));
        mIntent.setType("plain/text");
        mIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mUrl.replaceFirst("mailto:", "").trim()});
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        try {
            startActivity(mIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.exception_toast_email), Toast.LENGTH_SHORT).show();
        }
    }

    private void callPhone() {
        try {
            mIntent = new Intent(Intent.ACTION_DIAL);
            mIntent.setData(Uri.parse(mUrl));
            startActivity(mIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.exception_toast_phone), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void intent() {
        if (mIntent == null) {
            return;
        }
        LogUtil.e(TAG, "intent()");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mIntent);
        overridePendingTransPad();
    }


}
