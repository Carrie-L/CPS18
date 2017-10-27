package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.databinding.ItemEventBinding;
import com.adsale.ChinaPlas.ui.ScheduleActivity;
import com.adsale.ChinaPlas.viewmodel.EventModel;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2017/9/21.
 */

public class EventAdapter extends CpsBaseAdapter<ConcurrentEvent.Pages> {
    private ArrayList<ConcurrentEvent.Pages> list;
    private EventModel mModel;
    private final int TYPE_HEADER=0;
    private final int TYPE_ITEM=1;
    private ConcurrentEvent.Pages entity;
    private ItemEventBinding mEventBinding;

    public EventAdapter(ArrayList<ConcurrentEvent.Pages> list, EventModel model) {
        this.list = list;
        mModel = model;
    }

    @Override
    public void setList(ArrayList<ConcurrentEvent.Pages> list) {
        this.list=list;
        super.setList(list);
    }

    /* useless */
//    @Override
//    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//        if( mEventBinding.ivEventPay.getVisibility()== View.GONE){
//            ImageView ivPreReg=mEventBinding.ivEventPrereg;
//            RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.rightMargin=0;
//            params.bottomMargin=8;
//            ivPreReg.setLayoutParams(params);
//        }
//    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        entity = list.get(position);
        if (position == 0) {
            entity.isTypeLabel.set(true);
        } else if (list.get(position).date.equals(list.get(position - 1).date)) {
            entity.isTypeLabel.set(false);
        } else {
            entity.isTypeLabel.set(true);
        }
        list.set(position, entity);
        return R.layout.item_event;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.eventModel,mModel);
        super.bindVariable(binding);
        mEventBinding = (ItemEventBinding) binding;
    }
}
