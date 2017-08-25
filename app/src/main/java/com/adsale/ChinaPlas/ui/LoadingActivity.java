package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;

import java.util.UUID;

import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_READ_PHONE_STATE;
import static com.adsale.ChinaPlas.utils.PermissionUtil.getGrantResults;

public class LoadingActivity extends AppCompatActivity {
    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        boolean hasPhonePermission = PermissionUtil.checkPermission(this, PermissionUtil.PERMISSION_READ_PHONE_STATE);
        LogUtil.i(TAG, "hasPhonePermission=" + hasPhonePermission);
        if (hasPhonePermission) {
            getDeviceId();
        } else {
            PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_READ_PHONE_STATE, PMS_CODE_READ_PHONE_STATE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean getPermission = PermissionUtil.getGrantResults(grantResults);
        LogUtil.i(TAG, "getPermission=" + getPermission);

        if (getPermission&&requestCode==PMS_CODE_READ_PHONE_STATE) {
            getDeviceId();
        }
    }

    private void getDeviceId() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        LogUtil.i(TAG, "deviceId=" + deviceId);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtil.i(TAG, "deviceId  =" + deviceId);
        }

        String uniqueID = UUID.randomUUID().toString();
        LogUtil.i(TAG, "uniqueID=" + uniqueID);

        int phoneType = tm.getPhoneType();
        LogUtil.i(TAG, "phoneType=" + phoneType);

        int networkType = tm.getNetworkType();
        LogUtil.i(TAG, "networkType=" + networkType);

        String NetworkOperator = tm.getNetworkOperator();
        LogUtil.i(TAG, "NetworkOperator=" + NetworkOperator);

        String NetworkOperatorName = tm.getNetworkOperatorName();
        LogUtil.i(TAG, "NetworkOperatorName=" + NetworkOperatorName);//Android


        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
//        LogUtil.i(TAG, "deviceId=" + deviceId);
        App.mSP_Config.edit().putString("UUID", uniqueID).apply();
    }


}
