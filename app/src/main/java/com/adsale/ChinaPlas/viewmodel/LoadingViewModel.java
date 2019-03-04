package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.MainIconTest;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ApkVersion;
import com.adsale.ChinaPlas.helper.CSVHelper;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.helper.NewTecHelper;
import com.adsale.ChinaPlas.helper.ProgressCallback;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.ui.LoadingActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.adsale.ChinaPlas.utils.ReleaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.adsale.ChinaPlas.App.mSP_Config;
import static com.adsale.ChinaPlas.App.mSP_EventPage;
import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.Constant.APK_NAME;
import static com.adsale.ChinaPlas.utils.Constant.TXT_APK_VERSION;
import static com.adsale.ChinaPlas.utils.Constant.TXT_NEW_TEC;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;
import static com.adsale.ChinaPlas.utils.NetWorkHelper.APK_VERSION_TXT_URL;
import static com.adsale.ChinaPlas.utils.NetWorkHelper.TXT_AD_URL;
import static com.adsale.ChinaPlas.utils.NetWorkHelper.TXT_MAIN_URL;
import static com.adsale.ChinaPlas.utils.NetWorkHelper.TXT_NOTIFICATION_URL;

/**
 * Created by Carrie on 2017/9/8.
 * Loading
 */

public class LoadingViewModel {
    private static final String TAG = "LoadingViewModel";
    private static final String AD_TXT = "advertisement.txt";  // normal
    private static final Integer AD_COUNT_DOWN_TIMES = 3;  // 倒计时几秒
    private Context mContext;

    //    public final ObservableBoolean showLangBtn = new ObservableBoolean(false);
    public final ObservableBoolean isShowM1 = new ObservableBoolean(false);
    public final ObservableField<String> D1ImageUrl = new ObservableField<>();

    private LoadingClient mClient;
    private Disposable mAdDisposable;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private boolean isM1Showing = false;

    private OnIntentListener mIntentListener;
    private int localVersion;
    private DownloadClient mProgressClient;
    private EPOHelper mEPOHelper;

    public LoadingViewModel(Context mContext, OnIntentListener listener) {
        this.mContext = mContext;
        this.mIntentListener = listener;
    }

    public final ObservableBoolean showProgressBar = new ObservableBoolean();

    public void run(boolean isFirstRunning) {
        setupDownload();
        downloadTxt();
        getMainIcon();
    }

    private void getMainIcon() {
        final MainIconRepository repository = MainIconRepository.getInstance();
        BmobQuery<MainIcon> query = new BmobQuery<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String localUpdateAt = repository.getLastUpdateDate();
        LogUtil.i(TAG, "getMainIcon: localUpdateAt=" + localUpdateAt);
        if (!TextUtils.isEmpty(localUpdateAt)) {
            Date date = null;
            try {
                date = sdf.parse(localUpdateAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereGreaterThan("updatedAt", new BmobDate(date));
        }
        query.findObjects(new FindListener<MainIcon>() {
            @Override
            public void done(List<MainIcon> list, BmobException e) {
                if (e != null) {
                    LogUtil.i(TAG, "getMainIcon e=" + e.getMessage());
                }
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "updateMainIcon: " + list.size());
                    repository.updateOrInsertMainIcons((ArrayList<MainIcon>) list);
                }
                Intent intent = new Intent(LOADING_ACTION);
                mSP_Config.edit().putBoolean("mainIconFinish", true).apply();
                mContext.sendBroadcast(intent);
            }
        });


    }

