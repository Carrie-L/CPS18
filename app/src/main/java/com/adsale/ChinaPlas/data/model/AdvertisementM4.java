package com.adsale.ChinaPlas.data.model;

import java.util.Arrays;

public class AdvertisementM4 {
	public String format;
	public String function;
	public String filepath;
	public String[] version;
	public String[] action_companyID;
	public String[] action_eventID;
	public String[] action_seminarID;
	public String[] action_newsID;
	public String[] floor;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String[] getVersion() {
		return version;
	}
	public void setVersion(String[] version) {
		this.version = version;
	}
	public String[] getAction_companyID() {
		return action_companyID;
	}
	public void setAction_companyID(String[] action_companyID) {
		this.action_companyID = action_companyID;
	}
	public String[] getAction_eventID() {
		return action_eventID;
	}
	public void setAction_eventID(String[] action_eventID) {
		this.action_eventID = action_eventID;
	}
	public String[] getAction_seminarID() {
		return action_seminarID;
	}
	public void setAction_seminarID(String[] action_seminarID) {
		this.action_seminarID = action_seminarID;
	}
	public String[] getAction_newsID() {
		return action_newsID;
	}
	public void setAction_newsID(String[] action_newsID) {
		this.action_newsID = action_newsID;
	}
	public String[] getFloor() {
		return floor;
	}
	public void setFloor(String[] floor) {
		this.floor = floor;
	}
	@Override
	public String toString() {
		return "AdvertisementM4 [format=" + format + ", function=" + function + ", filepath=" + filepath + ", version=" + Arrays.toString(version)
				+ ", action_companyID=" + Arrays.toString(action_companyID) + ", action_eventID=" + Arrays.toString(action_eventID)
				+ ", action_seminarID=" + Arrays.toString(action_seminarID) + ", action_newsID=" + Arrays.toString(action_newsID) + ", floor="
				+ Arrays.toString(floor) + "]";
	}
	
	
	
	

}
