package com.adsale.ChinaPlas.data.model;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/16.
 * 新技术产品
 */

public class NewTec {
    public String NewTechInfoLink;
    public String imageLink;

    public ArrayList<ADProduct> adProduct;


    @Override
    public String toString() {
        return "NewTec{" +
                "NewTechInfoLink='" + NewTechInfoLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", adProduct=" + adProduct +
                '}';
    }

    public static class ADProduct{
        public String FirstPageImage;
        public ArrayList<String> ImageLinks;
        public String ProductName_SC;
        public String ProductName_EN;
        public String Description_SC;
        public String Description_EN;
        public String Description_TC;
        public String CompanyID;
        public String BoothNo;
        public String vedioLink;

        @Override
        public String toString() {
            return "AD{" +
                    "FirstPageImage='" + FirstPageImage + '\'' +
                    ", ImageLinks=" + ImageLinks +
                    ", ProductName_SC='" + ProductName_SC + '\'' +
                    ", ProductName_EN='" + ProductName_EN + '\'' +
                    ", Description_SC='" + Description_SC + '\'' +
                    ", Description_EN='" + Description_EN + '\'' +
                    ", Description_TC='" + Description_TC + '\'' +
                    ", CompanyID='" + CompanyID + '\'' +
                    ", BoothNo='" + BoothNo + '\'' +
                    ", vedioLink='" + vedioLink + '\'' +
                    '}';
        }



    }









}
