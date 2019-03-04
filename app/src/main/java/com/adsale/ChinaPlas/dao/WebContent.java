package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity mapped to table "WEB_CONTENT".
 */
public class WebContent implements Parcelable {

    private String ContentID;
    private String FileName;
    private String createdAt;
    private String updatedAt;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public WebContent() {
    }

    public WebContent(String ContentID) {
        this.ContentID = ContentID;
    }

    public WebContent(String ContentID, String FileName, String createdAt, String updatedAt) {
        this.ContentID = ContentID;
        this.FileName = FileName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getContentID() {
        return ContentID;
    }

    public void setContentID(String ContentID) {
        this.ContentID = ContentID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
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

    @Override
    public String toString() {
        return "WebContent{" +
                "ContentID='" + ContentID + '\'' +
                ", FileName='" + FileName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }

    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ContentID);
        dest.writeString(this.FileName);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
    }

    protected WebContent(Parcel in) {
        this.ContentID = in.readString();
        this.FileName = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
    }

    public static final Parcelable.Creator<WebContent> CREATOR = new Parcelable.Creator<WebContent>() {
        public WebContent createFromParcel(Parcel source) {
            return new WebContent(source);
        }

        public WebContent[] newArray(int size) {
            return new WebContent[size];
        }
    };
}
