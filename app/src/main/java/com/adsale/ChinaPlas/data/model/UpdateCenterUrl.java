package com.adsale.ChinaPlas.data.model;

/**
 * Created by Carrie on 2017/10/27.
 * 内容更新中心items文件json
 */

public class UpdateCenterUrl {
    //FloorPlan
    public String csvlink;
    public String imagePath;

    //others txt（同期活动在AppContent）
    public String link;

    @Override
    public String toString() {
        return "UpdateCenterUrl [csvlink=" + csvlink + ", imagePath=" + imagePath + ", link=" + link + "]";
    }



}

