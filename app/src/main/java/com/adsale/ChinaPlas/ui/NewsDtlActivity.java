package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.viewmodel.NewsModel;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.utils.Constant.WEB_URL;

/**
 * todo track photo显示和点击
 */
public class NewsDtlActivity extends BaseActivity implements View.OnClickListener {
    public final ObservableField<String> newsTitle = new ObservableField<>();
    public final ObservableField<String> content = new ObservableField<>();
    public final ObservableField<String> logoUrl = new ObservableField<>();
    public final ObservableArrayList<String> links = new ObservableArrayList<>();

    private News news;
    private ActivityNewsDtlBinding binding;
    private ImageView ivPhoto;
    private NewsModel model;
    private String photoUrl;

    @Override
    protected void initView() {
        binding = ActivityNewsDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        ivPhoto = binding.ivPhoto;
        model = new NewsModel();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            news = bundle.getParcelable("News");
            barTitle.set(bundle.getString(Constant.TITLE));
        } else {
            news = model.getItemNews(getIntent().getStringExtra("ID"));
            barTitle.set(getString(R.string.title_news));
        }
        LogUtil.i(TAG, "NEWS= " + news.toString());

    }

    @Override
    protected void initData() {
        newsTitle.set(news.getTitle());
        content.set(news.getDescription());

        ArrayList<NewsLink> links = model.getLinks(news.getNewsID());

        LinearLayout linkLayout = binding.lyLink;
        int size = links.size();
        LayoutInflater inflater = getLayoutInflater();
        if (!links.isEmpty()) {
            for (NewsLink oNewsLink : links) {
                if (oNewsLink.getPhoto() != null && !oNewsLink.getPhoto().equals("")) {
                    photoUrl = NetWorkHelper.DOWNLOAD_PATH.concat("News/").concat(oNewsLink.getPhoto());
                    LogUtil.i(TAG, "photoUrl=" + photoUrl);
//                    Glide.with(this).load(Uri.parse(photoUrl)).into(ivPhoto);
                    GlideApp.with(this).load(Uri.parse(photoUrl)).diskCacheStrategy(DiskCacheStrategy.DATA).into(ivPhoto); // 缓存原始图片
                }

                String strLink = oNewsLink.getLink();
                if (!TextUtils.isEmpty(strLink)) {
                    View linkView = inflater.inflate(R.layout.view_link, linkLayout, false);
                    TextView txtLink = (TextView) linkView.findViewById(R.id.textView1);
                    txtLink.setText(oNewsLink.getTitle());
                    linkView.setTag(oNewsLink.getLink());
                    linkView.setOnClickListener(this);
                    linkLayout.addView(linkView, getParams());
                }
            }
        }
    }

    public LinearLayout.LayoutParams getParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, DisplayUtil.dip2px(getApplicationContext(), 10));
        return params;
    }


    public void share() {
        AppUtil.trackViewLog( 424, "SN", "", news.getNewsID());
        AppUtil.setStatEvent(getApplicationContext(), "ShareNews", "SN_" + news.getNewsID());
    }

    @Override
    public void onClick(View v) {
        String url = v.getTag().toString();
        LogUtil.i(TAG, "url=" + url);
        AppUtil.trackViewLog( 190, "Page", news.getNewsID(), "NewsLink");
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", barTitle.get());
        intent.putExtra(WEB_URL, url);
        startActivity(intent);
        overridePendingTransPad();
    }

    public void onPhotoClick() {
        AppUtil.trackViewLog( 189, "Page", news.getNewsID(), "NewsPhoto");
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("url", photoUrl);
        intent.putExtra("title", barTitle.get());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }
}
