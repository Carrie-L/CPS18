package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import static com.adsale.ChinaPlas.R.id.language;
// KEEP INCLUDES END

/**
 * Entity mapped to table "EXHIBITOR".
 */
public class Exhibitor implements Parcelable {

    private String CompanyID;
    private String CompanyNameEN;
    private String CompanyNameTW;
    private String CompanyNameCN;
    private String AddressE;
    private String AddressT;
    private String AddressS;
    private String Postal;
    private String Tel;
    private String Fax;
    private String Email;
    private String Website;
    private String CountryID;
    private String AddressE1;
    private String AddressT1;
    private String AddressS1;
    private String AddressE2;
    private String AddressT2;
    private String AddressS2;
    private String BoothNo;
    private String StrokeEng;
    private String StrokeTrad;
    private String StrokeSimp;
    private String PYSimp;
    private String ImgFolder;
    private String ExhibitNameE;
    private String ExhibitNameS;
    private String ExhibitNameT;
    private String DescE;
    private String DescS;
    private String DescT;
    private String PhotoFileName;
    private Integer SeqEN;
    private Integer SeqTC;
    private Integer SeqSC;
    private String HallNo;
    private Integer IsFavourite;
    private String Note;

    // KEEP FIELDS - put your custom fields here
    public String CompanyName;
    public String Sort;

    public ObservableBoolean isPhotoEmpty = new ObservableBoolean(true);

    // KEEP FIELDS END

    public Exhibitor() {
    }

    public Exhibitor(String CompanyID) {
        this.CompanyID = CompanyID;
    }

    public Exhibitor(String CompanyID, String CompanyNameEN, String CompanyNameTW, String CompanyNameCN, String AddressE, String AddressT, String AddressS, String Postal, String Tel, String Fax, String Email, String Website, String CountryID, String AddressE1, String AddressT1, String AddressS1, String AddressE2, String AddressT2, String AddressS2, String BoothNo, String StrokeEng, String StrokeTrad, String StrokeSimp, String PYSimp, String ImgFolder, String ExhibitNameE, String ExhibitNameS, String ExhibitNameT, String DescE, String DescS, String DescT, String PhotoFileName, Integer SeqEN, Integer SeqTC, Integer SeqSC, String HallNo, Integer IsFavourite, String Note) {
        this.CompanyID = CompanyID;
        this.CompanyNameEN = CompanyNameEN;
        this.CompanyNameTW = CompanyNameTW;
        this.CompanyNameCN = CompanyNameCN;
        this.AddressE = AddressE;
        this.AddressT = AddressT;
        this.AddressS = AddressS;
        this.Postal = Postal;
        this.Tel = Tel;
        this.Fax = Fax;
        this.Email = Email;
        this.Website = Website;
        this.CountryID = CountryID;
        this.AddressE1 = AddressE1;
        this.AddressT1 = AddressT1;
        this.AddressS1 = AddressS1;
        this.AddressE2 = AddressE2;
        this.AddressT2 = AddressT2;
        this.AddressS2 = AddressS2;
        this.BoothNo = BoothNo;
        this.StrokeEng = StrokeEng;
        this.StrokeTrad = StrokeTrad;
        this.StrokeSimp = StrokeSimp;
        this.PYSimp = PYSimp;
        this.ImgFolder = ImgFolder;
        this.ExhibitNameE = ExhibitNameE;
        this.ExhibitNameS = ExhibitNameS;
        this.ExhibitNameT = ExhibitNameT;
        this.DescE = DescE;
        this.DescS = DescS;
        this.DescT = DescT;
        this.PhotoFileName = PhotoFileName;
        this.SeqEN = SeqEN;
        this.SeqTC = SeqTC;
        this.SeqSC = SeqSC;
        this.HallNo = HallNo;
        this.IsFavourite = IsFavourite;
        this.Note = Note;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String CompanyID) {
        this.CompanyID = CompanyID;
    }

    public String getCompanyNameEN() {
        return CompanyNameEN;
    }

    public void setCompanyNameEN(String CompanyNameEN) {
        this.CompanyNameEN = CompanyNameEN;
    }

