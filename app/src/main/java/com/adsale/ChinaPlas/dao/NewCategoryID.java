package com.adsale.ChinaPlas.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "NEW_CATEGORY_ID".
 */
public class NewCategoryID {

    private String Category;
    private String RID;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public NewCategoryID() {
    }

    public NewCategoryID(String category, String RID) {
        Category = category;
        this.RID = RID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }



    // KEEP METHODS - put your custom methods here
    public void parser(String[] strings) {
        this.Category = strings[0];
        this.RID = strings[1];
    }

    // KEEP METHODS END

}