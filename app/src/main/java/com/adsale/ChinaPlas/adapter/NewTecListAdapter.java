package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.NewTecDtlActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/11/16.
 */

public class NewTecListAdapter extends CpsBaseAdapter<NewProductInfo> {
    private static final String TAG = "NewTecListAdapter";
    private ArrayList<NewProductInfo> list;
    private OnIntentListener mListener;
    private RequestOptions options;

    public NewTecListAdapter( ArrayList<NewProductInfo> list, OnIntentListener listener) {
        this.list = list;
        this.mListener = listener;

        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.place_holder).error(R.drawable.place_holder);
    }

    public void setList(ArrayList<NewProductInfo> list){
        this.list=list;
        super.setList(list);
    }

    public void onItemClick(NewProductInfo entity) {
        mListener.onIntent(entity, NewTecDtlActivity.class);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(com.adsale.ChinaPlas.BR.adapter,this);
        binding.setVariable(com.adsale.ChinaPlas.BR.options,options);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if(list.get(position).adItem){
            return R.layout.item_new_tec_ad;
        }
        return R.layout.item_new_tec;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
