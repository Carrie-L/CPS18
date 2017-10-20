package com.adsale.ChinaPlas.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carrie on 2017/10/17.
 */

public class ExhibitorFilter implements Parcelable {
    public int index;
    public String id;
    public String filter;

    public ExhibitorFilter(int index, String id, String filter) {
        this.index = index;
        this.id = id;
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "ExhibitorFilter{" +
                "index=" + index +
                ", id='" + id + '\'' +
                ", filter='" + filter + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.id);
        dest.writeString(this.filter);
    }

    protected ExhibitorFilter(Parcel in) {
        this.index = in.readInt();
        this.id = in.readString();
        this.filter = in.readString();
    }

    public static final Parcelable.Creator<ExhibitorFilter> CREATOR = new Parcelable.Creator<ExhibitorFilter>() {
        public ExhibitorFilter createFromParcel(Parcel source) {
            return new ExhibitorFilter(source);
        }

        public ExhibitorFilter[] newArray(int size) {
            return new ExhibitorFilter[size];
        }
    };
}
