package com.adsale.ChinaPlas.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AdvertisementM5 {
	public String format;
	public String function;
	public String filepath;
	public String[] version;
	public String logo;
	public String[] product;
	
	public String[] companyID;
	public String[] website;
	public String[] action_eventID;
	public String[] action_seminarID;
	public String[] action_newsID;
	public List<Description> description;
	public ArrayList<M5Video> videos;

	public static class M5Video{
		public String[] imagelinks;
		public String[] videolinks;

		@Override
		public String toString() {
			return "M5Video{" +
					"imagelinks=" + Arrays.toString(imagelinks) +
					", videolinks=" + Arrays.toString(videolinks) +
					'}';
		}
	}
	
	@Override
	public String toString() {
		return "AdvertisementM5 [format=" + format + ", function=" + function + ", filepath=" + filepath + ", version=" + Arrays.toString(version)
				+ ", logo=" + logo + ", product=" + Arrays.toString(product) + ", action_companyID="
				+ Arrays.toString(companyID) + ", action_eventID=" + Arrays.toString(action_eventID) + ", action_seminarID="
				+ Arrays.toString(action_seminarID) + ", action_newsID=" + Arrays.toString(action_newsID) + ", description=" + description + "]";
	}
	
	

}
