package com.adsale.ChinaPlas.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.adsale.ChinaPlas.R;

public class WebViewTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);


        WebView webView = findViewById(R.id.web_view_test);
        webView.loadUrl("file:///android_asset/infotest.html");




    }
}
