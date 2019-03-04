package com.adsale.ChinaPlas.dao;

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.App;

/**
 * Created by Carrie on 2018/1/26.
 */

public class Zone implements Parcelable {
    private String ThemeZoneCode;
    private String ThemeZoneDescription;
    private String ThemeZoneDescriptionTC;
    private String ThemeZoneDescriptionSC;
    private Boolean IsDelete;
    private String createdAt;
    private String updatedAt;

    // KEEP FIELDS - put your custom fields here
    public final ObservableBoolean isSelected = new ObservableBoolean(false);
    // KEEP FIELDS END

    public Zone() {
    }

    public Zone(String ThemeZoneCode) {
        this.ThemeZoneCode = ThemeZoneCode;
    }

    public Zone(String ThemeZoneCode, String ThemeZoneDescription, String ThemeZoneDescriptionTC, String ThemeZoneDescriptionSC, Boolean IsDelete, String createdAt, String updatedAt) {
        this.ThemeZoneCode = ThemeZoneCode;
        this.ThemeZoneDescription = ThemeZoneDescription;
        this.ThemeZoneDescriptionTC = ThemeZoneDescriptionTC;
        this.ThemeZoneDescriptionSC = ThemeZoneDescriptionSC;
        this.IsDelete = IsDelete;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getThemeZoneCode() {
        return ThemeZoneCode;
    }

    public void setThemeZoneCode(String ThemeZoneCode) {
        this.ThemeZoneCode = ThemeZoneCode;
    }

    public String getThemeZoneDescription() {
        return ThemeZoneDescription;
    }

    public void setThemeZoneDescription(String ThemeZoneDescription) {
        this.ThemeZoneDescription = ThemeZoneDescription;
    }

    public String getThemeZoneDescriptionTC() {
        return ThemeZoneDescriptionTC;
    }

    public void setThemeZoneDescriptionTC(String ThemeZoneDescriptionTC) {
        this.ThemeZoneDescriptionTC = ThemeZoneDescriptionTC;
    }

    public String getThemeZoneDescriptionSC() {
        return ThemeZoneDescriptionSC;
    }

    public void setThemeZoneDescriptionSC(String ThemeZoneDescriptionSC) {
        this.ThemeZoneDescriptionSC = ThemeZoneDescriptionSC;
    }

    public Boolean getIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(Boolean IsDelete) {
        this.IsDelete = IsDelete;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // KEEP METHODS - put your custom methods here
    public void parser(String[] strings) {
        this.ThemeZoneCode = strings[1];
        this.ThemeZoneDescription = strings[2];
        this.ThemeZoneDescriptionTC = strings[3];
        this.ThemeZoneDescriptionSC = strings[4];
    }

    public String getZone() {
        if (App.mLanguage.get() == 0) {
            return ThemeZoneDescriptionTC;
        } else if (App.mLanguage.get() == 1) {
            return ThemeZoneDescription;
        }
        return ThemeZoneDescriptionSC;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "ThemeZoneCode='" + ThemeZoneCode + '\'' +
                ", ThemeZoneDescription='" + ThemeZoneDescription + '\'' +
                ", ThemeZoneDescriptionTC='" + ThemeZoneDescriptionTC + '\'' +
                ", ThemeZoneDescriptionSC='" + ThemeZoneDescriptionSC + '\'' +
                '}';
    }

    // KEEP METHODS END


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ThemeZoneCode);
        dest.writeString(this.ThemeZoneDescription);
        dest.writeString(this.ThemeZoneDescriptionTC);
        dest.writeString(this.ThemeZoneDescriptionSC);
        dest.writeValue(this.IsDelete);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
    }

    protected Zone(Parcel in) {
        this.ThemeZoneCode = in.readString();
        this.ThemeZoneDescription = in.readString();
        this.ThemeZoneDescriptionTC = in.readString();
        this.ThemeZoneDescriptionSC = in.readString();
        this.IsDelete = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
    }

    public static final Parcelable.Creator<Zone> CREATOR = new Parcelable.Creator<Zone>() {
        public Zone createFromParcel(Parcel source) {
            return new Zone(source);
        }

        public Zone[] newArray(int size) {
            return new Zone[size];
        }
    };
}
