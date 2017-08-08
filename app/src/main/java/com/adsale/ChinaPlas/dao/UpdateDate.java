package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "UPDATE_DATE".
 */
public class UpdateDate {

    private String ModuleID;
    /** Not-null value. */
    private String LastUpdateDate;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public UpdateDate() {
    }

    public UpdateDate(String ModuleID) {
        this.ModuleID = ModuleID;
    }

    public UpdateDate(String ModuleID, String LastUpdateDate) {
        this.ModuleID = ModuleID;
        this.LastUpdateDate = LastUpdateDate;
    }

    public String getModuleID() {
        return ModuleID;
    }

    public void setModuleID(String ModuleID) {
        this.ModuleID = ModuleID;
    }

    /** Not-null value. */
    public String getLastUpdateDate() {
        return LastUpdateDate;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLastUpdateDate(String LastUpdateDate) {
        this.LastUpdateDate = LastUpdateDate;
    }

    // KEEP METHODS - put your custom methods here
	@Override
	public String toString() {
		return "UpdateDate [ModuleID=" + ModuleID + ", LastUpdateDate=" + LastUpdateDate + "]";
	}
    // KEEP METHODS END

}
