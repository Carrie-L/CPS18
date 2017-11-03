package com.adsale.ChinaPlas.data.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Parser;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/31.
 * 文档下载中心
 */

public class DocumentsCenter {
    public String CategoryName_SC;
    public String CategoryName_EN;
    public String CategoryName_TC;
    public ArrayList<Child> Items;

    public final ObservableBoolean isItemShow=new ObservableBoolean(false);
    public final ObservableBoolean isHeaderShow=new ObservableBoolean(true);

    public static class Child {
        public String FileName_SC;
        public String FileName_EN;
        public String FileName_TC;

        public String Cover_SC;

        public String FileLink_EN;
        public String FileLink_SC;
        public String FileLink_TC;

        public String FileSize_SC;
        public String FileSize_TC;
        public String FileSize_EN;

        /*  自定义variable  */
        public String CategoryName;
        public boolean isHeader;
        public boolean isExpanded=true; // true:当前已经展开，false:未展开
        /**
         * -1:未开始下载；0：点击下载，暂停中；1：正在下载; 2:下载完成
         */
        public final ObservableInt downloadStatus = new ObservableInt(-1);


        public String getFileName() {
            return AppUtil.getName(FileName_TC, FileName_EN, FileName_SC);
        }

        public String getFileSize() {
            return AppUtil.getName(FileSize_TC, FileSize_EN, FileSize_SC);
        }

        public String getFileLink() {
            return AppUtil.getName(FileLink_TC, FileLink_EN, FileLink_SC);
        }

        @Override
        public String toString() {
            return "Child{" +
                    "FileName_SC='" + FileName_SC + '\'' +
                    ", FileName_EN='" + FileName_EN + '\'' +
                    ", FileName_TC='" + FileName_TC + '\'' +
                    ", Cover_SC='" + Cover_SC + '\'' +
                    ", FileLink_EN='" + FileLink_EN + '\'' +
                    ", FileLink_SC='" + FileLink_SC + '\'' +
                    ", FileLink_TC='" + FileLink_TC + '\'' +
                    ", FileSize_SC='" + FileSize_SC + '\'' +
                    ", FileSize_TC='" + FileSize_TC + '\'' +
                    ", FileSize_EN='" + FileSize_EN + '\'' +
                    '}';
        }
    }

    public String getCategoryName() {
        return AppUtil.getName(CategoryName_TC, CategoryName_EN, CategoryName_SC);
    }

    @Override
    public String toString() {
        return "DocumentsCenter{" +
                "CategoryName_SC='" + CategoryName_SC + '\'' +
                ", CategoryName_EN='" + CategoryName_EN + '\'' +
                ", CategoryName_TC='" + CategoryName_TC + '\'' +
                ", Items=" + Items +
                '}';
    }
}
