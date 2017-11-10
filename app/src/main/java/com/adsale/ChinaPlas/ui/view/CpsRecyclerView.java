package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener.OnItemClickListener;

public class CpsRecyclerView extends RelativeLayout {

	private RecyclerView recyclerView;
	private Context mContext;
	private LinearLayoutManager llManager;

	public CpsRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CpsRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CpsRecyclerView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		mContext=context;
		View view= LayoutInflater.from(context).inflate(R.layout.f_recycler_view, this);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		recyclerView.setHasFixedSize(true);
		llManager = new LinearLayoutManager(context);
		recyclerView.setLayoutManager(llManager);
		recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL));
		
	}
	
	public <T> void setCpsAdapter(T adapter){
		recyclerView.setAdapter((Adapter) adapter);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, recyclerView, listener));
	}
	
	public void setOnRVScrollListener(OnScrollListener listener){
		recyclerView.addOnScrollListener(listener);
		
	}
	
	public int getLastVisibleItemPosition(){
		return llManager.findLastVisibleItemPosition();
	}

}
