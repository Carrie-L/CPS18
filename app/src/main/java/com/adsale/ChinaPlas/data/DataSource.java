package com.adsale.ChinaPlas.data;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/8.
 */

public interface DataSource<T> {

    void insertData(ArrayList<T> list);

    void insertItemData(T entity);

    ArrayList<T> getData();

    T getItemData(String id);

    void saveData(ArrayList<T> list);

    void saveItemData(T entity);

    void updateData(ArrayList<T> list);

    void updateItemData(T entity);

    void deleteData();

    void deleteItemData(String id);

    void queryData(String text);

    interface SearchData {
        void queryData(String queryText);

        void clearData();
    }


}
