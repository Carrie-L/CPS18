package com.adsale.ChinaPlas.data.model;

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.utils.AppUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Carrie on 2017/9/19.
 * 同期活动实体类
 */

public class ConcurrentEvent implements Parcelable {

    /* 同期活动zip包下载地址base url. Full url is 'htmlFilePath+ pages.pageID' */
    public String htmlFilePath;

    public ArrayList<Pages> pages;

    public static class Pages {
        public String title;
        public String getTitle(){
            return AppUtil.getName(title_tc,title_en,title_cn);
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
        public String[] applications;

        /* 活动地点 */
        public String location;

        /* 为时 */
        public String duration;

        /* 列表排序字段 */
        public String sort;

        /* 同期活动ID */
        public String pageID;

        /* 头部Bar */
        public final ObservableBoolean isTypeLabel = new ObservableBoolean();

        @Override
        public String toString() {
            return "Pages{" +
                    "title_en='" + title_en + '\'' +
                    ", title_cn='" + title_cn + '\'' +
                    ", title_tc='" + title_tc + '\'' +
                    ", pre_reg='" + pre_reg + '\'' +
                    ", entrance_fee='" + entrance_fee + '\'' +
                    ", date='" + date + '\'' +
                    ", applications=" + Arrays.toString(applications) +
                    ", location='" + location + '\'' +
                    ", duration='" + duration + '\'' +
                    ", sort='" + sort + '\'' +
                    ", pageID='" + pageID + '\'' +
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
