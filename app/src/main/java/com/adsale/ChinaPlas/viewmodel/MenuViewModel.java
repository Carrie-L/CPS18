package com.adsale.ChinaPlas.viewmodel;

import android.content.SharedPreferences;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.utils.LogUtil;

/**
 * Created by Carrie on 2017/9/22.
 * 主界面
 */

public class MenuViewModel {
    private static final String TAG = "MenuViewModel";
    private SharedPreferences mSPConfig;

    public void init() {
        mSPConfig = App.mSP_Config;
    }

    public void showHelpPage() {
        boolean firstShowHelpPage = mSPConfig.getBoolean("isFirstShowHelpPage", true);
        LogUtil.i(TAG,"firstShowHelpPage="+firstShowHelpPage);
        if (firstShowHelpPage) {

        }
    }

    public void toUpdateCenter() {

    }


}
