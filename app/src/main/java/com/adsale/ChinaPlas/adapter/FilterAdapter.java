package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 筛选结果列表 (红底白字×三列)
 */

public class FilterAdapter extends CpsBaseAdapter<ExhibitorFilter> {
    private final String TAG = "FilterAdapter";
    private ArrayList<ExhibitorFilter> list;

    public FilterAdapter(ArrayList<ExhibitorFilter> list) {
        this.list = list;
    }

    public void setList(ArrayList<ExhibitorFilter> list) {
        this.list = list;
        super.setList(list);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_filter;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onClear(ExhibitorFilter entity) {
        LogUtil.i(TAG, "onClear:=" + entity.filter);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (entity.id.equals(list.get(i).id)) {
                list.remove(i);
//                notifyItemRemoved(i);//使用这个方法时，删除某项，高度不变，下面被删除行都是空的
                notifyDataSetChanged();//使用这个方法时，删除某行，高度减少，下面的上移
                break;
            }
        }
    }

}
