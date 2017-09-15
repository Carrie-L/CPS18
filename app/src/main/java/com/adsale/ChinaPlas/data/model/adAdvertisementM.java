package com.adsale.ChinaPlas.data.model;

/**
 * 对应的M1-M6的每一个广告详细信息。
 * 
 * @author orz
 * 
 */
public class adAdvertisementM {

	public String cmsID;
	public String filepath;
	public String format;
	public String function;
	public String isChange;
	public String time;
	public String version;

	public String action_companyID;
	public String action_companyID_en;
	public String action_companyID_sc;
	public String action_companyID_tc;
	
	public String action_eventID;
	public String action_newsID;
	public String action_seminarID;
	public String action_seminariD;


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
	
	public String getCompanyID(int language){
		if (language==1) {
			return action_companyID_en;
		} else if (language==2) {
			return action_companyID_sc;
		} else {
			return action_companyID_tc;
		}
	}

}
