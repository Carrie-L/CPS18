package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Floor;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/19.
 */

public class FloorDistributeAdapter extends CpsBaseAdapter<Floor>{
    private ArrayList<Floor> list;
    private OnIntentListener mListener;

    public FloorDistributeAdapter(ArrayList<Floor> list, OnIntentListener mListener) {
        this.list = list;
        this.mListener = mListener;
    }

    public void onItemClick(Floor entity){
//        mListener.onIntent(entity,);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter,this);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_floor_distribute;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
