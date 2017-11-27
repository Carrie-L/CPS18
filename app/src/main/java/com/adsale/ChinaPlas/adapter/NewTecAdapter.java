package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.net.Uri;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.databinding.ItemNewTecBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/16.
 */

public class NewTecAdapter extends CpsBaseAdapter<NewTec> {

    private ArrayList<NewProductInfo> list;
    private Context mContext;
    private ItemNewTecBinding techBinding;
    private String baseUrl;
    private OnIntentListener mListener;

    public NewTecAdapter(Context mContext, ArrayList<NewProductInfo> list, String baseUrl, OnIntentListener listener) {
        this.list = list;
        this.mContext = mContext;
        this.baseUrl = baseUrl;
        this.mListener = listener;
    }

    public void onItemClick(NewProductInfo entity) {
//        mListener.onIntent(entity,NewTecDtlActivity.class);
//        Intent intent = new Intent(mContext, NewTecDtlActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(Constant.INTENT_NEW_TEC,  entity);
//        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Glide.with(mContext).load(Uri.parse(baseUrl.concat("230328_170331160600.jpg"))).into(techBinding.ivProductPic);
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        techBinding = (ItemNewTecBinding) binding;
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_new_tec;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
