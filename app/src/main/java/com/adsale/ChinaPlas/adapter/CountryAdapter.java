package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2017/10/18.
 * 国家列表
 */
public class CountryAdapter extends CpsBaseAdapter<Country> {
    private ArrayList<Country> list;
    private ArrayList<ExhibitorFilter> filters;
    private ExhibitorFilter filter;
    private Country mCountry;

    public CountryAdapter(ArrayList<Country> list, ArrayList<ExhibitorFilter> filters) {
        this.list = list;
        this.filters = filters;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        mCountry = list.get(position);
        if (position == 0) {
            mCountry.isTypeLabel.set(true);
        } else if (list.get(position).getSort().equals(list.get(position - 1).getSort())) {
            mCountry.isTypeLabel.set(false);
        } else {
            mCountry.isTypeLabel.set(true);
        }
        list.set(position, mCountry);
        return R.layout.item_country;
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

    public void onSelect(Country country) {
        if (country.selected.get()) {
            LogUtil.i(TAG, "取消:" + country.getCountryName());
            country.selected.set(false);
            int size = filters.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (country.getCountryID().equals(filters.get(i).id)) {
                        filters.remove(i);
                        break;
                    }
                }
            }
        } else {
            LogUtil.i(TAG, "添加：" + country.getCountryName());
            country.selected.set(true);
            filter = new ExhibitorFilter(2, country.getCountryID(), country.getCountryName());
            filters.add(filter);
        }
        LogUtil.i(TAG, "onSelect::filters=" + filters.size());
    }
}
