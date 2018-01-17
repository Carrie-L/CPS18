package com.adsale.ChinaPlas.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carrie on 2018/1/15.
 */

public class Progress implements Parcelable {
    private int progress;
    private long currentFileSize;
    private long totalFileSize;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
    }

    public Progress() {
    }

    protected Progress(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
    }

    public static final Parcelable.Creator<Progress> CREATOR = new Parcelable.Creator<Progress>() {
        public Progress createFromParcel(Parcel source) {
            return new Progress(source);
        }

        public Progress[] newArray(int size) {
            return new Progress[size];
        }
    };
}
