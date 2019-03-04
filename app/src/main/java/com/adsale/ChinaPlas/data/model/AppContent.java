package com.adsale.ChinaPlas.data.model;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;

import java.io.File;
import java.util.ArrayList;

/**
 * 实际是同期活动
 *
 * @author Carrie
 * @version 创建时间：2016年9月7日 下午5:15:11
 */
public class AppContent {
    //url=htmlFilePath+pageID+".zip"

    public String htmlFilePath;
    public ArrayList<Pages> pages;
    private final String TAG = "AppContent";
    private StringBuilder mHtmPath;

    public static class Pages {
        public String version;
        public String title_en;
        public String title_cn;
        public String title_tc;
        public String pageID;

        public String getTitle() {
            return AppUtil.getName(title_tc, title_en, title_cn);
        }

    }

    public static AppContent getAppContent() {
        return Parser.parseJson(AppContent.class, Constant.UC_TXT_APP_CONTENTS);
    }

    public boolean isEventHtmExists(String pageId) {
        mHtmPath = new StringBuilder();
        mHtmPath.append(App.rootDir).append("ConcurrentEvent").append("/").append(pageId).append("/").append(getHtm());
        LogUtil.i(TAG, "mHtmPath.toString()=" + mHtmPath.toString());

        if (!new File(mHtmPath.toString()).exists()) {
            mHtmPath.delete(0, App.rootDir.length()).insert(0, "/android_asset/");
            LogUtil.i(TAG, "mHtmPath=" + mHtmPath.toString());
            boolean isInAsset = AppUtil.isFileInAsset("ConcurrentEvent", pageId);
            LogUtil.i(TAG, "isInAsset=" + isInAsset);
            return isInAsset;
        } else {
            LogUtil.i(TAG, "存在于SD中=" + pageId);
            return true;
        }
    }

    private String getHtm() {
        return AppUtil.getName("TC.htm", "EN.html", "SC.htm");
    }

}
