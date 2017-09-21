package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.ActivityLoadingBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.LoadingReceiver;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.LoadingViewModel;

import java.util.UUID;

import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_READ_PHONE_STATE;

public class LoadingActivity extends AppCompatActivity implements LoadingReceiver.OnLoadFinishListener {
    private static final String TAG = "LoadingActivity";
    private SharedPreferences mConfigSP;
    private boolean isFirstRunning;
    private LoadingViewModel mLoadingModel;
    private LoadingReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoadingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        mLoadingModel = new LoadingViewModel(getApplicationContext());
        binding.setLoadingModel(mLoadingModel);

        mConfigSP = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mConfigSP.edit().putBoolean("M1ShowFinish", false).putBoolean("txtDownFinish", false).putBoolean("webServicesDownFinish", false).putString("M1ClickId", "").apply();
//        isFirstRunning = mConfigSP.getBoolean("isFirstRunning", true);
        isFirstRunning = AppUtil.isFirstRunning();
        mConfigSP.edit().putBoolean("isFirstGetMaster", isFirstRunning).apply();
        LogUtil.i(TAG, "== isFirstRunning == " + isFirstRunning);

        mLoadingModel.initM1(binding.vpindicator, binding.autoVP, binding.tvSkip, binding.framelayout);
        registerBroadcastReceiver();

        if (isFirstRunning) {
            mLoadingModel.showLangBtn.set(true);
            setDeviceType();
            requestPermission();
            getDeviceInfo();
        } else {
            getDeviceInfo2();

            mLoadingModel.run();
        }

    }

    private void registerBroadcastReceiver() {
        mReceiver = new LoadingReceiver();
        IntentFilter intentFilter = new IntentFilter(LOADING_ACTION);
        registerReceiver(mReceiver, intentFilter);
        mReceiver.setOnLoadFinishListener(this);
    }

    private void setDeviceType() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        LogUtil.i(TAG, "isTablet=" + isTablet);
        mConfigSP.edit().putBoolean("isTablet", isTablet).apply();
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void getDeviceInfo() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(point);
        } else {
            display.getSize(point);
        }
        int width = point.x;
        int height = point.y;
        LogUtil.i(TAG, "device 的宽高为：width=" + width + ",height=" + height);
        mConfigSP.edit().putInt("ScreenWidth", width).putInt("ScreenHeight", height).apply();
    }

    private void requestPermission() {
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

        if (PermissionUtil.getGrantResults(grantResults) && requestCode == PMS_CODE_READ_PHONE_STATE) {
            getDeviceId();
        } else {
            App.mSP_Config.edit().putString("deviceId", UUID.randomUUID().toString()).apply();
        }
    }

    private void getDeviceInfo2() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        int phoneType = tm.getPhoneType();
        LogUtil.i(TAG, "phoneType=" + phoneType);

        int networkType = tm.getNetworkType();
        LogUtil.i(TAG, "networkType=" + networkType);

        String NetworkOperator = tm.getNetworkOperator();
        LogUtil.i(TAG, "NetworkOperator=" + NetworkOperator);

        String NetworkOperatorName = tm.getNetworkOperatorName();
        LogUtil.i(TAG, "NetworkOperatorName=" + NetworkOperatorName);//Android
    }

    private void getDeviceId() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        LogUtil.i(TAG, "deviceId=" + deviceId);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtil.i(TAG, "deviceId  =" + deviceId);
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
        }
        App.mSP_Config.edit().putString("deviceId", deviceId).apply();

//        App.mSP_Config.edit().putString("UUID", uniqueID).apply();

        LogUtil.i(TAG, "deviceId=" + deviceId);
    }


    @Override
    public void intent(String companyId) {
        LogUtil.i(TAG, ")))) ALL END ,GO AHEAD");
        mConfigSP.edit().putBoolean("M1ShowFinish", false).putBoolean("txtDownFinish", false).putBoolean("webServicesDownFinish", false).putString("M1ClickId", "").apply();
        Intent i = new Intent(this, companyId.isEmpty() ? MainActivity.class : ExhibitorActivity.class);
        startActivity(i);
        mLoadingModel.unSubscribe();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        if (mLoadingModel != null) {
            mLoadingModel.unSubscribe();
            mLoadingModel = null;
        }
    }
}
