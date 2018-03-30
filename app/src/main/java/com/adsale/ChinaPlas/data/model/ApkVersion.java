package com.adsale.ChinaPlas.data.model;

/**
 * Created by Carrie on 2018/3/27.
 */

public class ApkVersion {
    public int versionCode;
    public String link;

    @Override
    public String toString() {
        return "ApkVersion{" +
                "versionCode=" + versionCode +
                ", link='" + link + '\'' +
                '}';
    }
}
