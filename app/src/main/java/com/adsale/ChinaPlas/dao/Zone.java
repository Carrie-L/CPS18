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

    // KEEP FIELDS - put your custom fields here
    public final ObservableBoolean isSelected = new ObservableBoolean(false);
    // KEEP FIELDS END

    public String getThemeZoneCode() {
        return ThemeZoneCode;
    }

    public String getThemeZoneDescription() {
        return ThemeZoneDescription;
    }

    public String getThemeZoneDescriptionTC() {
        return ThemeZoneDescriptionTC;
    }

    public String getThemeZoneDescriptionSC() {
        return ThemeZoneDescriptionSC;
    }

    public void setThemeZoneCode(String themeZoneCode) {
        ThemeZoneCode = themeZoneCode;
    }

    public void setThemeZoneDescription(String themeZoneDescription) {
        ThemeZoneDescription = themeZoneDescription;
    }

    public void setThemeZoneDescriptionTC(String themeZoneDescriptionTC) {
        ThemeZoneDescriptionTC = themeZoneDescriptionTC;
    }

    public void setThemeZoneDescriptionSC(String themeZoneDescriptionSC) {
        ThemeZoneDescriptionSC = themeZoneDescriptionSC;
    }

    // KEEP METHODS - put your custom methods here
    public void parser(String[] strings) {
        this.ThemeZoneCode = strings[0];
        this.ThemeZoneDescription = strings[1];
        this.ThemeZoneDescriptionTC = strings[2];
        this.ThemeZoneDescriptionSC = strings[3];
    }

    public String getZone() {
        if (App.mLanguage.get() == 0) {
            return ThemeZoneDescriptionTC;
        } else if (App.mLanguage.get() == 1) {
            return ThemeZoneDescription;
        }
        return ThemeZoneDescriptionSC;
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
    }

    public Zone() {
    }

    public Zone(String themeZoneCode, String themeZoneDescription, String themeZoneDescriptionTC, String themeZoneDescriptionSC) {
        ThemeZoneCode = themeZoneCode;
        ThemeZoneDescription = themeZoneDescription;
        ThemeZoneDescriptionTC = themeZoneDescriptionTC;
        ThemeZoneDescriptionSC = themeZoneDescriptionSC;
    }

    protected Zone(Parcel in) {
        this.ThemeZoneCode = in.readString();
        this.ThemeZoneDescription = in.readString();
        this.ThemeZoneDescriptionTC = in.readString();
        this.ThemeZoneDescriptionSC = in.readString();
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
