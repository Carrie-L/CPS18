package com.adsale.ChinaPlas.data.model;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/23.
 * Top
 * Left
 * Right
 */

public class MainPic {
    public String iconPath;
    /**
     * 位于顶部的viewPager滚动图
     */
    public ArrayList<Banners> TopBanners;
    /**
     * 位于底部广告上方两按钮之一的 左按钮
     */
    public Banners LeftBottomBanner;
    /**
     * 位于底部广告上方两按钮之一的 右按钮
     */
    public Banners RightBottomBanner;

    public static class Banners {
        public Property EN;
        public Property SC;
        public Property TC;

        @Override
        public String toString() {
            return "Banners{" +
                    "EN=" + EN +
                    ", SC=" + SC +
                    ", TC=" + TC +
                    '}';
        }
    }

    public static class Property {
        public String BannerImage;
        public String Function;
        public String InnerPage;
        public String Link;
        public String companyID_sc;
        public String companyID_en;
        public String companyID_tc;
        public String eventID;
        public String seminarID;
        public String newsID;

        @Override
        public String toString() {
            return "Property{" +
                    "BannerImage='" + BannerImage + '\'' +
                    ", Function='" + Function + '\'' +
                    ", InnerPage='" + InnerPage + '\'' +
                    ", Link='" + Link + '\'' +
                    ", companyID_sc='" + companyID_sc + '\'' +
                    ", companyID_en='" + companyID_en + '\'' +
                    ", companyID_tc='" + companyID_tc + '\'' +
                    ", eventID='" + eventID + '\'' +
                    ", seminarID='" + seminarID + '\'' +
                    ", newsID='" + newsID + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "MainPic{" +
                "iconPath='" + iconPath + '\'' +
                ", TopBanners=" + TopBanners +
                ", LeftBottomBanner=" + LeftBottomBanner +
                ", RightBottomBanner=" + RightBottomBanner +
                '}';
    }
}
