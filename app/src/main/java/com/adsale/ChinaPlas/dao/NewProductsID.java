package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "NEW_PRODUCTS_ID".
 * 新技术产品 应用ID
 */
public class NewProductsID {
    private String RID;
    private String Spot;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public NewProductsID() {
    }

    public NewProductsID(String Spot, String RID) {
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

    public void parser(String[] strings) {
        this.RID = strings[0];
        this.Spot = strings[1];
    }

    // KEEP METHODS END

}