    public String getCompanyNameTW() {
        return CompanyNameTW;
    }

    public void setCompanyNameTW(String CompanyNameTW) {
        this.CompanyNameTW = CompanyNameTW;
    }

    public String getCompanyNameCN() {
        return CompanyNameCN;
    }

    public void setCompanyNameCN(String CompanyNameCN) {
        this.CompanyNameCN = CompanyNameCN;
    }

    public String getAddressE() {
        return AddressE;
    }

    public void setAddressE(String AddressE) {
        this.AddressE = AddressE;
    }

    public String getAddressT() {
        return AddressT;
    }

    public void setAddressT(String AddressT) {
        this.AddressT = AddressT;
    }

    public String getAddressS() {
        return AddressS;
    }

    public void setAddressS(String AddressS) {
        this.AddressS = AddressS;
    }

    public String getPostal() {
        return Postal;
    }

    public void setPostal(String Postal) {
        this.Postal = Postal;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String Tel) {
        this.Tel = Tel;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String Fax) {
        this.Fax = Fax;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String Website) {
        this.Website = Website;
    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String CountryID) {
        this.CountryID = CountryID;
    }

    public String getAddressE1() {
        return AddressE1;
    }

    public void setAddressE1(String AddressE1) {
        this.AddressE1 = AddressE1;
    }

    public String getAddressT1() {
        return AddressT1;
    }

    public void setAddressT1(String AddressT1) {
        this.AddressT1 = AddressT1;
    }

    public String getAddressS1() {
        return AddressS1;
    }

    public void setAddressS1(String AddressS1) {
        this.AddressS1 = AddressS1;
    }

    public String getAddressE2() {
        return AddressE2;
    }

    public void setAddressE2(String AddressE2) {
        this.AddressE2 = AddressE2;
    }

    public String getAddressT2() {
        return AddressT2;
    }

    public void setAddressT2(String AddressT2) {
        this.AddressT2 = AddressT2;
    }

    public String getAddressS2() {
        return AddressS2;
    }

    public void setAddressS2(String AddressS2) {
        this.AddressS2 = AddressS2;
    }

    public String getBoothNo() {
        return BoothNo;
    }

    public void setBoothNo(String BoothNo) {
        this.BoothNo = BoothNo;
    }

    public String getStrokeEng() {
        return StrokeEng;
    }

    public void setStrokeEng(String StrokeEng) {
        this.StrokeEng = StrokeEng;
    }

    public String getStrokeTrad() {
        return StrokeTrad;
    }

    public void setStrokeTrad(String StrokeTrad) {
        this.StrokeTrad = StrokeTrad;
    }

    public String getStrokeSimp() {
        return StrokeSimp;
    }

    public void setStrokeSimp(String StrokeSimp) {
        this.StrokeSimp = StrokeSimp;
    }

    public String getPYSimp() {
        return PYSimp;
    }

    public void setPYSimp(String PYSimp) {
        this.PYSimp = PYSimp;
    }

    public String getImgFolder() {
        return ImgFolder;
    }

    public void setImgFolder(String ImgFolder) {
        this.ImgFolder = ImgFolder;
    }

    public String getExhibitNameE() {
        return ExhibitNameE;
    }

    public void setExhibitNameE(String ExhibitNameE) {
        this.ExhibitNameE = ExhibitNameE;
    }

    public String getExhibitNameS() {
        return ExhibitNameS;
    }

    public void setExhibitNameS(String ExhibitNameS) {
        this.ExhibitNameS = ExhibitNameS;
    }

    public String getExhibitNameT() {
        return ExhibitNameT;
    }

    public void setExhibitNameT(String ExhibitNameT) {
        this.ExhibitNameT = ExhibitNameT;
    }

    public String getDescE() {
        return DescE;
    }

    public void setDescE(String DescE) {
        this.DescE = DescE;
    }

    public String getDescS() {
        return DescS;
    }

    public void setDescS(String DescS) {
        this.DescS = DescS;
    }

    public String getDescT() {
        return DescT;
    }

