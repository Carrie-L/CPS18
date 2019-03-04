package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.databinding.ViewAboutBinding;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import static com.adsale.ChinaPlas.App.mLogHelper;

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
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(536, 441); // 1072*882  /2
    }

    private EPO.D4 D4;
    public void initData(EPO.D4 D4) {
        this.D4 = D4;
        binding.tvAbout.setText(D4.getAbout());
        if (D4.website != null) {
            binding.tvWebsite.setText(D4.website);
        }
        if (!TextUtils.isEmpty(D4.videoCover)) {
            showImage1();
        }
    }

    private void showImage1() {
        binding.ivVideo0.setVisibility(VISIBLE);
        Glide.with(mContext).load(D4.videoCover).apply(options).into(binding.ivVideo0);
    }

//    private void showImage2() {
//        showImage1();
//        binding.ivVideo1.setVisibility(VISIBLE);
//        Glide.with(mContext).load(imageLinks[1]).apply(options).into(binding.ivVideo1);
//    }

//    private void showImage3() {
//        showImage1();
//        showImage2();
//        binding.ivVideo2.setVisibility(VISIBLE);
//        Glide.with(mContext).load(imageLinks[2]).apply(options).into(binding.ivVideo2);
//    }

    public void onClick() {
        mLogHelper.logD4(D4.companyID+"_video", false);
        mLogHelper.setBaiDuLog(mContext, LogHelper.EVENT_ID_AD_Click);

        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(Constant.WEB_URL, D4.videoLink);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void onWebsite() {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(Constant.WEB_URL, D4.website);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


}
