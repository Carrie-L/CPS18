package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableBoolean;
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
import com.adsale.ChinaPlas.data.model.LoadUrl;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.NewTecHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    private Disposable mTxtDisposable, mWCDisposable, mAdDisposable;
    private SQLiteDatabase mTempDB;
    private boolean isM1Showing = false;
    private ProgressBar progressBar;

    public LoadingViewModel(Context mContext, ProgressBar progressBar) {
        this.mContext = mContext;
        this.progressBar = progressBar;
        mConfigSP = mContext.getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mLoadRepository = LoadRepository.getInstance(mContext);
    }

    public final ObservableBoolean showProgressBar = new ObservableBoolean();

    public void run(boolean isFirstRunning) {
//        boolean isNetworkAvailable = App.isNetworkAvailable;
//        LogUtil.e(TAG, "????? isNetworkAvailable=" + isNetworkAvailable);
        setupDownload();
        loadingData();
//        downNewTecZip();
        if (!isFirstRunning) {
            getUpdateInfo();
        }
    }

    public void intent() {
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("webServicesDownFinish", true).putBoolean("txtDownFinish", true).apply();
        mContext.sendBroadcast(intent);
        showM1();
    }

    private void setupDownload() {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(LoadingClient.class, NetWorkHelper.DOWNLOAD_PATH);
        }
    }

    private void downNewTecZip() {
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
                        mWCDisposable = d;
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

//    private Observable<Boolean> downIconPic(final LoadUrl entity) {
//        return mClient.downIcons(entity.urlName)
//                .subscribeOn(Schedulers.io())
//                .map(new Function<Response<ResponseBody>, Boolean>() {
//                    @Override
//                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
//                        ResponseBody body = responseBodyResponse.body();
//                        if (body != null) {
//                            FileOutputStream fos = new FileOutputStream(new File(mMainIconDir.concat(AppUtil.subStringLast(entity.urlName, "/"))));
//                            fos.write(body.bytes());
//                            fos.close();
//                            body.close();
//                        }
//                        return true;
//                    }
//                });
//    }

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

                                //把ConcurrentEvent.txt手動加上去。。。
                                UpdateCenter updateCenter = new UpdateCenter();
                                updateCenter.setScanFile("CurrentEvents.txt");
                                scanFiles.add(updateCenter);

                                logListString(scanFiles);
                            }
                            result = null;
                            body.close();
                        }
                        return Observable.fromIterable(scanFiles);
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
                        mTxtDisposable = d;
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

    /**
     * 根据解析出来的ScanFileJson数据，判断哪些txt有更新，以及下载txt
     */
    private Observable<String> downTxt(UpdateCenter updateCenter) {
        final String fileName = updateCenter.getScanFile().trim();
        LogUtil.i(TAG, "downTxt: fileName=" + fileName);

        //比较最后更新时间
        if (isOneOfFiveTxt(fileName)) {
            LogUtil.i(TAG, "~~isOneOfFiveTxt~~");
            String localUT = mLoadRepository.getLocalTxtLUT(fileName);
            String txtUT = updateCenter.FPDate.compareTo(updateCenter.FUDate) > 0 ? updateCenter.FPDate : updateCenter.FUDate;
            int result = txtUT.compareTo(localUT);
            if (result > 0) {// txtUT > localUT, update
                LogUtil.e(TAG, fileName + " has update!! " + " @@@ compare update time: result= " + result + ", localUT=" + localUT + ", txtUT=" + AppUtil.GMT2UTC(txtUT));
                updateCenter.setUCId();
                updateCenter.setStatus(0);
                updateCenter.setLUT(AppUtil.GMT2UTC(txtUT));
                mLoadRepository.updateLocalLUT(updateCenter);
                return getTxt(fileName);// has update, so download
            }
            LogUtil.i(TAG, "~~isOneOfFiveTxt, but no update.~~" + AppUtil.GMT2UTC(txtUT));
            return Observable.just(fileName);// no update
        }
        LogUtil.i(TAG, "!!~~isOneOfFiveTxt~~!!");
        return getTxt(fileName);// has network, download others txt. always download
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
                    mAdDisposable.dispose();//停止倒计时
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

    /**
     * 当数据库版本增加时，升级数据库
     */
    public void upgradeDB() {
        boolean needUpdateDB = mConfigSP.getBoolean(Constant.DB_UPGRADE, false);
        LogUtil.i(TAG, "needUpdateDB=" + needUpdateDB);
        if (needUpdateDB) {
            createTempDB();
            LogUtil.e(TAG, "① 将数据存入临时表");
            LoadTransferTempDB transferTempDB = LoadTransferTempDB.getInstance(mContext, mTempDB);
            transferTempDB.saveTempData();
            transferTempDB.deleteOldDB();
            LogUtil.e(TAG, "② 导入新数据库");
            transferTempDB.importNewDB();
            LogUtil.e(TAG, "③ 从临时表中取出数据，插入新数据库中:");
            transferTempDB.insertNewTable();
            LogUtil.e(TAG, "④ 清空临时表");
            transferTempDB.clearTemp();
            mTempDB.close();
            App.mSP_Config.edit().putBoolean(Constant.DB_UPGRADE, false).apply();
        }
    }

    private void createTempDB() {
        LogUtil.i(TAG, "createTempDB");
        TempOpenHelper mTempOpenHelper = new TempOpenHelper(mContext, "temp.db", null, 1);
        mTempOpenHelper.getReadableDatabase();
        mTempDB = mTempOpenHelper.getWritableDatabase();
    }

    public void unSubscribe() {
        dispose(mWCDisposable);
        dispose(mTxtDisposable);
        dispose(mAdDisposable);
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
