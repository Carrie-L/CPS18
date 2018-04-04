package com.adsale.ChinaPlas.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carrie on 2018/4/4.
 */

public class NewCategoryMaster implements Parcelable {
//    MainTypeId|NameEn|NameTc|NameSc|OrderId
    private String MainTypeId;
    private String NameEn;
    private String NameTc;
    private String NameSc;
    private int OrderId;

    public NewCategoryMaster(){

    }

    public NewCategoryMaster(String mainTypeId, String NameEn, String NameTc, String NameSc, int OrderId) {
        MainTypeId = mainTypeId;
        this.NameEn = NameEn;
        this.NameTc = NameTc;
        this.NameSc = NameSc;
        this.OrderId = OrderId;
    }

    public void setMainTypeId(String mainTypeId) {
        MainTypeId = mainTypeId;
    }

    public void setNameEn(String NameEn) {
        NameEn = NameEn;
    }

    public void setNameTc(String NameTc) {
        NameTc = NameTc;
    }

    public void setNameSc(String NameSc) {
        NameSc = NameSc;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getMainTypeId() {
        return MainTypeId;
    }

    public String getNameEn() {
        return NameEn;
    }

    public String getNameTc() {
        return NameTc;
    }

    public String getNameSc() {
        return NameSc;
    }

    public int getOrderId() {
        return OrderId;
    }

    // KEEP METHODS - put your custom methods here
    //MainTypeId|NameEn|NameTc|NameSc|CategoryId|OrderId
    public void parser(String[] strings) {
        this.MainTypeId = strings[0];
        this.NameEn = strings[1];
        this.NameTc = strings[2];
        this.NameSc = strings[3];
        this.OrderId = Integer.valueOf(strings[4]);
    }
    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.MainTypeId);
        dest.writeString(this.NameEn);
        dest.writeString(this.NameTc);
        dest.writeString(this.NameSc);
        dest.writeInt(this.OrderId);
    }

    protected NewCategoryMaster(Parcel in) {
        this.MainTypeId = in.readString();
        this.NameEn = in.readString();
        this.NameTc = in.readString();
        this.NameSc = in.readString();
        this.OrderId = in.readInt();
    }

    public static final Creator<NewCategoryMaster> CREATOR = new Creator<NewCategoryMaster>() {
        public NewCategoryMaster createFromParcel(Parcel source) {
            return new NewCategoryMaster(source);
        }

        public NewCategoryMaster[] newArray(int size) {
            return new NewCategoryMaster[size];
        }
    };
}
