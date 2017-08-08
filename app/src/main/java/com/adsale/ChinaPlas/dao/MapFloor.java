package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "MAP_FLOOR".
 */
public class MapFloor {

    private String MapFloorID;
    private String ParentID;
    private Integer Type;
    private String NameTW;
    private String NameCN;
    private String NameEN;
    private Integer SEQ;
    /** Not-null value. */
    private String CreateDateTime;
    /** Not-null value. */
    private String UpdateDateTime;
    /** Not-null value. */
    private String RecordTimeStamp;
    private Integer Down;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MapFloor() {
    }

    public MapFloor(String MapFloorID) {
        this.MapFloorID = MapFloorID;
    }

    public MapFloor(String MapFloorID, String ParentID, Integer Type, String NameTW, String NameCN, String NameEN, Integer SEQ, String CreateDateTime, String UpdateDateTime, String RecordTimeStamp, Integer Down) {
        this.MapFloorID = MapFloorID;
        this.ParentID = ParentID;
        this.Type = Type;
        this.NameTW = NameTW;
        this.NameCN = NameCN;
        this.NameEN = NameEN;
        this.SEQ = SEQ;
        this.CreateDateTime = CreateDateTime;
        this.UpdateDateTime = UpdateDateTime;
        this.RecordTimeStamp = RecordTimeStamp;
        this.Down = Down;
    }

    public String getMapFloorID() {
        return MapFloorID;
    }

    public void setMapFloorID(String MapFloorID) {
        this.MapFloorID = MapFloorID;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String ParentID) {
        this.ParentID = ParentID;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer Type) {
        this.Type = Type;
    }

    public String getNameTW() {
        return NameTW;
    }

    public void setNameTW(String NameTW) {
        this.NameTW = NameTW;
    }

    public String getNameCN() {
        return NameCN;
    }

    public void setNameCN(String NameCN) {
        this.NameCN = NameCN;
    }

    public String getNameEN() {
        return NameEN;
    }

    public void setNameEN(String NameEN) {
        this.NameEN = NameEN;
    }

    public Integer getSEQ() {
        return SEQ;
    }

    public void setSEQ(Integer SEQ) {
        this.SEQ = SEQ;
    }

    /** Not-null value. */
    public String getCreateDateTime() {
        return CreateDateTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreateDateTime(String CreateDateTime) {
        this.CreateDateTime = CreateDateTime;
    }

    /** Not-null value. */
    public String getUpdateDateTime() {
        return UpdateDateTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUpdateDateTime(String UpdateDateTime) {
        this.UpdateDateTime = UpdateDateTime;
    }

    /** Not-null value. */
    public String getRecordTimeStamp() {
        return RecordTimeStamp;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setRecordTimeStamp(String RecordTimeStamp) {
        this.RecordTimeStamp = RecordTimeStamp;
    }

    public Integer getDown() {
        return Down;
    }

    public void setDown(Integer Down) {
        this.Down = Down;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
