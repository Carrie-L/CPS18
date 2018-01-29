package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2018/1/29.
 */

public class ExhibitorListAdapter extends CpsBaseAdapter<Exhibitor> {
    private static final String TAG = "ExhibitorListAdapter";
    private Context mContext;
    private ArrayList<Exhibitor> exhibitors;
    private LayoutInflater inflater;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private Exhibitor exhibitor;

    private ExhibitorRepository mRepository;
    private OnItemClickCallback mCallback;

    public final ObservableBoolean isM3Show = new ObservableBoolean(false);

    private ViewDataBinding mBinding;

    public ExhibitorListAdapter(Context context, ArrayList<Exhibitor> lists, ExhibitorRepository repository, OnItemClickCallback callback, boolean isM3Open) {
        this.mContext = context;
        this.exhibitors = lists;
        mRepository = repository;
        this.mCallback = callback;
        isM3Show.set(isM3Open);
        inflater = LayoutInflater.from(mContext);
    }

    public void setList(ArrayList<Exhibitor> lists) {
        this.exhibitors = lists;
        LogUtil.e(TAG, "setList_" + exhibitors.size());
        notifyDataSetChanged();
    }

    public void setList2(ArrayList<Exhibitor> lists) {
        this.exhibitors = lists;
    }

    @Override
    protected Object getObjForPosition(int position) {
        mBinding.setVariable(BR.pos,position);
        return exhibitors.get(position);
    }

    public void onCollect(Exhibitor exhibitor, int pos) {
        exhibitor.setIsFavourite(exhibitor.getIsFavourite() == 0 ? 1 : 0);
        exhibitor.isCollected.set(exhibitor.getIsFavourite() == 1);
        exhibitors.set(pos, exhibitor);
        mRepository.updateItemData(exhibitor.getCompanyID(), exhibitor.getIsFavourite());
        LogUtil.i(TAG, "pos=" + pos + "//isCollected=" + exhibitor.isCollected.get() + "//IsFavourite=" + exhibitor.getIsFavourite()
                + "//IsFavourite2=" + exhibitors.get(pos).getIsFavourite() + ",logo=" + exhibitor.getPhotoFileName());
    }

    public void onItemClick(Exhibitor exhibitor, int pos) {
        if (mCallback != null) {
            mCallback.onItemClick(exhibitor, pos);
        }
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if (position == 0) {
            return R.layout.item_exhi_detail_header;
        } else if (exhibitors.get(position).getSort().equals(exhibitors.get(position - 1).getSort())) {
            return R.layout.item_exhi_detail_child;
        } else {
            return R.layout.item_exhi_detail_header;
        }
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        mBinding= binding;
        binding.setVariable(BR.adapter,this);
        super.bindVariable(binding);
    }

    @Override
    public int getItemCount() {
        return exhibitors.size();
    }
}
