package com.adsale.ChinaPlas.utils;


/**
 * Created by Carrie on 2017/8/8.
 */

public class Constant {
    public static final String SHOW_ID = "479";

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

    //    public static final int M6_BANNER_WIDTH = AppUtil.isTablet() ? 1740 : 1754;
//    public static final int M6_BANNER_HEIGHT = AppUtil.isTablet() ? 212 : 538;
    public static final int M6_BANNER_WIDTH = AppUtil.isTablet() ? 1740 : 877;
    public static final int M6_BANNER_HEIGHT = AppUtil.isTablet() ? 212 : 269;

    public static final int EVENT_BG_WIDTH = AppUtil.isTablet() ? 765 : 520;
    public static final int EVENT_BG_HEIGHT = AppUtil.isTablet() ? 89 : 232;

    /**
     * ---------------------Config 配置信息---------------------------------------
     **/
    public static final String SP_CONFIG = "Config";
    public static final String SP_UPDATE_INFO = "UpdateInfo";
    public static final String SP_UPDATE_INFO_BEFORE = "UpdateInfoBefore";
    /*  txt files:  FileSizeInfo.txt  NewTechInfo.txt ... */
    public static final String SP_LASTMODIFIED = "LastModified";
    /* webservice：MainIcon WebContent News ...  */
    public static final String SP_LUT = "LastUpdateTime";
    public static final String SP_LOGIN = "Login";
    public static final String SP_HP = "HelpPage";
    public static final String SP_DOWNLOAD_CENTER = "DownloadCenter";
    public static final String SP_EVENT_PAGE = "EventPage";


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
    public static final String SCHEDULE_DAY00 = "2019-05-20";
    public static final String SCHEDULE_DAY01 = "2019-05-21";
    public static final String SCHEDULE_DAY02 = "2019-05-22";
    public static final String SCHEDULE_DAY03 = "2019-05-23";
    public static final String SCHEDULE_DAY_END = "2019-05-24";

    public static final int DAY00 = 20;  // 5.20不是开展日，但这天有同期活动
    public static final int DAY01 = 21;  // 开展第一天
    public static final int DAY02 = 22;
    public static final int DAY03 = 23;
    public static final int DAY04 = 24;

    public static final String INTENT_SCHEDULE = "Schedule";
    public static final String INTENT_EXHIBITOR = "Exhibitor";
    public static final String COMPANY_ID = "CompanyID";
    public static final String INTENT_SEMINAR_DTL_ID = "SeminarDtlID";

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
    public static final String BDTJ_NEWS = "News";
    public static final String BDTJ_NEW_THECH = "NewTechCollection";

    public static final String BDTJ_MY_ACCOUNT = "MyAccount";
    public static final String BDTJ_MyCHINAPLAS = "MyCHINAPLAS";
    public static final String BDTJ_MY_EXHIBITOR = "MyExhibitor";
    public static final String BDTJ_MySchedule = "MySchedule";
    public static final String BDTJ_MY_NAME_CARD = "MyNameCard";
    public static final String BDTJ_ExhibitorHistory = "ExhibitorHistory";
    public static final String BDTJ_Sync = "Sync";

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
    public static final String BDTJ_PDF_CENTER = "PDFCenter";

    public static final String DB_UPGRADE = "dbUpgrade";

    public static final String IS_AD_OPEN = "isADOpen";
    public static final String ADBaseUrl = "";

    public static final String APK_NAME = "ChinaPlas_%d.apk";


    public static final String TXT_CONCURRENT_EVENT = "CurrentEvents.txt";
    public static final String TXT_MAIN_PIC_INFO = "MainMenuInfo.txt";
    public static final String TXT_PDF_CENTER_INFO = "PDFCenterInfos.txt";
    public static final String TXT_NOTIFICATION = "notification.txt";
    public static final String TXT_NEW_TEC = "NewTechInfo.txt";
    public static final String TXT_NEW_TEC_TEST = "NewTechInfo_44.txt";
    public static final String TXT_AD = ReleaseHelper.IsAdTest ? "advertisement_test.txt" : "advertisement_2019.txt";
    public static final String TXT_AD_TEST = "advertisement_19test.txt";
    public static final String TXT_APK_VERSION = "apkVersion.txt";
    public static final String TXT_COORDINATE = "coordinate.txt";
    public static final String TXT_FILE_SIZE = "FileSizeInfo.txt";
    public static final String TXT_PREREG_INFO = "preregInfo.txt";
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
    public static final String CSV_NEWTEC_CATEGORY_ID = DIR_NEW_TEC.concat("CategoryID.csv");
    public static final String CSV_NEWTEC_PRODUCTS_ID = DIR_NEW_TEC.concat("ProductID.csv");
    public static final String CSV_NEWTEC_PRODUCT_IMG = DIR_NEW_TEC.concat("ProductImage.csv");
    public static final String CSV_NEWTEC_CATOGORY_SUB = DIR_NEW_TEC.concat("CategorySub.csv");

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

    /**
     * 腾讯防水墙APP_ID
     */
    public static String TX_ID_EN = "2098446147";
    public static String TX_KEY_EN = "0Cg9GJSGbTffnp-zLObrnhg**";
    public static String TX_ID_CN = "2019984811";
    public static String TX_KEY_CN = "03yepRDLXD14UyUCFbxSTXw**";
    public static String TX_ID_TC = "2010013688";
    public static String TX_KEY_TC = "0dCuA_mtsY50aQznFe3tZfg**";


    public static String MY_CPS_MY_TOOL = "MyCPSTools";
    public static String MY_CPS_Exhibitor = "MyCPSExhibitorInfo";
    public static String MY_CPS = "MyCPS";

    public static String ACTION_EXHIBITOR_DTL = "com.adsale.Chinaplas.ExhibitorDetail";
    public static String ACTION_WEB_CONTENT = "com.adsale.Chinaplas.WebContent";
    public static String ACTION_SEMINAR_INFO = "com.adsale.Chinaplas.TechSeminarDtl";
    public static String ACTION_NEWS = "com.adsale.Chinaplas.News";
    public static String ACTION_NEWS_DTL = "com.adsale.Chinaplas.NewsDtl";
    public static String ACTION_WEB_VIEW = "com.adsale.Chinaplas.WebView";
    public static String ACTION_MAP_DETAIL = "com.adsale.Chinaplas.MapDetail";
    public static String ACTION_REGISTER = "com.adsale.Chinaplas.Register";


    public static String TITLE_EXHIBITOR_DTL = AppUtil.getName("公司資料", "Company Info", "公司资料");
    public static String TITLE_EVENT = AppUtil.getName("同期活動", "Concurrent Event", "同期活动");
    public static String TITLE_SEMINAR = AppUtil.getName("技術交流會", "Technical Seminar", "技术交流会");
    public static String TITLE_NEWS = AppUtil.getName("最新消息", "News", "最新消息");

}
