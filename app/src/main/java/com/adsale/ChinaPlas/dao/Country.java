package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "COUNTRY".
 */
public class Country {

    private String CountryID;
    /** Not-null value. */
    private String CountryNameTW;
    /** Not-null value. */
    private String CountryNameCN;
    /** Not-null value. */
    private String CountryNameEN;
    /** Not-null value. */
    private String SortTW;
    /** Not-null value. */
    private String SortEN;
    /** Not-null value. */
    private String SortCN;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Country() {
    }

    public Country(String CountryID) {
        this.CountryID = CountryID;
    }

    public Country(String CountryID, String CountryNameTW, String CountryNameCN, String CountryNameEN, String SortTW, String SortEN, String SortCN) {
        this.CountryID = CountryID;
        this.CountryNameTW = CountryNameTW;
        this.CountryNameCN = CountryNameCN;
        this.CountryNameEN = CountryNameEN;
        this.SortTW = SortTW;
        this.SortEN = SortEN;
        this.SortCN = SortCN;
    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String CountryID) {
        this.CountryID = CountryID;
    }

    /** Not-null value. */
    public String getCountryNameTW() {
        return CountryNameTW;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCountryNameTW(String CountryNameTW) {
        this.CountryNameTW = CountryNameTW;
    }

    /** Not-null value. */
    public String getCountryNameCN() {
        return CountryNameCN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCountryNameCN(String CountryNameCN) {
        this.CountryNameCN = CountryNameCN;
    }

    /** Not-null value. */
    public String getCountryNameEN() {
        return CountryNameEN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCountryNameEN(String CountryNameEN) {
        this.CountryNameEN = CountryNameEN;
    }

    /** Not-null value. */
    public String getSortTW() {
        return SortTW;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSortTW(String SortTW) {
        this.SortTW = SortTW;
    }

    /** Not-null value. */
    public String getSortEN() {
        return SortEN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSortEN(String SortEN) {
        this.SortEN = SortEN;
    }

    /** Not-null value. */
    public String getSortCN() {
        return SortCN;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSortCN(String SortCN) {
        this.SortCN = SortCN;
    }

    // KEEP METHODS - put your custom methods here
    public String getSort(int language){
    	if(language==0){
    		return SortTW;
    	}else if(language==1){
    		return SortEN;
    	}else{
    		return SortCN;
    	}
    }
    
    public String getCountryName(int language){
    	if(language==0){
    		return CountryNameTW;
    	}else if(language==1){
    		return CountryNameEN;
    	}else{
    		return CountryNameCN;
    	}
    }
    // KEEP METHODS END

}
