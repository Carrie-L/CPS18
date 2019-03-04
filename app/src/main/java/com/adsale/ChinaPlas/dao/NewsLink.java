package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "NEWS_LINK".
 */
public class NewsLink {

    private String LinkID;
    /** Not-null value. */
    private String NewsID;
    private String Photo;
    private String Title;
    private String Link;
    private String SEQ;
    /** Not-null value. */
    private String createdAt;
    /** Not-null value. */
    private String updatedAt;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public NewsLink() {
    }

    public NewsLink(String LinkID) {
        this.LinkID = LinkID;
    }

    public NewsLink(String LinkID, String NewsID, String Photo, String Title, String Link, String SEQ, String CreateDateTime, String UpdateDateTime) {
        this.LinkID = LinkID;
        this.NewsID = NewsID;
        this.Photo = Photo;
        this.Title = Title;
        this.Link = Link;
        this.SEQ = SEQ;
        this.createdAt = CreateDateTime;
        this.updatedAt = UpdateDateTime;
    }

    public String getLinkID() {
        return LinkID;
    }

    public void setLinkID(String LinkID) {
        this.LinkID = LinkID;
    }

    /** Not-null value. */
    public String getNewsID() {
        return NewsID;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNewsID(String NewsID) {
        this.NewsID = NewsID;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String Photo) {
        this.Photo = Photo;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String Link) {
        this.Link = Link;
    }

    public String getSEQ() {
        return SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    /** Not-null value. */
    public String getCreateDateTime() {
        return createdAt;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreateDateTime(String CreateDateTime) {
        this.createdAt = CreateDateTime;
    }

    /** Not-null value. */
    public String getUpdateDateTime() {
        return updatedAt;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUpdateDateTime(String UpdateDateTime) {
        this.updatedAt = UpdateDateTime;
    }



    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
