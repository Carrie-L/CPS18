package com.adsale.ChinaPlas.viewmodel;

import android.databinding.ObservableArrayList;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsDao;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.dao.NewsLinkDao;
import com.adsale.ChinaPlas.data.OtherReposity;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/15.
 */

public class NewsModel {
    public final ObservableArrayList<News> newsList = new ObservableArrayList<>();

    private OtherReposity mOtherReposity;
    private NewsDao mNewsDao;
    private NewsLinkDao mLinkDao;

    public NewsModel() {
        mNewsDao = App.mDBHelper.mNewsDao;
        mLinkDao = App.mDBHelper.mLinkDao;
    }

    public ArrayList<News> getNewsList() {
        return (ArrayList<News>) mNewsDao.queryBuilder().orderDesc(NewsDao.Properties.PublishDate).list();
    }

    //select  * from NEWS_LINK NL,NEWS N where NL.NEWS_ID=N.NEWS_ID AND N.NEWS_ID="0000000668"
    public ArrayList<NewsLink> getLinks(String newsId){
        return (ArrayList<NewsLink>) mLinkDao.queryBuilder()
                .where(NewsLinkDao.Properties.NewsID.eq(newsId))
                .orderAsc(NewsLinkDao.Properties.SEQ).list();
    }

}
