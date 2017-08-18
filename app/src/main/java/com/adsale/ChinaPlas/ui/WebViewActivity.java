package com.adsale.ChinaPlas.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
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

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_web_view,mBaseFrameLayout,true);
    }

    @Override
    protected void initData() {
        barTitle.set(getIntent().getStringExtra("title"));
    }
}
