package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ItemTechBinding;
import com.adsale.ChinaPlas.ui.TechSeminarDtlActivity;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2017/10/26.
 * 技术交流会列表
 */

public class TechAdapter extends CpsBaseAdapter<SeminarInfo> {
    private ArrayList<SeminarInfo> list;
    private SeminarInfo entity;
    private ItemTechBinding techBinding;
    private Context mContext;
    private OnIntentListener mListener;

    public TechAdapter(Context context, ArrayList<SeminarInfo> list, OnIntentListener listener) {
        this.mContext = context;
        this.list = list;
        this.mListener=listener;
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
        techBinding = (ItemTechBinding) binding;
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (techBinding.getObj().isADer) {
            LogUtil.i(TAG, "是广告啦！" + techBinding.getObj().getCompanyID() + "," + techBinding.getObj().getTopic());
            Glide.with(mContext).load(Uri.parse(techBinding.getObj().adLogoUrl)).into(techBinding.ivTechPic);
        }
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        entity = list.get(position);
        if (position == 0) {
            entity.isTypeLabel.set(true);
        } else if (list.get(position).getDate().equals(list.get(position - 1).getDate())) {
            entity.isTypeLabel.set(false);
        } else {
            entity.isTypeLabel.set(true);
        }
        list.set(position, entity);
        return R.layout.item_tech;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
