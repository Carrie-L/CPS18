package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.model.AgentInfo;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by new on 2016/12/1.
 */
public class HotelAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<AgentInfo> hotels;
    private int mLanguage;
    private AgentInfo mEntity;
    private final int TYPE_PARENT = 0;
    private final int TYPE_CHILD = 1;
    private int type;
    private HotelChildViewHolder mChildHolder;
    private HotelParentViewHolder mParentHolder;
    private Context mContext;

    public HotelAdapter(ArrayList<AgentInfo> hotels, Context context) {
        this.hotels = hotels;
        this.mContext = context;
        mLanguage = AppUtil.getCurLanguage();

        if (mLanguage != 1) {
            hotels.remove(1);
        }

        LogUtil.i("HotelAdapter","list="+hotels.size()+","+hotels.toString());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PARENT) {
            return new HotelParentViewHolder(inflater.inflate(R.layout.item_hotel_parent, parent, false));
        } else {
            return new HotelChildViewHolder(inflater.inflate(R.layout.item_hotel_child, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mEntity = hotels.get(position);
        type = holder.getItemViewType();
        if (type == TYPE_PARENT) {
            mParentHolder = (HotelParentViewHolder) holder;
            if (mEntity.getTitle().toLowerCase().equals("empty")) {
                mParentHolder.parentName.setVisibility(View.GONE);
            } else {
                mParentHolder.parentName.setText(mEntity.getTitle());
            }
            setChildData(mParentHolder);
        } else if (type == TYPE_CHILD) {
            mChildHolder = (HotelChildViewHolder) holder;
            setChildData(mChildHolder);
        }
    }

    private void setChildData(HotelChildViewHolder holder) {
        if (mLanguage == 1 && mEntity.titleENG.contains("Endorsed")) {
            holder.imgContent.setVisibility(View.VISIBLE);
            holder.childName.setVisibility(View.GONE);
            holder.logo.setVisibility(View.GONE);
        } else {
            holder.childName.setText(mEntity.getAgentName());
//            holder.logo.setImageURI(Uri.parse(mEntity.logoMobile));
            Glide.with(mContext).load(Uri.parse(mEntity.logoMobile)).into(holder.logo);
            holder.imgContent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_PARENT;
        } else if (hotels.get(position).getTitle().equals(hotels.get(position - 1).getTitle())) {
            return TYPE_CHILD;
        } else {
            return TYPE_PARENT;
        }
    }

    public class HotelParentViewHolder extends HotelChildViewHolder {
        private TextView parentName;

        public HotelParentViewHolder(View itemView) {
            super(itemView);
            parentName = (TextView) itemView.findViewById(R.id.tv_hotel_name);
        }
    }

    public class HotelChildViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgContent;
        private ImageView logo;
        private TextView childName;

        public HotelChildViewHolder(View itemView) {
            super(itemView);

            imgContent = (ImageView) itemView.findViewById(R.id.travelImgContent);
            logo = (ImageView) itemView.findViewById(R.id.iv_travelInfo_hotel);
            childName = (TextView) itemView.findViewById(R.id.tv_travelInfo_hotel);
        }
    }

}
