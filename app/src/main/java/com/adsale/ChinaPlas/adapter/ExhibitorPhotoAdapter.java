package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ItemPhotoBinding;
import com.adsale.ChinaPlas.ui.ImageActivity;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Carrie on 2017/12/3.
 *
 */

public class ExhibitorPhotoAdapter extends CpsBaseAdapter<String> {
    private ArrayList<String> list;
    private Context mContext;
    private ItemPhotoBinding photoBinding;
    private OnIntentListener mListener;

    public ExhibitorPhotoAdapter(ArrayList<String> list, Context context, OnIntentListener listener) {
        this.list = list;
        this.mContext = context;
        this.mListener=listener;
    }

    @Override
    public void setList(ArrayList<String> list) {
        this.list = list;
        super.setList(list);
    }

    public void onItemClick(String path){
        mListener.onIntent(path, ImageActivity.class);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        photoBinding = (ItemPhotoBinding) binding;
        photoBinding.setAdapter(this);
        super.bindVariable(binding);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Glide.with(mContext).load(new File(list.get(position))).into(photoBinding.ivPhoto);
    }

    @Override
    protected Object getObjForPosition(int position) {
        LogUtil.i("getObjForPosition","list.get(position)="+list.get(position));
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
