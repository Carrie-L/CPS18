package com.adsale.ChinaPlas.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.File;

/**
 * must intent data:
 * [Url] : e.g.:【"ConcurrentEvent/001"】,内存中的文件夹名称需与asset目录下的一致，最后不需要[/]
 */
public class WebContentActivity extends BaseActivity {

    private WebView webView;
    private String webUrl;
    private Intent mIntent;

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_web_content, mBaseFrameLayout, true);
        webView = (WebView) findViewById(R.id.web_content_view);
        TAG="WebContentActivity";
    }

    @Override
    protected void initData() {
        final Intent intent = getIntent();
        webUrl = intent.getStringExtra("Url");
        LogUtil.i(TAG, "webUrl=" + webUrl);

        if (webUrl.toLowerCase().startsWith("http") || webUrl.toLowerCase().startsWith("web:")) {
            loadWebUrl();
        } else {
            loadLocalHtml();
        }

        setWebViewClient();

    }

    private void loadLocalHtml() {
        webUrl = webUrl.concat("/").concat(getHtmName());
        if (new File(App.rootDir.concat(webUrl)).exists()) {
            loadDataHtml();
        } else {
            loadAssetHtml();
        }
    }

    private void loadDataHtml() {
        LogUtil.i(TAG, "loadDataHtml=" + "file://".concat(App.rootDir).concat(webUrl));
        webView.loadUrl("file://".concat(App.rootDir).concat(webUrl));
    }

    private void loadAssetHtml() {
        LogUtil.i(TAG, "loadAssetHtml= " + "/android_asset/".concat(webUrl));
        webView.loadUrl("file:///android_asset/".concat(webUrl));
    }

    private void loadWebUrl() {
        LogUtil.i(TAG, "-- loadWebUrl --");
        webView.loadUrl(webUrl);
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
                if (startsWith("web")) {
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
                }

                intent();
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private boolean startsWith(String abc) {
        return mUrl.toLowerCase().startsWith(abc);
    }

    private void web() {
        mUrl = mUrl.replace("web://", "http://");
        LogUtil.i(TAG, "startsWith(\"web_————url=" + mUrl);
        Uri uri = Uri.parse(mUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void http() {
        mIntent = new Intent(WebContentActivity.this, WebViewActivity.class);
        mIntent.putExtra("WebUrl", mUrl);
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
        if (AppUtil.isTablet()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }


}
