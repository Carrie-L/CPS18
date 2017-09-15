package com.adsale.ChinaPlas.data.model;

import java.util.List;


public class AdvertisementM6 {
	public String format;
	public String[] function;
	public String filepath;
	public String[] version;
	public String logo;
	public String banner;
	public String header;
	
	public String[] companyID;
	public String[] website;
	public String[] advertisementEventID_SC;
	public String[] advertisementEventID_TC;
	public String[] advertisementEventID_EN;
	public List<Description> description;
	public List<Data> data;

	public String[] getAdEventID(int language){
		if(language==0){
			return advertisementEventID_TC;
		}else if(language==1){
			return advertisementEventID_EN;
		}else{
			return advertisementEventID_SC;
		}
	}
	
	public static class Data {
		public String date;
		public String time;
		public String hall;
		public String seminalRoom;
		public String booth;


	}
	
	

}
