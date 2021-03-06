package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;

import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entity mapped to table "SEMINAR_SPEAKER".
 */
public class SeminarSpeaker {

    private Integer EventID;
    private String CompanyID;
    private String Seminarsummary;
    private String SpeakerPhoto;
    private String SpeakerName;
    private String SpeakerPosition;
    private String SpeakerInfo;
    private String Language;
    private String FreeParticipation;
    private String ContactPerson;
    private String Email;
    private String Tel;
    private String LangID;
    private Integer ID;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public SeminarSpeaker() {
    }

    public SeminarSpeaker(Integer EventID, String CompanyID, String Seminarsummary, String SpeakerPhoto, String SpeakerName, String SpeakerPosition, String SpeakerInfo, String Language, String FreeParticipation, String ContactPerson, String Email, String Tel, String LangID, Integer ID) {
        this.EventID = EventID;
        this.CompanyID = CompanyID;
        this.Seminarsummary = Seminarsummary;
        this.SpeakerPhoto = SpeakerPhoto;
        this.SpeakerName = SpeakerName;
        this.SpeakerPosition = SpeakerPosition;
        this.SpeakerInfo = SpeakerInfo;
        this.Language = Language;
        this.FreeParticipation = FreeParticipation;
        this.ContactPerson = ContactPerson;
        this.Email = Email;
        this.Tel = Tel;
        this.LangID = LangID;
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

    public String getSeminarsummary() {
        Seminarsummary = Seminarsummary.replaceAll("  ", "\n\n");
        return Seminarsummary;
    }

    public void setSeminarsummary(String Seminarsummary) {
        this.Seminarsummary = Seminarsummary;
    }

    public String getSpeakerPhoto() {
        return SpeakerPhoto;
    }

    public void setSpeakerPhoto(String SpeakerPhoto) {
        this.SpeakerPhoto = SpeakerPhoto;
    }

    public String getSpeakerName() {
        return SpeakerName;
    }

    public void setSpeakerName(String SpeakerName) {
        this.SpeakerName = SpeakerName;
    }

    public String getSpeakerPosition() {
        return SpeakerPosition;
    }

    public void setSpeakerPosition(String SpeakerPosition) {
        this.SpeakerPosition = SpeakerPosition;
    }

    public String getSpeakerInfo() {
        return SpeakerInfo;
    }

    public void setSpeakerInfo(String SpeakerInfo) {
        this.SpeakerInfo = SpeakerInfo;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String Language) {
        this.Language = Language;
    }

    public String getFreeParticipation() {
        return FreeParticipation;
    }

    public void setFreeParticipation(String FreeParticipation) {
        this.FreeParticipation = FreeParticipation;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String ContactPerson) {
        this.ContactPerson = ContactPerson;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String Tel) {
        this.Tel = Tel;
    }

    public String getLangID() {
        return LangID;
    }

    public void setLangID(String LangID) {
        this.LangID = LangID;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    // KEEP METHODS - put your custom methods here
    public void parser(String[] strings) {
        this.EventID = Integer.valueOf(strings[0]);
        this.CompanyID = strings[1];
        this.Seminarsummary = strings[2];
        this.SpeakerPhoto = strings[3];
        this.SpeakerName = strings[4];
        this.SpeakerPosition = strings[5];
        this.SpeakerInfo = strings[6];
        this.Language = strings[7];
        this.FreeParticipation = strings[8];
        this.ContactPerson = strings[9];
        this.Email = strings[10];
        this.Tel = strings[11];
        this.LangID = strings[12];
        this.ID = Integer.valueOf(strings[13]);
    }
    // KEEP METHODS END

}
