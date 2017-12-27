package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.utils.AppUtil;

import java.util.ArrayList;

public class ExhibitorHistoryAdapter extends RecyclerView.Adapter<ViewHolder>{
	private static final String TAG = "ExhibitorHistoryAdapter";
	private Context mContext;
	private ArrayList<HistoryExhibitor> lists;
	private int currLang;
	private LayoutInflater inflater;
	
	private final int TYPE_HEADER=0;
	private final int TYPE_ITEM=1;
	private final int TYPE_FOOTER=2;
	
	private int type;
	
	private HeaderViewHolder2 HeaderViewHolder2;
	private ExhibitorItemViewHolder itemViewHolder;

	private HistoryExhibitor exhibitor;
	
	public ExhibitorHistoryAdapter(Context context, ArrayList<HistoryExhibitor> lists) {
		this.mContext=context;
		this.lists=lists;
		currLang= AppUtil.getCurLanguage();
		inflater= LayoutInflater.from(mContext);
	}
	
	public void setList(ArrayList<HistoryExhibitor> lists){
		this.lists=lists;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
		if(viewType==TYPE_HEADER){
			View headerView=inflater.inflate(R.layout.item_history_parent, container,false);
			return new HeaderViewHolder2(headerView);
		}else {
			View itemView=inflater.inflate(R.layout.item_history_exhi, container,false);
			return new ExhibitorItemViewHolder(itemView);
		}
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		exhibitor = lists.get(position);
		type=holder.getItemViewType();
		
		if(type==TYPE_HEADER){
			HeaderViewHolder2 = (HeaderViewHolder2) holder;
			if(lists.get(position).status==1){
				HeaderViewHolder2.tvGroupName.setText(R.string.today);
			}else if(lists.get(position).status==0){
				HeaderViewHolder2.tvGroupName.setText(R.string.yesterday);
			}else{
				HeaderViewHolder2.tvGroupName.setText(R.string.past_records);
			}
			showItemText(HeaderViewHolder2);
			
		}else if(type==TYPE_ITEM){
			itemViewHolder = (ExhibitorItemViewHolder) holder;
			showItemText(itemViewHolder);
		}
//		else{
//			footerViewHolder=(ExhibitorFooterViewHolder) holder;
//		}
	}
	
	private void showItemText(ExhibitorItemViewHolder holder){
		holder.tvCompanyName.setText(exhibitor.getCompanyName(currLang));
		holder.tvExhibitorNo.setText(exhibitor.getBooth());
		holder.tvTime.setText(exhibitor.getTime());
		holder.tvCount.setText(exhibitor.frequency+"");
	}


	@Override
	public int getItemViewType(int position) {
		if(position==0){//Today
			return TYPE_HEADER;
		}else if(lists.get(position).status==1){
			return TYPE_ITEM;
		}else if(lists.get(position-1).status==1&&lists.get(position).status==0){//Yesterday
			return TYPE_HEADER;
		}else if(lists.get(position).status==0){
			return TYPE_ITEM;
		}else if(lists.get(position-1).status==0&&lists.get(position).status==-1){//Past
			return TYPE_HEADER;
		}else if(lists.get(position).status==-1){
			return TYPE_ITEM;
		}
		else if(position+1==getItemCount()){
			return TYPE_FOOTER;
		}else{
			return TYPE_ITEM;
		}
		
	}
	
	
	private class HeaderViewHolder2 extends ExhibitorItemViewHolder{
		private TextView tvGroupName;
		public HeaderViewHolder2(View itemView) {
			super(itemView);
			tvGroupName=(TextView) itemView.findViewById(R.id.tv_header_day);
		}
		
	}
	
	private class ExhibitorItemViewHolder extends ViewHolder {
		private TextView tvCompanyName;
		private TextView tvExhibitorNo;
		private TextView tvTime,tvCount;

		public ExhibitorItemViewHolder(View itemView) {
			super(itemView);
			tvCompanyName=(TextView) itemView.findViewById(R.id.tv_company_name);
			tvExhibitorNo=(TextView) itemView.findViewById(R.id.tv_booth);
			tvTime=(TextView) itemView.findViewById(R.id.tv_time);
			tvCount=(TextView) itemView.findViewById(R.id.tv_count);
		}
	}
	

	@Override
	public int getItemCount() {
		return lists.size();//加上footer_view
	}

}
