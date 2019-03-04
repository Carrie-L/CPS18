package com.adsale.ChinaPlas.utils;

import android.content.SharedPreferences;
import com.adsale.ChinaPlas.App;

import junit.framework.Test;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Carrie on 2017/8/9.
 */

public class NetWorkHelper {
    private static final String TAG = "NetWorkHelper";
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=UTF-8");

    public static final String CPS_URL = "https://www.chinaplasonline.com/CPS19/idx/%s";

    /* Test service */
//    private static final String PROJECT_CODE = "CPS18";
//    public static final String DOWNLOAD_PATH = "https://eform.adsale.com.hk/AppCPS18Services/";
//    public static final String WEBSERVICEURL = "https://eform.adsale.com.hk/AppCPS18Services/wsCLE15.asmx";
//    public static final String DOWN_TXT_URL = "https://forms.adsale.com.hk/VirtualDirectory/AppCPS18CMS/AppFiles/{fileName}";  /*  test url */

     /* release service */
    private static final String PROJECT_CODE = "CPS2018";// scanFile : normal
    public static final String DOWNLOAD_PATH = "https://eform.adsale.com.hk/AppCPS2018Services/";
    public static final String WEBSERVICEURL = "https://eform.adsale.com.hk/AppCPS2018Services/wsCLE15.asmx";
//    public static final String DOWN_TXT_URL = "https://forms.adsale.com.hk/VirtualDirectory/AppCPS2018CMS/AppFiles/{fileName}";
    public static final String DOWN_TXT_URL = "http://cdn.adsalecdn.com/App/2019/files/{fileName}";

    /* MainIcon 下載地址： https://eform.adsale.com.hk/AppCPS2018Services/WebContent/20180323092519267.png  */

    /*  最新 2019 url 下載地址 */
    public static final String DOWNLOAD_BASE_URL = "http://cdn.adsalecdn.com/App/2019/";
    public static final String Download_MainIcon_Url = "http://cdn.adsalecdn.com/App/2019/MainIcons/";
    public static final String DOWNLOAD_WCONTENT_URL = "http://cdn.adsalecdn.com/App/2019/WebContent/";    // webContent url

    /* DownloadClient 下载路径 */
    public static final String DOWN_WEBCONTENT_URL = "WebContent/{fileName}";
    public static final String DOWN_MAP_URL = "FloorPlan/{fileName}";
    public static final String DOWN_EVENT_URL = "ConcurrentEvent/{fileName}";  // 同期活动详情页 url   http://cdn.adsalecdn.com/App/2019/ConcurrentEvent/


    /** txt 下载地址 */
    public static final String TXT_MAIN_URL = "http://cdn.adsalecdn.com/App/2019/files/MainMenuInfo.txt";
    public static final String TXT_AD_URL = "http://cdn.adsalecdn.com/App/2019/files/advertisement.txt";
    public static final String TXT_NOTIFICATION_URL = "http://cdn.adsalecdn.com/App/2019/files/notification.txt";
    public static final String APK_VERSION_TXT_URL = "http://cdn.adsalecdn.com/App/2019/files/apkVersion.txt";
    public static final String TXT_NEW_TEC = "http://cdn.adsalecdn.com/App/2019/files/NewTechInfo.txt";  // 新技术产品的广告

    public static final String MOBILE_EXHI_HIGHLIGHTS="https://www.chinaplasonline.com/CPS18/Mobile/Home/%s/NewTechnologyeBook.aspx?eh=1";   //lang-trad

    //POST  BASE_URL IS DOWNLOAD_PATH
    public static final String GetFolderScanFilesJson = "wsCLE15.asmx?op=getFolderScanFilesJson";
    public static final String GetMasterV2 = "wsCLE15.asmx?op=getMasterV2";
    public static final String GetNewsList = "wsCLE15.asmx??op=getNewsList";

    public static final String HEADER_SOAP_ACTION_MASTER = "SOAPAction:http://tempuri.org/getMasterV2";
    public static final String HEADER_SOAP_ACTION_SCANFILES = "SOAPAction:http://tempuri.org/getFolderScanFilesJson";

    public static final String BASE_URL_CPS = "https://www.chinaplasonline.com/CPS18/";
    public static final String BASE_URL_EFORM = "http://eform.adsale.com.hk/";
    public static final String BASE_URL_EFORM_API = "http://eform.adsale.com.hk/GeniusAnalyst/api/appapi/";

