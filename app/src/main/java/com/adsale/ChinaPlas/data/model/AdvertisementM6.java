package com.adsale.ChinaPlas.data.model;

import com.adsale.ChinaPlas.utils.AppUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by new on 2017/4/17.
 * M6 广告
 */

public class AdvertisementM6 {
    public String[] version;
    public String[] function;
    public String filepath;
    public String format;
    public String header;
    public String logo;
    public String banner;
    public String[] companyID;
    public ArrayList<Topic> topics;
    public EventID EventID;

    public static class Topic {
        public String id;
        public String image;
        public String description;

        @Override
        public String toString() {
            return "Topic{" +
                    "id='" + id + '\'' +
                    ", image='" + image + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public static class EventID {
        public String[] EN;
        public String[] SC;
        public String[] TC;

        public String[] getEventId(int language) {
            if (language == 0) {
                return TC;
            } else if (language == 1) {
                return EN;
            } else {
                return SC;
            }
        }

    }

    @Override
    public String toString() {
        return "M6B{" +
                "version=" + Arrays.toString(version) +
                ", function=" + Arrays.toString(function) +
                ", filepath='" + filepath + '\'' +
                ", format='" + format + '\'' +
                ", header='" + header + '\'' +
                ", logo='" + logo + '\'' +
                ", banner='" + banner + '\'' +
                ", companyID=" + Arrays.toString(companyID) +
                ", topics=" + topics +
                '}';
    }
}
