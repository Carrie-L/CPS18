package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.databinding.ActivityNewsDtlBinding;
import com.adsale.ChinaPlas.glide.GlideApp;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.helper.URLImageParser;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ShareSDKDialog;
import com.adsale.ChinaPlas.viewmodel.NewsModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.URL;
import java.util.ArrayList;

import static com.adsale.ChinaPlas.App.mLogHelper;
import static com.adsale.ChinaPlas.utils.Constant.WEB_URL;

/**
 * todo track
 * SocalMediaActivity(2017):
 * if(gIntent.getStringExtra("fromCls").equals("NewsLink")){
 * mTypePrefix="Page_NewsLink";
 * }
 */
public class NewsDtlActivity extends BaseActivity  {
    public final ObservableField<String> newsTitle = new ObservableField<>();
    public final ObservableField<String> content = new ObservableField<>();
    public final ObservableField<String> logoUrl = new ObservableField<>();
    public final ObservableArrayList<String> links = new ObservableArrayList<>();

    private News news;
    private ActivityNewsDtlBinding binding;
    private NewsModel model;

    @Override
    protected void initView() {
        mTypePrefix = "Page_NewsDetail";
        binding = ActivityNewsDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        model = new NewsModel();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            news = bundle.getParcelable("News");
            barTitle.set(bundle.getString(Constant.TITLE));
        } else {
            news = model.getItemNews(getIntent().getStringExtra("ID"));
            barTitle.set(getString(R.string.title_news));
        }
    }

    @Override
    protected void initData() {
        newsTitle.set(news.getTitle());
        TextView tvContent = binding.txtDescription;
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页
        tvContent.setText(Html.fromHtml(news.getDescription(), new URLImageParser(getApplicationContext(), tvContent), null));

        mLogHelper.logNewsInfo(news.getNewsID());
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_Info);
    }

    public LinearLayout.LayoutParams getParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, DisplayUtil.dip2px(getApplicationContext(), 10));
        return params;
    }


    public void share() {
        AppUtil.trackViewLog(424, "Share", "", news.getNewsID());
        AppUtil.setStatEvent(getApplicationContext(), "Share", "Share_News_" + news.getNewsID());

        String shareLink = news.getShareLink();

        if (TextUtils.isEmpty(shareLink)) {
            shareLink = getString(R.string.share_news_link);
        }
        LogUtil.i(TAG, "mTitle=" + news.getTitle());
        LogUtil.i(TAG, "shareLink=" + shareLink);
        ShareSDKDialog share = new ShareSDKDialog();
        share.showDialog(NewsDtlActivity.this, news.getTitle(), news.getLogo(), shareLink,  Constant.SHARE_IMAGE_PATH);
    }


}
