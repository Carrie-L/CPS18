package com.adsale.ChinaPlas.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import com.adsale.ChinaPlas.utils.LogUtil;

import static com.adsale.ChinaPlas.R.id.sdv_logo;

public class ExhibitorAdapter extends RecyclerView.Adapter<ViewHolder>{
    private static final String TAG = "ExhibitorAdapter";
    private Context mContext;
    private ArrayList<Exhibitor> exhibitors;
    private int currLang;
    private LayoutInflater inflater;

    private final int TYPE_HEADER=0;
    private final int TYPE_ITEM=1;

    boolean showCategory = false;
    private Exhibitor exhibitor;
    private HeaderViewHolder headerViewHolder;
    private ExhibitorItemViewHolder itemViewHolder;

    public ExhibitorAdapter(Context context,ArrayList<Exhibitor> lists) {
        this.mContext=context;
        this.exhibitors=lists;
        inflater=LayoutInflater.from(mContext);

        LogUtil.e(TAG, "ExhibitorAdapter");

    }

    public void setList(ArrayList<Exhibitor> lists){
        this.exhibitors=lists;
        LogUtil.e(TAG, "setList_"+exhibitors.size());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        if(viewType==TYPE_HEADER){
            View headerView=inflater.inflate(R.layout.item_exhi_detail_header, container,false);
            return new HeaderViewHolder(headerView);
        }else{
            View itemView=inflater.inflate(R.layout.item_exhi_detail_child, container,false);
            return new ExhibitorItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        exhibitor = exhibitors.get(position);

        if(holder.getItemViewType()==TYPE_HEADER){
            headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tvGroupName.setText(exhibitor.getSort());
            showItemText(headerViewHolder);

        }else{
            itemViewHolder = (ExhibitorItemViewHolder) holder;
            showItemText(itemViewHolder);
        }
    }

    private void showItemText(ExhibitorItemViewHolder holder){
        holder.tvCompanyName.setText(exhibitor.getCompanyName());
        holder.tvExhibitorNo.setText(exhibitor.getBoothNo());
        if(!TextUtils.isEmpty(exhibitor.getPhotoFileName().trim())){
            LogUtil.i(TAG,"getPhotoFileName = "+exhibitor.getPhotoFileName().trim());
            holder.sdvLogo.setVisibility(View.VISIBLE);
            holder.sdvLogo.setImageURI(Uri.parse(exhibitor.getPhotoFileName()));
        }else{
            holder.sdvLogo.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_HEADER;
        }else if(exhibitors.get(position).getSort().equals(exhibitors.get(position-1).getSort())){
            return TYPE_ITEM;
        }else{
            return TYPE_HEADER;
        }
    }


    private class HeaderViewHolder extends ExhibitorItemViewHolder{
        private TextView tvGroupName;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvGroupName=(TextView) itemView.findViewById(R.id.txtGroupName);
        }

    }

    private class ExhibitorItemViewHolder extends ViewHolder{
        private TextView tvCompanyName;
        private TextView tvExhibitorNo;
        private ImageView sdvLogo;

        public ExhibitorItemViewHolder(View itemView) {
            super(itemView);
            tvCompanyName=(TextView) itemView.findViewById(R.id.txtCategoryName);
            tvExhibitorNo=(TextView) itemView.findViewById(R.id.txtBoothNo);
            sdvLogo=(ImageView) itemView.findViewById(sdv_logo);

            sdvLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i(TAG,"sdvLogo clicked");
                    sdvLogo.setImageResource(R.drawable.btn_star);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return exhibitors.size();
    }

}
