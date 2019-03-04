package com.adsale.ChinaPlas.dao;

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.data.model.Filter;
import com.adsale.ChinaPlas.utils.AppUtil;

/**
 * Created by Carrie on 2018/4/4.
 */

public class NewCategorySub implements Parcelable,Filter{

    private String MainTypeId;
    private String SubNameEn;
    private String SubNameTc;
    private String SubNameSc;
    private String CategoryId;
    private int OrderId;

    // KEEP FIELDS - put your custom fields here
    public ObservableBoolean isSelected = new ObservableBoolean();

    // KEEP FIELDS END

    public NewCategorySub(){

    }

    public NewCategorySub(String mainTypeId, String subNameEn, String subNameTc, String subNameSc, String categoryId, int orderId) {
        MainTypeId = mainTypeId;
        SubNameEn = subNameEn;
        SubNameTc = subNameTc;
        SubNameSc = subNameSc;
        CategoryId = categoryId;
        OrderId = orderId;
    }

    public void setMainTypeId(String mainTypeId) {
        MainTypeId = mainTypeId;
    }

    public void setSubNameEn(String subNameEn) {
        SubNameEn = subNameEn;
    }

    public void setSubNameTc(String subNameTc) {
        SubNameTc = subNameTc;
    }

    public void setSubNameSc(String subNameSc) {
        SubNameSc = subNameSc;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getMainTypeId() {
        return MainTypeId;
    }

    public String getSubNameEn() {
        return SubNameEn;
    }

    public String getSubNameTc() {
        return SubNameTc;
    }

    public String getSubNameSc() {
        return SubNameSc;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public int getOrderId() {
        return OrderId;
    }



    // KEEP METHODS - put your custom methods here
    //MainTypeId|SubNameEn|SubNameTc|SubNameSc|CategoryId|OrderId
    public void parser(String[] strings) {
        this.MainTypeId = strings[0];
        this.SubNameEn = strings[1];
        this.SubNameTc = strings[2];
        this.SubNameSc = strings[3];
        this.CategoryId = strings[4];
        this.OrderId =Integer.valueOf( strings[5]);
    }

    @Override
    public String getId() {
        return getCategoryId();
    }

    @Override
    public String getName() {
        return AppUtil.getName(SubNameTc,SubNameEn,SubNameSc);
    }

    @Override
    public boolean getIsSelected() {
        return isSelected.get();
    }

    @Override
    public void setIsSelected(boolean isSelect) {
        isSelected.set(isSelect);
    }

    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.MainTypeId);
        dest.writeString(this.SubNameEn);
        dest.writeString(this.SubNameTc);
        dest.writeString(this.SubNameSc);
        dest.writeString(this.CategoryId);
        dest.writeInt(this.OrderId);
    }

    protected NewCategorySub(Parcel in) {
        this.MainTypeId = in.readString();
        this.SubNameEn = in.readString();
        this.SubNameTc = in.readString();
        this.SubNameSc = in.readString();
        this.CategoryId = in.readString();
        this.OrderId = in.readInt();
    }

    public static final Parcelable.Creator<NewCategorySub> CREATOR = new Parcelable.Creator<NewCategorySub>() {
        public NewCategorySub createFromParcel(Parcel source) {
            return new NewCategorySub(source);
        }

        public NewCategorySub[] newArray(int size) {
            return new NewCategorySub[size];
        }
    };

    @Override
    public String toString() {
        return "NewCategorySub{" +
                "MainTypeId='" + MainTypeId + '\'' +
                ", SubNameEn='" + SubNameEn + '\'' +
                ", SubNameTc='" + SubNameTc + '\'' +
                ", SubNameSc='" + SubNameSc + '\'' +
                ", CategoryId='" + CategoryId + '\'' +
                ", OrderId=" + OrderId +
                ", isSelected=" + isSelected +
                '}';
    }
}
