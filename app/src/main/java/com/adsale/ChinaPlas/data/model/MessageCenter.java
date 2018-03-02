package com.adsale.ChinaPlas.data.model;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.utils.AppUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageCenter {
	public ArrayList<Message> notifications_en;
	public ArrayList<Message> notifications_sc;
	public ArrayList<Message> notifications_tc;

	public ArrayList<Message> getNotifications(){
		if(App.mLanguage.get()==0){
			return notifications_tc;
		}else if(App.mLanguage.get()==1){
			return notifications_en;
		}else{
			return notifications_sc;
		}
	}

	public static class Message implements Serializable {
		/**
		* @Fields serialVersionUID : TODO
		*/
		private static final long serialVersionUID = 1L;
		public String Message;
		public String date;
		public String function;
		public String ID;

		@Override
		public String toString() {
			return "Message{" +
					"Message='" + Message + '\'' +
					", date='" + date + '\'' +
					", function=" + function +
					", ID='" + ID + '\'' +
					'}';
		}
	}
}
