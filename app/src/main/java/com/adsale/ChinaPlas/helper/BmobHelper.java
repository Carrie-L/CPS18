package com.adsale.ChinaPlas.helper;

import android.util.Log;

import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2018/11/11.
 */

public class BmobHelper {

    public static <T> BmobQuery<T> getUpdateList(String lastUpdateTime) {
        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereGreaterThan("updatedAt", lastUpdateTime);
//        query.findObjects(new FindListener<T>() {
//            @Override
//            public void done(List<T> list, BmobException e) {
//                LogUtil.i(TAG, "getUpdateList::: list = " + list.size() + "," + list.toString());
//            }
//        });
        return query;
    }


}
