package com.adsale.ChinaPlas.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.App;

import static com.adsale.ChinaPlas.App.mLanguage;

/**
 * 酒店信息
 *
 * @author Carrie
 */
public class AgentInfo implements Parcelable {
    // TitleENG|TitleTC|TitleSC|AgentNameENG|AgentNameTC|AgentNameSC|ContactNameENG|ContactNameTC|ContactNameSC|HotelID
    // |addressENG|addressTC|addressSC|tel|fax|email|website|logo|logoMobile|withrecommend
    // |HotelCartgoryID|remarksENG|remarksTC|remarksSC|remarksMoreENG|remarksMoreTC|remarksMoreSC|orders
    // 28个
    // telENG|telTC|telSC|faxENG|faxTC|faxSC|emailENG|emailTC|emailSC|websiteENG|websiteTC|websiteSC
    // +6
    public String agentName;
    public String contactName;
    public String address;
    public String remarks;
    public String remarksMore;
    public String title;


    public String hotelName;
    public String titleENG;
    public String titleTC;
    public String titleSC;
    public String agentNameENG;
    public String agentNameTC;
    public String agentNameSC;
    public String contactNameENG;
    public String contactNameTC;
    public String contactNameSC;
    public String hotelID;
    public String addressENG;
    public String addressTC;
    public String addressSC;
    public String telENG;
    public String telTC;
    public String telSC;
    public String faxENG;
    public String faxTC;
    public String faxSC;
    public String emailENG;
    public String emailTC;
    public String emailSC;
    public String websiteENG;
    public String websiteTC;
    public String websiteSC;
    public String logo;
    public String logoMobile;
    public String withrecommend;
    public String HotelCartgoryID;
    public String remarksENG;
    public String remarksTC;
    public String remarksSC;
    public String remarksMoreENG;
    public String remarksMoreTC;
    public String remarksMoreSC;

    public AgentInfo() {

    }

    public AgentInfo(String titleENG, String titleTC, String titleSC, String agentNameENG, String agentNameTC, String agentNameSC,
                     String contactNameENG, String contactNameTC, String contactNameSC, String hotelID, String addressENG, String addressTC, String addressSC,
                     String telENG, String telTC, String telSC, String faxENG, String faxTC, String faxSC, String emailENG, String emailTC, String emailSC,
                     String websiteENG, String websiteTC, String websiteSC, String logo, String logoMobile, String withrecommend, String hotelCartgoryID,
                     String remarksENG, String remarksTC, String remarksSC, String remarksMoreENG, String remarksMoreTC, String remarksMoreSC, String orders) {
        super();
        this.titleENG = titleENG;
        this.titleTC = titleTC;
        this.titleSC = titleSC;
        this.agentNameENG = agentNameENG;
        this.agentNameTC = agentNameTC;
        this.agentNameSC = agentNameSC;
        this.contactNameENG = contactNameENG;
        this.contactNameTC = contactNameTC;
        this.contactNameSC = contactNameSC;
        this.hotelID = hotelID;
        this.addressENG = addressENG;
        this.addressTC = addressTC;
        this.addressSC = addressSC;
        this.telENG = telENG;
        this.telTC = telTC;
        this.telSC = telSC;
        this.faxENG = faxENG;
        this.faxTC = faxTC;
        this.faxSC = faxSC;
        this.emailENG = emailENG;
        this.emailTC = emailTC;
        this.emailSC = emailSC;
        this.websiteENG = websiteENG;
        this.websiteTC = websiteTC;
        this.websiteSC = websiteSC;
        this.logo = logo;
        this.logoMobile = logoMobile;
        this.withrecommend = withrecommend;
        HotelCartgoryID = hotelCartgoryID;
        this.remarksENG = remarksENG;
        this.remarksTC = remarksTC;
        this.remarksSC = remarksSC;
        this.remarksMoreENG = remarksMoreENG;
        this.remarksMoreTC = remarksMoreTC;
        this.remarksMoreSC = remarksMoreSC;
        this.orders = orders;
    }


