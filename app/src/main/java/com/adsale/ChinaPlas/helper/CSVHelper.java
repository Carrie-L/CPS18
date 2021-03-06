package com.adsale.ChinaPlas.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.dao.CompanyApplication;
import com.adsale.ChinaPlas.dao.BussinessMapping;
import com.adsale.ChinaPlas.dao.CompanyProduct;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorZone;
import com.adsale.ChinaPlas.dao.Map;
import com.adsale.ChinaPlas.dao.FloorPlanCoordinate;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.NewCategorySub;
import com.adsale.ChinaPlas.dao.NewCategoryID;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.dao.NewProductsID;
import com.adsale.ChinaPlas.dao.ProductImage;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarSpeaker;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OtherRepository;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.adsale.ChinaPlas.utils.AppUtil.getAssetInputStream;
import static com.adsale.ChinaPlas.utils.AppUtil.getInputStream;
import static com.adsale.ChinaPlas.utils.AppUtil.getInputStreamFromSd;
import static com.adsale.ChinaPlas.utils.AppUtil.getInputStreamFromSdOrAsset;


public class CSVHelper {
    private final String TAG = "CSVHelper";
    private Context mContext;
    private BufferedReader br;
    private ExhibitorRepository mExhibitorRepository;

    public CSVHelper() {

    }

    public void setContext(Context context) {
        mContext = context;
    }


