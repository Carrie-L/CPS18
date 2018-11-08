package com.adsale.ChinaPlas.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableBoolean;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.TempOpenHelper;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.data.ContentHandler;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.LoadRepository;
import com.adsale.ChinaPlas.data.LoadTransferTempDB;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ApkVersion;
import com.adsale.ChinaPlas.data.model.LoadUrl;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.NewTecHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.FileUtils;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.adsale.ChinaPlas.App.mSP_Config;
import static com.adsale.ChinaPlas.App.rootDir;
import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.AppUtil.logListString;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

/**
 * Created by Carrie on 2017/9/8.
 * Loading
 */

public class LoadingViewModel implements ADHelper.OnM1ClickListener {
    private static final String TAG = "LoadingViewModel";
    private static final String AD_TXT = "advertisement.txt";  // normal
    private static final Integer AD_COUNT_DOWN_TIMES = 3;  // 倒计时几秒
    private Context mContext;

    //    public final ObservableBoolean showLangBtn = new ObservableBoolean(false);
    public final ObservableBoolean isShowM1 = new ObservableBoolean(false);
    private final SharedPreferences mConfigSP;

    private LoadRepository mLoadRepository;
    private LoadingClient mClient;
    private String mWebContentDir;
    private String mMainIconDir;
    private ArrayList<WebContent> webContents;
    private ArrayList<MainIcon> mainIcons;
    private adAdvertisementObj adObj;
    private Disposable mAdDisposable;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private SQLiteDatabase mTempDB;
    private boolean isM1Showing = false;

    private OnIntentListener mIntentListener;
    private int localVersion;

    public LoadingViewModel(Context mContext, OnIntentListener listener) {
        this.mContext = mContext;
        this.mIntentListener = listener;
        mConfigSP = mContext.getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mLoadRepository = LoadRepository.getInstance(mContext);
    }

    public final ObservableBoolean showProgressBar = new ObservableBoolean();

    public void run(boolean isFirstRunning) {
        setupDownload();
//        loadingData();
        if (!isFirstRunning) {
//            getUpdateInfo();
        }

        showM1();
    }

    /**
     * 本地app的version code (info.getVersionCode()) 和存储在sp中的service version code 比较。
     * 如果 SVC > AVC，说明有更新，直接弹出对话框；
     * 否则（SVC=0 或 SVC == AVC），都下载apkVersion.txt比较version code.
     * <p>
     * ❤ apkDialogFinish ：为了避免弹出对话框时，还没有点击就已经进入主界面，在 LoadingReceiver 的判断中加入此字段，
     * 如果对话框没有点击，则一直停留在对话框界面（false）；如果点击了[NO],则设为true，通知LoadingReceiver；
     * 如果点击[YES],跳转到网页download，退出APP; 如果没有更新(vc相等),则设为true.
     */
    public void checkApkVersion() {
        localVersion = AppUtil.getLocalApkVersion();
        int spServiceVersionCode = AppUtil.getServiceApkVersionCode();
        LogUtil.i(TAG, "version code 》 app:" + localVersion + ",spServiceVersionCode:" + spServiceVersionCode);
        downloadApkVersionTxt();

//        if (spServiceVersionCode > localVersion) {
//            LogUtil.i(TAG, "有更新，弹出对话框");
//            // has update, dialog
//            mIntentListener.onIntent(null, null);
//        } else {
//            LogUtil.i(TAG, "下载ApkVersion.txt");
//            downloadApkVersionTxt();
//        }
    }