    public void setDescT(String DescT) {
        this.DescT = DescT;
    }

    public String getPhotoFileName() {
        return PhotoFileName;
    }

    public void setPhotoFileName(String PhotoFileName) {
        this.PhotoFileName = PhotoFileName;
        if(!TextUtils.isEmpty(PhotoFileName)){
            isPhotoEmpty.set(false);
        }
    }

    public Integer getSeqEN() {
        return SeqEN;
    }

    public void setSeqEN(Integer SeqEN) {
        this.SeqEN = SeqEN;
    }

    public Integer getSeqTC() {
        return SeqTC;
    }

    public void setSeqTC(Integer SeqTC) {
        this.SeqTC = SeqTC;
    }

    public Integer getSeqSC() {
        return SeqSC;
    }

    public void setSeqSC(Integer SeqSC) {
        this.SeqSC = SeqSC;
    }

    public String getHallNo() {
        return HallNo;
    }

    public void setHallNo(String HallNo) {
        this.HallNo = HallNo;
    }

    public Integer getIsFavourite() {
        return IsFavourite;
    }

    public void setIsFavourite(Integer IsFavourite) {
        this.IsFavourite = IsFavourite;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }

    // KEEP METHODS - put your custom methods here
    public String getCompanyName(int language) {
        if (language == 0) {
            return CompanyNameTW;
        } else if (language == 1) {
            return CompanyNameEN;
        } else {
            return CompanyNameCN;
        }
    }

    public String getCompanyName() {
        CompanyName = AppUtil.getName(CompanyNameTW, CompanyNameEN, CompanyNameCN);
        return CompanyName;
    }

    public void setCompanyName(int language, String companyName) {
        if (language == 0) {
            this.CompanyNameTW = companyName;
        } else if (language == 1) {
            this.CompanyNameEN = companyName;
        } else {
            this.CompanyNameCN = companyName;
        }
    }

    public void setSort(int language, int sort) {
        if (language == 0) {
            this.SeqTC = sort;
        } else if (language == 1) {
            this.SeqEN = sort;
        } else {
            this.SeqSC = sort;
        }
    }

    public static String columnName_Company(int language) {
        String columnName = "";
        if (language == 0) {
            columnName = "COMPANY_NAME_TW";
        } else if (language == 1) {
            columnName = "COMPANY_NAME_EN";
        } else if (language == 2) {
            columnName = "COMPANY_NAME_CN";
        } else {
            columnName = "COMPANY_NAME_CN,COMPANY_NAME_EN,COMPANY_NAME_TW";
        }
        return columnName;
    }

    public String getAddress(int language) {


//    	if(language==0){
//    		address=AddressT;
//    		if(TextUtils.isEmpty(address)){
//    			if(!TextUtils.isEmpty(AddressE)){
//    				address=AddressE;
//        		}else if(!TextUtils.isEmpty(AddressS)){
//    				address=AddressS;
//        		}
//    		}
//    	}else if(language==0){
//    		address=AddressE;
//    		if(TextUtils.isEmpty(address)){
//    			if(!TextUtils.isEmpty(AddressE)){
//    				address=AddressE;
//        		}else if(!TextUtils.isEmpty(AddressS)){
//    				address=AddressS;
//        		}
//    		}
//    	}else{
//    		address=AddressS;
//    		if(TextUtils.isEmpty(address)){
//    			if(!TextUtils.isEmpty(AddressE)){
//    				address=AddressE;
//        		}else if(!TextUtils.isEmpty(AddressS)){
//    				address=AddressS;
//        		}
//    		}
//    	}
//    	
//    	String address=SystemMethod.getName(language, AddressT, AddressE, AddressS);
//    	if(TextUtils.isEmpty(address)){
//    		if(TextUtils.isEmpty(add)){
//    			
//    		}
//    	}


        return "Address_TODO";
    }

    public String getSort() {
        Sort = AppUtil.getName(StrokeTrad + Constant.TRAD_STROKE, StrokeEng, PYSimp);
        return Sort;
    }

