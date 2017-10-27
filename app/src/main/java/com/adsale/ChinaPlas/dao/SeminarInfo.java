package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Entity mapped to table "SEMINAR_INFO".
 */
public class SeminarInfo implements Parcelable {
    private Integer ID;
    private Integer EventID;
    private String CompanyID;
    private String Booth;
    private String Date;
    private String Time;
    private String Hall;
    private String RoomNo;
    private String PresentCompany;
    private String Topic;
    private String Speaker;
    private Integer langID;
    private Integer OrderFull;
    private Integer OrderMob;

    // KEEP FIELDS - put your custom fields here
    public String startTime;
    public String endTime;
    public String timeStartStatus;
    public String timeEndStatus;
    public int colorCode;
    public String zone;

    public String timeStatus;

    public boolean isADer = false;//广告商
    public String adLogoUrl;
    public String adBannerUrl;

    public final ObservableBoolean isTypeLabel = new ObservableBoolean();

    // KEEP FIELDS END

    public SeminarInfo() {
    }

    public SeminarInfo(Integer ID, Integer EventID, String CompanyID, String Booth, String Date, String Time, String Hall, String RoomNo, String PresentCompany, String Topic, String Speaker, Integer langID, Integer OrderFull, Integer OrderMob) {
        this.ID = ID;
        this.EventID = EventID;
        this.CompanyID = CompanyID;
        this.Booth = Booth;
        this.Date = Date;
        this.Time = Time;
        this.Hall = Hall;
        this.RoomNo = RoomNo;
        this.PresentCompany = PresentCompany;
        this.Topic = Topic;
        this.Speaker = Speaker;
        this.langID = langID;
        this.OrderFull = OrderFull;
        this.OrderMob = OrderMob;

        setTime();
        setTimeStatus();
    }


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getEventID() {
        return EventID;
    }

    public void setEventID(Integer EventID) {
        this.EventID = EventID;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String CompanyID) {
        this.CompanyID = CompanyID;
    }

    public String getBooth() {
        return Booth;
    }

    public void setBooth(String Booth) {
        this.Booth = Booth;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public String getHall() {
        return Hall;
    }

    public void setHall(String Hall) {
        this.Hall = Hall;
    }

    public String getRoomNo() {
        return RoomNo;
    }

    public void setRoomNo(String RoomNo) {
        this.RoomNo = RoomNo;
    }

    public String getPresentCompany() {
        return PresentCompany;
    }

    public void setPresentCompany(String PresentCompany) {
        this.PresentCompany = PresentCompany;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String Topic) {
        this.Topic = Topic;
    }

    public String getSpeaker() {
        return Speaker;
    }

    public void setSpeaker(String Speaker) {
        this.Speaker = Speaker;
    }

    public Integer getLangID() {
        return langID;
    }

    public void setLangID(Integer langID) {
        this.langID = langID;
    }

    public Integer getOrderFull() {
        return OrderFull;
    }

    public void setOrderFull(Integer OrderFull) {
        this.OrderFull = OrderFull;
    }

    public Integer getOrderMob() {
        return OrderMob;
    }

    public void setOrderMob(Integer OrderMob) {
        this.OrderMob = OrderMob;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        return "SeminarInfo{" +
                "ID=" + ID +
                ", EventID=" + EventID +
                ", CompanyID='" + CompanyID + '\'' +
                ", Booth='" + Booth + '\'' +
                ", Date='" + Date + '\'' +
                ", Time='" + Time + '\'' +
                ", Hall='" + Hall + '\'' +
                ", RoomNo='" + RoomNo + '\'' +
                ", PresentCompany='" + PresentCompany + '\'' +
                ", Topic='" + Topic + '\'' +
                ", Speaker='" + Speaker + '\'' +
                ", langID=" + langID +
                ", OrderFull=" + OrderFull +
                ", OrderMob=" + OrderMob +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", timeStartStatus='" + timeStartStatus + '\'' +
                ", timeEndStatus='" + timeEndStatus + '\'' +
                ", colorCode=" + colorCode +
                ", zone='" + zone + '\'' +
                ", timeStatus='" + timeStatus + '\'' +
                ", isADer=" + isADer +
                '}';
    }

    public void setTime() {
        String timeToken[] = Time.split("-");
        if (timeToken.length > 1) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                java.util.Date startDate = sdf.parse(timeToken[0]);
                Date endDate = sdf.parse(timeToken[1]);

                SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");
                startTime = newFormat.format(startDate);
                endTime = newFormat.format(endDate);

                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);

                if (startCal.get(Calendar.HOUR_OF_DAY) > 12) {
                    timeStartStatus = "PM";
                    timeStatus = "下午时段";
                } else if (endCal.get(Calendar.HOUR_OF_DAY) < 12) {
                    timeStartStatus = "AM";
                    timeStatus = "上午时段";
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    public void setTimeStatus() {

    }

    public void parser(String[] strings) {
        this.ID = Integer.valueOf(strings[0]);
        this.EventID = Integer.valueOf(strings[1]);
        this.CompanyID = strings[2];
        this.Booth = strings[3];
        this.Date = strings[4];
        this.Time = strings[5];
        this.Hall = strings[6];
        this.RoomNo = strings[7];
        this.PresentCompany = strings[8];
        this.Topic = strings[9];
        this.Speaker = strings[10];
        this.langID = Integer.valueOf(strings[11]);
        this.OrderFull = Integer.valueOf(strings[12]);
        this.OrderMob = Integer.valueOf(strings[13]);
    }
    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.ID);
        dest.writeValue(this.EventID);
        dest.writeString(this.CompanyID);
        dest.writeString(this.Booth);
        dest.writeString(this.Date);
        dest.writeString(this.Time);
        dest.writeString(this.Hall);
        dest.writeString(this.RoomNo);
        dest.writeString(this.PresentCompany);
        dest.writeString(this.Topic);
        dest.writeString(this.Speaker);
        dest.writeValue(this.langID);
        dest.writeValue(this.OrderFull);
        dest.writeValue(this.OrderMob);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.timeStartStatus);
        dest.writeString(this.timeEndStatus);
        dest.writeInt(this.colorCode);
        dest.writeString(this.zone);
        dest.writeString(this.timeStatus);
        dest.writeByte(isADer ? (byte) 1 : (byte) 0);
    }

    protected SeminarInfo(Parcel in) {
        this.ID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.EventID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.CompanyID = in.readString();
        this.Booth = in.readString();
        this.Date = in.readString();
        this.Time = in.readString();
        this.Hall = in.readString();
        this.RoomNo = in.readString();
        this.PresentCompany = in.readString();
        this.Topic = in.readString();
        this.Speaker = in.readString();
        this.langID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.OrderFull = (Integer) in.readValue(Integer.class.getClassLoader());
        this.OrderMob = (Integer) in.readValue(Integer.class.getClassLoader());
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.timeStartStatus = in.readString();
        this.timeEndStatus = in.readString();
        this.colorCode = in.readInt();
        this.zone = in.readString();
        this.timeStatus = in.readString();
        this.isADer = in.readByte() != 0;
    }

    public static final Creator<SeminarInfo> CREATOR = new Creator<SeminarInfo>() {
        public SeminarInfo createFromParcel(Parcel source) {
            return new SeminarInfo(source);
        }

        public SeminarInfo[] newArray(int size) {
            return new SeminarInfo[size];
        }
    };
}
