package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.ConcurrentEvent;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ItemEventBinding;
import com.adsale.ChinaPlas.ui.TechnicalListActivity;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/9/21.
 * 同期活动
 */

public class EventAdapter extends CpsBaseAdapter<ConcurrentEvent> {
    private ArrayList<ConcurrentEvent> list;
    private ConcurrentEvent entity;
    private OnIntentListener mListener;
    private LinearLayout.LayoutParams params;
    private ItemEventBinding eventBinding;

    public EventAdapter(ArrayList<ConcurrentEvent> list, OnIntentListener listener) {
        this.list = list;
        mListener = listener;
    }

    public void setItemSize(int itemWidth){
        int itemHeight = (itemWidth * Constant.EVENT_BG_HEIGHT)/Constant.EVENT_BG_WIDTH;
        LogUtil.i("EventAdapter", "itemWidth=" + itemWidth+",itemHeight="+itemHeight);
        params = new LinearLayout.LayoutParams(itemWidth, itemHeight);
    }

    @Override
    public void setList(ArrayList<ConcurrentEvent> list0) {
        this.list = list0;
        super.setList(list);
    }

    public void onItemClick(ConcurrentEvent entity) {
        if(entity.getEventID().contains("TechnicalSeminar")){
            mListener.onIntent(entity, TechnicalListActivity.class);
        }else{
            mListener.onIntent(entity, WebContentActivity.class);
        }
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        entity = list.get(position);
        if (position == 0) {
            entity.isTypeLabel.set(1);
        } else if (entity.getDate().equals(list.get(position - 1).getDate())) {
            entity.isTypeLabel.set(0);
        } else {
            entity.isTypeLabel.set(1);
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
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
        if(binding instanceof ItemEventBinding){
            eventBinding= (ItemEventBinding) binding;
        }
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(params!=null&&eventBinding!=null){
            eventBinding.rlEvent.setLayoutParams(params);
        }
    }

    public void onTechClick(ConcurrentEvent entity) {
        LogUtil.i("EventAdAPTER", "onTechClick:" + entity.toString());
        mListener.onIntent(entity.getPageID(), TechnicalListActivity.class);
    }
}
