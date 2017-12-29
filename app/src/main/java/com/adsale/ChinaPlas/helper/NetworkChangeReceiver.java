package com.adsale.ChinaPlas.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.adsale.ChinaPlas.App;

/**
 * Created by Carrie on 2017/12/28.
 * 监听网络状态改变的广播
 * todo  title:android 监听网络状态的变化及实际应用
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiStatus = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiStatus) {
                case WifiManager.WIFI_STATE_DISABLED:
                    App.isNetworkAvailable = false;
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    App.isNetworkAvailable = true;
                    break;
            }

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

            }

        }
    }
}
