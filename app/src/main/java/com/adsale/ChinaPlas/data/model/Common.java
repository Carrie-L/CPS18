package com.adsale.ChinaPlas.data.model;

public class Common {
	public String baseUrl;
	public String time;
	public String tablet;
	public String phone;
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTablet() {
		return tablet;
	}
	public void setTablet(String tablet) {
		this.tablet = tablet;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "Common [baseUrl=" + baseUrl + ", time=" + time + ", tablet=" + tablet + ", phone=" + phone + "]";
	}
	
	

}
