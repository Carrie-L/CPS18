package com.adsale.ChinaPlas.adapter;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorAdapter extends CpsBaseAdapter<Exhibitor> {
    private ArrayList<Exhibitor> list ;

    public ExhibitorAdapter(ArrayList<Exhibitor> list) {
        this.list = list;
    }

    //必须重写
    @Override
    public void setList(ArrayList<Exhibitor> list) {
        this.list=list;
        super.setList(list);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_exhi_detail_child;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
