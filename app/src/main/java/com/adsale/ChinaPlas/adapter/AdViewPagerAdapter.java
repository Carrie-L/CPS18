package com.adsale.ChinaPlas.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AdViewPagerAdapter extends PagerAdapter {
	private List<View> mData;
	private boolean needRefresh=false;

	public AdViewPagerAdapter(List<View> mData) {
		this.mData = mData;
	}

	public void setList(List<View> list,boolean needRefresh){
		mData = list;
		this.needRefresh=needRefresh;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		if(needRefresh){
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = mData.get(position);
		container.addView(v);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// super.destroyItem(container, position, object);
		container.removeView(mData.get(position));
	}










}