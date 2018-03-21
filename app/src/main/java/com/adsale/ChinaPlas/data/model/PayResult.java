package com.adsale.ChinaPlas.data.model;

/**
 * Created by Carrie on 2018/3/13.
 */

public class PayResult {
    public String Status;
    public String Name;
    public int RefId;

    @Override
    public String toString() {
        return "PayResult{" +
                "Status='" + Status + '\'' +
                ", Name='" + Name + '\'' +
                ", RefId=" + RefId +
                '}';
    }
}
