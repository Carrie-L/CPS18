package com.adsale.ChinaPlas.dao;

import java.util.ArrayList;

import com.adsale.ChinaPlas.utils.LogUtil;

public class SideBar {
	private static final String TAG = null;
	public String action;
	public ArrayList<String> subViews;

	/** 默认全部展开，则设为true；默认全部折叠，则设为false */
	public boolean isExpanded = false;
	public String nameEN;
	public String nameSC;
	public String nameTC;

	public String getName(int language) {
		LogUtil.e(TAG, "getName::" + nameEN + ",sc=" + nameSC + ",tc=" + nameTC);
		if (language == 0) {
			return nameTC;
		} else if (language == 1) {
			return nameEN;
		} else {
			return nameSC;
		}
	}

	public void setName(String name, int language) {
		if (language == 0) {
			this.nameTC = name;
		} else if (language == 1) {
			this.nameEN = name;
		} else {
			this.nameSC = name;
		}
	}

	public SideBar() {
		// TODO Auto-generated constructor stub
	}

	public SideBar(String action, ArrayList<String> subViews, boolean isExpanded) {
		super();
		this.action = action;
		this.subViews = subViews;
		this.isExpanded = isExpanded;
	}

	@Override
	public String toString() {
		return "SideBar [action=" + action + ", subViews=" + subViews + ", isExpanded=" + isExpanded + ", name="
				+ nameEN + "]";
	}

}
