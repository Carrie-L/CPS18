package com.adsale.ChinaPlas.data.model;

import java.util.ArrayList;

public class adM6Advertisement {
	public String version;
	public String time;
	public ArrayList<String> advertisementM6DescriptionEng;
	public ArrayList<String> advertisementM6DescriptionTC;
	public ArrayList<String> advertisementM6DescriptionSC;
	public ArrayList<String> advertisementM6Banner;
	public ArrayList<String> advertisementM6Icon;
	public ArrayList<String> advertisementPosition;
	public ArrayList<String> advertisementEventID;
	public ArrayList<String> topBannerPosition;
	public ArrayList<String> topListingADBanner;
	public ArrayList<ArrayList<String>> topListingInfoEng;
	public ArrayList<ArrayList<String>> topListingInfoTC;
	public ArrayList<ArrayList<String>> topListingInfoSC;

	public String getStart() {
		if (time != null && time.split("-").length > 0) {
			return time.split("-")[0];
		}
		return "";
	}

	public String getEnd() {
		if (time != null && time.split("-").length > 0) {
			return time.split("-")[1];
		}
		return "";
	}
}
