package com.adsale.ChinaPlas.data.model;

/**
 * Created by new on 2016/11/4.
 * <p>通过用户登录的email获取的api返回数据</p>
 */
public class EmailVisitorData{
    public VisitorData VisitorData;

    public class VisitorData {
        public String ShowId;
        public String WechatOpenId;
        public String RegImageName;
        public String ukey;
        public String CompName;
        public String ContactName;
        public String CSPVisitorId;
        public String ShowVisitorId;
        public String memberId;
        public String BarCode;

        @Override
        public String toString() {
            return "VisitorData{" +
                    "BarCode='" + BarCode + '\'' +
                    ", ShowId='" + ShowId + '\'' +
                    ", WechatOpenId='" + WechatOpenId + '\'' +
                    ", RegImageName='" + RegImageName + '\'' +
                    ", ukey='" + ukey + '\'' +
                    ", CompName='" + CompName + '\'' +
                    ", ContactName='" + ContactName + '\'' +
                    ", CSPVisitorId='" + CSPVisitorId + '\'' +
                    ", ShowVisitorId='" + ShowVisitorId + '\'' +
                    ", memberId='" + memberId + '\'' +
                    '}';
        }
    }
}