    private String getDesc(int language) {
        if (language == 0) {
            return DescT;
        } else if (language == 1) {
            return DescE;
        } else {
            return DescS;
        }
    }

    /**
     * 获取描述，如果当前语言的描述为空，则显示其它语言的，其它语言也没有，则返回空
     *
     * @param language
     * @return
     */
    public String getDescription(int language) {
        if (TextUtils.isEmpty(getDesc(language).trim())) {
            if (!TextUtils.isEmpty(DescE)) {
                return DescE;
            } else if (!TextUtils.isEmpty(DescS)) {
                return DescS;
            } else if (!TextUtils.isEmpty(DescT)) {
                return DescT;
            } else {
                return "";
            }
        } else {
            return getDesc(language);
        }
    }

    public int percent;


    //CompanyID|DescE|DescS|DescT
    public void parseDescription(String[] csv) {
        this.CompanyID = csv[0];
        this.DescE = csv[1];
        this.DescS = csv[2];
        this.DescT = csv[3];
    }

    public void parseExhibitor(String[] csv) {
        //CompanyID|NameInEngDisp|NameInTradDisp|NameInSimpDisp|AddressE|AddressT|AddressS|Postal|Tel|Fax|Email|   —�??0-10
        //Website|CountryID|AddressE1|AddressT1|AddressS1|AddressE2|AddressT2|AddressS2|BoothNo|StrokeEng|StrokeTrad  —�?? 11-21
        //|StrokeSimp|PYSimp|ImgFolder|ExhibitNameE|ExhibitNameS|ExhibitNameT|DescE|DescS|DescT|PhotoFileName  —�?? 22-31
        //|SeqEN|SeqTC|SeqSC|HallNo
        this.CompanyID = csv[0];
        this.CompanyNameEN = csv[1];
        this.CompanyNameTW = csv[2];
        this.CompanyNameCN = csv[3];
        this.AddressE = csv[4];
        this.AddressT = csv[5];
        this.AddressS = csv[6];
        this.Postal = csv[7];
        this.Tel = csv[8];
        this.Fax = csv[9];
        this.Email = csv[10];
        this.Website = csv[11];
        this.CountryID = csv[12];
        this.AddressE1 = csv[13];
        this.AddressT1 = csv[14];
        this.AddressS1 = csv[15];
        this.AddressE2 = csv[16];
        this.AddressT2 = csv[17];
        this.AddressS2 = csv[18];
        this.BoothNo = csv[19];
        this.StrokeEng = csv[20];
        this.StrokeTrad = csv[21];
        this.StrokeSimp = csv[22];
        this.PYSimp = csv[23];
        this.ImgFolder = csv[24];
        this.ExhibitNameE = csv[25];
        this.ExhibitNameS = csv[26];
        this.ExhibitNameT = csv[27];
        this.DescE = csv[28];
        this.DescS = csv[29];
        this.DescT = csv[30];
        this.PhotoFileName = csv[31];
        this.SeqEN = Integer.valueOf(csv[32]);
        this.SeqTC = Integer.valueOf(csv[33]);
        this.SeqSC = Integer.valueOf(csv[34]);
        this.HallNo = csv[35];
    }

