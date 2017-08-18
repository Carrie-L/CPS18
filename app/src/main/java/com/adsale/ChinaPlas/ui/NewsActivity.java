package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.NewsAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.databinding.ActivityNewsBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewsModel;

import java.util.ArrayList;

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

        NewsAdapter adapter = new NewsAdapter(this,new ArrayList<News>(0));
        recyclerView.setAdapter(adapter);

        newsModel.getNewsList();


    }

    //String newsID, String title
    public void onItemClick(News news) {
        LogUtil.i(TAG,"news="+news.getTitle());

//        Intent intent=new Intent(this,NewsDtlActivity.class);
//        intent.putExtra("News",news);

        Bundle bundle = new Bundle();
        bundle.putParcelable("News",news);
        bundle.putString(Constant.TITLE,barTitle.get());
        intent(NewsDtlActivity.class, bundle);
    }
}
