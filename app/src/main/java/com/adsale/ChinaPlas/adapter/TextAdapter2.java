package com.adsale.ChinaPlas.adapter;

/**
 * Created by Carrie on 2017/11/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.ui.ExhibitorAllListActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
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
    private Context mContext;
    private int mType;

    public TextAdapter2(ArrayList<Text2> list, Context context, int type) {
        this.list = list;
        this.mType = type;
        this.mContext = context;
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

    public void onItemClick(String id) {
        LogUtil.i(TAG, "ID=" + id);

        Intent intent = new Intent(mContext, ExhibitorAllListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", mType);
        intent.putExtra("id", id);
        mContext.startActivity(intent);
        if (AppUtil.isTablet()) {
            ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
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

