package com.adsale.ChinaPlas.utils;


import android.content.SharedPreferences;

import com.adsale.ChinaPlas.App;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Carrie on 2017/8/9.
 */

public class NetWorkHelper {
    private static final String TAG = "NetWorkHelper";
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=UTF-8");
    private static final String PROJECT_CODE = "CPS17";//test: CPSTEST

    // WEBSERVICE 17年
    public static final String DOWNLOAD_PATH = "https://eform.adsale.com.hk/AppCPS17Services/";
    public static final String WEBSERVICEURL = "https://eform.adsale.com.hk/AppCPS17Services/wsCLE15.asmx";

    //POST  BASE_URL IS DOWNLOAD_PATH
    public static final String GetFolderScanFilesJson = "wsCLE15.asmx?op=getFolderScanFilesJson";
    public static final String GetMasterV2 = "wsCLE15.asmx?op=getMasterV2";
    public static final String GetNewsList = "wsCLE15.asmx??op=getNewsList";

    public static final String HEADER_SOAP_ACTION_MASTER = "SOAPAction:http://tempuri.org/getMasterV2";
    public static final String HEADER_SOAP_ACTION_SCANFILES = "SOAPAction:http://tempuri.org/getFolderScanFilesJson";


    public static final String BASE_URL_CPS = "https://www.chinaplasonline.com/CPS17/";
    public static final String BASE_URL_EFORM = "http://eform.adsale.com.hk/";
    public static final String BASE_URL_EFORM_API = "http://eform.adsale.com.hk/GeniusAnalyst/api/appapi/";

    public static final String LoginPreLink = "Membership/###/MyExhibitor.aspx?istest=321";
    public static final String syncMyExhibitor = "info/CartSync.aspx?vf=581";//prefix: BASE_URL_CPS

    public static final String GET_VISITOR_DATA_URL = "http://eform.adsale.com.hk/GeniusAnalyst/api/appapi/GetVisitorDataByEmail";

    /*预登记*/
    public static final String Register_EN_URL = "https://www.chinaplasonline.com/CPS17/Mobile/Home/lang-eng/QuickPreReg.aspx?device=mobileapp";
    public static final String Register_TW_URL = "https://www.chinaplasonline.com/CPS17/Mobile/Home/lang-trad/QuickPreReg.aspx?device=mobileapp";
    public static final String Register_CN_URL = "https://www.chinaplasonline.com/CPS17/Mobile/Home/lang-simp/QuickPreReg.aspx?device=mobileapp";

    //test
//    public static final String Register_TW_URL = "https://www.printingsouthchina.com/PRT18/Mobile/Home/lang-trad/QuickPreReg.aspx";

    /* loading页的下载地址 */
    public static final String DOWN_TXT_URL = "https://eform.adsale.com.hk/AppCPS17/AppFiles/{fileName}";
    public static final String DOWN_WEBCONTENT_URL = "WebContent/{fileName}";


    // |||-------------------------------------------------------------------|||


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
                .append("      <pNewsUpdateDateTime>").append(sp.getString("NewsUT", "")).append("</pNewsUpdateDateTime>\n")
                .append("      <pNewsLinkUpdateDateTime>").append(sp.getString("NewsLinkLUT", "")).append("</pNewsLinkUpdateDateTime>\n")
                .append("      <pWebContentDateTime>").append(sp.getString("WebContentLUT", "")).append("</pWebContentDateTime>\n")
                .append("      <pIndustryDateTime>").append("").append("</pIndustryDateTime>\n")
                .append("      <pMainIconDateTime>").append(sp.getString("MainIconLUT", "")).append("</pMainIconDateTime>")
                .append("      <pFloorDateTime>").append("").append("</pFloorDateTime>\n")
                .append("      <pMapFloorDateTime>").append(sp.getString("MapFloorLUT", "")).append("</pMapFloorDateTime>\n")
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


}
