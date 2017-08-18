package com.adsale.ChinaPlas.adapter;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.databinding.ItemExhibitorsBinding;
import com.adsale.ChinaPlas.ui.ExhibitorAllListActivity;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/13.
 */

public class ExhibitorAdapter3 extends RecyclerView.Adapter<ExhibitorAdapter3.ExhibitorViewHolder> {
    private static final String TAG = "ExhibitorAdapter3";
    private ExhibitorAllListActivity mActivity;
    private ArrayList<Exhibitor> exhibitors;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private Exhibitor exhibitor;
    public ObservableBoolean isPhotoEmpty = new ObservableBoolean(true);
    public ObservableBoolean isHeader = new ObservableBoolean();
    public ObservableField<String> sort = new ObservableField<>();
    public ObservableField<String> companyName = new ObservableField<>();
    public ObservableField<String> boothNO = new ObservableField<>();

    public ExhibitorAdapter3(ExhibitorAllListActivity mActivity, ArrayList<Exhibitor> exhibitors) {
        this.mActivity = mActivity;
        this.exhibitors = exhibitors;
    }

    public void setList(ArrayList<Exhibitor> list){
        exhibitors=list;
        notifyDataSetChanged();
    }

    @Override
    public ExhibitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemExhibitorsBinding binding = ItemExhibitorsBinding.inflate(LayoutInflater.from(mActivity), parent, false);
        return new ExhibitorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ExhibitorViewHolder holder, int position) {
        exhibitor = exhibitors.get(position);
        holder.mBinding.setObj(exhibitor);
        holder.mBinding.setAdapter(this);

        isHeader.set(getItemViewType(position) == TYPE_HEADER);
        holder.mBinding.executePendingBindings();


    }

    @Override
    public int getItemCount() {
        return exhibitors.size();
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

    public class ExhibitorViewHolder extends RecyclerView.ViewHolder {
        private ItemExhibitorsBinding mBinding;

        public ExhibitorViewHolder(ItemExhibitorsBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

//            binding.setAdapter(ExhibitorAdapter2.this);
        }
    }
}