    public ArrayList<AgentInfo> getHotelDetailsCsv() {
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
     */
    public ArrayList<AgentInfo> getHrHotelDetailCSV() {
        ArrayList<AgentInfo> mHotelInfos = new ArrayList<>();
        BufferedReader hotelBR = null;
        AgentInfo info;
        File hotelDetailFile = new File(App.rootDir + "TravelInfo/" + "hrHotelDetail.csv");

        try {
            if (hotelDetailFile.exists()) {
                hotelBR = new BufferedReader(new FileReader(hotelDetailFile));
                LogUtil.i(TAG, "hrHotelDetail.csv存在SD");
            } else {
                hotelBR = new BufferedReader(new InputStreamReader(AppUtil.getAssetInputStream(Constant.ASSET_HR_HOTEL_DETAIL)));
                LogUtil.i(TAG, "hrHotelDetail.csv不存在，读取asset");
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
        } catch (IOException e) {
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


    /* =================================== Exhibitor Data ============================ */

    public void initExhibitorCsvHelper() {
        mExhibitorRepository = ExhibitorRepository.getInstance();
        mExhibitorRepository.initCsvDao();
    }


    /**
     * 读取exhibitors.csv，且存入数据库表EXHIBITOR
     * <p> 解析两个csv，将数据合并起来，一起插入数据库
     * <p>
     * exhibitor.csv
     * <font color="#f97798">exhibitorDes.csv</font>
     */
    public void readExhibitorCSV(String exhibitorPath, String descPath) {
        if (!new File(exhibitorPath).exists()) {
            return;
        }
        if (!new File(descPath).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<Exhibitor> entities = new ArrayList<>();
        Exhibitor entity = null;
        CSVReader reader;
        InputStream isDes = getInputStream(descPath);
        InputStream is1 = getInputStream(exhibitorPath);
        if (isDes != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is1, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Exhibitor();
                        entity.parseExhibitor(line);
                        if (TextUtils.isEmpty(entity.getStrokeEng().trim())) {
                            entity.setStrokeEng(AppUtil.getFirstChar(entity.getCompanyNameEN().trim()));
                        }
                        if (Character.isDigit(entity.getStrokeEng().trim().charAt(0))) {
                            entity.setStrokeEng("#");
                        }
                        //去掉空格
                        entity.setCompanyNameCN(entity.getCompanyNameCN().trim());
                        entity.setCompanyNameEN(entity.getCompanyNameEN().trim());
                        entity.setCompanyNameTW(entity.getCompanyNameTW().trim());

                        if (entity.getStrokeEng().contains("#")) {
                            entity.setStrokeEng("ZZZ#");
                        }
                        if (entity.getStrokeTrad().contains("#")) {
                            entity.setStrokeTrad("999#");
                        }
                        if (entity.getPYSimp().contains("#")) {
                            entity.setPYSimp("ZZZ#");
                        }
                        if (entity.getIsFavourite() == null) {
                            entity.setIsFavourite(0);
                        }
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is1 != null) {
                        is1.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readExhibitorCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            readExhibitorDesCSV(isDes, entities);
        }
    }

    /**
     * 读取ExhibitorDescripton.csv，且存入数据库表EXHIBITOR
     */
    public void readExhibitorDesCSV(InputStream is, ArrayList<Exhibitor> list) {
        long startTime = System.currentTimeMillis();
        ArrayList<Exhibitor> lists = new ArrayList<>();
        Exhibitor entity = null;
        CSVReader reader;
        if (is != null) {
            //读取csv
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Exhibitor();
                        entity.parseDescription(line);
                        lists.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readExhibitorDescriptonCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            combineExhibitorList(list, lists);
        }
    }


    /**
     * 合并两个csv数据，插入数据库
     *
     * @param list1   exhibitors.csv的数据
     * @param listDes <font color="#f97798">ExhibitorDescripton.csv</font>
     */
    private void combineExhibitorList(ArrayList<Exhibitor> list1, ArrayList<Exhibitor> listDes) {
        int size1 = list1.size();
        int size2 = listDes.size();
        Exhibitor entity1;
        Exhibitor entity2;
        for (int i = 0; i < size1; i++) {
            entity1 = list1.get(i);
            for (int j = 0; j < size2; j++) {
                entity2 = listDes.get(j);
                if (entity1.getCompanyID().equals(entity2.getCompanyID())) {
                    entity1.setDescE(entity2.getDescE());
                    entity1.setDescS(entity2.getDescS());
                    entity1.setDescT(entity2.getDescT());
                    list1.set(i, entity1);
                    break;
                }
            }
        }

        long startTime2 = System.currentTimeMillis();
        mExhibitorRepository.insertExhibitorAll(list1);
        AppUtil.setUpdateExhibitorSuccess(true);
        AppUtil.setExhibitorHasUpdate(false);
        AppUtil.getDuringTime(TAG, "插入Exhibitor数据库", startTime2);
    }

    /**
     * 读取CompanyProduct.csv，且存入数据库表 CompanyProduct
     */
    private void readCompanyProductCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<CompanyProduct> entities = new ArrayList<>();
        CompanyProduct entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new CompanyProduct();
                        entity.parserID(line);
                        entity.setUpdatedAt("2019-01-08 17:42:27");
                        entity.setCreatedAt("2019-01-08 17:42:27");
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            LogUtil.i(TAG, "SD卡：readCompanyProductCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            // 此表沒有主鍵
            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.deleteAllCompanyProduct();
            mExhibitorRepository.insertCompanyProductAll(entities);
            LogUtil.i(TAG, "存儲 CompanyProduct 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    /**
     * 读取CompanyApplication.csv，且存入数据库表APPLICATION_COMPANY
     */
    private void readCompanyApplicationCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<CompanyApplication> entities = new ArrayList<>();
        CompanyApplication entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new CompanyApplication();
                        entity.parserID(line);
                        entity.setUpdatedAt("2019-01-08 17:42:27");
                        entity.setCreatedAt("2019-01-08 17:42:27");
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            LogUtil.i(TAG, "SD卡：readCompanyApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            // 此表沒有主鍵
            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.deleteAllAppCompany();
            mExhibitorRepository.insertApplicationCompaniesAll(entities);
            LogUtil.i(TAG, "存儲 CompanyApplication 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    /**
     * 读取Application.csv，且存入数据库表APPLICATION_INDUSTRY
     */
    private void readApplicationCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<Application> entities = new ArrayList<>();
        Application entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Application();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            LogUtil.i(TAG, "SD卡：readApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.insertAppIndustryAll(entities);
            LogUtil.i(TAG, "存儲readApplicationCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    /**
     * 读取ExhibitionCatalogProductLang.csv，且存入数据库表INDUSTRY
     */
    private void readExhibitionCatalogProductLangCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<Industry> entities = new ArrayList<>();
        Industry entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                String enSort = "";
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Industry();
                        entity.parser(line);
                        entity.setCatEng(entity.getCatEng().trim());

                        // 由于csv中没有 EN 排序，因此在代码中提取 CatEng 的首字母，保存到 enSort 中。
                        // 为了在get list时方便排序，将# 放到最后，将#赋值为 ZZZ#.因此在取出时记得replace ZZZ
                        enSort = entity.getCatEng();
                        if (enSort.startsWith("(")) {
                            enSort = enSort.replace("(", "#");
                        } else if (Character.isDigit(enSort.charAt(0))) {
                            enSort = enSort.replace(enSort.charAt(0), '#');
                        }
                        enSort = AppUtil.getFirstChar(enSort);
                        if (enSort.contains("#")) {
                            entity.setSortEN("ZZZ".concat(enSort));
                        } else {
                            entity.setSortEN(enSort);
                        }
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            LogUtil.i(TAG, "SD卡：readExhibitionCatalogProductLangCSV所花费的时间为:" + (System.currentTimeMillis() - startTime)
//                    + "ms");

            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.insertIndustryAll(entities);
            LogUtil.i(TAG, "存儲 Product 所花费的时间为:" + (System.currentTimeMillis() - startTime2)
                    + "ms");
        }
    }

    /**
     * 读取BusinessMapping.csv，且存入数据库表EXHIBITOR_INDUSTRY_DTL
     */
    private void readBusinessMappingCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<BussinessMapping> entities = new ArrayList<>();
        BussinessMapping entity = null;
        CSVReader reader;
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new BussinessMapping();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readBusinessMappingCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.deleteBsnsMappingAll();
            mExhibitorRepository.insertBsnsMappingAll(entities);
            LogUtil.i(TAG, "存儲readBusinessMappingCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    /* ==========================FloorPlan || FloorPlan.csv===================================  */
//

    /**
     * 读取FloorPlan.csv，且存入数据库表FLOOR_PLAN_COORDINATE 文件名也有可能加后缀，如：FloorPlan_0313V3.csv
     * <P>FloorPlan坐标信息
     */
    public boolean readFloorPlanCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<FloorPlanCoordinate> entities = new ArrayList<>();
        FloorPlanCoordinate entity;
        CSVReader reader;
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new FloorPlanCoordinate();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readFloorPlanCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            long startTime2 = System.currentTimeMillis();
            FloorRepository repository = FloorRepository.getInstance();
            repository.clearFloorCoordinate();
            repository.insertFloorCoordinate(entities);
            LogUtil.i(TAG, "存儲readFloorPlanCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
            return true;
        }
        return false;
    }
//
//

    /**
     * 读取Hall.csv，且存入数据库表FLOOR_PLAN_COORDINATE
     * <P>FloorPlan坐标信息
     */
    private void readHallCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<Map> entities = new ArrayList<>();
        Map entity;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
//                        entity = new Map();
//                        entity.parser(line);
//                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readHallCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.clearFloor();
            mExhibitorRepository.insertFloorAll(entities);
            LogUtil.i(TAG, "存儲readHallCSV所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    private void readExhibitorZoneCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<ExhibitorZone> entities = new ArrayList<>();
        ExhibitorZone entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new ExhibitorZone();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            LogUtil.i(TAG, "SD卡：ExhibitorZoneCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.clearExhibitorZone();
            mExhibitorRepository.insertExhibitorZoneAll(entities);
            LogUtil.i(TAG, "存儲 ExhibitorZone 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    private void readZoneCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<Zone> entities = new ArrayList<>();
        Zone entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Zone();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            LogUtil.i(TAG, "SD卡：ExhibitorZoneCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.clearZone();
            mExhibitorRepository.insertZoneAll(entities);
            LogUtil.i(TAG, "存儲 read Zone CSV 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    private void readCountryCSV(String path) {
        if (!new File(path).exists()) {
            return;
        }
        long startTime = System.currentTimeMillis();
        ArrayList<Country> entities = new ArrayList<>();
        Country entity = null;
        CSVReader reader;
        InputStream is = getInputStream(path);
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Country();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readCountryCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");
            long startTime2 = System.currentTimeMillis();
            mExhibitorRepository.clearCounutry();
            mExhibitorRepository.insertCountryAll(entities);
            LogUtil.i(TAG, "存儲 readCountryCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    //
    /* ==========================Technical Seminar=================================== */
    public void readSeminarCSV() {
        readSeminarInfoCSV(AppUtil.getInputStream("TechnicalSeminar/SeminarInfo.csv"));
        readSeminarSpeakerCSV(AppUtil.getInputStream("TechnicalSeminar/SeminarSpeaker.csv"));
    }

    /**
     * @param is SeminarInfo.csv
     */
    private void readSeminarInfoCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<SeminarInfo> entities = new ArrayList<>();
        SeminarInfo entity;
        CSVReader reader;
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new SeminarInfo();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：read SeminarInfo.csv 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            long startTime2 = System.currentTimeMillis();
            OtherRepository repository = OtherRepository.getInstance();
            repository.clearSeminarInfo();
            repository.insertSeminarInfoAll(entities);
            LogUtil.i(TAG, "存儲 SeminarInfo.csv 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }

    /**
     * @param is SeminarSpeaker.csv
     */
    private void readSeminarSpeakerCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<SeminarSpeaker> entities = new ArrayList<>();
        SeminarSpeaker entity;
        CSVReader reader;
        if (is != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new SeminarSpeaker();
                        entity.parser(line);
                        entities.add(entity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：read SeminarSpeaker.csv 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            long startTime2 = System.currentTimeMillis();
            OtherRepository repository = OtherRepository.getInstance();
            repository.clearSeminarSpeaker();
            repository.insertSeminarSpeakerAll(entities);
            LogUtil.i(TAG, "存儲 SeminarSpeaker.csv 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        }
    }


    //
//    public  void readBussinessMappingCSV(DBHelper InputStream is) {
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
//    public  InputStream getInputStream(String csv) {
//        return AppUtil.getInputStream("Exhibitor Data/" + csv);
//    }
//
//    public  InputStream getInputStreamAsscts(String csv){
//        return AppUtil.getAssetInputStream("Exhibitor Data/" + csv);
//    }
//
    //更新中心下载展商资料后的数据处理
    public void processExhibitorCsv() {
        long startTime = System.currentTimeMillis();
        LogUtil.i(TAG, "processExhibitorCsv......");
        if (mExhibitorRepository == null) {
            throw new NullPointerException("mExhibitorRepository cannot be null, please #initExhibitorCsvHelper() first.");
        }
        readApplicationCSV(App.rootDir + "ExhibitorData/Application.csv");
        readCompanyApplicationCSV(App.rootDir + "ExhibitorData/CompanyApplication.csv");
        readCompanyProductCSV(App.rootDir + "ExhibitorData/CompanyProduct.csv");
        readExhibitionCatalogProductLangCSV(App.rootDir + "ExhibitorData/Product.csv");
        readExhibitorCSV(App.rootDir + "ExhibitorData/exhibitors.csv", App.rootDir + "ExhibitorData/ExhibitorDescripton.csv");//Exhibitor Data/
//        testExhibitor();
        readExhibitorZoneCSV(App.rootDir + "ExhibitorData/ExhibitorZone.csv");
        readZoneCSV(App.rootDir + "ExhibitorData/Zone.csv");
        readCountryCSV(App.rootDir + "ExhibitorData/Country.csv");

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "导入完成：" + (endTime - startTime) + "ms");

    }

    private void testExhibitor() {
        readExhibitorCSVTest(getAssetInputStream("ExhibitorData/exhibitors.csv"), getAssetInputStream("ExhibitorData/ExhibitorDescripton.csv"));//Exhibitor Data/
    }

//    /**
//     * 检查 csv是否存在，存在则解析，否则不解析
//     * @param fileName
//     * @return
//     */
//    private boolean checkCsvExists(String fileName) {
//        return new File(App.rootDir.concat(fileName)).exists();
//    }

    /*   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  New Tec CSV  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    */
    private NewTecRepository mNewTecRepository;

    public void initNewTec(NewTecRepository repository) {
        mNewTecRepository = repository;
    }

    public boolean readNewTecCSV() {
        readNewProductInfoCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_PRODUCT_INFO));
        readNewProductIDCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_PRODUCTS_ID));
        readNewCategorySubCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_CATOGORY_SUB));
        readNewCategoryIDCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_CATEGORY_ID));
        readProductImageCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_PRODUCT_IMG));
//        if (readNewProductInfoCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_PRODUCT_INFO)) &&
//                readNewProductIDCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_PRODUCTS_ID)) &&
//                readNewCategorySubCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_CATOGORY_SUB)) &&
//                readNewCategoryIDCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_CATEGORY_ID)) &&
//                readProductImageCSV(getInputStreamFromSd(Constant.CSV_NEWTEC_PRODUCT_IMG))) {
        return true;
    }

    public void readNewTecCSV2(){
        LogUtil.i(TAG,"readNewTecCSV2");
        readNewCategoryIDCSV(getInputStreamFromSdOrAsset(Constant.CSV_NEWTEC_CATEGORY_ID));
        readProductImageCSV(getInputStreamFromSdOrAsset(Constant.CSV_NEWTEC_PRODUCT_IMG));
    }

    private boolean readNewProductInfoCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<NewProductInfo> entities = new ArrayList<>();
        NewProductInfo entity;
        CSVReader reader;
        if (is == null) {
            return false;
        }
        try {
            reader = new CSVReader(new InputStreamReader(is, "UTF8"));
            String[] line = reader.readNext();
            if (line != null) {
                while ((line = reader.readNext()) != null) {
                    entity = new NewProductInfo();
                    entity.parser(line);
                    entities.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i("NEWTECH:", "NEWTECH: SD卡：NewProductInfoCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

        long startTime2 = System.currentTimeMillis();
        if (mNewTecRepository == null) {
            throw new NullPointerException("mNewTecRepository cannot be null,please #initNewTec()");
        }
        mNewTecRepository.clearProductInfo();
        mNewTecRepository.insertNewProductInfoAll(entities);
        LogUtil.i("NEWTECH:", "存儲 NewProductInfoCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        return true;
    }

    private boolean readNewProductIDCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<NewProductsID> entities = new ArrayList<>();
        NewProductsID entity;
        CSVReader reader;
        if (is == null) {
            return false;
        }
        try {
            reader = new CSVReader(new InputStreamReader(is, "UTF8"));
            String[] line = reader.readNext();
            if (line != null) {
                while ((line = reader.readNext()) != null) {
                    entity = new NewProductsID();
                    entity.parser(line);
                    entities.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i("NEWTECH:", "SD卡：ProductID readNewProductAndApplicationCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

        long startTime2 = System.currentTimeMillis();
        if (mNewTecRepository == null) {
            throw new NullPointerException("mNewTecRepository cannot be null,please #initNewTec()");
        }
        mNewTecRepository.clearNewProductID();
        mNewTecRepository.insertNewProductsIDAll(entities);
        LogUtil.i("NEWTECH:", "存儲 ProductID insertNewProductsAndApplicationAll 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        return true;
    }

    private boolean readNewCategoryIDCSV(InputStream is) {
        LogUtil.i("NEWTECH:", "readNewCategoryIDCSV");
        long startTime = System.currentTimeMillis();
        ArrayList<NewCategoryID> entities = new ArrayList<>();
        NewCategoryID entity;
        CSVReader reader;
        if (is == null) {
            LogUtil.i("NEWTECH:", "readNewCategoryIDCSV   is == null");
            return false;
        }
        try {
            reader = new CSVReader(new InputStreamReader(is, "UTF8"));
            String[] line = reader.readNext();
            if (line != null) {
                while ((line = reader.readNext()) != null) {
                    entity = new NewCategoryID();
                    entity.parser(line);
                    entities.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i("NEWTECH:", "SD卡：readNewCategoryIDCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms" + entities.size() + "," + entities.toString());

        long startTime2 = System.currentTimeMillis();
        if (mNewTecRepository == null) {
            throw new NullPointerException("mNewTecRepository cannot be null,please #initNewTec()");
        }
        mNewTecRepository.clearCategoryID();
        mNewTecRepository.insertNewCategoryIDAll(entities);
        LogUtil.i("NEWTECH:", "存儲 readNewCategoryIDCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        return true;
    }


    private boolean readProductImageCSV(InputStream is) {
        long startTime = System.currentTimeMillis();
        ArrayList<ProductImage> entities = new ArrayList<>();
        ProductImage entity;
        CSVReader reader;
        if (is == null) {
            return false;
        }
        try {
            reader = new CSVReader(new InputStreamReader(is, "UTF8"));
            String[] line = reader.readNext();
            if (line != null) {
                while ((line = reader.readNext()) != null) {
                    entity = new ProductImage();
                    entity.parser(line);



                    entities.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i("NEWTECH:", "SD卡：readProductImageCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

        long startTime2 = System.currentTimeMillis();
        if (mNewTecRepository == null) {

        }
        mNewTecRepository.clearProductImage();
        mNewTecRepository.insertProductImageAll(entities);
        LogUtil.i("NEWTECH:", "存儲 insertProductImageAll 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        return true;
    }

    private boolean readNewCategorySubCSV(InputStream is) {
        LogUtil.i("NEWTECH:", "readNewCategorySubCSV");
        long startTime = System.currentTimeMillis();
        ArrayList<NewCategorySub> entities = new ArrayList<>();
        NewCategorySub entity;
        CSVReader reader;
        if (is == null) {
            LogUtil.i("NEWTECH:", "readNewCategorySubCSV  is == null");
            return false;
        }
        try {
            LogUtil.i("NEWTECH:", "readNewCategorySubCSV 1");
            reader = new CSVReader(new InputStreamReader(is, "UTF8"));
            String[] line = reader.readNext();
            if (line != null) {
                LogUtil.i("NEWTECH:", "readNewCategorySubCSV line != null");
                while ((line = reader.readNext()) != null) {
                    entity = new NewCategorySub();
                    entity.parser(line);
                    entities.add(entity);
                }
            }else{
                LogUtil.i("NEWTECH:", "readNewCategorySubCSV line == null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i("NEWTECH:", "SD卡：NewCategorySubCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms"  +entities.size());

        long startTime2 = System.currentTimeMillis();
        if (mNewTecRepository == null) {
            LogUtil.i("NEWTECH:", "readNewCategorySubCSV  mNewTecRepository == null");
        }
        mNewTecRepository.clearCategorySub();
        mNewTecRepository.insertCategorySubAll(entities);
        LogUtil.i("NEWTECH:", "存儲 NewCategorySubCSV 所花费的时间为:" + (System.currentTimeMillis() - startTime2) + "ms");
        return true;
    }


    //
//    /**
//     * 仅用于测试，asset目录下
//     * @param dbHelper
//     */
    public void processExhibitorCsvTest(DBHelper dbHelper) {
        long startTime = System.currentTimeMillis();

        LogUtil.i(TAG, "processExhibitorCsvTest......");

//        dbHelper.deleteAllAppCompany();
//        dbHelper.clearAppIndustry();
//        dbHelper.deleteBsnsMappingAll();
//        dbHelper.deleteExhibitorIndustryDtlAll();
//        dbHelper.clearIndustry();
//        dbHelper.clearFloor();
//        dbHelper.clearExhibitorAll();

//        readApplicationCSV(getInputStreamAsscts("Application.csv"));
//        readCompanyApplicationCSV(getInputStreamAsscts("CompanyApplication.csv"));
//        readBusinessMappingCSV(getInputStreamAsscts("BusinessMapping.csv"));
//        readCompanyCatalogProductCSV(getInputStreamAsscts("CompanyProduct.csv"));
//        readExhibitionCatalogProductLangCSV(getInputStreamAsscts("ExhibitionCatalogProductLang.csv"));
//        readExhibitorCSV(getInputStreamAsscts("exhibitors.csv"), getInputStreamAsscts("ExhibitorDescripton.csv"));//Exhibitor Data/
//        readHallCSV(getInputStreamAsscts("Hall.csv"));

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "导入完成：" + (endTime - startTime) + "ms");

    }

    public void readExhibitorCSVTest(InputStream is1, InputStream isDes) {
        long startTime = System.currentTimeMillis();
        ArrayList<Exhibitor> entities = new ArrayList<>();
        Exhibitor entity = null;
        CSVReader reader;
        if (isDes != null) {
            try {
                reader = new CSVReader(new InputStreamReader(is1, "UTF8"));
                String[] line = reader.readNext();
                if (line != null) {
                    while ((line = reader.readNext()) != null) {
                        entity = new Exhibitor();
                        entity.parseExhibitor(line);
                        if (TextUtils.isEmpty(entity.getStrokeEng().trim())) {
                            entity.setStrokeEng(AppUtil.getFirstChar(entity.getCompanyNameEN().trim()));
                        }
                        if (Character.isDigit(entity.getStrokeEng().trim().charAt(0))) {
                            entity.setStrokeEng("#");
                        }
                        //去掉空格
                        entity.setCompanyNameCN(entity.getCompanyNameCN().trim());
                        entity.setCompanyNameEN(entity.getCompanyNameEN().trim());
                        entity.setCompanyNameTW(entity.getCompanyNameTW().trim());

                        if (entity.getStrokeEng().contains("#")) {
                            entity.setStrokeEng("ZZZ#");
                        }
                        if (entity.getStrokeTrad().contains("#")) {
                            entity.setStrokeTrad("999#");
                        }
                        if (entity.getPYSimp().contains("#")) {
                            entity.setPYSimp("ZZZ#");
                        }

                        if (entity.getIsFavourite() == null) {
                            entity.setIsFavourite(0);
                        }
//                        entity.setUpdatedAt();
//                        entity.setDescUpdatedAt();
                        entities.add(entity);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is1 != null) {
                        is1.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i(TAG, "SD卡：readExhibitorCSV所花费的时间为:" + (System.currentTimeMillis() - startTime) + "ms");

            readExhibitorDesCSV(isDes, entities);
        }
    }

}
