package com.adsale.ChinaPlas.data.model;

/**
 * Created by Carrie on 2017/9/13.
 */

public class LoadUrl {
    public String urlName;
    public String dirName;

    public LoadUrl(){

    }

    public LoadUrl(String urlName, String dirName) {
        this.urlName = urlName;
        this.dirName = dirName;
    }

    @Override
    public String toString() {
        return "LoadUrl{" +
                "urlName='" + urlName + '\'' +
                ", dirName='" + dirName + '\'' +
                '}';
    }
}