    private void getMainIconTest() {
        final MainIconRepository repository = MainIconRepository.getInstance();
        BmobQuery<MainIconTest> query = new BmobQuery<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String localUpdateAt = repository.getLastUpdateDateTest();
        LogUtil.i(TAG, "getMainIcon: localUpdateAt=" + localUpdateAt);
        if (!TextUtils.isEmpty(localUpdateAt)) {
            Date date = null;
            try {
                date = sdf.parse(localUpdateAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereGreaterThan("updatedAt", new BmobDate(date));
        }
        query.findObjects(new FindListener<MainIconTest>() {
            @Override
            public void done(List<MainIconTest> list, BmobException e) {
                if (e != null) {
                    LogUtil.i(TAG, "getMainIconTest e=" + e.getMessage());
                }
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "updateMainIconTest: " + list.size());
                    repository.updateOrInsertMainIconsTest((ArrayList<MainIconTest>) list);
                }
                Intent intent = new Intent(LOADING_ACTION);
                mSP_Config.edit().putBoolean("mainIconFinish", true).apply();
                mContext.sendBroadcast(intent);
            }
        });


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
        downApkTxt();
    }

    private ApkVersion apkVersion;
    private String apkPath = String.format(App.filesDir + "apk/" + APK_NAME, AppUtil.getServiceApkVersionCode());

