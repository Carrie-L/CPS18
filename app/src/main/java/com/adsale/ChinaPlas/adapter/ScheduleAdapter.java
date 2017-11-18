package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.ui.ScheduleActivity;
import com.adsale.ChinaPlas.ui.ScheduleEditActivity;
import com.adsale.ChinaPlas.utils.Constant;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.utils.Constant.INTENT_SCHEDULE;

/**
 * Created by Carrie on 2017/8/11.
 */

public class ScheduleAdapter extends CpsBaseAdapter<ScheduleInfo> {
    private ArrayList<ScheduleInfo> list;
    private Context mContext;

    public ScheduleAdapter(ArrayList<ScheduleInfo> list, Context context) {
        this.list = list;
        mContext = context;
    }

    @Override
    public void setList(ArrayList<ScheduleInfo> list) {
        this.list=list;
        super.setList(list);
    }

    public void onItemClick(ScheduleInfo entity) {
        Intent intent = new Intent(mContext, ScheduleEditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(INTENT_SCHEDULE, entity);
        mContext.startActivity(intent);
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
        binding.setVariable(BR.adapter,this);
        super.bindVariable(binding);

    }

}
