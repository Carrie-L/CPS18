package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.databinding.ItemNewsBinding;
import com.adsale.ChinaPlas.ui.NewsActivity;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.android.databinding.library.baseAdapters.BR;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/15.
 */

public class NewsAdapter extends CpsBaseAdapter<News> {
    private ArrayList<News> list;
    private NewsActivity activity;
    private ItemNewsBinding newsBinding;
    private final RequestOptions options;

    public NewsAdapter(NewsActivity activity, ArrayList<News> list) {
        this.list = list;
        this.activity = activity;
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        int size = list.size();
        News entity;
        for(int i=0;i<size;i++){
            entity=list.get(i);
            entity.setLogo(NetWorkHelper.DOWNLOAD_PATH.concat("News/").concat(entity.getLogo()));
            list.set(i,entity);
        }
    }

    @Override
    public void setList(ArrayList<News> list) {
        this.list = list;
        super.setList(list);
    }

//    @Override
//    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//        GlideApp.with(activity).load(Uri.parse(NetWorkHelper.DOWNLOAD_PATH.concat("News/").concat(newsBinding.getObj().getLogo()))).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(newsBinding.ivNewsPic);// 缓存最终图片
//    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.activity, activity);
        binding.setVariable(BR.options, options);
        binding.setVariable(BR.baseUrl, NetWorkHelper.DOWNLOAD_PATH.concat("News/"));
        super.bindVariable(binding);
        newsBinding = (ItemNewsBinding) binding;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_news;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
