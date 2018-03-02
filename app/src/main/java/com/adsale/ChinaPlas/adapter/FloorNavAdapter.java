package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;

import java.util.ArrayList;

/**
 * Created by Carrie on 2018/2/8.
 */

public class FloorNavAdapter extends CpsBaseAdapter<Exhibitor> {
    private ArrayList<Exhibitor> list;
    private OnIntentListener mListener;

    public FloorNavAdapter(ArrayList<Exhibitor> list,OnIntentListener listener) {
        this.list = list;
        this.mListener = listener;
    }

    public void setList(ArrayList<Exhibitor> list) {
        this.list = list;
        super.setList(list);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_floor_nav;
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

    public void onCollect(Exhibitor exhibitor) {
        exhibitor.setIsFavourite(exhibitor.getIsFavourite() == 1 ? 0 : 1);
        ExhibitorRepository repository = ExhibitorRepository.getInstance();
        repository.updateIsFavourite(mContext,exhibitor.getCompanyID(), exhibitor.getIsFavourite());
    }

    public void onItemClick(String booth){
        mListener.onIntent(booth,null);
    }
}
