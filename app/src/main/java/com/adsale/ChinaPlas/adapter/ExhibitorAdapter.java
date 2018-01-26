package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.databinding.ItemExhiDetailChildBinding;
import com.adsale.ChinaPlas.databinding.ItemExhiDetailHeaderBinding;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

public class ExhibitorAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "ExhibitorAdapter";
    private Context mContext;
    private ArrayList<Exhibitor> exhibitors;
    private LayoutInflater inflater;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private Exhibitor exhibitor;
    private HeaderViewHolder headerViewHolder;
    private ExhibitorItemViewHolder itemViewHolder;

    private ExhibitorRepository mRepository;
    private OnItemClickCallback mCallback;

    public final ObservableBoolean isM3Show = new ObservableBoolean(false);

    public ExhibitorAdapter(Context context, ArrayList<Exhibitor> lists, ExhibitorRepository repository, OnItemClickCallback callback, boolean isM3Open) {
        this.mContext = context;
        this.exhibitors = lists;
        mRepository = repository;
        this.mCallback = callback;
        isM3Show.set(isM3Open);
        inflater = LayoutInflater.from(mContext);

        LogUtil.e(TAG, "ExhibitorAdapter");

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
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        if (viewType == TYPE_HEADER) {
            ItemExhiDetailHeaderBinding headerBinding = ItemExhiDetailHeaderBinding.inflate(inflater, container, false);
            return new HeaderViewHolder(headerBinding);
        } else {
            ItemExhiDetailChildBinding childBinding = ItemExhiDetailChildBinding.inflate(inflater, container, false);
            return new ExhibitorItemViewHolder(childBinding);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        exhibitor = exhibitors.get(position);

        if (holder.getItemViewType() == TYPE_HEADER) {
            headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.bind(exhibitor);
            headerViewHolder.headerBinding.setAdapter(this);
        } else {
            itemViewHolder = (ExhibitorItemViewHolder) holder;
            itemViewHolder.bind(exhibitor);
            itemViewHolder.childBinding.setAdapter(this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (exhibitors.get(position).getSort().equals(exhibitors.get(position - 1).getSort())) {
            return TYPE_ITEM;
        } else {
            return TYPE_HEADER;
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ItemExhiDetailHeaderBinding headerBinding;

        public HeaderViewHolder(ItemExhiDetailHeaderBinding binding) {
            super(binding.getRoot());
            headerBinding = binding;
        }

        public void bind(Exhibitor exhibitor) {
            headerBinding.setObj(exhibitor);
            headerBinding.setPos(getAdapterPosition());
            headerBinding.executePendingBindings();
        }

    }

    private class ExhibitorItemViewHolder extends RecyclerView.ViewHolder {
        private ItemExhiDetailChildBinding childBinding;

        public ExhibitorItemViewHolder(ItemExhiDetailChildBinding binding) {
            super(binding.getRoot());
            childBinding = binding;
        }

        public void bind(Exhibitor exhibitor) {
            childBinding.setObj(exhibitor);
            childBinding.setPos(getAdapterPosition());
            childBinding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return exhibitors.size();
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
}
