package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.utils.AppUtil;

/**
 * Entity mapped to table "FLOOR".
 */
public class Floor implements Parcelable {

    private String FloorID;
    private String FloorNameTW;
    private String FloorNameEN;
    private String FloorNameCN;
    private Integer SEQ;

    // KEEP FIELDS - put your custom fields here
    public int exhibitorCount;
    public final ObservableBoolean isSelected = new ObservableBoolean(false);
    public final ObservableInt count = new ObservableInt(0);
    // KEEP FIELDS END

    public Floor() {
    }

    public Floor(String FloorID) {
        this.FloorID = FloorID;
    }

    public Floor(String FloorID, String FloorNameTW, String FloorNameEN, String FloorNameCN, Integer SEQ) {
        this.FloorID = FloorID;
        this.FloorNameTW = FloorNameTW;
        this.FloorNameEN = FloorNameEN;
        this.FloorNameCN = FloorNameCN;
        this.SEQ = SEQ;
    }

    public String getFloorID() {
        return FloorID;
    }

    public void setFloorID(String FloorID) {
        this.FloorID = FloorID;
    }

    public String getFloorNameTW() {
        return FloorNameTW;
    }

    public void setFloorNameTW(String FloorNameTW) {
        this.FloorNameTW = FloorNameTW;
    }

    public String getFloorNameEN() {
        return FloorNameEN;
    }

    public void setFloorNameEN(String FloorNameEN) {
        this.FloorNameEN = FloorNameEN;
    }

    public String getFloorNameCN() {
        return FloorNameCN;
    }

    public void setFloorNameCN(String FloorNameCN) {
        this.FloorNameCN = FloorNameCN;
    }

    public Integer getSEQ() {
        return SEQ;
    }

    public void setSEQ(Integer SEQ) {
        this.SEQ = SEQ;
    }

    // KEEP METHODS - put your custom methods here
    public String getFloorName() {
        return AppUtil.getName(FloorNameTW, FloorNameEN, FloorNameCN);
    }

    public void parser(String[] strings) {
        this.FloorID = strings[0];
        this.FloorNameEN = strings[1];
        this.FloorNameTW = strings[2];
        this.FloorNameCN = strings[3];
        this.SEQ = Integer.valueOf(strings[4]);
    }


    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.FloorID);
        dest.writeString(this.FloorNameTW);
        dest.writeString(this.FloorNameEN);
        dest.writeString(this.FloorNameCN);
        dest.writeValue(this.SEQ);
        dest.writeInt(this.exhibitorCount);
    }

    protected Floor(Parcel in) {
        this.FloorID = in.readString();
        this.FloorNameTW = in.readString();
        this.FloorNameEN = in.readString();
        this.FloorNameCN = in.readString();
        this.SEQ = (Integer) in.readValue(Integer.class.getClassLoader());
        this.exhibitorCount = in.readInt();
    }

    public static final Parcelable.Creator<Floor> CREATOR = new Parcelable.Creator<Floor>() {
        public Floor createFromParcel(Parcel source) {
            return new Floor(source);
        }

        public Floor[] newArray(int size) {
            return new Floor[size];
        }
    };
}
