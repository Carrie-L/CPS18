package com.adsale.ChinaPlas.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.ActivityLoadingBinding;
import com.adsale.ChinaPlas.helper.LoadingReceiver;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.LoadingViewModel;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.lang.reflect.Field;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

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
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        mConfigSP = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        long loadingStartTime = System.currentTimeMillis();
        mConfigSP.edit().putLong("LoadingStartTime", loadingStartTime).apply();

        loadingProgress = binding.loadingProgress;
        mLoadingModel = new LoadingViewModel(getApplicationContext(), loadingProgress);
        binding.setLoadingModel(mLoadingModel);
        binding.setAty(this);
        binding.executePendingBindings();

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        LogUtil.i(TAG, "memoryClass=" + memoryClass);

        mConfigSP.edit().putBoolean("M1ShowFinish", false).putBoolean("txtDownFinish", false).putBoolean("webServicesDownFinish", false).putString("M1ClickId", "").apply();
        isFirstRunning = AppUtil.isFirstRunning();
        mConfigSP.edit().putBoolean("isFirstGetMaster", isFirstRunning).apply();
        LogUtil.i(TAG, "== isFirstRunning == " + isFirstRunning);

        mLoadingModel.initM1(binding.vpindicator, binding.autoVP, binding.tvSkip, binding.framelayout);
        registerBroadcastReceiver();
        hideNavBar();
        getAppVersion();
        mLoadingModel.upgradeDB();


        if (isFirstRunning) {
            loadingProgress.setVisibility(View.INVISIBLE);
            setDeviceType();
            getDeviceInfo();
        } else {
            binding.lyLanguage.setVisibility(View.GONE);
            isTablet = AppUtil.isTablet();
            setRequestedOrientation();
            AppUtil.switchLanguage(getApplicationContext(), AppUtil.getCurLanguage());
            getDeviceInfo2();
            isNetwork();
        }

    }

    private void isNetwork() {
           /* 用Glide加载图片的方式判断有没有网络。因为Glide会捕获无网络的Exception。在 requestListener 中进行后续操作。 */
        String url = "http://www.chinaplasonline.com/apps/2016/images/icon.png";
        Glide.with(this).load(Uri.parse(url)).listener(requestListener).into(binding.ivTestNet);
    }

    RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            LogUtil.i(TAG, "_____________________requestListener: onLoadFailed ");
            App.isNetworkAvailable = false;
            mLoadingModel.intent();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            LogUtil.i(TAG, "_____________________requestListener: onResourceReady ");
            App.isNetworkAvailable = true;
            if (isFirstRunning) {
                mLoadingModel.getUpdateInfo();
                mLoadingModel.run(true);
            } else {
                mLoadingModel.run(false);
            }
            return false;
        }
    };

    public void chooseLang(int language) {
        AppUtil.switchLanguage(getApplicationContext(), language);
        LogUtil.i(TAG, "chooseLang:language=" + language);
        loadingProgress.setVisibility(View.VISIBLE);
        binding.lyLanguage.setVisibility(View.GONE);
        mLoadingModel.showProgressBar.set(true);
        AppUtil.setNotFirstRunning();
        requestPermission();
        initJpushAlias(language);
        isNetwork();
    }

    private void hideNavBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
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
        setRequestedOrientation();
    }

    private void setRequestedOrientation() {
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
            float screenWidthRate = ((float) width / 2048f); // 实际屏幕宽度 比 主界面设计图片宽度 。这样在计算显示宽度时只需用 图片宽度 * rate 即为需要的宽度
            float heightRate = (float) height / 1536f;
            LogUtil.i(TAG, "heightRate = " + heightRate + ",screenWidthRate = " + screenWidthRate);
            width = contentWidth;
            LogUtil.i(TAG, "mScreenHeight = " + height);

            mConfigSP.edit().putInt(Constant.PAD_LEFT_MARGIN, leftMargin).putFloat("PadWidthRate", screenWidthRate).putFloat("PadHeightRate", heightRate).apply();
        }

        mConfigSP.edit().putInt(Constant.SCREEN_WIDTH, width).putInt(SCREEN_HEIGHT, height)
                .putInt(Constant.DISPLAY_HEIGHT, displayHeight)
                .apply();
    }

    private void initJpushAlias(int language){
        if (language == 0) {
            JPushInterface.setAlias(getApplicationContext(), 1, "TCUser");
        } else if (language == 1) {
            JPushInterface.setAlias(getApplicationContext(), 1, "ENUser");
        } else {
            JPushInterface.setAlias(getApplicationContext(), 1, "SCUser");
        }
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
        LogUtil.i(TAG, "isTablet=" + isTablet);

        if (!companyId.isEmpty()) {
            Intent i = new Intent(this, ExhibitorDetailActivity.class);
            i.putExtra(Constant.COMPANY_ID, companyId);
            i.putExtra("FromM1", true);
            startActivity(i);
        } else {
            Intent i = new Intent(this, isTablet ? PadMainActivity.class : MainActivity.class);
            startActivity(i);
        }
        mLoadingModel.unSubscribe();
        finish();
        if (AppUtil.isTablet()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        long loadingEndTime = System.currentTimeMillis();
        long loadingTime = loadingEndTime - mConfigSP.getLong("LoadingStartTime", 0);
        LogUtil.i(TAG, "intent:loadingTime=" + loadingTime + "ms");
        AppUtil.trackViewLog(426, "LT", "", loadingTime + "");
        StatService.onEventEnd(getApplicationContext(), "LoadingTime", "LT_" + AppUtil.getLanguageType() + "_Android");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 极光推送onResume
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 极光推送onPause
        JPushInterface.onPause(this);

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
