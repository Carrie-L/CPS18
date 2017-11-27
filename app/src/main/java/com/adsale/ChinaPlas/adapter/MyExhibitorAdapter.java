package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/19.
 */

public class MyExhibitorAdapter extends CpsBaseAdapter<Exhibitor> {
    private ArrayList<Exhibitor> list;
    private OnIntentListener mListener;
    private Exhibitor entity;

    public MyExhibitorAdapter(ArrayList<Exhibitor> list,OnIntentListener listener){
        this.list=list;
        this.mListener=listener;
    }

    public void setList(ArrayList<Exhibitor> list){
        this.list=list;
        super.setList(list);
    }

    public void onItemClick(Exhibitor exhibitor) {
        mListener.onIntent(exhibitor, ExhibitorDetailActivity.class);
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
        entity = list.get(position);
        if (position == 0) {
            entity.isTypeLabel.set(true);
        } else if (list.get(position).getSort().equals(list.get(position - 1).getSort())) {
            entity.isTypeLabel.set(false);
        } else {
            entity.isTypeLabel.set(true);
        }
        list.set(position, entity);
        return R.layout.item_my_exhibitor;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
