package com.adsale.ChinaPlas.data.model;

import com.adsale.ChinaPlas.App;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * AdvertisementObj.java 好像只是定义M1-M6.
 *
 * @author orz
 */
public class adAdvertisementObj {
    public AdvertisementM1 M1;
    public adAdvertisementM M2;
    public adAdvertisementM M3;
    public AdvertisementM4 M4;
    public AdvertisementM5 M5;
    public AdvertisementM6 M6;
    public M6V2 M6_V2;
    public Common Common;
    public AdM4 M4_left;
    public AdM4 M4_right;

//	public static adAdvertisementObj parseAd() {
//		return new Gson().fromJson(AppUtil.readTxt(Constant.AdvertisementPath), adAdvertisementObj.class);
//	}

    @Override
    public String toString() {
        return " Common=" + Common + "adAdvertisementObj [M1=" + M1 + ", M2=" + M2 + ", M3=" + M3 + ", M4=" + M4 + ", M5=" + M5 + ", M6="
                + M6 + "]";
    }

    public static class AdM4 {
        public String[] version;
        public Integer[] function;
        public String[] floor;
        public String[] action_companyID;
        public String[] action_eventID;
        public String filepath;
        public String format;
    }

    public static class M6V2 {
        public String[] version;
        public String[] function;
        public String filepath;
        public String format;
        public String header;
        public String logo;
        public String banner;
        public ArrayList<String> companyID;
        public ArrayList<Topic> topics;
        public EventID EventID;

        public static class Topic {
            public String eventID;
            public String image_sc;
            public String image_tc;
            public String image_en;
            public String description;

            public String getImage(){
                return App.mLanguage.get()==0?image_tc:App.mLanguage.get()==1?image_en:image_sc;
            }

            @Override
            public String toString() {
                return "Topic{" +
                        "eventID='" + eventID + '\'' +
                        ", image_sc='" + image_sc + '\'' +
                        ", image_tc='" + image_tc + '\'' +
                        ", image_en='" + image_en + '\'' +
                        ", description='" + description + '\'' +
                        '}';
            }
        }

        public static class EventID {
            public String[] EN;
            public String[] SC;
            public String[] TC;

            public String[] getEventId(int language) {
                if (language == 0) {
                    return TC;
                } else if (language == 1) {
                    return EN;
                } else {
                    return SC;
                }
            }

        }

        @Override
        public String toString() {
            return "M6B{" +
                    "version=" + Arrays.toString(version) +
                    ", function=" + Arrays.toString(function) +
                    ", filepath='" + filepath + '\'' +
                    ", format='" + format + '\'' +
                    ", header='" + header + '\'' +
                    ", logo='" + logo + '\'' +
                    ", banner='" + banner + '\'' +
                    ", companyID=" + companyID.toString() +
                    ", topics=" + topics +
                    '}';
        }
    }


}
