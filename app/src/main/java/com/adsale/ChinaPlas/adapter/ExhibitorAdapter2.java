package com.adsale.ChinaPlas.adapter;

import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.databinding.ItemExhibitorsBinding;
import com.adsale.ChinaPlas.ui.ExhibitorAllListActivity;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/8/13.
 */

public class ExhibitorAdapter2 extends CpsBaseAdapter<Exhibitor> {
    private static final String TAG = "ExhibitorAdapter2";
    private ExhibitorAllListActivity mActivity;
    private ArrayList<Exhibitor> exhibitors;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private int mViewType;

    private ItemExhibitorsBinding mBinding;

    public ObservableBoolean isPhotoEmpty = new ObservableBoolean(true);
    public ObservableBoolean isHeader = new ObservableBoolean();
    private Exhibitor exhibitor;

    public ExhibitorAdapter2(ExhibitorAllListActivity activity, ArrayList<Exhibitor> lists) {
        this.mActivity = activity;
        this.exhibitors = lists;
    }

    @Override
    public void setList(ArrayList<Exhibitor> list) {
        this.exhibitors = list;
        super.setList(list);
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        exhibitor = exhibitors.get(position);
        holder.bind(exhibitor);

        if (TextUtils.isEmpty(exhibitor.getPhotoFileName())) {
            isPhotoEmpty.set(true);
        } else {
            isPhotoEmpty.set(false);
            mBinding.sdvLogo.setImageURI(Uri.parse(exhibitor.getPhotoFileName()));
        }

        isHeader.set(mViewType == TYPE_HEADER);

        LogUtil.i(TAG,"onBindViewHolder: position="+position+",isHeader="+isHeader.get());
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        super.bindVariable(binding);
        this.mBinding = (ItemExhibitorsBinding) binding;
        binding.setVariable(BR.activity, mActivity);
        binding.setVariable(BR.adapter, this);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return exhibitors.get(position);
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEADER;
//        } else if (exhibitors.get(position).getSort().equals(exhibitors.get(position - 1).getSort())) {
//            return TYPE_ITEM;
//        } else {
//            return TYPE_HEADER;
//        }
//    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        LogUtil.i(TAG,"getLayoutIdForPosition: position="+position);
        if (position == 0) {
            mViewType= TYPE_HEADER;
        } else if (exhibitors.get(position).getSort().equals(exhibitors.get(position - 1).getSort())) {
            mViewType= TYPE_ITEM;
        } else {
            mViewType= TYPE_HEADER;
        }
        return R.layout.item_exhibitors;
    }

    @Override
    public int getItemCount() {
        return exhibitors.size();
    }
}
