package com.adsale.ChinaPlas.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageCenter {
	public ArrayList<Message> notifications;

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
