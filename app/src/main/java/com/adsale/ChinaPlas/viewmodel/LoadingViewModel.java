package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.data.ContentHandler;
import com.adsale.ChinaPlas.data.LoadRepository;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.adsale.ChinaPlas.data.model.LoadUrl;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.LoadingReceiver;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.adsale.ChinaPlas.App.mSP_Config;
import static com.adsale.ChinaPlas.App.rootDir;
import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.AppUtil.getCurrentDate;
import static com.adsale.ChinaPlas.utils.AppUtil.logListString;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

/**
 * Created by Carrie on 2017/9/8.
 */

public class LoadingViewModel implements ADHelper.OnM1ClickListener {
    private static final String TAG = "LoadingViewModel";
    private static final String AD_TXT = "advertisement.txt";  // normal
    private static final Integer AD_COUNT_DOWN_TIMES = 3;  // 倒计时几秒
    private Context mContext;

    public final ObservableBoolean showLangBtn = new ObservableBoolean(false);
    public final ObservableBoolean isShowM1 = new ObservableBoolean(false);
    private final SharedPreferences mConfigSP;

    private LoadRepository mLoadRepository;
    private LoadingClient mClient;
    private String mWebContentDir;
    private String mMainIconDir;
    private ArrayList<WebContent> webContents;
    private ArrayList<MainIcon> mainIcons;
    private adAdvertisementObj adObj;
    private ADHelper mAdHelper;
    private Disposable mTxtDisposable, mWCDisposable, mAdDisposable;

    public LoadingViewModel(Context mContext) {
        this.mContext = mContext;
        mConfigSP = mContext.getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mLoadRepository = LoadRepository.getInstance(mContext);
    }

    public void startDownload() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(NetWorkHelper.DOWNLOAD_PATH)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(httpClient.build()).build();
        mClient = retrofit.create(LoadingClient.class);
    }

    //第一次运行，则获取所有数据。然后将本地数据表的数据清空，插入新的数据。
    public void loadingData() {
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
                        if (loadUrl.urlName.endsWith("zip")) {
                            return downZip(loadUrl.urlName, loadUrl.dirName);
                        } else {
                            return downIconPic(loadUrl);
                        }
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

    private ArrayList<LoadUrl> processUrls() {
        int iconSize = mainIcons.size();
        int wcSize = webContents.size();
        ArrayList<LoadUrl> urls = new ArrayList<>();
        LoadUrl loadUrl;
        MainIcon mainIcon;
        WebContent webContent;
        for (int i = 0; i < iconSize; i++) {
            mainIcon = mainIcons.get(i);
            loadUrl = new LoadUrl(mainIcon.getIcon(), mainIcon.getIconID());
            urls.add(loadUrl);
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

    private Observable<Boolean> downIconPic(final LoadUrl entity) {
        return mClient.downWebContent(entity.urlName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            FileOutputStream fos = new FileOutputStream(new File(mMainIconDir + entity.urlName));
                            fos.write(body.bytes());
                            fos.close();
                            body.close();
                        }
                        return true;
                    }
                });
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
                        if (s.toLowerCase().equals(AD_TXT) && adObj != null) {
                            LogUtil.i(TAG, "* 显示M1广告 * ");
                            showM1();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

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
        //比较最后更新时间
        if (isOneOfFiveTxt(fileName)) {
            String localUT = mLoadRepository.getLocalTxtLUT(fileName);
            String txtUT = updateCenter.FPDate.compareTo(updateCenter.FUDate) > 0 ? updateCenter.FPDate : updateCenter.FUDate;
            int result = txtUT.compareTo(localUT);
            if (result > 0) {// txtUT > localUT, update
                LogUtil.e(TAG, fileName + " has update!! " + " @@@ compare update time: result= " + result + ", localUT=" + localUT + ", txtUT=" + txtUT);
                updateCenter.setUCId();
                updateCenter.setStatus(0);
                updateCenter.setLUT(txtUT);
                mLoadRepository.updateLocalLUT(updateCenter);
                return getTxt(fileName);// has update, so download
            }
            return Observable.just(fileName);// no update
        }
        getTxt(AD_TXT);
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
        return mClient.downTxt(fileName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, String>() {
                    @Override
                    public String apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        long startTime = System.currentTimeMillis();
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            if (fileName.trim().equals(AD_TXT)) {
                                adObj = new Gson().fromJson(body.string(), adAdvertisementObj.class);
                            }
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

    public void showM1() {
        mAdHelper = ADHelper.getInstance(mContext);
        adObj = mAdHelper.getAdObj();
//        if (adObj == null) {
//            LogUtil.e(TAG, "adObj == null ,so parse it");
//            adObj = mAdHelper.getAdObj();
//        }
        mAdHelper.setAdObj(adObj);
        if (isAdOpen() && mAdHelper.isM1Open()) {
            isShowM1.set(true);
            mAdHelper.setOnM1ClickListener(this);
            List<View> pagers = mAdHelper.generateM1View(viewIndicator);
            autoChangeViewPager.setAdapter(new AdViewPagerAdapter(pagers));
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
//                            tvSkip.setText(Html.fromHtml(String.format(Locale.getDefault(), mContext.getString(R.string.skip), 0)));
                            LogUtil.i(TAG, "----------广告倒计时结束------------");

                            Intent intent = new Intent(LOADING_ACTION);
                            mSP_Config.edit().putBoolean("M1ShowFinish", true).apply();
                            mContext.sendBroadcast(intent);
                            LogUtil.i(TAG, "))))) m1 end, send broadcast");
                        }
                    });

        }
    }

    private boolean isAdOpen() {
        String todayDate = getCurrentDate();
        String adStartTime = adObj.Common.time.split("-")[0];
        String adEndTime = adObj.Common.time.split("-")[1];
        LogUtil.i(TAG, "todayDate=" + todayDate + ".adStartTime=" + adStartTime + ".adEndTime=" + adEndTime);
        int c1 = todayDate.compareTo(adStartTime);
        int c2 = todayDate.compareTo(adEndTime);
        LogUtil.i(TAG, "c1=" + c1 + ".c2=" + c2);
        if (todayDate.compareTo(adStartTime) > 0 && todayDate.compareTo(adEndTime) < 0) {
            mSP_Config.edit().putBoolean(Constant.IS_AD_OPEN, true).apply();
            LogUtil.e(TAG, "~~~ad opening~~");
            return true;
        }
        LogUtil.e(TAG, "~~~ad closed~~");
        return false;


    }


    /**
     * 当数据库版本增加时，升级数据库
     */
    private void upgradeDB() {
        boolean needUpdateDB = mConfigSP.getBoolean(Constant.DB_UPGRADE, false);
        if (needUpdateDB) {
            LogUtil.i(TAG, "①将数据存入临时表");
            LogUtil.i(TAG, "② 导入新数据库");
            LogUtil.i(TAG, "③ 从临时表中取出数据，插入新数据库中:");
            LogUtil.i(TAG, "④ 清空临时表");
            mConfigSP.edit().putBoolean(Constant.DB_UPGRADE, false).apply();
        }
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
        Intent intent = new Intent(LOADING_ACTION);
//        intent.setAction(LOADING_ACTION);
        mSP_Config.edit().putBoolean("M1ShowFinish", true).putString("M1ClickId", companyId).apply();
        mContext.sendBroadcast(intent);

        LogUtil.i(TAG, "))))) onClick, send broadcast");
        if (mAdDisposable != null && !mAdDisposable.isDisposed()) {
            mAdDisposable.dispose();//停止倒计时
        }
    }

    public void onM1Click(String companyId) {

    }
}
