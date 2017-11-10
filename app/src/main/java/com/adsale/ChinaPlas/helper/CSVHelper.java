package com.adsale.ChinaPlas.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.data.model.AgentInfo;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CSVReader;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CSVHelper {
    private static final String TAG = "CSVHelper";
    private static DBHelper dbHelper;
    private static ContentValues cv;
    private Context mContext;
    private static BufferedReader br;

    public CSVHelper(Context context) {
        dbHelper = App.mDBHelper;
        mContext=context;
    }


    public ArrayList<AgentInfo> getHotelDetailsCsv(){
        ArrayList<AgentInfo> mAgentInfos = new ArrayList<>();
        String agentInfoPath = App.rootDir.concat(Constant.ASSET_HOTEL_DETAIL);
        File csvFile = new File(agentInfoPath);
        AgentInfo info;
        try {
            if (!csvFile.exists()) {// 文件不存在
                LogUtil.i(TAG, "TravelInfo:Asset");
                AssetManager am = App.mAssetManager;
                br = new BufferedReader(new InputStreamReader(am.open(Constant.ASSET_HOTEL_DETAIL)));
            } else {
                LogUtil.i(TAG, "TravelInfo:SD");
                br = new BufferedReader(new FileReader(csvFile));
            }

            CSVReader csvReader = new CSVReader(br);
            String[] next = {};
            while (true) {
                next = csvReader.readNext();
                if (next != null) {
                    info = new AgentInfo(next[0], next[1], next[2], next[3], next[4], next[5], next[6], next[7], next[8], next[9],
                            next[10], next[11], next[12], next[13], next[14], next[15], next[16], next[17], next[18], next[19], next[20], next[21],
                            next[22], next[23], next[24], next[25], next[26], next[27], next[28], next[29], next[30], next[31], next[32], next[33],
                            next[34], next[35]);
                    mAgentInfos.add(info);
                } else {
                    break;
                }
            }
            LogUtil.i(TAG, "mAgentInfos=" + mAgentInfos.size());
            if (mAgentInfos.get(0).titleENG.contains("TitleENG")) {
                mAgentInfos.remove(0);
            }
            // TODO: 2016/12/1 按照orders排序
            Collections.sort(mAgentInfos, new Comparator<AgentInfo>() {
                @Override
                public int compare(AgentInfo lhs, AgentInfo rhs) {
                    return Integer.valueOf(lhs.orders).compareTo(Integer.valueOf(rhs.orders));
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mAgentInfos;
    }

    /**
     * hrHotelDetail.CSV
     *
     * @version 创建时间：2016年3月24日 下午1:49:01
     * @param
     * @return
     */
    public ArrayList<AgentInfo> getHrHotelDetailCSV() {
        ArrayList<AgentInfo> mHotelInfos = new ArrayList<>();
        BufferedReader hotelBR = null;
        AgentInfo info;
        File hotelDetailFile = new File(App.rootDir + "TravelInfo/" + "hrHotelDetail.csv");

        try {
            if (hotelDetailFile.exists()) {
                hotelBR = new BufferedReader(new FileReader(hotelDetailFile));
                LogUtil.i(TAG,"hrHotelDetail.csv存在SD");
            } else {
                hotelBR = new BufferedReader(new InputStreamReader(AppUtil.getAssetInputStream(Constant.ASSET_HR_HOTEL_DETAIL)));
                LogUtil.i(TAG,"hrHotelDetail.csv不存在，读取asset");
            }
            CSVReader csvReader = new CSVReader(hotelBR);
            String[] next = {};
            while (true) {
                next = csvReader.readNext();
                if (next != null) {
                    info = new AgentInfo(next[0], next[1], next[2], next[3], next[4], next[5], next[6], next[7], next[8], next[9],
                            next[10], next[11], next[12], next[13], next[14], next[15], next[16], next[17], next[18], next[19], next[20], next[21],
                            next[22], next[23], next[24], next[25], next[26], next[27], next[28], next[29], next[30], next[31], next[32], next[33],
                            next[34], next[35]);
                    mHotelInfos.add(info);
                    if (mHotelInfos.get(0).titleENG.contains("TitleENG")) {
                        mHotelInfos.remove(0);
                    }
                } else {
                    break;
                }
            }
            LogUtil.i(TAG, "mHotelInfos=" + mHotelInfos.size());
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hotelBR != null) {
                try {
                    hotelBR.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mHotelInfos;
    }


}
//
//    // ===================================Exhibitor Data============================
//
//    /**
//     * 读取exhibitors.csv，且存入数据库表EXHIBITOR
//     * <p> 解析两个csv，将数据合并起来，一起插入数据库
//     *
//     * @param is1   exhibitor.csv
//     * @param isDes <font color="#f97798">exhibitorDes.csv</font>
//     */
//    public static void readExhibitorCSV(DBHelper dbHelper, InputStream is1, InputStream isDes) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<Exhibitor> entities = new ArrayList<>();
//        Exhibitor entity = null;
//        CSVReader reader;
//        if (isDes != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is1, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new Exhibitor();
//                        entity.parseExhibitor(line);
//                        if (TextUtils.isEmpty(entity.getStrokeEng().trim())) {
//                            entity.setStrokeEng(SystemMethod.getFirstChar(entity.getCompanyNameEN().trim()));
//                        }
//                        if (Character.isDigit(entity.getStrokeEng().trim().charAt(0))) {
//                            entity.setStrokeEng("#");
//                        }
//                        //去掉空格
//                        entity.setCompanyNameCN(entity.getCompanyNameCN().trim());
//                        entity.setCompanyNameEN(entity.getCompanyNameEN().trim());
//                        entity.setCompanyNameTW(entity.getCompanyNameTW().trim());
//
//                        if (entity.getIsFavourite() == null) {
//                            entity.setIsFavourite(0);
//                        }
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is1 != null) {
//                        is1.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readExhibitorCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            readExhibitorDesCSV(dbHelper, isDes, entities);
//        }
//    }
//
//    /**
//     * 读取ExhibitorDescripton.csv，且存入数据库表EXHIBITOR
//     */
//    public static void readExhibitorDesCSV(DBHelper dbHelper, InputStream is, ArrayList<Exhibitor> list) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<Exhibitor> lists = new ArrayList<>();
//        Exhibitor entity = null;
//        CSVReader reader;
//        if(is!=null) {
//            //读取csv
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new Exhibitor();
//                        entity.parseDescription(line);
//                        lists.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readExhibitorDescriptonCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            combineExhibitorList(dbHelper, list, lists);
//        }
//    }
//
//    /**
//     * 合并两个csv数据，插入数据库
//     *
//     * @param list1   exhibitors.csv的数据
//     * @param listDes <font color="#f97798">ExhibitorDescripton.csv</font>
//     */
//    public static void combineExhibitorList(DBHelper dbHelper, ArrayList<Exhibitor> list1, ArrayList<Exhibitor> listDes) {
//        long startTime = System.currentTimeMillis();
//        int size1 = list1.size();
//        int size2 = listDes.size();
//        Exhibitor entity1 = null;
//        Exhibitor entity2 = null;
//        for (int i = 0; i < size1; i++) {
//            entity1 = list1.get(i);
//            for (int j = 0; j < size2; j++) {
//                entity2 = listDes.get(j);
//                if (entity1.getCompanyID().equals(entity2.getCompanyID())) {
//                    entity1.setDescE(entity2.getDescE());
//                    entity1.setDescS(entity2.getDescS());
//                    entity1.setDescT(entity2.getDescT());
//                    list1.set(i, entity1);
//                    break;
//                }
//            }
//        }
//        SystemMethod.getDuringTime(TAG, "合并两个List", startTime);
//
//        long startTime2 = System.currentTimeMillis();
//        dbHelper.insertExhibitorAll(list1);
//        SystemMethod.getDuringTime(TAG, "插入Exhibitor数据库", startTime2);
//    }
//
//    /**
//     * 读取CompanyApplication.csv，且存入数据库表APPLICATION_COMPANY
//     */
//    public static void readCompanyApplicationCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<ApplicationCompany> entities = new ArrayList<>();
//        ApplicationCompany entity = null;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new ApplicationCompany();
//                        entity.parserID(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readCompanyApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            // 此表沒有主鍵
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.deleteAllAppCompany();
//            dbHelper.insertApplicationCompaniesAll(entities);
//            LogUtil.i(TAG, "存儲readCompanyApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//    /**
//     * 读取Application.csv，且存入数据库表APPLICATION_INDUSTRY
//     */
//    public static void readApplicationCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<ApplicationIndustry> entities = new ArrayList<>();
//        ApplicationIndustry entity = null;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new ApplicationIndustry();
//                        entity.parser(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.insertAppIndustryAll(entities);
//            LogUtil.i(TAG, "存儲readApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//    /**
//     * 读取ExhibitionCatalogProductLang.csv，且存入数据库表INDUSTRY
//     */
//    public static void readExhibitionCatalogProductLangCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<Industry> entities = new ArrayList<>();
//        Industry entity = null;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                String enSort = "";
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new Industry();
//                        entity.parser(line);
//                        enSort = entity.getCatEng().trim();
//                        entity.setCatEng(entity.getCatEng().trim());
//                        if (enSort.startsWith("(")) {
//                            enSort = enSort.replace("(", "#");
//                        } else if (Character.isDigit(enSort.charAt(0))) {
//                            enSort = enSort.replace(enSort.charAt(0), '#');
//                        }
//                        enSort = SystemMethod.getFirstChar(enSort);
//                        entity.setEN_SORT(enSort);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readExhibitionCatalogProductLangCSV所花费的时间为:" + (System.currentTimeMillis() - startTime)
//                    + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.insertIndustryAll(entities);
//            LogUtil.i(TAG, "存儲readExhibitionCatalogProductLangCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2)
//                    + "ms");
//        }
//    }
//
//    /**
//     * 读取CompanyCatalogProduct.csv，且存入数据库表EXHIBITOR_INDUSTRY_DTL
//     */
//    public static void readCompanyCatalogProductCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<ExhibitorIndustryDtl> entities = new ArrayList<>();
//        ExhibitorIndustryDtl entity = null;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new ExhibitorIndustryDtl();
//                        entity.parser(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readExhibitionCatalogProductCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.deleteExhibitorIndustryDtlAll();
//            dbHelper.insertExhibitorDtlAll(entities);
//            LogUtil.i(TAG, "存儲readExhibitionCatalogProductCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//    /**
//     * 读取BusinessMapping.csv，且存入数据库表EXHIBITOR_INDUSTRY_DTL
//     */
//    public static void readBusinessMappingCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<BussinessMapping> entities = new ArrayList<>();
//        BussinessMapping entity = null;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new BussinessMapping();
//                        entity.parser(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readBusinessMappingCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.deleteBsnsMappingAll();
//            dbHelper.insertBsnsMappingAll(entities);
//            LogUtil.i(TAG, "存儲readBusinessMappingCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//    // ==========================FloorPlan || FloorPlan.csv===================================
//
//    /**
//     * 读取FloorPlan.csv，且存入数据库表FLOOR_PLAN_COORDINATE 文件名也有可能加后缀，如：FloorPlan_0313V3.csv
//     * <P>FloorPlan坐标信息
//     */
//    public static void readFloorPlanCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<FloorPlanCoordinate> entities = new ArrayList<>();
//        FloorPlanCoordinate entity;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new FloorPlanCoordinate();
//                        entity.parser(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readFloorPlanCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.deleteFloorPlanCordntAll();
//            dbHelper.insertFloorPlanCordnt(entities);
//            LogUtil.i(TAG, "存儲readFloorPlanCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//
//    /**
//     * 读取Hall.csv，且存入数据库表FLOOR_PLAN_COORDINATE
//     * <P>FloorPlan坐标信息
//     */
//    public static void readHallCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<Floor> entities = new ArrayList<>();
//        Floor entity;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new Floor();
//                        entity.parser(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readHallCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.clearFloor();
//            dbHelper.insertFloorAll(entities);
//            LogUtil.i(TAG, "存儲readHallCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//    // ==========================Technical Seminar===================================
//    public static void readSeminarInfoCSV() {
//        long startTime = System.currentTimeMillis();
//        ArrayList<SeminarInfo> entities = new ArrayList<>();
//        SeminarInfo entity = null;
//        CSVReader reader;
//        DBHelper dbHelper = App.dbHelper;
//        InputStream is = SystemMethod.getInputStream("TechnicalSeminar/SeminarInfo.csv");
//        if (is != null) {
//            dbHelper.deleteSeminarInfoAll();
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new SeminarInfo();
//                        entity.parser(line);
//                        dbHelper.insertSeminarInfo(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readSeminarInfoCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            LogUtil.i(TAG, "存儲readSeminarInfoCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//    public static void readSeminarSpeakCSV() {
//        long startTime = System.currentTimeMillis();
//        ArrayList<SeminarSpeaker> entities = new ArrayList<>();
//        SeminarSpeaker entity = null;
//        CSVReader reader;
//        DBHelper dbHelper = App.dbHelper;
//        InputStream is = SystemMethod.getInputStream("TechnicalSeminar/SeminarSpeaker.csv");
//        if (is != null) {
//            dbHelper.deleteSeminarSpeakerAll();
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new SeminarSpeaker();
//                        entity.parser(line);
//                        dbHelper.insertSeminarSpeak(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readSeminarSpeakCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//            long startTime2 = System.currentTimeMillis();
//            LogUtil.i(TAG, "存儲readSeminarSpeakCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//
//    public static void readBussinessMappingCSV(DBHelper dbHelper, InputStream is) {
//        long startTime = System.currentTimeMillis();
//        ArrayList<BussinessMapping> entities = new ArrayList<>();
//        BussinessMapping entity = null;
//        CSVReader reader;
//        if (is != null) {
//            try {
//                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
//                String[] line = reader.readNext();
//                if (line != null) {
//                    while ((line = reader.readNext()) != null) {
//                        entity = new BussinessMapping();
//                        entity.parser(line);
//                        entities.add(entity);
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is != null) {
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            LogUtil.i(TAG, "SD卡：readBusinessMappingCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
//
//            long startTime2 = System.currentTimeMillis();
//            dbHelper.deleteBsnsMappingAll();
//            dbHelper.insertBsnsMappingAll(entities);
//            LogUtil.i(TAG, "存儲readBusinessMappingCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
//        }
//    }
//    //---------------------------------FloorPlan.csv-------------------------------
//
//    public static InputStream getInputStream(String csv) {
//        return SystemMethod.getInputStream("Exhibitor Data/" + csv);
//    }
//
//    public static InputStream getInputStreamAsscts(String csv){
//        return SystemMethod.getAssetInputStream("Exhibitor Data/" + csv);
//    }
//
//    //更新中心下载展商资料后的数据处理
//    public static void processExhibitorCsv(DBHelper dbHelper) {
//        long startTime = System.currentTimeMillis();
//
//        LogUtil.i(TAG, "processExhibitorCsv......");
//
//        dbHelper.deleteAllAppCompany();
//        dbHelper.clearAppIndustry();
//        dbHelper.deleteBsnsMappingAll();
//        dbHelper.deleteExhibitorIndustryDtlAll();
//        dbHelper.clearIndustry();
//        dbHelper.clearFloor();
//        dbHelper.clearExhibitorAll();
//
//        readApplicationCSV(dbHelper, getInputStream("Application.csv"));
//        readCompanyApplicationCSV(dbHelper, getInputStream("CompanyApplication.csv"));
//        readBusinessMappingCSV(dbHelper, getInputStream("BusinessMapping.csv"));
//        readCompanyCatalogProductCSV(dbHelper, getInputStream("CompanyCatalogProduct.csv"));
//        readExhibitionCatalogProductLangCSV(dbHelper, getInputStream("ExhibitionCatalogProductLang.csv"));
//        readExhibitorCSV(dbHelper, getInputStream("exhibitors.csv"), getInputStream("ExhibitorDescripton.csv"));//Exhibitor Data/
//        readHallCSV(dbHelper, getInputStream("Hall.csv"));
//
//        long endTime = System.currentTimeMillis();
//        LogUtil.i(TAG, "导入完成：" + (endTime - startTime) + "ms");
//
//    }
//
//    /**
//     * 仅用于测试，asset目录下
//     * @param dbHelper
//     */
//    public static void processExhibitorCsvTest(DBHelper dbHelper) {
//        long startTime = System.currentTimeMillis();
//
//        LogUtil.i(TAG, "processExhibitorCsvTest......");
//
//        dbHelper.deleteAllAppCompany();
//        dbHelper.clearAppIndustry();
//        dbHelper.deleteBsnsMappingAll();
//        dbHelper.deleteExhibitorIndustryDtlAll();
//        dbHelper.clearIndustry();
//        dbHelper.clearFloor();
//        dbHelper.clearExhibitorAll();
//
//        readApplicationCSV(dbHelper, getInputStreamAsscts("Application.csv"));
//        readCompanyApplicationCSV(dbHelper, getInputStreamAsscts("CompanyApplication.csv"));
//        readBusinessMappingCSV(dbHelper, getInputStreamAsscts("BusinessMapping.csv"));
//        readCompanyCatalogProductCSV(dbHelper, getInputStreamAsscts("CompanyCatalogProduct.csv"));
//        readExhibitionCatalogProductLangCSV(dbHelper, getInputStreamAsscts("ExhibitionCatalogProductLang.csv"));
//        readExhibitorCSV(dbHelper, getInputStreamAsscts("exhibitors.csv"), getInputStreamAsscts("ExhibitorDescripton.csv"));//Exhibitor Data/
//        readHallCSV(dbHelper, getInputStreamAsscts("Hall.csv"));
//
//        long endTime = System.currentTimeMillis();
//        LogUtil.i(TAG, "导入完成：" + (endTime - startTime) + "ms");
//
//    }
//
//}