    public static final String LoginPreLink = "Membership/###/MyExhibitor.aspx?istest=321";
//    https://www.chinaplasonline.com/CPS18/Membership/lang-simp/MyExhibitor.aspx?istest=321
    public static final String syncMyExhibitor = "info/CartSync.aspx?vf=581";//prefix: BASE_URL_CPS

    public static final String GET_VISITOR_DATA_URL = "http://eform.adsale.com.hk/GeniusAnalyst/api/appapi/GetVisitorDataByEmail";

    /*预登记*/
//    public static final String Register_URL = "https://www.chinaplasonline.com/CPS18/Mobile/Home/%s/QuickPreReg.aspx?device=mobileapp";
//    public static final String Register_URL = "https://www.chinaplasonline.com/CPS19/preregistration/%s?device=mobileapp";
    public static final String Register_URL = "https://www.chinaplasonline.com/CPS19/preregistrationlanding/%s?device=APP";

//    public static final String MY_CHINAPLAS_URL = "https://www.chinaplasonline.com/CPS19/mychinaplas/%s?device=APP";  /*  网页登录链接  */
//    public static final String MY_CHINAPLAS_URL = "https://www.chinaplasonline.com/CPS19/mychinaplas/%s/APPLogin?device=APP";  /*  网页登录链接  */
    public static final String MY_CHINAPLAS_URL = "https://www.chinaplasonline.com/CPS19/mychinaplas/%s?device=APP";  /*  网页登录链接  */

    public static final String REGISTER_INVOICE_URL = "https://www.chinaplasonline.com/CPS19/PreRegInvoice/%s?device=APP&guid=%s";
    public static final String REGISTER_CHARGE="https://epayment.adsale-marketing.com.cn/vreg/PreregSubmitCloud/PayAPPjump";/* 获取charge数据 */
    public static final String REGISTER_BASE_URL="https://epayment.adsale-marketing.com.cn/vreg/PreregSubmitCloud/";
    public static final String REGISTER_CONFIRM_PAY="https://epayment.adsale-marketing.com.cn/vreg/PreregMobileSMS/APPSelectPay"; /* 向服务器确认是否支付成功 */
    public static final String CONFIRM_URL = "https://www.chinaplasonline.com/CPS19/PreregSuccess/%s/?device=mobileapp&guid=%s";


    public static final String API_TOKEN = "https://www.chinaplasonline.com/CPS19/CPSAPI/GetVistorInfoByMid?token=%s&langid=%s";


//    public static final String REGISTER_CONFIRM_URL="https://www.chinaplasonline.com/CPS18/Mobile/Home/%1s/QuickPreRegResult.aspx?image=%2s";  /*  确认信链接 */
//    public static final String REGISTER_CONFIRM_IMG_URL="https://eform.adsale.com.hk/vreg/Files/Mobile/PreReg/479/";  /*  确认信图片链接 */

//    public static final String SMS_GET_CODE_URL0 = "https://eform.adsale.com.hk/vreg/Content/handler/MobileCode.ashx?Handler=GetCode&CellNum=%s&RLang=%s&showid=505"; /*  获取短信验证码的接口  */
//    public static final String SMS_GET_CODE_URL = "https://eform.adsale.com.hk/vreg/Content/handler"; /*  获取短信验证码的接口  */
//    public static final String SMS_LOGIN_URL = "https://eform.adsale.com.hk/vreg/Content/handler/MobileCode.ashx?Handler=SelectCode&Guid=%s&Code=%s&Url=APP"; // 验证码登录，guid是发送验证码时返回的值
//    public static final String SMS_LOGIN_URL = "https://www.chinaplasonline.com/CPS19/CPSAPI/MembershipGenecral?Handler=GeValidateMembershipBySMS&userEmail=%s&userCell=%s&Code=%s&Guid=%s&clienurl=APP"; // 验证码登录，guid是发送验证码时返回的值

//    public static final String PWD_LOGIN_URL = "https://www.chinaplasonline.com/CPS19/CPSAPI/MembershipGenecral?Handler=GeValidateMembership&userEmail=%s&userCell=%s&password=%s&lang=%s";





    //   https://www.chinaplasonline.com/CPS19/PreregSuccess/simp/?device=mobileapp&guid=99DB247DA11F40C583D5A1CCFE81751F


//    public static final String REGISTER_INVOICE_URL="https://www.chinaplasonline.com/CPS18/Mobile/Home/%1s/PreRegInvoice.aspx?image=%2s";  /*  发票链接 */
    public static final String REGISTER_EMAIL_DATA="http://eform.adsale.com.hk/GeniusAnalyst/api/appapi/GetVisitorDataByEmail";  /*  登录后返回预登记图片信息 */