    private Observable<Boolean> downApk() {
        downloadStatus.set(STATUS_DOWNLOADING);
        if (mProgressClient == null)
            mProgressClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, "https://www.chinaplas.com/", mCallback, this);
        return mProgressClient.largeDownload(apkVersion.apk)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            createFile(App.filesDir + "apk/");
                            LogUtil.i(TAG, "downUrls发射文件:" + apkPath);
//                                                FileUtils.writeFileToRootDir(APK_NAME,responseBody.string());
                            return FileUtil.writeFile(body.byteStream(), apkPath);
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void downloadApk() {
        LogUtil.i(TAG, "---------------downloadApk() ");
        downApk()
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "---------------downloadApk() onSubscribe ");
                        disposable = d;
                        downloadStatus.set(STATUS_DOWNLOADING);
                        mIntentListener.onIntent(STATUS_DOWNLOADING, null);
                    }

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        LogUtil.i(TAG, "down apk onNext: " + success);
                        if (success) {
                            downloadStatus.set(STATUS_FINISHED);
                            LogUtil.i(TAG, "apk下载完成，安装");
                            mIntentListener.onIntent(STATUS_FINISHED, null);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "down apk onError: " + e.getMessage());
                        downloadStatus.set(STATUS_ERROR);
                        sendApkDownBrocastMessage(true);
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "down apk onComplete---------------");
                        disposable.dispose();
                    }
                });
    }

    private void downApkTxt() {
        setupDownload();
        mClient.download(APK_VERSION_TXT_URL)
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        String content = responseBody.string();
                        LogUtil.i(TAG, "downApkTxt content=" + content);
                        apkVersion = Parser.parseJson(ApkVersion.class, content);
                        responseBody.close();
                        return FileUtil.writeFile(content, Constant.TXT_APK_VERSION);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.i(TAG, "=======downApkTxt  onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "=======downApkTxt  onNext " + value);
                        if (value) {
                            down();
                        } else {
                            sendApkDownBrocastMessage(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "=======downApkTxt  onError " + e.getMessage());
                        downloadStatus.set(STATUS_ERROR);
                        sendApkDownBrocastMessage(true);
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "=======downApkTxt  onComplete ");
                        disposable.dispose();
                    }
                });
    }

    private void down() {
        apkVersion = Parser.parseJsonFilesDirFile(ApkVersion.class, TXT_APK_VERSION);
        LogUtil.i(TAG, "version code 》 app:" + localVersion + ", service:" + apkVersion.versionCode + ", localServiceCode=" + AppUtil.getServiceApkVersionCode());
        if ((apkVersion.versionCode == AppUtil.getServiceApkVersionCode())
                && (apkVersion.versionCode > localVersion)
                && new File(apkPath).exists()) { // 有更新，且已經下載了apk，則直接安裝
            LogUtil.i(TAG, "apk存在，直接安装");
            AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.apk);
            downloadStatus.set(STATUS_FINISHED);
            mIntentListener.onIntent(STATUS_FINISHED, null);
        } else if (apkVersion.versionCode > localVersion) {
            sendApkDownBrocastMessage(false);
            AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.apk);
            apkPath = String.format(App.filesDir + "apk/" + APK_NAME, AppUtil.getServiceApkVersionCode());
            if (apkVersion.isForceUpdate) {
                downloadStatus.set(STATUS_DOWNLOADING);
                downloadApk();
            } else {
                downloadStatus.set(STATUS_NS);
                mIntentListener.onIntent(apkVersion, LoadingActivity.class);
            }
            LogUtil.i(TAG, "下載更新apk: " + downloadStatus.get());
        } else {
            sendApkDownBrocastMessage(true);
        }
    }

    private void downloadApkVersionTxt() {
        setupDownload();
        if (mProgressClient == null)
            mProgressClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, "https://www.chinaplas.com/", mCallback, this);
        mClient.download(APK_VERSION_TXT_URL)
                .flatMap(new Function<ResponseBody, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(ResponseBody responseBody) throws Exception {
                        String content = responseBody.string();
                        apkVersion = Parser.parseJson(ApkVersion.class, content);
//                        FileUtil.writeFile(content, Constant.TXT_APK_VERSION);
                        responseBody.close();
                        LogUtil.i(TAG, "version code 》 app:" + localVersion + ", service:" + apkVersion.versionCode + ", localServiceCode=" + AppUtil.getServiceApkVersionCode());

                        if ((apkVersion.versionCode == AppUtil.getServiceApkVersionCode())
                                && (apkVersion.versionCode > localVersion)
                                && new File(apkPath).exists()) { // 有更新，且已經下載了apk，則直接安裝
                            LogUtil.i(TAG, "apk存在，直接安装");
                            AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.apk);
                            downloadStatus.set(STATUS_FINISHED);
                            mIntentListener.onIntent(STATUS_FINISHED, null);
                            return Observable.just(true);
                        } else if (apkVersion.versionCode > localVersion) {
                            downloadStatus.set(STATUS_DOWNLOADING);
                            LogUtil.i(TAG, "下載更新apk: " + downloadStatus.get());
                            sendApkDownBrocastMessage(false);
                            AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.apk);
                            if (apkVersion.isForceUpdate) {
                                return mProgressClient.largeDownload(apkVersion.apk)
                                        .map(new Function<Response<ResponseBody>, Boolean>() {
                                            @Override
                                            public Boolean apply(Response<ResponseBody> response) throws Exception {
                                                ResponseBody body = response.body();
                                                if (body != null) {
                                                    createFile(App.filesDir + "apk/");
                                                    LogUtil.i(TAG, "downUrls发射文件:" + apkPath);
                                                    return FileUtil.writeFile(body.byteStream(), apkPath);
                                                }
                                                return false;
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());
                            } else {
                                downloadStatus.set(STATUS_NS);
                                mIntentListener.onIntent(apkVersion, LoadingActivity.class);
                                return Observable.just(false);
                            }
                        } else {
                            sendApkDownBrocastMessage(true);
                            return Observable.just(false);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "---------------down apk onSubscribe ");
                        disposable = d;
//                        if (downloadStatus.get() == STATUS_DOWNLOADING) {
//                            LogUtil.i(TAG, "---------------down apk onSubscribe STARTING  ");

//                        }
                    }

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        LogUtil.i(TAG, "down apk onNext: " + success);
                        if (success) {
                            downloadStatus.set(STATUS_FINISHED);
                            LogUtil.i(TAG, "apk下载完成，安装");
                            mIntentListener.onIntent(STATUS_FINISHED, null);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "down apk onError: " + e.getMessage());
                        downloadStatus.set(STATUS_ERROR);
                        sendApkDownBrocastMessage(true);
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "down apk onComplete---------------");
                        disposable.dispose();
                    }
                });


