package com.adsale.ChinaPlas.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.SideBar;
import com.adsale.ChinaPlas.databinding.ItemSideLetterBinding;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2017/8/12.
 */

public class SideAdapter extends CpsBaseAdapter<SideBar> {
    private ArrayList<SideBar> list;
    private int mSideHeight;
    private int count;
    private int itemHeight;
    private TextView tvLetter;
    private ItemSideLetterBinding binding1;

    public SideAdapter(ArrayList<SideBar> list, int height) {
        this.list = list;
        this.mSideHeight=height;
        LogUtil.i(TAG,"SideAdapter");
    }

    @Override
    public void setList(ArrayList<SideBar> list) {
        this.list = list;
        super.setList(list);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        LogUtil.i(TAG,"onBindViewHolder");

        tvLetter = binding1.tvLetter;
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) tvLetter.getLayoutParams();
        params.height=itemHeight;
        tvLetter.setLayoutParams(params);


    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        super.bindVariable(binding);
        LogUtil.i(TAG,"bindVariable");
        binding1 = (ItemSideLetterBinding) binding;

        count=list.size();
        itemHeight=mSideHeight/count;
        LogUtil.i("SideAdapter","itemHeight="+itemHeight);

    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_side_letter;
    }

    @Override
    public int getItemCount() {
        LogUtil.i(TAG,"getItemCount");
        return list.size();
    }
}
