package com.adsale.ChinaPlas.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2017/10/23.
 * Top
 * Left
 * Right
 */

public class MainPic implements Parcelable {
    public String IconPath;
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

    public static class Banners implements Parcelable {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.EN, flags);
            dest.writeParcelable(this.SC, flags);
            dest.writeParcelable(this.TC, flags);
        }

        public Banners() {
        }

        protected Banners(Parcel in) {
            this.EN = in.readParcelable(Property.class.getClassLoader());
            this.SC = in.readParcelable(Property.class.getClassLoader());
            this.TC = in.readParcelable(Property.class.getClassLoader());
        }

        public static final Creator<Banners> CREATOR = new Creator<Banners>() {
            public Banners createFromParcel(Parcel source) {
                return new Banners(source);
            }

            public Banners[] newArray(int size) {
                return new Banners[size];
            }
        };
    }

    public static class Property implements Parcelable {
        public String BannerImage;
        public String BannerImage_Pad;
        public String Function;
        public String InnerPage;
        public String Link;
        public String companyID;
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
                    ", companyID='" + companyID + '\'' +
                    ", eventID='" + eventID + '\'' +
                    ", seminarID='" + seminarID + '\'' +
                    ", newsID='" + newsID + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.BannerImage);
            dest.writeString(this.Function);
            dest.writeString(this.InnerPage);
            dest.writeString(this.Link);
            dest.writeString(this.companyID);
            dest.writeString(this.eventID);
            dest.writeString(this.seminarID);
            dest.writeString(this.newsID);
        }

        public Property() {
        }

        protected Property(Parcel in) {
            this.BannerImage = in.readString();
            this.Function = in.readString();
            this.InnerPage = in.readString();
            this.Link = in.readString();
            this.companyID = in.readString();
            this.eventID = in.readString();
            this.seminarID = in.readString();
            this.newsID = in.readString();
        }

        public static final Creator<Property> CREATOR = new Creator<Property>() {
            public Property createFromParcel(Parcel source) {
                return new Property(source);
            }

            public Property[] newArray(int size) {
                return new Property[size];
            }
        };
    }


    @Override
    public String toString() {
        return "MainPic{" +
                "IconPath='" + IconPath + '\'' +
                ", TopBanners=" + TopBanners +
                ", LeftBottomBanner=" + LeftBottomBanner +
                ", RightBottomBanner=" + RightBottomBanner +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.IconPath);
        dest.writeList(this.TopBanners);
        dest.writeParcelable(this.LeftBottomBanner, flags);
        dest.writeParcelable(this.RightBottomBanner, flags);
    }

    public MainPic() {
    }

    protected MainPic(Parcel in) {
        this.IconPath = in.readString();
        this.TopBanners = new ArrayList<Banners>();
        in.readList(this.TopBanners, List.class.getClassLoader());
        this.LeftBottomBanner = in.readParcelable(Banners.class.getClassLoader());
        this.RightBottomBanner = in.readParcelable(Banners.class.getClassLoader());
    }

    public static final Parcelable.Creator<MainPic> CREATOR = new Parcelable.Creator<MainPic>() {
        public MainPic createFromParcel(Parcel source) {
            return new MainPic(source);
        }

        public MainPic[] newArray(int size) {
            return new MainPic[size];
        }
    };
}
