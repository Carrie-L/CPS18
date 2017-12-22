package com.adsale.ChinaPlas.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/3.
 */

public abstract class CpsBaseAdapter<T> extends RecyclerView.Adapter<CpsBaseViewHolder> {
        protected Context mContext;

    @Override
    public CpsBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ViewDataBinding  binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),viewType,parent,false);
        bindVariable(binding);
        return new CpsBaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        Object object = getObjForPosition(position);
        holder.bind(object);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    protected void bindVariable(ViewDataBinding binding){
        binding.executePendingBindings();
    }

    public void setList(ArrayList<T> list){
        notifyDataSetChanged();
    }

}
