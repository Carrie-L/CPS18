package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.TechSeminarDtlActivity;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/26.
 * 技术交流会列表
 */

public class TechAdapter extends CpsBaseAdapter<SeminarInfo> {
    private ArrayList<SeminarInfo> list;
    private OnIntentListener mListener;
    private static final String TAG = "TechAdapter";

    public TechAdapter(Context context, ArrayList<SeminarInfo> list, OnIntentListener listener) {
        this.mContext = context;
        this.list = list;
        this.mListener = listener;
    }

    public void setList(ArrayList<SeminarInfo> list) {
        this.list = list;
        super.setList(list);
    }

    public void onItemClick(SeminarInfo entity) {
        mListener.onIntent(entity, TechSeminarDtlActivity.class);
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
        if (list.get(position).isTypeLabel) {
            return R.layout.item_tech_header;
        }
        return R.layout.item_tech;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
