package com.adsale.ChinaPlas.data.model;

public class LogJson {
	public String AppName;
	public int ActionGroup;
	public int ActionID;
	public String DeviceID;
	public String Platform;
	public int LangID;
	public String PreregID;
	public String Type;
	public String SubType;
	public String Location;
	public String TrackingName;
	public String TrackingOS;
	public String CreateDate;
	public int Year;
	public String TimeZone;
	public String VisitorID;
	public String IsTest;
	public String SystemLanguage;
	public String CountryCode;
//	private boolean IsTest;
	@Override
	public String toString() {
		return "LogJson [AppName=" + AppName + ", ActionGroup=" + ActionGroup + ", ActionID=" + ActionID + ", DeviceID=" + DeviceID + ", Platform="
				+ Platform + ", LangID=" + LangID + ", PreregID=" + PreregID + ", Type=" + Type + ", SubType=" + SubType + ", Location=" + Location
				+ ", TrackingName=" + TrackingName + ", TrackingOS=" + TrackingOS + ", CreateDate=" + CreateDate + ", Year=" + Year + ", TimeZone="
				+ TimeZone + ", VisitorID=" + VisitorID + ", IsTest=" + IsTest + ", SystemLanguage=" + SystemLanguage + ", CountryCode="
				+ CountryCode + "]";
	}
	
	
}