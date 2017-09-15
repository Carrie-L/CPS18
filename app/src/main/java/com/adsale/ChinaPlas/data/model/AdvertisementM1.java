package com.adsale.ChinaPlas.data.model;

import java.util.Arrays;
import java.util.List;

public class AdvertisementM1 {
	public String format;
	public int function;
	public String filepath;
	public int isChange;	
	public String[] version;
	public List<String[]> action_companyID;
	public String[] action_eventID;
	public String[] action_seminarID;
	public String[] action_newsID;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getFunction() {
		return function;
	}
	public void setFunction(int function) {
		this.function = function;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public int getIsChange() {
		return isChange;
	}
	public void setIsChange(int isChange) {
		this.isChange = isChange;
	}
	public String[] getVersion() {
		return version;
	}
	public void setVersion(String[] version) {
		this.version = version;
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
	
	
	public static class M1CompangID{
		
	}


	public List<String[]> getAction_companyID() {
		return action_companyID;
	}
	public void setAction_companyID(List<String[]> action_companyID) {
		this.action_companyID = action_companyID;
	}
	@Override
	public String toString() {
		return "AdvertisementM1 [format=" + format + ", function=" + function + ", filepath=" + filepath + ", isChange=" + isChange + ", version="
				+ Arrays.toString(version) + ", action_companyID=" + action_companyID + ", action_eventID=" + Arrays.toString(action_eventID)
				+ ", action_seminarID=" + Arrays.toString(action_seminarID) + ", action_newsID=" + Arrays.toString(action_newsID) + "]";
	}
	
	
	

}
