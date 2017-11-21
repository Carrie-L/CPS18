package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.InterestedExhibitor;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/19.
 */

public class InterestedExhibitorAdapter extends CpsBaseAdapter<InterestedExhibitor>{
    private ArrayList<InterestedExhibitor> list;
    private OnIntentListener mListener;

    public InterestedExhibitorAdapter(ArrayList<InterestedExhibitor> list, OnIntentListener mListener) {
        this.list = list;
        this.mListener = mListener;
    }

    public void onItemClick(InterestedExhibitor entity){
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
