package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.TechSeminarDtlActivity;
import com.adsale.ChinaPlas.utils.Constant;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/26.
 * 技术交流会列表
 */

public class TechAdapter extends CpsBaseAdapter<SeminarInfo> {
    private ArrayList<SeminarInfo> list;
    private SeminarInfo entity;
    private Context mContext;
    private OnIntentListener mListener;

    public TechAdapter(Context context, ArrayList<SeminarInfo> list, OnIntentListener listener) {
        this.mContext = context;
        this.list = list;
        this.mListener=listener;

//        int width = (Constant.M6_BANNER_HEIGHT_PHONE*)/ Constant.M6_BANNER_WIDTH_PHONE;
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams();
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
//        techBinding = (ItemTechBinding) binding;
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        if (techBinding.getObj().isADer) {
//            LogUtil.i(TAG, "是广告啦！" + techBinding.getObj().getCompanyID() + "," + techBinding.getObj().adLogoUrl);
//            Glide.with(mContext).load(Uri.parse(techBinding.getObj().adLogoUrl)).into(techBinding.ivLogo);
//        }
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if(list.get(position).isTypeLabel){
            return R.layout.item_tech_header;
        }
        return R.layout.item_tech;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
