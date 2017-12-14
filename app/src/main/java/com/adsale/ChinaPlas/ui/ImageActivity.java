package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.net.Uri;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityImageBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

/**
 * M5 Product 查看大图、News Dtl 查看大图
 * 可缩放
 * 接收 [(String)url] [(boolean)del]
 */
public class ImageActivity extends BaseActivity {
    public final ObservableBoolean isShowDel = new ObservableBoolean(false);
    private PhotoView imageView;
    private String url;

    @Override
    protected void initView() {
        ActivityImageBinding binding = ActivityImageBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();
        imageView = findViewById(R.id.iv_scale);
    }

    @Override
    protected void initData() {
        url = getIntent().getStringExtra("url");
        LogUtil.i(TAG, "url PATH =" + url);

        isShowDel.set(getIntent().getBooleanExtra("del", false));
        RequestOptions options = new RequestOptions();
//        options.placeholder(R.drawable.scan_mask);
//        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        if (isShowDel.get()) {
            options.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        }

        if (new File(url).exists()) {
            Glide.with(getApplicationContext()).load(new File(url)).apply(options).into(imageView);
        } else {
            Glide.with(getApplicationContext()).load(Uri.parse(url)).apply(options).into(imageView);
        }
    }

    public void onDel() {
        Intent intent = new Intent();
        intent.putExtra("path", url);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransPad();
    }
}
