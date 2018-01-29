package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.databinding.ViewAboutBinding;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Carrie on 2018/1/24.
 */

public class AboutView extends LinearLayout {

    private ViewAboutBinding binding;
    private Context mContext;
    private RequestOptions options;

    public AboutView(Context context) {
        super(context);
        init(context);
    }

    public AboutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AboutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        binding = ViewAboutBinding.inflate(LayoutInflater.from(context), this, true);
        binding.setView(this);
        binding.executePendingBindings();
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(536,441); // 1072*882  /2
    }

    private String[] imageLinks;
    private String[] videoLinks;
    private String website;

    public void initData(String description, String website, String[] imageLinks, String[] videoLinks) {
        this.imageLinks = imageLinks;
        this.videoLinks = videoLinks;
        this.website = website;

        binding.tvAbout.setText(description);
        binding.tvWebsite.setText(website);

        switch (imageLinks.length) {
            case 1:
                showImage1();
                break;
            case 2:
                showImage2();
                break;
            case 3:
                showImage3();
                break;
        }
    }

    private void showImage1() {
        binding.ivVideo0.setVisibility(VISIBLE);
        Glide.with(mContext).load(imageLinks[0]).apply(options).into(binding.ivVideo0);
    }

    private void showImage2() {
        showImage1();
        binding.ivVideo1.setVisibility(VISIBLE);
        Glide.with(mContext).load(imageLinks[1]).apply(options).into(binding.ivVideo1);
    }

    private void showImage3() {
        showImage1();
        showImage2();
        binding.ivVideo2.setVisibility(VISIBLE);
        Glide.with(mContext).load(imageLinks[2]).apply(options).into(binding.ivVideo2);
    }

    public void onClick(int index) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(Constant.WEB_URL, videoLinks[index]);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void onWebsite(){
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(Constant.WEB_URL, website);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


}
