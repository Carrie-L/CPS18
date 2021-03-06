package com.adsale.ChinaPlas.dao;


// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "HISTORY__EXHIBITOR__TEMP".
 */
public class HISTORY_EXHIBITOR_TEMP {

    private String _id;
    private Integer COMPANY_ID;
    private String COMPANY_NAME_EN;
    private String COMPANY_NAME_CN;
    private String COMPANY_NAME_TW;
    private String BOOTH;
    private Integer TIME;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public HISTORY_EXHIBITOR_TEMP() {
    }

    public HISTORY_EXHIBITOR_TEMP(String _id) {
        this._id = _id;
    }

    public HISTORY_EXHIBITOR_TEMP(String _id, Integer COMPANY_ID, String COMPANY_NAME_EN, String COMPANY_NAME_CN, String COMPANY_NAME_TW, String BOOTH, Integer TIME) {
        this._id = _id;
        this.COMPANY_ID = COMPANY_ID;
        this.COMPANY_NAME_EN = COMPANY_NAME_EN;
        this.COMPANY_NAME_CN = COMPANY_NAME_CN;
        this.COMPANY_NAME_TW = COMPANY_NAME_TW;
        this.BOOTH = BOOTH;
        this.TIME = TIME;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getCOMPANY_ID() {
        return COMPANY_ID;
    }

    public void setCOMPANY_ID(Integer COMPANY_ID) {
        this.COMPANY_ID = COMPANY_ID;
    }

    public String getCOMPANY_NAME_EN() {
        return COMPANY_NAME_EN;
    }

    public void setCOMPANY_NAME_EN(String COMPANY_NAME_EN) {
        this.COMPANY_NAME_EN = COMPANY_NAME_EN;
    }

    public String getCOMPANY_NAME_CN() {
        return COMPANY_NAME_CN;
    }

    public void setCOMPANY_NAME_CN(String COMPANY_NAME_CN) {
        this.COMPANY_NAME_CN = COMPANY_NAME_CN;
    }

    public String getCOMPANY_NAME_TW() {
        return COMPANY_NAME_TW;
    }

    public void setCOMPANY_NAME_TW(String COMPANY_NAME_TW) {
        this.COMPANY_NAME_TW = COMPANY_NAME_TW;
    }

    public String getBOOTH() {
        return BOOTH;
    }

    public void setBOOTH(String BOOTH) {
        this.BOOTH = BOOTH;
    }

    public Integer getTIME() {
        return TIME;
    }

    public void setTIME(Integer TIME) {
        this.TIME = TIME;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