//        mClient.download(APK_VERSION_TXT_URL)
//                .map(new Function<ResponseBody, Boolean>() {
//
//
//                    @Override
//                    public Boolean apply(ResponseBody responseBody) throws Exception {
//                        if (responseBody != null) {
//                            String content = responseBody.string();
//                            LogUtil.i(TAG, "content= " + content);
//                            apkVersion = Parser.parseJson(ApkVersion.class, content);
//                            responseBody.close();
//                            LogUtil.i(TAG, "version code 》 app:" + localVersion + ",service:" + apkVersion.versionCode);
//
//                            if (apkVersion.versionCode > localVersion) {
////                                AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.apk);
////                                    AppUtil.setServiceApkVersion(apkVersion.versionCode, apkVersion.link);
//                                LogUtil.i(TAG, "有更新，弹出对话框");
//                                mIntentListener.onIntent(apkVersion.apk, null);
//                                mSP_Config.edit().putBoolean("apkDialogFinish", false).apply();
//
//                                return true;
//                            } else {
//                                mSP_Config.edit().putBoolean("apkDialogFinish", true).apply();
//                            }
//                        }
//                        return false;
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Boolean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        mCompositeDisposable.add(d);
//                    }
//
//                    @Override
//                    public void onNext(final Boolean value) {
//                        LogUtil.i(TAG, "is apk has update? " + value);
////                        if (value) {
////                            mIntentListener.onIntent(true, null);
////                        } else {
//////                            mSP_Config.edit().putBoolean("apkDialogFinish", true).apply();
//////                            Intent intent0 = new Intent(LOADING_ACTION);
//////                            mContext.sendBroadcast(intent0);
////                            mIntentListener.onIntent(false, null);
////                        }
//
//                        downloadApk(apkVersion.apk);
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.i(TAG, " downloadApkVersionTxt error: " + e.getMessage());
//                        sendApkDownBrocastMessage(true);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    public void sendApkDownBrocastMessage(boolean isApkFinish) {
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("apkDialogFinish", isApkFinish).apply();
        mContext.sendBroadcast(intent);
    }

    public void intent() {
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("webServicesDownFinish", true).putBoolean("txtDownFinish", true)
                .putBoolean("apkDialogFinish", true).putBoolean("mainIconFinish", true).apply();
        mContext.sendBroadcast(intent);
        showM1();
    }

    private void setupDownload() {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(LoadingClient.class, NetWorkHelper.DOWNLOAD_PATH);
        }
    }

    public void downloadTxt() {
        String[] links = new String[]{TXT_AD_URL, TXT_MAIN_URL, TXT_NOTIFICATION_URL, APK_VERSION_TXT_URL, TXT_NEW_TEC};
        setupDownload();
        Observable.fromArray(links)
                .flatMap(new Function<String, Observable<String>>() {
                    @Override
                    public Observable<String> apply(@NonNull String url) throws Exception {
                        return getTxt(AppUtil.subStringLast(url, "/"));
                    }
                }).subscribeOn(Schedulers.computation())
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


    /**
     * 下载单个txt，并写入内存data/.../files中
     */
    private Observable<String> getTxt(final String fileName) {
//        final String fileName = AppUtil.subStringLast(url, "/");
        LogUtil.e(TAG, "getTxt:::fileName=" + fileName);
        return mClient.downTxt(fileName)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, String>() {
                    @Override
                    public String apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        long startTime = System.currentTimeMillis();
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                            fos.write(body.bytes());

                            if (fileName.equals(Constant.TXT_APK_VERSION)) {

                            }


                            body.close();
                            fos.close();
                        }
                        long endTime = System.currentTimeMillis();
                        LogUtil.i(TAG, "write txt spend time：" + fileName + ", " + (endTime - startTime) + " ms");
                        return fileName;
                    }
                });
    }

    public final static Integer STATUS_NS = -1;
    public final static Integer STATUS_DOWNLOADING = 1;
    public final static Integer STATUS_FINISHED = 2;
    public final static Integer STATUS_ERROR = 3;
    private Disposable disposable;
    public final ObservableInt mProgress = new ObservableInt(0);
    public final ObservableInt max = new ObservableInt(0);
    public final ObservableLong percent = new ObservableLong(0);
    /**
     * -1:未开始下载；0：点击下载，暂停中；1：正在下载; 2:下载完成
     */
    public final ObservableInt downloadStatus = new ObservableInt(0);
    private ProgressCallback mCallback = new ProgressCallback() {

        @Override
        public <T> void onProgress(long progress, long total, boolean done, T entity) {
            mProgress.set((int) progress);
            max.set((int) total);
            percent.set((long) ((float) (progress * 100) / total));

            if (percent.get() / 10 == 0) {
                LogUtil.i(TAG, "progress=" + progress + ",total=" + total + ", per= " + percent.get() + "%");
            }

        }
    };

    private void downloadApk(final String link) {
        DownloadClient mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, NetWorkHelper.BASE_URL_CPS, mCallback, this);
        mClient.largeDownload(link)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            LogUtil.i(TAG, "headers=" + response.headers().toString());
                            LogUtil.i(TAG, "downUrls发射文件:" + link);
                            return FileUtil.writeFile(body, App.filesDir + APK_NAME);
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "---------------down apk onSubscribe ");
                        disposable = d;
                        downloadStatus.set(STATUS_DOWNLOADING);
                    }

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        LogUtil.i(TAG, "down apk onNext: " + success);
                        if (success) {
                            downloadStatus.set(STATUS_FINISHED);
                            LogUtil.i(TAG, "apk下载完成，安装");
//                            installAPK();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "down apk onError:" + e.getMessage());
                        downloadStatus.set(STATUS_NS);
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "down apk onComplete---------------");
                        disposable.dispose();
                    }
                });
    }


    private TextView tvSkip;
    private FrameLayout m1Frame;

    public void initM1(TextView tvSkip, FrameLayout m1Frame) {
        this.tvSkip = tvSkip;
        this.m1Frame = m1Frame;
    }

    private void showM1() {
        mEPOHelper = EPOHelper.getInstance();
        if (!mEPOHelper.isD1Open()) {
            Intent intent = new Intent(LOADING_ACTION);
            mSP_Config.edit().putBoolean("M1ShowFinish", true).apply();
            mContext.sendBroadcast(intent);
            LogUtil.i(TAG, "))))) m1 关闭");
            return;
        }

        LogHelper logHelper = LogHelper.getInstance();
        logHelper.logD1(mEPOHelper.getD1CompanyID(), true);
        AppUtil.setStatEventFull(mContext, LogHelper.EVENT_ID_AD_VIEW, logHelper.getTrackingName());

        isShowM1.set(true);
        D1ImageUrl.set(mEPOHelper.getD1Image());
        LogUtil.i(TAG, "D1ImageUrl= ❤❤❤ " + D1ImageUrl.get());
        tvSkip.setVisibility(View.VISIBLE);

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


    public void onD1Click() {
        mIntentListener.onIntent(mEPOHelper.getD1CompanyID(), ExhibitorDetailActivity.class);
        LogUtil.i(TAG, "))))) onClick, send broadcast");
        if (mAdDisposable != null && !mAdDisposable.isDisposed()) {
            mAdDisposable.dispose();//停止倒计时
        }
    }

    public void onSkipClick() {
        Intent intent = new Intent(LOADING_ACTION);
        mSP_Config.edit().putBoolean("M1ShowFinish", true).apply();
        mContext.sendBroadcast(intent);
        LogUtil.i(TAG, "))))) skip, send broadcast");
        if (mAdDisposable != null && !mAdDisposable.isDisposed()) {
            mAdDisposable.dispose();//停止倒计时
        }
    }

}
