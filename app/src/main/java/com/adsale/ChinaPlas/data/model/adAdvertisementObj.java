package com.adsale.ChinaPlas.data.model;

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


}
