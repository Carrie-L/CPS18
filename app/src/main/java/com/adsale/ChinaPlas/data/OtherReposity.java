package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsDao;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/13.
 */

public class OtherReposity{
    private static OtherReposity INSTANCE;
    private final String TAG="OtherReposity";

    public static OtherReposity  getInstance(){
        if(INSTANCE==null){
            return new OtherReposity();
        }
        return INSTANCE;
    }




    /* [News] */


}
