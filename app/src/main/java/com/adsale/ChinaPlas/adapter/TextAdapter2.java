package com.adsale.ChinaPlas.adapter;

/**
 * Created by Carrie on 2017/11/16.
 */

import com.adsale.ChinaPlas.BR;

import android.content.Intent;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Floor;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/10/17.
 * 应用行业列表
 * TextAdapter 与 ApplicationIndustryAdapter 完全一样。no: xml中的 obj 不一样。
 */

public class TextAdapter2 extends CpsBaseAdapter<Text2> {
    private final String TAG = "TextAdapter2";
    private ArrayList<Text2> list;
    private OnIntentListener mListener;

    public TextAdapter2(ArrayList<Text2> list,OnIntentListener listener) {
        this.list = list;
        this.mListener=listener;
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    public void setList(ArrayList<Text2> list) {
        this.list = list;
        super.setList(list);
        LogUtil.i(TAG, "setList=" + list.size());
    }

    public void onItemClick(String id){
       mListener.onIntent(id,null);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_text2;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



}

