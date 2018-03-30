package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.LoadRepository;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Locale;
// KEEP INCLUDES END

/**
 * Entity mapped to table "UPDATE_CENTER".
 */
public class UpdateCenter {

    private Long id;
    private String ScanFile;
    private String LUT;
    private Integer Status;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public UpdateCenter() {
    }

    public UpdateCenter(Long id) {
        this.id = id;
    }

    public UpdateCenter(Long id, String ScanFile, String LUT, Integer Status) {
        this.id = id;
        this.ScanFile = ScanFile;
        this.LUT = LUT;
        this.Status = Status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScanFile() {
        return ScanFile;
    }

    public void setScanFile(String ScanFile) {
        this.ScanFile = ScanFile;
    }

    public String getLUT() {
        return LUT;
    }

    public void setLUT(String LUT) {
        this.LUT = LUT;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer Status) {
        this.Status = Status;
    }

    // KEEP METHODS - put your custom methods here

    /**
     * 比较FUDate，FPDate，使用最大的那个作为更新时间
     */
    public String FUDate;
    public String FPDate;

    /**status: 0: Update �???要更�??? ; 1: Updated  已更�???*/


    /**
     * 根据解析出来的ScanFileJson数据，判断哪些txt有更新，以及下载txt
     */
    public static ArrayList<UpdateCenter> getUpdateFiles(ArrayList<UpdateCenter> scanFiles, LoadRepository repository) {
        ArrayList<UpdateCenter> updates = new ArrayList<>();
        int size = scanFiles.size();
        UpdateCenter entity;
        for (int i = 0; i < size; i++) {
            entity = scanFiles.get(i);
            if ((entity.getScanFile().equals(Constant.TXT_NEW_TEC)
                    || entity.getScanFile().equals(Constant.TXT_CONCURRENT_EVENT)
                    || entity.getScanFile().equals(Constant.TXT_COORDINATE)
                    || entity.getScanFile().equals(Constant.TXT_FILE_SIZE)
                    || entity.getScanFile().equals(Constant.TXT_MAIN_PIC_INFO)
                    || entity.getScanFile().equals(Constant.TXT_NOTIFICATION)
                    || entity.getScanFile().equals(Constant.TXT_PDF_CENTER_INFO)
                    || entity.getScanFile().equals(Constant.TXT_PREREG_INFO))
                    && (entity.FUDate.compareTo(repository.getLocalTxtLUT(entity.getScanFile())) > 0)) {
                LogUtil.e("UpdateCenter !!isOneOfFiveTxt", entity.getScanFile() + " has update!! ");
                updates.add(entity); // must!
                // 非更新中心的status永远等于1，因为status是用于在[更新中心]判断是否有更新的。
                entity.setStatus(1);
                entity.setLUT(entity.FUDate); // 更新时间
                repository.updateLocalLUT2(entity);
            }
            else if ((entity.getScanFile().equals(Constant.UC_TXT_APP_CONTENTS) && entity.FUDate.compareTo(repository.getLocalTxtLUT(Constant.UC_TXT_APP_CONTENTS)) > 0)
                            || (entity.getScanFile().equals(Constant.UC_TXT_EXHIBITOR) && entity.FUDate.compareTo(repository.getLocalTxtLUT(Constant.UC_TXT_EXHIBITOR)) > 0)
                            || (entity.getScanFile().equals(Constant.UC_TXT_FLOOR_PLAN) && entity.FUDate.compareTo(repository.getLocalTxtLUT(Constant.UC_TXT_FLOOR_PLAN)) > 0)
                            || (entity.getScanFile().equals(Constant.UC_TXT_SEMINAR) && entity.FUDate.compareTo(repository.getLocalTxtLUT(Constant.UC_TXT_SEMINAR)) > 0)
                            || (entity.getScanFile().equals(Constant.UC_TXT_TRAVEL) && entity.FUDate.compareTo(repository.getLocalTxtLUT(Constant.UC_TXT_TRAVEL)) > 0)) {
                LogUtil.e("UpdateCenter isOneOfFiveTxt", entity.getScanFile() + " has update!! ");
                updates.add(entity); // must!
                entity.setUCId();
                entity.setStatus(0);
                entity.setLUT(entity.FUDate);
                repository.updateLocalLUT(entity);
            }

            if (entity.getScanFile().equals(Constant.TXT_AD)) { // 广告文件总是下载
                updates.add(entity);
            }

        }
        return updates;
    }

    /**
     * @param fileName txt
     * @param lut      last update time
     */
    private static void setLUT(String fileName, String lut) {
        App.mSP_LastModified.edit().putString(fileName, lut).apply();
    }

//    private static String repository.getLocalTxtLUT(String fileName){
//       return App.mSP_LastModified.getString(fileName,"");
//     }


    @Override
    public String toString() {
        return "UpdateCenter{" +
                "id=" + id +
                ", ScanFile='" + ScanFile + '\'' +
                ", LUT='" + LUT + '\'' +
                ", Status=" + Status +
                ", FUDate='" + FUDate + '\'' +
                ", FPDate='" + FPDate + '\'' +
                '}';
    }

    /**
     * 根据下载中心列表，按顺序排列
     */
    public void setUCId() {
        if (ScanFile.toLowerCase(Locale.getDefault()).contains("exhibitor")) {
            setId(1L);
        } else if (ScanFile.toLowerCase(Locale.getDefault()).contains("floorplan")) {
            setId(2L);
        } else if (ScanFile.toLowerCase(Locale.getDefault()).contains("seminar")) {
            setId(3L);
        } else if (ScanFile.toLowerCase(Locale.getDefault()).contains("appcontents")) {
            setId(4L);
        } else if (ScanFile.toLowerCase(Locale.getDefault()).contains("travel")) {
            setId(5L);
        }
    }

    public void setUCId(long id) {
        setId(id);
    }
    // KEEP METHODS END

}
