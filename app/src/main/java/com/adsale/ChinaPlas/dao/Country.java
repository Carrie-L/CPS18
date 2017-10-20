package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.utils.AppUtil;

import static com.adsale.ChinaPlas.R.id.language;

/**
 * Entity mapped to table "COUNTRY".
 */
public class Country implements Parcelable {

    private String CountryID;
    /** Not-null value. */
    private String CountryNameTW;
    /** Not-null value. */
    private String CountryNameCN;
    /** Not-null value. */
    private String CountryNameEN;
    /** Not-null value. */
    private String SortTW;
    /** Not-null value. */
    private String SortEN;
    /** Not-null value. */
    private String SortCN;

    // KEEP FIELDS - put your custom fields here
    public final ObservableBoolean isTypeLabel = new ObservableBoolean();
    public final ObservableBoolean selected = new ObservableBoolean(false);
    // KEEP FIELDS END

    public Country() {
    }

    public Country(String CountryID) {
        this.CountryID = CountryID;
    }

    public Country(String CountryID, String CountryNameTW, String CountryNameCN, String CountryNameEN, String SortTW, String SortEN, String SortCN) {
        this.CountryID = CountryID;
        this.CountryNameTW = CountryNameTW;
        this.CountryNameCN = CountryNameCN;
        this.CountryNameEN = CountryNameEN;
        this.SortTW = SortTW;
        this.SortEN = SortEN;
        this.SortCN = SortCN;
    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String CountryID) {
        this.CountryID = CountryID;
    }

    /** Not-null value. */
    public String getCountryNameTW() {
        return CountryNameTW;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCountryNameTW(String CountryNameTW) {
        this.CountryNameTW = CountryNameTW;
    }

    /** Not-null value. */
    public String getCountryNameCN() {
        return CountryNameCN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCountryNameCN(String CountryNameCN) {
        this.CountryNameCN = CountryNameCN;
    }

    /** Not-null value. */
    public String getCountryNameEN() {
        return CountryNameEN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCountryNameEN(String CountryNameEN) {
        this.CountryNameEN = CountryNameEN;
    }

    /** Not-null value. */
    public String getSortTW() {
        return SortTW;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSortTW(String SortTW) {
        this.SortTW = SortTW;
    }

    /** Not-null value. */
    public String getSortEN() {
        return SortEN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSortEN(String SortEN) {
        this.SortEN = SortEN;
    }

    /** Not-null value. */
    public String getSortCN() {
        return SortCN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSortCN(String SortCN) {
        this.SortCN = SortCN;
    }

    // KEEP METHODS - put your custom methods here
    public String getSort(){
    	return AppUtil.getName(SortTW,SortEN,SortCN);
    }
    
    public String getCountryName(){
        return AppUtil.getName(CountryNameTW,CountryNameEN,CountryNameCN);
    }
    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CountryID);
        dest.writeString(this.CountryNameTW);
        dest.writeString(this.CountryNameCN);
        dest.writeString(this.CountryNameEN);
        dest.writeString(this.SortTW);
        dest.writeString(this.SortEN);
        dest.writeString(this.SortCN);
    }

    protected Country(Parcel in) {
        this.CountryID = in.readString();
        this.CountryNameTW = in.readString();
        this.CountryNameCN = in.readString();
        this.CountryNameEN = in.readString();
        this.SortTW = in.readString();
        this.SortEN = in.readString();
        this.SortCN = in.readString();
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