    /*订阅电子快讯*/
    public static final String Subscribe_BASE_URL = "https://eform.adsale.com.hk/FormR/ContactUs/";
    public static final String Subscribe_LAST_URL = "EnewsSub.aspx?showid=505&device=app&WAct=9105";//&lang={langType}

    /*  内容更新中心baseUrl */
    public static final String UC_BASE_URL="https://o97tbiy1f.qnssl.com/";

    public static final String LOGJSON="http://eform.adsale.com.hk/adsaleApi/CpsApp/CPSAddTrackingJson";
//    public static final String SHARE_EXHIBITOR_URL="http://www.chinaplasonline.com/CPS18/Mobile/Home/lang-%1$s/searchType-5/company-%2$s/SearchExhibitor.aspx";
    public static final String SHARE_EXHIBITOR_URL="https://www.chinaplasonline.com/ExhibitorList19/exhibitorinfo/%1$s/展商详情?compid=%2$s&src=61";
    public static final String SHARE_EXHIBITOR_EN_URL="https://www.chinaplasonline.com/ExhibitorList19/exhibitorinfo/eng/exhibitor_info?compid=%1$s&src=61";



    /** |||-------------------------------------------------------------------||| */

    public static RequestBody getMasterRequestBody() {
        return RequestBody.create(MEDIA_TYPE_XML, getMasterSoapBody());
    }

    public static RequestBody getScanRequestBody() {
        return RequestBody.create(MEDIA_TYPE_XML, getScanSoapBody());
    }

    private static String getMasterSoapBody() {
        SharedPreferences sp = App.mSP_UpdateTime;
        return new StringBuilder().append(getSoapHeader())
                .append("<soap:Body>\n")
                .append("    <getMasterV2 xmlns=\"http://tempuri.org/\">\n")
                .append("      <pExhibitorUpdateDateTime>").append("").append("</pExhibitorUpdateDateTime>\n")
                .append("      <pIndustryDtlUpdateDateTime>").append("").append("</pIndustryDtlUpdateDateTime>\n")
                .append("      <pNewsUpdateDateTime>").append(sp.getString("NEWS", "")).append("</pNewsUpdateDateTime>\n")
                .append("      <pNewsLinkUpdateDateTime>").append(sp.getString("NEWS_LINK", "")).append("</pNewsLinkUpdateDateTime>\n")
                .append("      <pWebContentDateTime>").append(sp.getString("WEB_CONTENT", "")).append("</pWebContentDateTime>\n")
                .append("      <pIndustryDateTime>").append("").append("</pIndustryDateTime>\n")
                .append("      <pMainIconDateTime>").append(sp.getString("MAIN_ICON", "")).append("</pMainIconDateTime>")
                .append("      <pFloorDateTime>").append("").append("</pFloorDateTime>\n")
                .append("      <pMapFloorDateTime>").append(sp.getString("MAP_FLOOR", "")).append("</pMapFloorDateTime>\n")
                .append("    </getMasterV2>\n")
                .append("  </soap:Body>\n")
                .append("</soap:Envelope>").toString();
    }

    private static String getScanSoapBody() {
        StringBuffer sbBody = new StringBuffer();
        sbBody.append(getSoapHeader())
                .append("  <soap:Body>\n")
                .append("    <getFolderScanFilesJson xmlns=\"http://tempuri.org/\">\n")
                .append("      <pProjectCode>").append(PROJECT_CODE).append("</pProjectCode>\n")
                .append("    </getFolderScanFilesJson>\n")
                .append("  </soap:Body>\n")
                .append("</soap:Envelope>");
        return sbBody.toString();
    }

    private static String getSoapHeader() {
        return new StringBuffer().append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n")
                .append("  <soap:Header>\n")
                .append("    <RoxvellWebserviceHeader xmlns=\"http://tempuri.org/\">\n")
                .append("      <UserName>CLE15</UserName>\n")
                .append("      <Password>pass</Password>\n")
                .append("    </RoxvellWebserviceHeader>\n")
                .append("  </soap:Header>\n").toString();
    }

    public static RequestBody getRegRequestBody() {
        return new FormBody.Builder().add("showid", Constant.SHOW_ID).add("email", AppUtil.getUserEmail()).build();
    }

    //----------CPS19 广告测试-----------
    public static final String BASE_QINIU =  "";

}
