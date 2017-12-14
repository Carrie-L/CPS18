package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.ImageActivity;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/12/3.
 *
 */

public class ExhibitorPhotoAdapter extends CpsBaseAdapter<String> {
    private ArrayList<String> list;
    private Context mContext;
    private OnIntentListener mListener;
    private RequestOptions options;

    public ExhibitorPhotoAdapter(ArrayList<String> list, Context context, OnIntentListener listener) {
        this.list = list;
        this.mContext = context;
        this.mListener = listener;
        LogUtil.i("ExhibitorPhotoAdapter","list="+list.toString());
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
    }

    @Override
    public void setList(ArrayList<String> list) {
        this.list = list;
        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        super.setList(list);
    }

    public void onItemClick(String path) {
        mListener.onIntent(path, ImageActivity.class);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter,this);
        binding.setVariable(BR.options,options);
        super.bindVariable(binding);
    }

// Glide.with(mContext).load(new File(list.get(position))).into(photoBinding.ivPhoto);

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_photo;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
