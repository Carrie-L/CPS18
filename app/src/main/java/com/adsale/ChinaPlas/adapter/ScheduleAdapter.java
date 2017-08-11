package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.ui.ScheduleActivity;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/11.
 */

public class ScheduleAdapter extends CpsBaseAdapter {
    private ArrayList<ScheduleInfo> list;
    private ScheduleActivity mContext;

    public ScheduleAdapter(ArrayList<ScheduleInfo> list, ScheduleActivity context) {
        this.list = list;
        mContext = context;
    }

    public void setList(ArrayList<ScheduleInfo> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_schedule;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        super.bindVariable(binding);
        binding.setVariable(BR.activity,mContext);
    }

}
