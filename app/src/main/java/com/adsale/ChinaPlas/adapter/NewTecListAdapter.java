package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.Parser;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/11/16.
 */

public class NewTecListAdapter extends CpsBaseAdapter<NewProductInfo> {
    private static final String TAG = "NewTecListAdapter";
    private ArrayList<NewProductInfo> list;
    private Context mContext;
    private OnIntentListener mListener;

    private final static int TYPE_ITEM = 0;
    private final static int TYPE_AD = 1;
    private int mType = 0;
    private NewProductInfo entity;
    private ArrayList<NewTec.ADProduct> adProducts=new ArrayList<>();
    private int adSize = 0;
    private NewTec newTec;
    private RequestOptions options;


    public NewTecListAdapter(Context mContext, ArrayList<NewProductInfo> list, OnIntentListener listener) {
        this.list = list;
        this.mContext = mContext;
        this.mListener = listener;

        options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        newTec = Parser.parseJsonFilesDirFile(NewTec.class, Constant.TXT_NEW_TEC);
    }

    public void setList(ArrayList<NewProductInfo> list){
        this.list=list;
        super.setList(list);
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
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(com.adsale.ChinaPlas.BR.adapter,this);
        binding.setVariable(com.adsale.ChinaPlas.BR.options,options);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if(list.get(position).adItem){
            return R.layout.item_new_tec_ad;
        }
        return R.layout.item_new_tec;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
