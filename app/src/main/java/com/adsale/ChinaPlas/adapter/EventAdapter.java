package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.ui.TechnicalListActivity;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/9/21.
 * 同期活动
 */

public class EventAdapter extends CpsBaseAdapter<ConcurrentEvent.Pages> {
    private ArrayList<ConcurrentEvent.Pages> list;
    private ConcurrentEvent.Pages entity;
    private OnIntentListener mListener;

    public EventAdapter(ArrayList<ConcurrentEvent.Pages> list, OnIntentListener listener) {
        this.list = list;
        mListener = listener;
    }

    @Override
    public void setList(ArrayList<ConcurrentEvent.Pages> list0) {
        this.list = list0;
        super.setList(list);
    }

    public void onItemClick(ConcurrentEvent.Pages entity) {
        mListener.onIntent(entity, WebContentActivity.class);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        entity = list.get(position);
        if (entity.isTypeLabel.get() == 2) {
            return R.layout.item_event_seminar;
        } else {
            if (position == 0) {
                entity.isTypeLabel.set(1);
            } else if (position == 1) {
                entity.isTypeLabel.set(0);
            } else if (list.get(position).date.equals(list.get(position - 2).date)) { /* 因为中间插了一个技术交流会，所以 -2，上上个 */
                entity.isTypeLabel.set(0);
            } else {
                entity.isTypeLabel.set(1);
            }
            list.set(position, entity);
            return R.layout.item_event;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
//        Glide.with(eventBinding.getRoot()).load(Uri.parse(eventBinding.getObj().getImageLink())).into(eventBinding.rlEvent);
        super.onBindViewHolder(holder, position);
    }

    public void onTechClick(ConcurrentEvent.Pages entity) {//String date
        LogUtil.i("EventAdAPTER", "onTechClick:" + entity.toString());
        mListener.onIntent(entity.pageID, TechnicalListActivity.class);
    }
}
