package com.adsale.ChinaPlas.adapter;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2017/10/17.
 * 产品类别
 */

public class IndustryAdapter extends CpsBaseAdapter<Industry> {
    private ArrayList<Industry> list;
    private Industry industry;
    private ArrayList<ExhibitorFilter> filters;
    private ExhibitorFilter filter;

    public IndustryAdapter(ArrayList<Industry> list, ArrayList<ExhibitorFilter> filters) {
        this.list = list;
        this.filters = filters;
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    public void setList(ArrayList<Industry> list) {
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
        industry = list.get(position);
        if (position == 0) {
            industry.isTypeLabel.set(true);
        } else if (list.get(position).getSort().equals(list.get(position - 1).getSort())) {
            industry.isTypeLabel.set(false);
        } else {
            industry.isTypeLabel.set(true);
        }
        list.set(position, industry);
        return R.layout.item_industry;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void onSelect(Industry industry) {
        if (industry.selected.get()) {
            LogUtil.i(TAG, "取消:" + industry.getIndustryName());
            industry.selected.set(false);
            int size = filters.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (industry.getCatalogProductSubID().equals(filters.get(i).id)) {
                        filters.remove(i);
                        break;
                    }
                }
            }
        } else {
            LogUtil.i(TAG, "添加：" + industry.getIndustryName());
            industry.selected.set(true);
            filter = new ExhibitorFilter(0, industry.getCatalogProductSubID(), industry.getIndustryName());
            filters.add(filter);
        }
        LogUtil.i(TAG, "onSelect::filters=" + filters.size());
    }


}
