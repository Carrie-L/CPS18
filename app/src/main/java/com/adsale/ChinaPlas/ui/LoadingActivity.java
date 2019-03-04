package com.adsale.ChinaPlas.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.LoadTransferTempDB;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ApkVersion;
import com.adsale.ChinaPlas.databinding.ActivityLoadingBinding;
import com.adsale.ChinaPlas.databinding.ViewDialogProgressBinding;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.LoadingReceiver;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.utils.ReleaseHelper;
import com.adsale.ChinaPlas.viewmodel.LoadingViewModel;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.adsale.ChinaPlas.App.mSP_Config;
import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.Constant.APK_NAME;
import static com.adsale.ChinaPlas.utils.Constant.SCREEN_HEIGHT;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_READ_PHONE_STATE;
import static com.adsale.ChinaPlas.viewmodel.LoadingViewModel.STATUS_DOWNLOADING;
import static com.adsale.ChinaPlas.viewmodel.LoadingViewModel.STATUS_ERROR;
import static com.adsale.ChinaPlas.viewmodel.LoadingViewModel.STATUS_FINISHED;
import static com.adsale.ChinaPlas.viewmodel.LoadingViewModel.STATUS_NS;

/**
 * ❥❥ 更新apk版本：
 * 1. 请求服务器版本号，与本地版本号比较，如果不相等，Step2, 如果相等, Step3，
 * 2. 对话框提示是否下载更新apk，true，Step4; false，Step3
 * 3. continue
 * 4. 下载服务器apk包，安装新的apk，更新本地apk版本。
 */
public class LoadingActivity extends AppCompatActivity implements LoadingReceiver.OnLoadFinishListener, OnIntentListener {
    private static final String TAG = "LoadingActivity";
    private SharedPreferences mConfigSP;
    private boolean isFirstRunning;
    private LoadingViewModel mLoadingModel;
    private LoadingReceiver mReceiver;
    private ActivityLoadingBinding binding;
    private boolean isTablet;
    private ProgressBar loadingProgress;

    /**
     * 在迁移数据库（备份）
     */
    public final ObservableBoolean isMigrateData = new ObservableBoolean(false);
    public final ObservableBoolean isShowLanguage = new ObservableBoolean(false);

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
        mLoadingModel = new LoadingViewModel(getApplicationContext(), this);
        binding.setLoadingModel(mLoadingModel);
        binding.setAty(this);
        binding.executePendingBindings();

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        LogUtil.i(TAG, "memoryClass=" + memoryClass);

        mConfigSP.edit().putBoolean("M1ShowFinish", false)
                .putBoolean("txtDownFinish", false)
                .putBoolean("webServicesDownFinish", false)
                .putBoolean("apkDialogFinish", false)
                .putString("M1ClickId", "")
                .apply();
        isFirstRunning = AppUtil.isFirstRunning();
        mConfigSP.edit().putBoolean("isFirstGetMaster", isFirstRunning).apply();
        LogUtil.i(TAG, "== isFirstRunning == " + isFirstRunning);

        mLoadingModel.initM1(binding.tvSkip, binding.framelayout);
        registerBroadcastReceiver();
        hideNavBar();
        getAppVersion();

        /*  当数据库版本增加时，升级数据库 */
        boolean needUpdateDB = mConfigSP.getBoolean(Constant.DB_UPGRADE, false);
        LogUtil.i(TAG, "needUpdateDB=" + needUpdateDB);
        if (needUpdateDB && AppUtil.getLocalApkVersion() > ReleaseHelper.NEW_APP_FIRST_VERSION) {
            isMigrateData.set(true);
            isShowLanguage.set(false);

            final LoadTransferTempDB transferTempDB = LoadTransferTempDB.getInstance(getApplicationContext());
            transferTempDB.processTempData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            LogUtil.i(TAG, "processTempData: aBoolean=" + aBoolean);
                            App.mSP_Config.edit().putBoolean(Constant.DB_UPGRADE, false).apply();
                            LoadTransferTempDB.destroyInstance();
                            isMigrateData.set(false);
                            Toast.makeText(getApplicationContext(), getString(R.string.data_bk_success), Toast.LENGTH_SHORT).show();

                            initData();
                        }
                    });
        } else {
            initData();
        }

