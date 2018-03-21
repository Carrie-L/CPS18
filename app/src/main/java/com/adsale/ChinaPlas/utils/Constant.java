package com.adsale.ChinaPlas.utils;


/**
 * Created by Carrie on 2017/8/8.
 */

public class Constant {
    public static final String SHOW_ID="479";

    /**
     * --------------------- 图片尺寸---------------------------------------
     **/
    /* toolbar 图片尺寸 */
    public static final int PHONE_HEADER_WIDTH = 320;
    public static final int PHONE_HEADER_HEIGHT = 68;

    public static final int PAD_CONTENT_WIDTH = 1840; // 平板内容宽度 1840.没有特意指明的则主界面平板宽度都为这个值
    public static final int PAD_HEADER_HEIGHT = 148;

    /* 主界面menu */
    public static final int MAIN_MENU_WIDTH = 115;
    public static final int MAIN_MENU_HEIGHT = 120;

    public static final int MAIN_MENU_WIDTH_PAD = 408;
    public static final int MAIN_MENU_HEIGHT_PAD = 288;

    /* 主界面top banner */
    public static final int MAIN_TOP_BANNER_WIDTH = 345;
    public static final int MAIN_TOP_BANNER_HEIGHT = 158;
    public static final int MAIN_TOP_BANNER_HEIGHT_PAD = 616;

    /* 主界面icon */
    public static final int MENU_ICON_SIZE = 90;// 90 * 90
    public static final int MENU_ICON_SIZE_PAD = 150;// 90 * 90

    public static final int PAD_RIGHT_BANNER_WIDTH = 560;// 560*288
    public static final int PAD_TOP_MARGIN = 16;// 560*288


    /* 广告尺寸  */
    public static final int M1_WIDTH_PHONE = 1920;
    public static final int M1_HEIGHT_PHONE = 3408;
    public static final int M1_WIDTH_TABLET = 2048;
    public static final int M1_HEIGHT_TABLET = 1536;

    public static final int M2_WIDTH = AppUtil.isTablet() ? 1636 : 1920;
    public static final int M2_HEIGHT_BIG = AppUtil.isTablet() ? 768 : 1704;

    public static final int M3_WIDTH = AppUtil.isTablet() ? 1636 : 1920;
    public static final int M3_HEIGHT = AppUtil.isTablet() ? 138 : 346;

    public static final int M4_WIDTH = AppUtil.isTablet() ? 818 : 960;
    public static final int M4_HEIGHT = AppUtil.isTablet() ? 138 : 346;

    public static final int M5_VIDEO_WIDTH = 1072;
    public static final int M5_VIDEO_HEIGHT = 822;

    public static final int M6_BANNER_WIDTH = AppUtil.isTablet() ? 1740 : 1754;
    public static final int M6_BANNER_HEIGHT = AppUtil.isTablet() ? 212 : 538;


    /**
     * ---------------------Config 配置信息---------------------------------------
     **/
    public static final String SP_CONFIG = "Config";
    public static final String SP_UPDATE_INFO = "UpdateInfo";
    public static final String SP_UPDATE_INFO_BEFORE = "UpdateInfoBefore";
    public static final String SP_LASTMODIFIED = "LastModified";
    public static final String SP_LUT = "LastUpdateTime";
    public static final String SP_LOGIN = "Login";
    public static final String SP_HP = "HelpPage";

    /* property */
    public static final String SCREEN_WIDTH = "ScreenWidth";
    public static final String SCREEN_HEIGHT = "ScreenHeight";
    public static final String DISPLAY_HEIGHT = "DisplayHeight";//不包括状态栏的高度
    public static final String TOOLBAR_HEIGHT = "ActionBarHeight";
    public static final String PAD_LEFT_MARGIN = "PadLeftMargin";


    //-------------------MainIcon的最后更新时间----------------
    public static final String mainIconLUT = "MainIconLUT";
    public static final String newsLUT = "NewsUT";
    public static final String newsLinkLUT = "NewsLinkLUT";
    public static final String webContentLUT = "WebContentLUT";
    public static final String exFloorLUT = "ExFloorLUT";
    public static final String mapFloorLUT = "MapFloorLUT";

    public static final String TRAD_STROKE = "劃";

    public static final String IS_LOGIN = "isLogin";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_PWD = "userPassword";

    public static final String REG_PNG = "reg.png";

    /*  Schedule */
    public static final String SCHEDULE_DAY01 = "2018-04-24";
    public static final String SCHEDULE_DAY02 = "2018-04-25";
    public static final String SCHEDULE_DAY03 = "2018-04-26";
    public static final String SCHEDULE_DAY_END = "2018-04-27";

    public static final String INTENT_SCHEDULE = "Schedule";
    public static final String INTENT_EXHIBITOR = "Exhibitor";
    public static final String COMPANY_ID = "CompanyID";

    /* intent value */
    public static final String INTENT_BAIDU_TJ = "BaiDuTJ";
    public static final String INTENT_COMMON_TYPE = "TYPE";
    public static final String PUSH_INTENT = "PUSH_INTENT";
    public static final String LEFT_INTENT = "LEFT_INTENT";
    public static final String INTENT_NEW_TEC = "NewTecInfo";

    public static final int REQUEST_CODE_ADD_SCHEDULE = 5;


    //同步 2017.1.6
    public static final String SYNC_DATA = "SyncData";
    public static final String SYNC_UPDATE = "SyncUpdate";
    public static final String SYNC_DELETE = "SyncDelete";
    public static final String VMID = "vmid";

    public static final String BAIDU_TJ = "BD_TJ";
    public static final String MAIN_ICON = "MainIcon";
    public static final String TITLE = "title";


