package com.adsale.ChinaPlas.ui;

import android.net.Uri;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

/**
 * M5 Product 查看大图、News Dtl 查看大图
 * 可缩放
 * 接收 [(String)url]
 */
public class ImageActivity extends BaseActivity {

    private PhotoView imageView;

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_image, mBaseFrameLayout, true);
        imageView = findViewById(R.id.iv_scale);
    }

    @Override
    protected void initData() {
        String url = getIntent().getStringExtra("url");
        RequestOptions options = new RequestOptions();
//        options.placeholder(R.drawable.scan_mask);
//        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        if (new File(url).exists()) {
            Glide.with(getApplicationContext()).load(new File(url)).apply(options).into(imageView);
        } else {
            Glide.with(getApplicationContext()).load(Uri.parse(url)).apply(options).into(imageView);
        }


    }
}