    public String orders;


    public String getTelSC() {
        return telSC;
    }

    public String getTel() {
        if (App.mLanguage.get() == 1) {
            return telENG;
        } else if (App.mLanguage.get() == 2) {
            return telSC;
        } else {
            return telTC;
        }
    }

    public String getFax(int lang) {
        String fax = "";
        if (lang == 1) {
            fax = faxENG;
        } else if (lang == 2) {
            fax = faxSC;
        } else if (lang == 0) {
            fax = faxTC;
        }
        return fax;
    }

    public String getEmail() {
        if (App.mLanguage.get() == 1) {
            return emailENG;
        } else if (App.mLanguage.get() == 2) {
            return emailSC;
        } else {
            return emailTC;
        }
    }


    public String getWebsiteENG() {
        return websiteENG;
    }

    public String getWebsite() {
        if (App.mLanguage.get() == 1) {
            return websiteENG;
        } else if (App.mLanguage.get() == 2) {
            return websiteSC;
        } else {
            return websiteTC;
        }
    }


    public String getAgentName() {
        if (mLanguage.get() == 0) {
            agentName = agentNameTC;
        } else if (mLanguage.get() == 1) {
            agentName = agentNameENG;
        } else {
            agentName = agentNameSC;
        }
        return agentName;
    }

    public String getContactName() {
        if (mLanguage.get() == 0) {
            contactName = contactNameTC;
        } else if (mLanguage.get() == 1) {
            contactName = contactNameENG;
        } else {
            contactName = contactNameSC;
        }
        return contactName;
    }

    public String getRemarksMore() {
        if (mLanguage.get() == 1) {
            remarksMore = remarksMoreENG;
        } else if (mLanguage.get() == 2) {
            remarksMore = remarksMoreSC;
        } else {
            remarksMore = remarksMoreTC;
        }
        return remarksMore;
    }

    public String getRemarks() {
        if (mLanguage.get() == 1) {
            remarks = remarksENG;
        } else if (mLanguage.get() == 2) {
            remarks = remarksSC;
        } else {
            remarks = remarksTC;
        }
        return remarks;
    }

    public String getAddress() {
        if (mLanguage.get() == 0) {
            address = addressTC;
        } else if (mLanguage.get() == 1) {
            address = addressENG;
        } else {
            address = addressSC;
        }
        return address;
    }

    public String getTitle() {
        if (mLanguage.get() == 0) {
            title = titleTC;
        } else if (mLanguage.get() == 1) {
            title = titleENG;
        } else {
            title = titleSC;
        }
        return title;
    }