    /*百度统计ID*/
    public static final String BDTJ_VISITOR_REG = "VisitorPreRegistration";
    public static final String BDTJ_VISITOR_REG_TEXT = "VisitorPreRegistrationText";
    public static final String BDTJ_SUBSRIBEE_NEWSLETTER = "SubscribeeNewsletter";
    public static final String BDTJ_SCHEDULE = "Schedule";
    public static final String BDTJ_SETTING = "Settings";
    public static final String BDTJ_EXHIBITOR_LIST = "ExhibitorList";
    public static final String BDTJ_MY_EXHIBITOR = "MyExhibitor";
    public static final String BDTJ_NEWS = "News";
    public static final String BDTJ_NEW_THECH = "NewTechCollection";
    public static final String BDTJ_MY_NAME_CARD = "MyNameCard";
    public static final String BDTJ_MY_ACCOUNT = "MyAccount";
    public static final String BDTJ_VISITO = "Visitor";
    public static final String BDTJ_EXHIBOR_LIST = "Exhibitor List";
    public static final String BDTJ_CONTENT_UPDATE = "ContentUpdate";
    public static final String BDTJ_QR_SCANNER = "QRCodeScanner";
    public static final String BDTJ_NOTIFICATION_CENTER = "NotificationCenter";
    public static final String BDTJ_EVENTS = "CurrentEvents";
    public static final String BDTJ_EVENTS_TXT = "CurrentEventsTxt";
//    public static final String BDTJ_HALL_MAP_TEXT = "HallMapText";
    public static final String BDTJ_HALL_MAP_TEXT = "FloorplanImage";
    public static final String BDTJ_HALL_MAP = "HallMap";
    public static final String BDTJ_INTERESTED_EXHIBITOR = "MyInterestedExhibitor";
    public static final String BDTJ_TRAVEL_INFO = "TravelInfo";
    public static final String BDTJ_NEW_TEC = "NewTechCollection";
    public static final String BDTJ_NEW_TEC_TXT = "NewTechCollectionText";

    public static final String DB_UPGRADE = "dbUpgrade";

    public static final String IS_AD_OPEN = "isADOpen";

    public static final String TXT_CONCURRENT_EVENT = "CurrentEvents.txt";
    public static final String TXT_MAIN_PIC_INFO = "MainMenuInfo.txt";
    public static final String TXT_PDF_CENTER_INFO = "PDFCenterInfos.txt";
    public static final String TXT_NOTIFICATION = "notification.txt";
    public static final String TXT_NEW_TEC = "NewTechInfo.txt";
    public static final String TXT_NEW_TEC_TEST = "NewTechInfoTest.txt";

    public static final String UC_TXT_EXHIBITOR = "ExhibitorInfo.txt";
    public static final String UC_TXT_FLOOR_PLAN = "FloorPlan.txt";
    public static final String UC_TXT_SEMINAR = "SeminarInfo.txt";
    public static final String UC_TXT_TRAVEL = "Travelnfo.txt";
    public static final String UC_TXT_APP_CONTENTS = "appContents.txt";

    /* asset目录下文件夹名称 */
    public static final String DIR_EXHIBITOR = "ExhibitorData";
    public static final String DIR_FLOOR_PLAN = "FloorPlan";
    public static final String DIR_SEMINAR = "TechnicalSeminar";
    public static final String DIR_TRAVEL = "TravelInfo";
    public static final String DIR_EVENT = "ConcurrentEvent";
    public static final String DIR_NEW_TEC = "NewTec/";
    public static final String DIR_WEB_CONTENT = "WebContent/";

    public static final String ASSET_APPLICATION_CSV = "Application/Application.csv";
    public static final String ASSET_COMPANY_APPLICATION_CSV = "Application/CompanyApplication.csv";
    public static final String ASSET_TECHNICAL_SEMINAR_INFO = "TechnicalSeminar/SeminarInfo.csv";
    public static final String ASSET_TECHNICAL_SEMINAR_SPEAK = "TechnicalSeminar/SeminarSpeaker.csv";
    public static final String ASSET_HOTEL_DETAIL = "TravelInfo/HotelDetail.csv";
    public static final String ASSET_HR_HOTEL_DETAIL = "TravelInfo/hrHotelDetail.csv";

    /*  new tec 的 csv 名称 */
    public static final String CSV_NEWTEC_PRODUCT_INFO = DIR_NEW_TEC.concat("NewProductInfo.csv");
    public static final String CSV_NEWTEC_PRODUCT_CATEGORY = DIR_NEW_TEC.concat("NewProductAndCategory.csv");
    public static final String CSV_NEWTEC_PRODUCTS_AND_APPLICATION = DIR_NEW_TEC.concat("NewProductsAndApplication.csv");
    public static final String CSV_NEWTEC_PRODUCT_APPLICATION = DIR_NEW_TEC.concat("ProductApplication.csv");
    public static final String CSV_NEWTEC_PRODUCT_IMG = DIR_NEW_TEC.concat("ProductImage.csv");


    /*  WebView */
    public static final String WEB_URL = "Url";


    /* ---- 分享 ---- **/
    public static final String SHARE_IMAGE_PATH = "file:///android_asset/icon.png";
    public static final String SHARE_IMAGE_URL = "http://www.chinaplasonline.com/apps/2016/images/icon.png";


    /*  ----- 跳转到平面图的字段  ------    */
    /**
     * 兴趣产品
     */
    public static String InterestedExhibitor = "InterestedExhibitor";
    /**
     * 我的参展商
     */
    public static String MyExhibitor = "MyExhibitor";
    /***/
    public static String MapFloor = "MapFloor";
    /**
     * 展馆详情页
     */
    public static String ExhibitorDetail = "ExhibitorDetail";
    /**
     * 扫描二维码定位
     */
    public static String QRCODE_FLOOR = "QrcodeFloorPlan";
}
