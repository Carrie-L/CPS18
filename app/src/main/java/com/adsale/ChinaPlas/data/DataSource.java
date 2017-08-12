package com.adsale.ChinaPlas.data;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/8.
 */

public interface DataSource<T> {

    ArrayList<T> getData();

    T getItemData(Object obj);

    void updateItemData(T entity);

    void insertItemData(T entity);

    void deleteItemData(Object obj);

    interface SearchData {
        void queryData(String queryText);

        void clearData();
    }


}
