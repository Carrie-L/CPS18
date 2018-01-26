package com.adsale.ChinaPlas.dao;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carrie on 2018/1/26.
 */

public class ExhibitorZone implements Parcelable {

    private String ThemeZoneCode;
    private String CompanyId;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END


    public ExhibitorZone(String themeZoneCode, String companyId) {
        ThemeZoneCode = themeZoneCode;
        CompanyId = companyId;
    }

    public String getThemeZoneCode() {
        return ThemeZoneCode;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setThemeZoneCode(String themeZoneCode) {
        ThemeZoneCode = themeZoneCode;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }

    // KEEP METHODS - put your custom methods here
    public void parser(String[] strings) {
        this.ThemeZoneCode = strings[0];
        this.CompanyId = strings[1];
    }


    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ThemeZoneCode);
        dest.writeString(this.CompanyId);
    }

    public ExhibitorZone() {
    }

    protected ExhibitorZone(Parcel in) {
        this.ThemeZoneCode = in.readString();
        this.CompanyId = in.readString();
    }

    public static final Parcelable.Creator<ExhibitorZone> CREATOR = new Parcelable.Creator<ExhibitorZone>() {
        public ExhibitorZone createFromParcel(Parcel source) {
            return new ExhibitorZone(source);
        }

        public ExhibitorZone[] newArray(int size) {
            return new ExhibitorZone[size];
        }
    };
}
