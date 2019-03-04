package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Map;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/10/17.
 * 应用行业列表
 * TextAdapter 与 ApplicationIndustryAdapter 完全一样。no: xml中的 obj 不一样。
 */

public class TextAdapter<T> extends CpsBaseAdapter<T> {
    private final String TAG = "ApplicationAdapter";
    private ArrayList<T> list;
    private ArrayList<ExhibitorFilter> filters;
    private ExhibitorFilter filter;
    private Map floor;

    public TextAdapter(ArrayList<T> list, ArrayList<ExhibitorFilter> filters) {
        this.list = list;
        this.filters = filters;
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    public void setList(ArrayList<T> list) {
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
        return R.layout.item_text;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void onSelect(T entity) {
        floor = (Map) entity;
        if (floor.isSelected.get()) {
            LogUtil.i(TAG, "取消:" + floor.getFloorID());
            floor.isSelected.set(false);
            int size = filters.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (floor.getFloorID().equals(filters.get(i).id)) {
                        filters.remove(i);
                        break;
                    }
                }
            }
        } else {
            LogUtil.i(TAG, "添加：" + floor.getFloorID());
            floor.isSelected.set(true);
            filter = new ExhibitorFilter(3, floor.getFloorID(), floor.getFloorID());
            filters.add(filter);
        }
        LogUtil.i(TAG, "onSelect::filters=" + filters.size());
    }

}
