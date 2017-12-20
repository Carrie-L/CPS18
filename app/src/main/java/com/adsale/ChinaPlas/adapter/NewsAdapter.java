package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.News;
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
    private final RequestOptions options;

    public NewsAdapter(NewsActivity activity, ArrayList<News> list) {
        this.list = list;
        this.activity = activity;
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        int size = list.size();
        News entity;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            if (entity.getLogo().contains(NetWorkHelper.DOWNLOAD_PATH.concat("News/"))) {
                break;
            }
            entity.setLogo(NetWorkHelper.DOWNLOAD_PATH.concat("News/").concat(entity.getLogo()));
            list.set(i, entity);
        }
    }

    @Override
    public void setList(ArrayList<News> list) {
        this.list = list;
        super.setList(list);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.activity, activity);
        binding.setVariable(BR.options, options);
        super.bindVariable(binding);
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
