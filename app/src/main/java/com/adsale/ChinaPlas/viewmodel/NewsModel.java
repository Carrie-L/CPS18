package com.adsale.ChinaPlas.viewmodel;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsDao;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.dao.NewsLinkDao;
import com.adsale.ChinaPlas.utils.AppUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/15.
 */

public class NewsModel {
    private NewsDao mNewsDao;

    public NewsModel() {
        mNewsDao = App.mDBHelper.mNewsDao;
    }

    /**
     * LType = 1, TC;
     * LType = 2, SC;
     * LType = 3, EN.
     */
    public ArrayList<News> getNewsList() {
        return (ArrayList<News>) mNewsDao.queryBuilder()
                .where(NewsDao.Properties.LType.eq(AppUtil.getCurLanguage() == 0 ? 1 : AppUtil.getCurLanguage() == 2 ? 2 : 3),NewsDao.Properties.IsDelete.notEq(1))
                .orderDesc(NewsDao.Properties.PublishDate).list();
    }

    //select  * from NEWS_LINK NL,NEWS N where NL.NEWS_ID=N.NEWS_ID AND N.NEWS_ID="0000000668"

    public News getItemNews(String newsId) {
        return mNewsDao.load(newsId);
    }

}
