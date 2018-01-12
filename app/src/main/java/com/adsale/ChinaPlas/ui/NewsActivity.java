package com.adsale.ChinaPlas.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.NewsAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.databinding.ActivityNewsBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewsModel;

public class NewsActivity extends BaseActivity {

    private NewsModel newsModel;
    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        ActivityNewsBinding binding = ActivityNewsBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        newsModel = new NewsModel();
        binding.setNewsModel(newsModel);
        recyclerView = binding.rvNews;
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        NewsAdapter adapter = new NewsAdapter(this,newsModel.getNewsList());
        recyclerView.setAdapter(adapter);

    }

    //String newsID, String title
    public void onItemClick(News news) {
        LogUtil.i(TAG,"news="+news.getTitle());
        AppUtil.trackViewLog(191, "Page", news.getNewsID(), "NewsDetail");
        Bundle bundle = new Bundle();
        bundle.putParcelable("News",news);
        bundle.putString(Constant.TITLE,barTitle.get());
        intent(NewsDtlActivity.class, bundle);
    }
}
