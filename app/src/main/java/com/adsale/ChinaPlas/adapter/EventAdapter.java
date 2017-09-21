package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.ui.ScheduleActivity;
import com.adsale.ChinaPlas.viewmodel.EventModel;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/9/21.
 */

public class EventAdapter extends CpsBaseAdapter<ConcurrentEvent.Pages> {
    private ArrayList<ConcurrentEvent.Pages> list;
    private EventModel mModel;

    public EventAdapter(ArrayList<ConcurrentEvent.Pages> list, EventModel model) {
        this.list = list;
        mModel = model;
    }

    @Override
    public void setList(ArrayList<ConcurrentEvent.Pages> list) {
        this.list=list;
        super.setList(list);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_event;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        super.bindVariable(binding);
        binding.setVariable(BR.eventModel,mModel);
    }
}