    @Override
    public String toString() {
        return "AgentInfo [titleENG=" + titleENG + ", titleTC=" + titleTC + ", titleSC=" + titleSC + ", agentNameENG=" + agentNameENG
                + ", agentNameTC=" + agentNameTC + ", agentNameSC=" + agentNameSC + ", contactNameENG=" + contactNameENG + ", contactNameTC="
                + contactNameTC + ", contactNameSC=" + contactNameSC + ", hotelID=" + hotelID + ", addressENG=" + addressENG + ", addressTC="
                + addressTC + ", addressSC=" + addressSC + ", telENG=" + telENG + ", telTC=" + telTC + ", telSC=" + telSC + ", faxENG=" + faxENG
                + ", faxTC=" + faxTC + ", faxSC=" + faxSC + ", emailENG=" + emailENG + ", emailTC=" + emailTC + ", emailSC=" + emailSC
                + ", websiteENG=" + websiteENG + ", websiteTC=" + websiteTC + ", websiteSC=" + websiteSC + ", logo=" + logo + ", logoMobile="
                + logoMobile + ", withrecommend=" + withrecommend + ", HotelCartgoryID=" + HotelCartgoryID + ", remarksENG=" + remarksENG
                + ", remarksTC=" + remarksTC + ", remarksSC=" + remarksSC + ", remarksMoreENG=" + remarksMoreENG + ", remarksMoreTC=" + remarksMoreTC
                + ", remarksMoreSC=" + remarksMoreSC + ", orders=" + orders + ", HotelAgentID=]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.agentName);
        dest.writeString(this.contactName);
        dest.writeString(this.address);
        dest.writeString(this.remarks);
        dest.writeString(this.remarksMore);
        dest.writeString(this.title);
        dest.writeString(this.hotelName);
        dest.writeString(this.titleENG);
        dest.writeString(this.titleTC);
        dest.writeString(this.titleSC);
        dest.writeString(this.agentNameENG);
        dest.writeString(this.agentNameTC);
        dest.writeString(this.agentNameSC);
        dest.writeString(this.contactNameENG);
        dest.writeString(this.contactNameTC);
        dest.writeString(this.contactNameSC);
        dest.writeString(this.hotelID);
        dest.writeString(this.addressENG);
        dest.writeString(this.addressTC);
        dest.writeString(this.addressSC);
        dest.writeString(this.telENG);
        dest.writeString(this.telTC);
        dest.writeString(this.telSC);
        dest.writeString(this.faxENG);
        dest.writeString(this.faxTC);
        dest.writeString(this.faxSC);
        dest.writeString(this.emailENG);
        dest.writeString(this.emailTC);
        dest.writeString(this.emailSC);
        dest.writeString(this.websiteENG);
        dest.writeString(this.websiteTC);
        dest.writeString(this.websiteSC);
        dest.writeString(this.logo);
        dest.writeString(this.logoMobile);
        dest.writeString(this.withrecommend);
        dest.writeString(this.HotelCartgoryID);
        dest.writeString(this.remarksENG);
        dest.writeString(this.remarksTC);
        dest.writeString(this.remarksSC);
        dest.writeString(this.remarksMoreENG);
        dest.writeString(this.remarksMoreTC);
        dest.writeString(this.remarksMoreSC);
        dest.writeString(this.orders);
    }

    protected AgentInfo(Parcel in) {
        this.agentName = in.readString();
        this.contactName = in.readString();
        this.address = in.readString();
        this.remarks = in.readString();
        this.remarksMore = in.readString();
        this.title = in.readString();
        this.hotelName = in.readString();
        this.titleENG = in.readString();
        this.titleTC = in.readString();
        this.titleSC = in.readString();
        this.agentNameENG = in.readString();
        this.agentNameTC = in.readString();
        this.agentNameSC = in.readString();
        this.contactNameENG = in.readString();
        this.contactNameTC = in.readString();
        this.contactNameSC = in.readString();
        this.hotelID = in.readString();
        this.addressENG = in.readString();
        this.addressTC = in.readString();
        this.addressSC = in.readString();
        this.telENG = in.readString();
        this.telTC = in.readString();
        this.telSC = in.readString();
        this.faxENG = in.readString();
        this.faxTC = in.readString();
        this.faxSC = in.readString();
        this.emailENG = in.readString();
        this.emailTC = in.readString();
        this.emailSC = in.readString();
        this.websiteENG = in.readString();
        this.websiteTC = in.readString();
        this.websiteSC = in.readString();
        this.logo = in.readString();
        this.logoMobile = in.readString();
        this.withrecommend = in.readString();
        this.HotelCartgoryID = in.readString();
        this.remarksENG = in.readString();
        this.remarksTC = in.readString();
        this.remarksSC = in.readString();
        this.remarksMoreENG = in.readString();
        this.remarksMoreTC = in.readString();
        this.remarksMoreSC = in.readString();
        this.orders = in.readString();
    }

    public static final Parcelable.Creator<AgentInfo> CREATOR = new Parcelable.Creator<AgentInfo>() {
        public AgentInfo createFromParcel(Parcel source) {
            return new AgentInfo(source);
        }

        public AgentInfo[] newArray(int size) {
            return new AgentInfo[size];
        }
    };
}
