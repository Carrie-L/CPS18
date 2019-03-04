package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.adapter.NewsAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.databinding.ActivityNewsBinding;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewsModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class NewsActivity extends BaseActivity {

    private NewsModel newsModel;
    private RecyclerView recyclerView;
    private ImageView ivAd;

    @Override
    protected void initView() {
        ActivityNewsBinding binding = ActivityNewsBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        newsModel = new NewsModel();
        binding.setNewsModel(newsModel);
        recyclerView = binding.rvNews;
        ivAd = binding.ivAd;
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        NewsAdapter adapter = new NewsAdapter(this, newsModel.getNewsList());
        recyclerView.setAdapter(adapter);

        showD7();

    }

    //String newsID, String title
    public void onItemClick(News news) {
        LogUtil.i(TAG, "news=" + news.getTitle());
        AppUtil.trackViewLog(191, "Page", news.getNewsID(), "NewsDetail");
        Bundle bundle = new Bundle();
        bundle.putParcelable("News", news);
        bundle.putString(Constant.TITLE, barTitle.get());
        intent(NewsDtlActivity.class, bundle);
    }

    private static final Integer D7_INDEX = 4;

    private void showD7() {
        final EPOHelper epoHelper = EPOHelper.getInstance();
        if (!epoHelper.isD7Open(D7_INDEX)) {
            return;
        }
        ivAd.setVisibility(View.VISIBLE);
//        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivAd.getLayoutParams();
//        params.width = AppUtil.getScreenWidth();
//        params.height = AppUtil.getCalculatedHeight(Constant.M3_WIDTH, Constant.M3_HEIGHT);
//        ivAd.setLayoutParams(params);
        epoHelper.setD7ViewLog(D7_INDEX,getApplicationContext());
        Glide.with(getApplicationContext()).load(epoHelper.getD7Image(D7_INDEX)).into(ivAd);
        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.adsale.ChinaPlas.data.model.EPO.D7 D7 = epoHelper.getItemD7(D7_INDEX);
                Intent intent = epoHelper.intentAd(D7.function, D7.companyID, D7.PageID);
                if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                    epoHelper.setD7ClickLog(D7_INDEX,getApplicationContext());
                    LogUtil.i(TAG, "intent.getAction()=" + intent.getAction());
                    startActivity(intent);
                    overridePendingTransPad();
                }
            }
        });


    }
}
