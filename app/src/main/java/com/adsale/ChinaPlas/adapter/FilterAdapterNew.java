package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.Filter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2018/4/4.
 */

public class FilterAdapterNew extends CpsBaseAdapter<Filter> {
    private final String TAG = "ApplicationAdapter";
    private ArrayList<Filter> list;
    private ExhibitorFilter filter;

    public FilterAdapterNew(ArrayList<Filter> list) {
        this.list = list;
    }

    private int index = 1; // 默认为1，在第二个位置。但是当从NewTecFilter跳转过来时，0表示NewTecProduct, 1 表示 NewTecApplications
    public void setIndex(int index){
        this.index = index;
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    public void setList(ArrayList<Filter> list) {
        this.list = list;
        super.setList(list);
        LogUtil.i(TAG, "setList=" + list.size());
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_application_industry;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

//    public void onSelect(Filter entity) {
//        if (entity.getIsSelected()) {
//            LogUtil.i(TAG, "取消:" + entity.getApplicationName());
//            entity.setIsSelected(false);
//            int size = list.size();
//            if (size > 0) {
//                for (int i = 0; i < size; i++) {
//                    if (entity.getIndustryID().equals(filters.get(i).id)) {
//                        filters.remove(i);
//                        break;
//                    }
//                }
//            }
//        } else {
//            LogUtil.i(TAG, "添加：" + entity.getApplicationName());
//            entity.isSelected.set(true);
//            filter = new ExhibitorFilter(index, entity.getIndustryID(), entity.getApplicationName());
//            filters.add(filter);
//        }
//        LogUtil.i(TAG, "onSelect::filters=" + filters.size());
//    }
}