//        getDeviceInfo();
//        isShowLanguage.set(true);


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

    /**
     * 语言按钮的位置 设置在白线上方
     */
    private void setLanguageLocation(int sh) {
        if (AppUtil.isTablet()) {
            return;
        }
//        int sh = mSP_Config.getInt(SCREEN_HEIGHT, 0);
        int statusHeight = mSP_Config.getInt("STATUS_HEIGHT", 0);
        int h = (820 * sh) / 1334;
        LogUtil.i(TAG, "sh=" + sh);
        LogUtil.i(TAG, "h=" + h);
        LogUtil.i(TAG, "statusHeight=" + statusHeight);
        LogUtil.i(TAG, "px2dip=" + DisplayUtil.px2dip(getApplicationContext(), statusHeight));
        LogUtil.i(TAG, "dip2px=" + DisplayUtil.dip2px(getApplicationContext(), statusHeight));

        h = h - statusHeight;

        LinearLayout lyLanguage = binding.lyLanguage;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(640, (65 * h) / 820);
        params.topMargin = h;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lyLanguage.setLayoutParams(params);
//        LogUtil.i(TAG, "sh=" + sh + ", topMargin=" + h + ", rate = " + (820 / h) + ", y=" + ((65 * h) / 820));
        LogUtil.i(TAG, "statusHeight=" + statusHeight + ", 2 =" + DisplayUtil.px2dip(getApplicationContext(), statusHeight));

//        ImageView ivBg = binding.ivBg;
//        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading).copy(Bitmap.Config.ARGB_4444, true);
//        Canvas canvas =new Canvas(bgBitmap);
//        Bitmap scBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bttn_simp).copy(Bitmap.Config.ARGB_4444, true);
//        canvas.drawBitmap(scBitmap,50,h,null);
//        ivBg.setImageBitmap(bgBitmap);


    }

    private void initData() {
        LogUtil.i(TAG, "~~~~~~~~ initData ~~~~~~~~~~~~~~");
        if (isFirstRunning) {
            isShowLanguage.set(true);
            loadingProgress.setVisibility(View.INVISIBLE);
            AppUtil.setLogin(false);
            setDeviceType();
            getDeviceInfo();
        } else {
            isShowLanguage.set(false);
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
            LogUtil.i(TAG, "_____________________requestListener: onLoadFailed :" + e.getMessage());
            App.isNetworkAvailable = false;
            mLoadingModel.intent();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            LogUtil.i(TAG, "_____________________requestListener: onResourceReady ");
            App.isNetworkAvailable = true;
            mLoadingModel.checkApkVersion();
            if (isFirstRunning) {
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
        isShowLanguage.set(false);
        mLoadingModel.showProgressBar.set(true);
        AppUtil.setNotFirstRunning();
        requestPermission();
        initJpushAlias(language);
        isNetwork();
    }

    private void hideNavBar() {
        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);/* 20180126添加  */
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        }


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

        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        mConfigSP.edit().putInt(Constant.SCREEN_WIDTH, width).putInt(SCREEN_HEIGHT, height)
                .putInt(Constant.DISPLAY_HEIGHT, displayHeight)
                .putInt("STATUS_HEIGHT", statusBarHeight)
                .apply();

        setLanguageLocation(height);
    }

    private void lhp() {

    }

    private void initJpushAlias(int language) {
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
            App.mSP_Config.edit().putString("AppVersion", info.versionName).putInt("LocalVersionCode", info.versionCode).apply();
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
        mConfigSP.edit().putBoolean("M1ShowFinish", false).putBoolean("txtDownFinish", false).putBoolean("webServicesDownFinish", false).putBoolean("apkDialogFinish", false).putString("M1ClickId", "").apply();
        LogUtil.i(TAG, "isTablet=" + isTablet);

        if (!companyId.isEmpty()) {
            Intent i = new Intent(this, ExhibitorDetailActivity.class);
            i.putExtra(Constant.COMPANY_ID, companyId);
            i.putExtra("FromM1", true);
            i.putExtra("from", "D1");
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

    AlertDialog dialog = null;

    private int status = 0;

    private void D1Intent(String companyId) {
        if (TextUtils.isEmpty(companyId)) {
            return;
        }
        ExhibitorRepository repository = new ExhibitorRepository();
        if (!repository.isExhibitorIDExists(companyId)) {
            repository = null;
            LogUtil.e(TAG, "展商資料庫裏滅有這家展商。");
            Toast.makeText(getApplicationContext(), getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("M1ShowFinish", true).putString("M1ClickId", companyId).apply();
        sendBroadcast(intent);
    }

    @Override
    public <T> void onIntent(final T entity, Class toCls) {
        // ♦ on D1 click intent
        if (toCls != null && toCls.getSimpleName().equals("ExhibitorDetailActivity")) {
            D1Intent((String) entity);
        }
        // ♦ download  install apk
        else if (toCls == null) { // 为空时，传递 下载状态
            status = (Integer) entity;
        } else { // 不为空的时候，传递 apkVersion
            status = mLoadingModel.downloadStatus.get();
        }
        LogUtil.i(TAG, "onIntent=" + status);

        if (status == STATUS_DOWNLOADING) { // 显示下载进度条对话框
            LogUtil.i(TAG, "onIntent STATUS_DOWNLOADING");
            View progressView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_dialog_progress, null);
            ViewDialogProgressBinding binding = DataBindingUtil.bind(progressView);
            binding.setModel(mLoadingModel);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(progressView);
            builder.setCancelable(false);
            dialog = builder.show();
        } else if (status == STATUS_FINISHED) { // 下载完成，安装apk
            LogUtil.i(TAG, "onIntent STATUS_FINISHED");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            installAPK();
        } else if (status == STATUS_NS) { // 非强制更新，则弹出对话框
            AppUtil.switchLanguage(getApplicationContext(), AppUtil.getCurLanguage());
            ApkVersion apkVersion = (ApkVersion) entity;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (apkVersion.updateMsg.length > 0) {
                builder.setMessage(apkVersion.getUpdateMsg());
                builder.setTitle(getString(R.string.apk_update_title));
            } else {
                builder.setMessage(getString(R.string.apk_update_title));
            }
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mLoadingModel.sendApkDownBrocastMessage(false);
                    mLoadingModel.downloadApk();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mLoadingModel.sendApkDownBrocastMessage(true);
                }
            }).show();
        }


    }

    private void installAPK() {
        File apkFile = new File(String.format(App.filesDir + "apk/" + APK_NAME, AppUtil.getServiceApkVersionCode()));
//        File apkFile = new File(App.filesDir +  APK_NAME);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), "com.adsale.ChinaPlas.fileprovider", apkFile);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile),
                        "application/vnd.android.package-archive");
            }
            startActivity(intent);
//            apkFile.delete();
        }
    }

    @Override
    public void onBackPressed() {


        if (status == STATUS_DOWNLOADING) {
            LogUtil.i(TAG, "下载中，不可返回");
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && status == STATUS_DOWNLOADING || super.onKeyDown(keyCode, event);
    }


}
