package com.adsale.ChinaPlas.data.model;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/16.
 * 新技术产品
 */

public class NewTec {
    public String NewTechInfoLink;
    public String imageLink;
    public String listImageLink;

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
        public String LogoImageLink;
        public ArrayList<String> ImageLinks;
        public String ProductName_SC;
        public String ProductName_EN;
        public String ProductName_TC;
        public String Description_SC;
        public String Description_EN;
        public String Description_TC;
        public String CompanyID;
        public String BoothNo;
        public String vedioLink;
        public String CompanyName_SC;
        public String CompanyName_TC;
        public String CompanyName_EN;

        @Override
        public String toString() {
            return "AD{" +
                    " BoothNo='" + BoothNo + '\'' +
                    " LogoImageLink='" + LogoImageLink + '\'' +
                    " vedioLink='" + vedioLink + '\'' +
                    '}';
        }



    }









}