    private void downloadApkVersionTxt() {
        setupDownload();
        mClient.download(NetWorkHelper.APK_VERSION_TXT_URL)
                .map(new Function<ResponseBody, Boolean>() {

                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        if (responseBody != null) {
                            String content = responseBody.string();
                            final ApkVersion apkVersion = Parser.parseJson(ApkVersion.class, content);
                            responseBody.close();
                            LogUtil.i(TAG, "version code 》 app:" + localVersion + ",service:" + apkVersion.versionCode);

                            if (apkVersion.versionCode > localVersion) {
                                AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.link);
                                return true;
                            } else {
                                mSP_Config.edit().putBoolean("apkDialogFinish", true).apply();
                            }
                        }
                        return false;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(final Boolean value) {
                        LogUtil.i(TAG, "is apk has update? " + value);
                        if (value) {
                            mIntentListener.onIntent(true, null);
                        }else{
//                            mSP_Config.edit().putBoolean("apkDialogFinish", true).apply();
//                            Intent intent0 = new Intent(LOADING_ACTION);
//                            mContext.sendBroadcast(intent0);
                            mIntentListener.onIntent(false, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, " downloadApkVersionTxt error" + e.getMessage());
                        Intent intent = new Intent(LOADING_ACTION);
                        mSP_Config.edit().putBoolean("apkDialogFinish", true).apply();
                        mContext.sendBroadcast(intent);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

//    private void updateApk() {
//        if (new File(apkPath).exists()) {
//            // 安装apk
//            Intent install = new Intent(Intent.ACTION_VIEW);
//            install.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
//            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(install);
//        }
//    }

    public void intent() {
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("webServicesDownFinish", true).putBoolean("txtDownFinish", true).putBoolean("apkDialogFinish", true).apply();
        mContext.sendBroadcast(intent);
        showM1();
    }

    private void setupDownload() {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(LoadingClient.class, NetWorkHelper.DOWNLOAD_PATH);
        }
    }

    private void downNewTecZip() {
        LogUtil.i(TAG,"downNewTecZip");
        NewTecHelper newTecHelper = new NewTecHelper();
        newTecHelper.init();
        newTecHelper.downNewTecZip(mClient);
    }

    //第一次运行，则获取所有数据。然后将本地数据表的数据清空，插入新的数据。
    private void loadingData() {
        mWebContentDir = rootDir + "WebContent/";
        mMainIconDir = rootDir + "MainIcon/";
        createFile(mWebContentDir);
        createFile(mMainIconDir);

        webContents = new ArrayList<>();
        mainIcons = new ArrayList<>();

        mClient.getMaster(NetWorkHelper.getMasterRequestBody())
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            ContentHandler contentHandler = ContentHandler.getInstance(mLoadRepository);
                            contentHandler.parseXmlWithSAX(body.string());
                            mainIcons = contentHandler.mMainIcons;
                            webContents = contentHandler.mWebContents;
                            body.close();
                        }
                        return true;
                    }
                })
                .flatMap(new Function<Boolean, Observable<LoadUrl>>() {
                    @Override
                    public Observable<LoadUrl> apply(@NonNull Boolean aBoolean) throws Exception {
                        return Observable.fromIterable(processUrls());
                    }
                })
                .flatMap(new Function<LoadUrl, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(@NonNull LoadUrl loadUrl) throws Exception {
                        return downZip(loadUrl.urlName, loadUrl.dirName);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {

                    private long mStartTime, mEndTime;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mStartTime = System.currentTimeMillis();
//                        mWCDisposable = d;
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.e(TAG, "getMaster -- onError=" + e.getMessage());/* todo SSL handshake timed out */
                        Intent intent = new Intent(LOADING_ACTION);
                        mSP_Config.edit().putBoolean("webServicesDownFinish", true).apply();
                        mContext.sendBroadcast(intent);
                        LogUtil.e(TAG, "))))) down wc error, send broadcast");
                    }

                    @Override
                    public void onComplete() {//只执行一次,在最后
                        mEndTime = System.currentTimeMillis();

                        Intent intent = new Intent(LOADING_ACTION);
                        mSP_Config.edit().putBoolean("webServicesDownFinish", true).apply();
                        mContext.sendBroadcast(intent);

                        LogUtil.i(TAG, "))))) down wc end, send broadcast");
                        LogUtil.i(TAG, "wc onComplete----spend time= " + (mEndTime - mStartTime) + "ms");//2073ms
                    }
                });
    }

    /**
     * 将MainIcon \ WebContent 的 zip、icon 下载链接都整合到一起，便于用Retrofit+RxJava下载
     *
     * @return ArrayList
     */
    private ArrayList<LoadUrl> processUrls() {
        int iconSize = mainIcons.size();
        int wcSize = webContents.size();
        ArrayList<LoadUrl> urls = new ArrayList<>();
        LoadUrl loadUrl = new LoadUrl();
        MainIcon mainIcon;
        WebContent webContent;

        for (int i = 0; i < iconSize; i++) {
            mainIcon = mainIcons.get(i);
            // zips
            if (mainIcon.getCFile() != null && !mainIcon.getCFile().trim().isEmpty()) {
                loadUrl = new LoadUrl(mainIcon.getCFile(), mainIcon.getIconID());
                urls.add(loadUrl);
            }
        }

        for (int i = 0; i < wcSize; i++) {
            webContent = webContents.get(i);
            if (webContent.getCType() == 1) {
                loadUrl = new LoadUrl(webContent.getCFile(), webContent.getPageId());
                urls.add(loadUrl);
            }
        }

        LogUtil.i(TAG, "urls=" + urls.size() + "," + urls.toString());
        return urls;
    }

    private Observable<Boolean> downZip(final String cFile, final String iconId) {
        return mClient.downWebContent(cFile)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            FileUtil.unpackZip(cFile, body.byteStream(), mWebContentDir + iconId + "/");
                            body.close();
                        }
                        return true;
                    }
                });
    }

    /**
     * 根据更新信息下载txt
     */
    public void getUpdateInfo() {
        setupDownload();
        mClient.getScanFile(NetWorkHelper.getScanRequestBody())
                .flatMap(new Function<Response<ResponseBody>, Observable<UpdateCenter>>() {// getScanFilesJson
                    @Override
                    public Observable<UpdateCenter> apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        ArrayList<UpdateCenter> scanFiles = new ArrayList<>();
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            String result = body.string();
                            if (result != null && !result.isEmpty()) {
                                result = AppUtil.subStringLastFront1(result, '[', ']');
                                Type listType = new TypeToken<ArrayList<UpdateCenter>>() {
                                }.getType();
                                scanFiles = new Gson().fromJson(result, listType);
//                                logListString(scanFiles);
                            }
                            body.close();
                        }
                        return Observable.fromIterable(UpdateCenter.getUpdateFiles(scanFiles, mLoadRepository));
                    }
                })
                .flatMap(new Function<UpdateCenter, Observable<String>>() {
                    @Override
                    public Observable<String> apply(@NonNull UpdateCenter updateCenter) throws Exception {
                        return downTxt(updateCenter);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        LogUtil.i(TAG, "getScanFile: onNext = " + s);
                        if (s.toLowerCase().equals(AD_TXT)) {
                            LogUtil.i(TAG, "* 是否显示M1广告 * ");
                            showM1();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.e(TAG, "getScanFile: onError = " + e.getMessage());
                        if (!isM1Showing) {
                            showM1();
                        }
                        Intent intent = new Intent(LOADING_ACTION);
                        mSP_Config.edit().putBoolean("txtDownFinish", true).apply();
                        mContext.sendBroadcast(intent);
                        LogUtil.i(TAG, "))))) down txt error, send broadcast");
                    }

                    @Override
                    public void onComplete() {
                        Intent intent = new Intent(LOADING_ACTION);
                        mSP_Config.edit().putBoolean("txtDownFinish", true).apply();
                        mContext.sendBroadcast(intent);
                        LogUtil.i(TAG, "))))) down txt end, send broadcast");
                    }
                });
    }

    private Observable<String> downTxt(UpdateCenter updateCenter) {
        final String fileName = updateCenter.getScanFile().trim();
        LogUtil.i(TAG, "downTxt: fileName=" + fileName);

        if (updateCenter.getScanFile().equals(Constant.TXT_NEW_TEC)) {
            LogUtil.i(TAG, "downTxt: NewTechInfo 有更新，下载zip包");
            downNewTecZip();
        }
        return getTxt(fileName);// has network, download txt.
    }

    /**
     * txt是以下5个txt名称之一，则返回true，检查更新，有更新才下载。否则返回false
     */
    private boolean isOneOfFiveTxt(String fileName) {
        return fileName.equals("ExhibitorInfo.txt") || fileName.equals("FloorPlan.txt") || fileName.equals("SeminarInfo.txt")
                || fileName.equals("appContents.txt") || fileName.equals("Travelnfo.txt");
    }

    /**
     * 下载单个txt，并写入内存data/.../files中
     */
    private Observable<String> getTxt(final String fileName) {
        LogUtil.e(TAG, "getTxt:::fileName=" + fileName);
        return mClient.downTxt(fileName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, String>() {
                    @Override
                    public String apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        long startTime = System.currentTimeMillis();
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
//                            if (fileName.trim().equals(AD_TXT)) {
//                                adObj = new Gson().fromJson(body.string(), adAdvertisementObj.class);
//                            }
                            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                            fos.write(body.bytes());
                            body.close();
                            fos.close();
                        }
                        long endTime = System.currentTimeMillis();
                        LogUtil.i(TAG, "write txt spend time：" + fileName + ", " + (endTime - startTime) + " ms");
                        return fileName;
                    }
                });
    }

    private LinearLayout viewIndicator;
    private ViewPager autoChangeViewPager;
    private TextView tvSkip;
    private FrameLayout m1Frame;

    public void initM1(LinearLayout ll, ViewPager pager, TextView tvSkip, FrameLayout m1Frame) {
        viewIndicator = ll;
        autoChangeViewPager = pager;
        this.tvSkip = tvSkip;
        this.m1Frame = m1Frame;
    }

    private void showM1() {
        ADHelper mAdHelper = new ADHelper(mContext);
        adObj = mAdHelper.getAdObj();
        if (mAdHelper.setIsAdOpen() && mAdHelper.isM1Open()) {
            isShowM1.set(true);
            mAdHelper.setOnM1ClickListener(this);
            List<View> pagers = mAdHelper.generateM1View(viewIndicator);
            autoChangeViewPager.setAdapter(new AdViewPagerAdapter(pagers));
            autoChangeViewPager.addOnPageChangeListener(new ViewPageChangeListener());
            tvSkip.setVisibility(View.VISIBLE);
            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LOADING_ACTION);
                    mSP_Config.edit().putBoolean("M1ShowFinish", true).apply();
                    mContext.sendBroadcast(intent);
                    LogUtil.i(TAG, "))))) skip, send broadcast");
                    if (mAdDisposable != null && !mAdDisposable.isDisposed()) {
                        mAdDisposable.dispose();//停止倒计时
                    }
                }
            });

            Observable.interval(1, TimeUnit.SECONDS)
                    .take(AD_COUNT_DOWN_TIMES) // up to 3 items
                    .map(new Function<Long, Long>() {
                        @Override
                        public Long apply(Long v) throws Exception {
                            return AD_COUNT_DOWN_TIMES - v;
                        }
                    })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            isM1Showing = true;
                            mAdDisposable = d;
                            mCompositeDisposable.add(mAdDisposable);
                            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.bottom_to_top);
                            m1Frame.startAnimation(animation);
                        }

                        @Override
                        public void onNext(@NonNull Long aLong) {
                            LogUtil.e(TAG, "count down : aLong=" + aLong);
                            tvSkip.setText(Html.fromHtml(String.format(Locale.getDefault(), mContext.getString(R.string.skip), aLong)));
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            LogUtil.i(TAG, "----------广告倒计时结束------------");
                            Intent intent = new Intent(LOADING_ACTION);
                            mSP_Config.edit().putBoolean("M1ShowFinish", true).apply();
                            mContext.sendBroadcast(intent);
                            LogUtil.i(TAG, "))))) m1 end, send broadcast");
                        }
                    });

        }
    }

    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        // 监听页面改变事件来改变viewIndicator中的指示图片
        @Override
        public void onPageSelected(int arg0) {
            int len = viewIndicator.getChildCount();
            for (int i = 0; i < len; ++i)
                viewIndicator.getChildAt(i).setBackgroundResource(R.drawable.dot_normal);
            viewIndicator.getChildAt(arg0).setBackgroundResource(R.drawable.dot_focused);
        }

    }

    public void unSubscribe() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onClick(String companyId) {
        if (TextUtils.isEmpty(companyId)) {
            return;
        }
        ExhibitorRepository repository = new ExhibitorRepository();
        if (!repository.isExhibitorIDExists(companyId)) {
            repository = null;
            LogUtil.e(TAG, "展商資料庫裏滅有這家展商。");
            return;
        }
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("M1ShowFinish", true).putString("M1ClickId", companyId).apply();
        mContext.sendBroadcast(intent);
        LogUtil.i(TAG, "))))) onClick, send broadcast");
        if (mAdDisposable != null && !mAdDisposable.isDisposed()) {
            mAdDisposable.dispose();//停止倒计时
        }
    }

}