    @Override
    public String toString() {
        return "Exhibitor [CompanyID=" + CompanyID + ", CompanyNameEN=" + CompanyNameEN + ", CompanyNameTW="
                + CompanyNameTW + ", CompanyNameCN=" + CompanyNameCN + ", BoothNo=" + BoothNo + ", StrokeEng=" + StrokeEng
                + ", StrokeTrad=" + StrokeTrad + ", StrokeSimp=" + StrokeSimp + ", PYSimp=" + PYSimp + ", ImgFolder="
                + ImgFolder + ", ExhibitNameE=" + ExhibitNameE + ", ExhibitNameS=" + ExhibitNameS + ", ExhibitNameT="
                + ExhibitNameT + ", DescE=" + DescE + ", DescS=" + DescS + ", DescT=" + DescT + ", PhotoFileName="
                + PhotoFileName + ", SeqEN=" + SeqEN + ", SeqTC=" + SeqTC + ", SeqSC=" + SeqSC + ", HallNo=" + HallNo
                + ", IsFavourite=" + IsFavourite + ", percent=" + percent + "]";
    }
    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CompanyID);
        dest.writeString(this.CompanyNameEN);
        dest.writeString(this.CompanyNameTW);
        dest.writeString(this.CompanyNameCN);
        dest.writeString(this.AddressE);
        dest.writeString(this.AddressT);
        dest.writeString(this.AddressS);
        dest.writeString(this.Postal);
        dest.writeString(this.Tel);
        dest.writeString(this.Fax);
        dest.writeString(this.Email);
        dest.writeString(this.Website);
        dest.writeString(this.CountryID);
        dest.writeString(this.AddressE1);
        dest.writeString(this.AddressT1);
        dest.writeString(this.AddressS1);
        dest.writeString(this.AddressE2);
        dest.writeString(this.AddressT2);
        dest.writeString(this.AddressS2);
        dest.writeString(this.BoothNo);
        dest.writeString(this.StrokeEng);
        dest.writeString(this.StrokeTrad);
        dest.writeString(this.StrokeSimp);
        dest.writeString(this.PYSimp);
        dest.writeString(this.ImgFolder);
        dest.writeString(this.ExhibitNameE);
        dest.writeString(this.ExhibitNameS);
        dest.writeString(this.ExhibitNameT);
        dest.writeString(this.DescE);
        dest.writeString(this.DescS);
        dest.writeString(this.DescT);
        dest.writeString(this.PhotoFileName);
        dest.writeValue(this.SeqEN);
        dest.writeValue(this.SeqTC);
        dest.writeValue(this.SeqSC);
        dest.writeString(this.HallNo);
        dest.writeValue(this.IsFavourite);
        dest.writeString(this.Note);
        dest.writeString(this.CompanyName);
        dest.writeString(this.Sort);
        dest.writeParcelable(this.isPhotoEmpty, 0);
        dest.writeInt(this.percent);
    }

    protected Exhibitor(Parcel in) {
        this.CompanyID = in.readString();
        this.CompanyNameEN = in.readString();
        this.CompanyNameTW = in.readString();
        this.CompanyNameCN = in.readString();
        this.AddressE = in.readString();
        this.AddressT = in.readString();
        this.AddressS = in.readString();
        this.Postal = in.readString();
        this.Tel = in.readString();
        this.Fax = in.readString();
        this.Email = in.readString();
        this.Website = in.readString();
        this.CountryID = in.readString();
        this.AddressE1 = in.readString();
        this.AddressT1 = in.readString();
        this.AddressS1 = in.readString();
        this.AddressE2 = in.readString();
        this.AddressT2 = in.readString();
        this.AddressS2 = in.readString();
        this.BoothNo = in.readString();
        this.StrokeEng = in.readString();
        this.StrokeTrad = in.readString();
        this.StrokeSimp = in.readString();
        this.PYSimp = in.readString();
        this.ImgFolder = in.readString();
        this.ExhibitNameE = in.readString();
        this.ExhibitNameS = in.readString();
        this.ExhibitNameT = in.readString();
        this.DescE = in.readString();
        this.DescS = in.readString();
        this.DescT = in.readString();
        this.PhotoFileName = in.readString();
        this.SeqEN = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SeqTC = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SeqSC = (Integer) in.readValue(Integer.class.getClassLoader());
        this.HallNo = in.readString();
        this.IsFavourite = (Integer) in.readValue(Integer.class.getClassLoader());
        this.Note = in.readString();
        this.CompanyName = in.readString();
        this.Sort = in.readString();
        this.isPhotoEmpty = in.readParcelable(ObservableBoolean.class.getClassLoader());
        this.percent = in.readInt();
    }

    public static final Parcelable.Creator<Exhibitor> CREATOR = new Parcelable.Creator<Exhibitor>() {
        public Exhibitor createFromParcel(Parcel source) {
            return new Exhibitor(source);
        }

        public Exhibitor[] newArray(int size) {
            return new Exhibitor[size];
        }
    };
}
