package com.adsale.ChinaPlas.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

/**
 * Created by Carrie on 2017/9/15.
 */

public class LoadingReceiver extends BroadcastReceiver {

    private static final String TAG = "LoadingReceiver";
    public static final String LOADING_ACTION = "com.adsale.ChinaPlas.LoadingReceiver";
    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE);
        boolean isM1ShowFinish = sp.getBoolean("M1ShowFinish", false);
        boolean isTxtDownFinish = sp.getBoolean("txtDownFinish", false);
        boolean isWebServicesDownFinish = sp.getBoolean("webServicesDownFinish", false);
        boolean isApkDialogClicked = sp.getBoolean("apkDialogFinish",false); /* 1. 没有更新时，true； 2. 有更新，点击了（不管是yes or no），true; 3. 有更新，没有点击，false   */

        LogUtil.i(TAG, "m1 = " + isM1ShowFinish + ", txt = " + isTxtDownFinish + ", wc =" + isWebServicesDownFinish);

        if (isM1ShowFinish && isTxtDownFinish && isWebServicesDownFinish && isApkDialogClicked) {
            String companyId = sp.getString("M1ClickId", "");
            LogUtil.i(TAG, "companyId=" + companyId);
            mListener.intent(companyId); //todo zszs
        }
    }

    public interface OnLoadFinishListener {
        void intent(String companyId);
    }

    private OnLoadFinishListener mListener;

    public void setOnLoadFinishListener(OnLoadFinishListener listener) {
        mListener = listener;
    }

}
