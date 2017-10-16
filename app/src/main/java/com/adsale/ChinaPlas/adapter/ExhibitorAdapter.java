package com.adsale.ChinaPlas.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.databinding.ItemExhiDetailChildBinding;
import com.adsale.ChinaPlas.databinding.ItemExhiDetailHeaderBinding;
import com.adsale.ChinaPlas.utils.LogUtil;

import static com.adsale.ChinaPlas.R.id.sdv_logo;

public class ExhibitorAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "ExhibitorAdapter";
    private Context mContext;
    private ArrayList<Exhibitor> exhibitors;
    private int currLang;
    private LayoutInflater inflater;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    boolean showCategory = false;
    private Exhibitor exhibitor;
    private HeaderViewHolder headerViewHolder;
    private ExhibitorItemViewHolder itemViewHolder;


    private ExhibitorRepository mRepository;
    private OnItemClickCallback mCallback;
    public int mPos;

    public ExhibitorAdapter(Context context, ArrayList<Exhibitor> lists, ExhibitorRepository repository, OnItemClickCallback callback) {
        this.mContext = context;
        this.exhibitors = lists;
        mRepository = repository;
        this.mCallback = callback;
        inflater = LayoutInflater.from(mContext);

        LogUtil.e(TAG, "ExhibitorAdapter");

    }

    public void setList(ArrayList<Exhibitor> lists) {
        this.exhibitors = lists;
        LogUtil.e(TAG, "setList_" + exhibitors.size());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        if (viewType == TYPE_HEADER) {
//            View headerView=inflater.inflate(R.layout.item_exhi_detail_header, container,false);
            ItemExhiDetailHeaderBinding headerBinding = ItemExhiDetailHeaderBinding.inflate(inflater, container, false);
            return new HeaderViewHolder(headerBinding);
        } else {
//            View itemView=inflater.inflate(R.layout.item_exhi_detail_child, container,false);
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
        mRepository.updateItemData(exhibitor);
        LogUtil.i(TAG, "pos=" + pos + "//isCollected=" + exhibitor.isCollected.get() + "//IsFavourite=" + exhibitor.getIsFavourite() + "//IsFavourite2=" + exhibitors.get(pos).getIsFavourite());
    }

    public void onItemClick(Exhibitor exhibitor) {
        if (mCallback != null) {
            mCallback.onItemClick(exhibitor);
        }
    }
}
