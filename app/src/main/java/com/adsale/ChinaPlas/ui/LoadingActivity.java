package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.ActivityLoadingBinding;
import com.adsale.ChinaPlas.helper.LoadingReceiver;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.LoadingViewModel;

import java.util.UUID;

import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.Constant.SCREEN_HEIGHT;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_READ_PHONE_STATE;

// TODO: 2017/10/30 down txt error, send broadcast 
public class LoadingActivity extends AppCompatActivity implements LoadingReceiver.OnLoadFinishListener {
    private static final String TAG = "LoadingActivity";
    private SharedPreferences mConfigSP;
    private boolean isFirstRunning;
    private LoadingViewModel mLoadingModel;
    private LoadingReceiver mReceiver;
    private ActivityLoadingBinding binding;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        mLoadingModel = new LoadingViewModel(getApplicationContext());
        binding.setLoadingModel(mLoadingModel);

        mConfigSP = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mConfigSP.edit().putBoolean("M1ShowFinish", false).putBoolean("txtDownFinish", false).putBoolean("webServicesDownFinish", false).putString("M1ClickId", "").apply();
        isFirstRunning = AppUtil.isFirstRunning();
        mConfigSP.edit().putBoolean("isFirstGetMaster", isFirstRunning).apply();
        LogUtil.i(TAG, "== isFirstRunning == " + isFirstRunning);

        mLoadingModel.initM1(binding.vpindicator, binding.autoVP, binding.tvSkip, binding.framelayout);
        registerBroadcastReceiver();

        hideNavBar();

        getAppVersion();
        if (isFirstRunning) {
            mLoadingModel.showLangBtn.set(true);
            setDeviceType();
            requestPermission();
            getDeviceInfo();
        } else {
            int language = AppUtil.getCurLanguage();
            AppUtil.switchLanguage(getApplicationContext(), language);
            App.mLanguage.set(language);

            getDeviceInfo2();
            mLoadingModel.run();
        }
    }

    private void hideNavBar() {
        binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void registerBroadcastReceiver() {
        mReceiver = new LoadingReceiver();
        IntentFilter intentFilter = new IntentFilter(LOADING_ACTION);
        registerReceiver(mReceiver, intentFilter);
        mReceiver.setOnLoadFinishListener(this);
    }

    private void setDeviceType() {
        isTablet = getResources().getBoolean(R.bool.isTablet);
        LogUtil.i(TAG, "isTablet=" + isTablet);
        mConfigSP.edit().putBoolean("isTablet", isTablet).apply();
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void getDeviceInfo() {
        Display display = getWindowManager().getDefaultDisplay();
        int displayHeight = display.getHeight();
        Point point = new Point();
        display.getRealSize(point);
        int width = point.x;
        int height = point.y;
        LogUtil.i(TAG, "device 的宽高为：width=" + width + ",height=" + height + ",displayHeight=" + displayHeight);

        if (isTablet) {
            LogUtil.i(TAG, "isTablet");
            int contentWidth = (1840 * height) / 1536; // 2014*1536是设计图的尺寸； 1840*1536 是中间内容的尺寸。
            int leftMargin = (width - contentWidth) / 2;
            LogUtil.i(TAG, "leftMargin = " + leftMargin);
            LogUtil.i(TAG, "screenWidth =" + width + ",contentWidth=" + contentWidth);

            float screenWidthRate = ((float) width / 2048f); // 实际屏幕宽度 比 主界面设计图片宽度 。这样在计算显示宽度时只需用 图片宽度 * rate 即为需要的宽度
            LogUtil.i(TAG, "screenWidthRate = " + screenWidthRate);
            float heightRate = (float) height / 1536f;
            LogUtil.i(TAG, "heightRate = " + heightRate);
//            width = width - leftMargin * 2;
//            LogUtil.i(TAG, "new mScreenWidth = " + width);
            width = contentWidth;
            LogUtil.i(TAG, "mScreenHeight = " + height);

            mConfigSP.edit().putInt(Constant.PAD_LEFT_MARGIN, leftMargin).putFloat("PadWidthRate", screenWidthRate).putFloat("PadHeightRate", heightRate).apply();
        }

        mConfigSP.edit().putInt(Constant.SCREEN_WIDTH, width).putInt(SCREEN_HEIGHT, height)
                .putInt(Constant.DISPLAY_HEIGHT, displayHeight)
                .apply();
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

    private void getAppVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            App.mSP_Config.edit().putString("AppVersion", info.versionName).apply();
            LogUtil.i("App", "mAppVersion=" + info.versionName + ",mVersionCode=" + info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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
        if (AppUtil.isTablet()) {
            Intent i = new Intent(this, companyId.isEmpty() ? PadMainActivity.class : ExhibitorActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, companyId.isEmpty() ? MainActivity.class : ExhibitorActivity.class);
            startActivity(i);
        }


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
