package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "NEW_PRODUCTS_AND_APPLICATION".
 */
public class NewProductsAndApplication {

    private String Spot;
    private String RID;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public NewProductsAndApplication() {
    }

    public NewProductsAndApplication(String Spot, String RID) {
        this.Spot = Spot;
        this.RID = RID;
    }

    public String getSpot() {
        return Spot;
    }

    public void setSpot(String Spot) {
        this.Spot = Spot;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}