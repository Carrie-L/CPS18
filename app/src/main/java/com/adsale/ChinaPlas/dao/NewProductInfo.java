package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.os.Parcel;
import android.os.Parcelable;

import com.adsale.ChinaPlas.App;

/**
 * Entity mapped to table "NEW_PRODUCT_INFO".
 */
public class NewProductInfo implements Parcelable {

    private String RID;
    private String CompanyID;
    private String BoothNo;
    private String CompanyNameEn;
    private String CompanyNameSc;
    private String CompanyNameTc;
    private String Product_Name_SC;
    private String Rroduct_Description_SC;
    private String Product_Name_TC;
    private String Rroduct_Description_TC;
    private String Product_Name_EN;
    private String Rroduct_Description_EN;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public NewProductInfo() {
    }

    public NewProductInfo(String RID, String CompanyID, String BoothNo, String CompanyNameEn, String CompanyNameSc, String CompanyNameTc, String Product_Name_SC, String Rroduct_Description_SC, String Product_Name_TC, String Rroduct_Description_TC, String Product_Name_EN, String Rroduct_Description_EN) {
        this.RID = RID;
        this.CompanyID = CompanyID;
        this.BoothNo = BoothNo;
        this.CompanyNameEn = CompanyNameEn;
        this.CompanyNameSc = CompanyNameSc;
        this.CompanyNameTc = CompanyNameTc;
        this.Product_Name_SC = Product_Name_SC;
        this.Rroduct_Description_SC = Rroduct_Description_SC;
        this.Product_Name_TC = Product_Name_TC;
        this.Rroduct_Description_TC = Rroduct_Description_TC;
        this.Product_Name_EN = Product_Name_EN;
        this.Rroduct_Description_EN = Rroduct_Description_EN;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String CompanyID) {
        this.CompanyID = CompanyID;
    }

    public String getBoothNo() {
        return BoothNo;
    }

    public void setBoothNo(String BoothNo) {
        this.BoothNo = BoothNo;
    }

    public String getCompanyNameEn() {
        return CompanyNameEn;
    }

    public void setCompanyNameEn(String CompanyNameEn) {
        this.CompanyNameEn = CompanyNameEn;
    }

    public String getCompanyNameSc() {
        return CompanyNameSc;
    }

    public void setCompanyNameSc(String CompanyNameSc) {
        this.CompanyNameSc = CompanyNameSc;
    }

    public String getCompanyNameTc() {
        return CompanyNameTc;
    }

    public void setCompanyNameTc(String CompanyNameTc) {
        this.CompanyNameTc = CompanyNameTc;
    }

    public String getProduct_Name_SC() {
        return Product_Name_SC;
    }

    public void setProduct_Name_SC(String Product_Name_SC) {
        this.Product_Name_SC = Product_Name_SC;
    }

    public String getRroduct_Description_SC() {
        return Rroduct_Description_SC;
    }

    public void setRroduct_Description_SC(String Rroduct_Description_SC) {
        this.Rroduct_Description_SC = Rroduct_Description_SC;
    }

    public String getProduct_Name_TC() {
        return Product_Name_TC;
    }

    public void setProduct_Name_TC(String Product_Name_TC) {
        this.Product_Name_TC = Product_Name_TC;
    }

    public String getRroduct_Description_TC() {
        return Rroduct_Description_TC;
    }

    public void setRroduct_Description_TC(String Rroduct_Description_TC) {
        this.Rroduct_Description_TC = Rroduct_Description_TC;
    }

    public String getProduct_Name_EN() {
        return Product_Name_EN;
    }

    public void setProduct_Name_EN(String Product_Name_EN) {
        this.Product_Name_EN = Product_Name_EN;
    }

    public String getRroduct_Description_EN() {
        return Rroduct_Description_EN;
    }

    public void setRroduct_Description_EN(String Rroduct_Description_EN) {
        this.Rroduct_Description_EN = Rroduct_Description_EN;
    }

    // KEEP METHODS - put your custom methods here
    public void parser(String[] strings) {
        this.RID = strings[0];
        this.CompanyID = strings[1];
        this.BoothNo = strings[2];
        this.CompanyNameEn = strings[3];
        this.CompanyNameSc = strings[4];
        this.CompanyNameTc = strings[5];
        this.Product_Name_SC = strings[6];
        this.Rroduct_Description_SC = strings[7];
        this.Product_Name_TC = strings[8];
        this.Rroduct_Description_TC = strings[9];
        this.Product_Name_EN = strings[10];
        this.Rroduct_Description_EN = strings[11];
    }

    public String getCompanyName() {
        if (App.mLanguage.get() == 0) {
            return CompanyNameTc;
        } else if (App.mLanguage.get() == 1) {
            return CompanyNameEn;
        } else {
            return CompanyNameSc;
        }
    }

    public String getProductName() {
        if (App.mLanguage.get() == 0) {
            return Product_Name_TC;
        } else if (App.mLanguage.get() == 1) {
            return Product_Name_EN;
        } else {
            return Product_Name_SC;
        }
    }


    // KEEP METHODS END

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.RID);
        dest.writeString(this.CompanyID);
        dest.writeString(this.BoothNo);
        dest.writeString(this.CompanyNameEn);
        dest.writeString(this.CompanyNameSc);
        dest.writeString(this.CompanyNameTc);
        dest.writeString(this.Product_Name_SC);
        dest.writeString(this.Rroduct_Description_SC);
        dest.writeString(this.Product_Name_TC);
        dest.writeString(this.Rroduct_Description_TC);
        dest.writeString(this.Product_Name_EN);
        dest.writeString(this.Rroduct_Description_EN);
    }

    protected NewProductInfo(Parcel in) {
        this.RID = in.readString();
        this.CompanyID = in.readString();
        this.BoothNo = in.readString();
        this.CompanyNameEn = in.readString();
        this.CompanyNameSc = in.readString();
        this.CompanyNameTc = in.readString();
        this.Product_Name_SC = in.readString();
        this.Rroduct_Description_SC = in.readString();
        this.Product_Name_TC = in.readString();
        this.Rroduct_Description_TC = in.readString();
        this.Product_Name_EN = in.readString();
        this.Rroduct_Description_EN = in.readString();
    }

    public static final Parcelable.Creator<NewProductInfo> CREATOR = new Parcelable.Creator<NewProductInfo>() {
        public NewProductInfo createFromParcel(Parcel source) {
            return new NewProductInfo(source);
        }

        public NewProductInfo[] newArray(int size) {
            return new NewProductInfo[size];
        }
    };
}