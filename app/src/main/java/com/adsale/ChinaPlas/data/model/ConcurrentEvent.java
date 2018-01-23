package com.adsale.ChinaPlas.data.model;

import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2017/9/19.
 * 同期活动实体类
 */

public class ConcurrentEvent implements Parcelable {

    /* 同期活动zip包下载地址base url. Full url is 'htmlFilePath+ pages.pageID' */
    public String htmlFilePath;

    public ArrayList<Pages> pages;

    public AdInfo AdInfo;

    public static class Pages {
        public String title;

        public String getTitle() {
            return AppUtil.getName(title_tc, title_en, title_cn);
        }

        /* 同期活动标题 */
        public String title_en;
        public String title_cn;
        public String title_tc;

        /* 是否需要预登记，0 不需要；1 需要，显示图标 */
        public String pre_reg;

        /*是否需要支付，1 需要，显示图标*/
        public String entrance_fee;

        /* 同期活动日期 */
        public String date;

        /* 应用行业ID，作为筛选条件 */
        public ArrayList<String> applications;

        /* 活动地点 */
        public String location;

        /* 为时 */
        public String duration;

        /* 列表排序字段 */
        public String sort;

        /* 同期活动ID */
        public String pageID;

        /* item 背景图 */
        public String imageLink;

        /* 平板 item 背景图 */
        public String imageLink_Pad;

        /* 头部Bar : 0 没有bar， 1 有bar；2 tech */
        public final ObservableInt isTypeLabel = new ObservableInt(0);

        @Override
        public String toString() {
            return "Pages{" +
                    "title='" + title + '\'' +
                    ", pre_reg='" + pre_reg + '\'' +
                    ", entrance_fee='" + entrance_fee + '\'' +
                    ", date='" + date + '\'' +
                    ", applications=" + applications +
                    ", location='" + location + '\'' +
                    ", duration='" + duration + '\'' +
                    ", sort='" + sort + '\'' +
                    ", pageID='" + pageID + '\'' +
                    ", imageLink='" + imageLink + '\'' +
                    ", imageLink_Pad='" + imageLink_Pad + '\'' +
                    ", isTypeLabel=" + isTypeLabel +
                    '}';
        }

        public String getImageLink() {
            return AppUtil.isTablet() ? imageLink_Pad : imageLink;
        }
    }

    public static class AdInfo {
        public String Image_SC;
        public String Image_TC;
        public String Image_EN;
        public String Image_Pad_SC;
        public String Image_Pad_TC;
        public String Image_Pad_EN;
        public String EventID_SC;
        public String EventID_TC;
        public String EventID_EN;

        public String getImageUrl() {
            if (AppUtil.isTablet()) {
                return App.mLanguage.get() == 0 ? Image_Pad_TC : App.mLanguage.get() == 1 ? Image_Pad_EN : Image_Pad_SC;
            }
            return App.mLanguage.get() == 0 ? Image_TC : App.mLanguage.get() == 1 ? Image_EN : Image_SC;
        }

        public String getEventID() {
            return App.mLanguage.get() == 0 ? EventID_TC : App.mLanguage.get() == 1 ? EventID_EN : EventID_SC;
        }

        @Override
        public String toString() {
            return "AdInfo{" +
                    "Image_SC='" + Image_SC + '\'' +
                    ", Image_TC='" + Image_TC + '\'' +
                    ", Image_EN='" + Image_EN + '\'' +
                    ", Image_Pad_SC='" + Image_Pad_SC + '\'' +
                    ", Image_Pad_TC='" + Image_Pad_TC + '\'' +
                    ", Image_Pad_EN='" + Image_Pad_EN + '\'' +
                    ", EventID_SC='" + EventID_SC + '\'' +
                    ", EventID_TC='" + EventID_TC + '\'' +
                    ", EventID_EN='" + EventID_EN + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ConcurrentEvent{" +
                "htmlFilePath='" + htmlFilePath + '\'' +
                ", pages=" + pages +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.htmlFilePath);
        dest.writeList(this.pages);
    }

    protected ConcurrentEvent(Parcel in) {
        this.htmlFilePath = in.readString();
        this.pages = new ArrayList<Pages>();
        in.readList(this.pages, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<ConcurrentEvent> CREATOR = new Parcelable.Creator<ConcurrentEvent>() {
        public ConcurrentEvent createFromParcel(Parcel source) {
            return new ConcurrentEvent(source);
        }

        public ConcurrentEvent[] newArray(int size) {
            return new ConcurrentEvent[size];
        }
    };
}
