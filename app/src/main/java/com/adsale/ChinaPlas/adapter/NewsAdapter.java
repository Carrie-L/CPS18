package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.widget.ImageView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.databinding.ItemNewsBinding;
import com.adsale.ChinaPlas.ui.NewsActivity;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/15.
 */

public class NewsAdapter extends CpsBaseAdapter<News> {
    private ArrayList<News> list;
    private ImageView ivLogo;
    private NewsActivity activity;

    public NewsAdapter(NewsActivity activity, ArrayList<News> list) {
        this.list = list;
        this.activity=activity;
    }

    @Override
    public void setList(ArrayList<News> list) {
        this.list = list;
        super.setList(list);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ivLogo.setImageURI(Uri.parse(NetWorkHelper.DOWNLOAD_PATH + "News/" + list.get(position).getLogo()));
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        super.bindVariable(binding);
        ItemNewsBinding newsBinding = (ItemNewsBinding) binding;
        ivLogo = newsBinding.ivNewsPic;
        newsBinding.setActivity(activity);
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
