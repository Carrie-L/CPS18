package com.adsale.ChinaPlas.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.databinding.ObservableInt;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2018/4/12.
 */

public class DownloadCenterViewModel extends AndroidViewModel{
    private List<DocumentsCenter.Child> collectEntities = new ArrayList<>();
    private static final String TAG = "DownViewModel";
    private static DownloadCenterViewModel mInstance;

    public DownloadCenterViewModel(Application mApplication) {
        super(mApplication);
        LogUtil.i(TAG, "DownViewModel Application");
    }

    public static DownloadCenterViewModel getInstance(Application mApplication) {
        if (mInstance == null) {
            synchronized (DownloadCenterViewModel.class) {
                if (mInstance == null) {
                    mInstance = new DownloadCenterViewModel(mApplication);
                }
            }
        }
        return mInstance;
    }

    public void add(DocumentsCenter.Child entity) {
        collectEntities.add(entity);
    }

    public DocumentsCenter.Child getDownloadingEntity(int id) throws NullPointerException {
        int s = collectEntities.size();
        for (int k = 0; k < s; k++) {
            if (collectEntities.get(k).getId() == id) {
                return collectEntities.get(k);
            }
        }
        throw new NullPointerException();
    }

    public void clearEntity(int _id) {
        int s = collectEntities.size();
        for (int k = 0; k < s; k++) {
            if (collectEntities.get(k).getId() == _id) {
                LogUtil.i(TAG, "clear " + collectEntities.get(k).getFileName());
                collectEntities.remove(k);
                break;
            }
        }
    }






}
