package com.adsale.ChinaPlas.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adsale.ChinaPlas.dao.MapFloor;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.AppContent;
import com.adsale.ChinaPlas.data.model.UpdateCenterUrl;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.adsale.ChinaPlas.App.rootDir;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

/**
 * Created by Carrie on 2017/10/27.
 */

public class UpdateCenterViewModel {
    private final String TAG = "UpdateCenterViewModel";
    private UpdateCenterUrl urlEntity;
    private ArrayList<String> linkUrls = new ArrayList<>();
    private ArrayList<MapFloor> mMapFloors = new ArrayList<>();
    /**
     * 从ScanFile获取的平面图的最新更新时间，存储在UpdateCenter表中
     */
    private String mapFloorLUT;
    /**
     * 是否允许返回：当前正在下载，不允许，false; 否则，true
     */
    public boolean canBack = true;
    private DownloadClient mClient;
    private FloorRepository floorRepository;
    /**
     * url 的后缀名称，如 001.zip || xxx.csv
     */
    private String name;
    /**
     * 文件目录，绝对路径
     */
    private String mDir;
    private ProgressBar downloadProgress;
    private int count;
    private Disposable disposable;

    /**
     * true: 0 有更新，红色按钮； false：1 无更新，灰色按钮
     */
    public final ObservableBoolean statusAll = new ObservableBoolean();
    public final ObservableBoolean isUpdating = new ObservableBoolean();
    public final ObservableField<String> progressRate = new ObservableField<>();
    public final ObservableArrayList<UpdateCenter> list = new ObservableArrayList<>();
    /**
     * 待更新的个数
     */
    private int updateCount = 0;
    private OtherRepository mRepository;

    public void init(ProgressBar progressBar) {
        this.downloadProgress = progressBar;
        mRepository = OtherRepository.getInstance();
        mRepository.initUpdateCenterDao();
        list.addAll(mRepository.getUpdateCenters());
        setData();
    }

    private void setData() {
        int size = list.size();
        UpdateCenter entity;
        int j = 0;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            entity.setStatus(0);/* JFT */
            if (entity.getStatus() == 0) {
                updateCount++;
            }
        }
        LogUtil.i(TAG, "updateCount=" + updateCount);
        statusAll.set(updateCount <= 5);
    }

    public void onUpdate(int index) {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.UC_BASE_URL);
        }
        linkUrls.clear();
        if (index == 0) { // ExhibitorData
            getUpdateLink0();
        } else if (index == 2) { // Technical Seminar
            getUpdateLink2();
        } else if (index == 4) { // Travel Info
            getUpdateLink4();
        } else if (index == 1) { // Floor Plan
            getUpdateLink1();
        } else if (index == 3) { // Concurrent Event
            getUpdateLink3();
        }
        download(index);
    }

    public void onUpdateAll() {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.UC_BASE_URL);
        }
        getUrls();
        LogUtil.i(TAG, "onUpdateAll:" + linkUrls.size() + "," + linkUrls.toString());
        download(-1);
    }

    /**
     * @param index update all: -1 | others: start from 0
     */
    private void download(final int index) {
        mClient.downUrls("")
                .flatMap(new Function<Response<ResponseBody>, Observable<String>>() {
                    @Override
                    public Observable<String> apply(@NonNull Response<ResponseBody> response) throws Exception {
                        return Observable.fromIterable(linkUrls);
                    }
                })
                .flatMap(new Function<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(@NonNull String url) throws Exception {
                        LogUtil.i(TAG, "linkUrls=" + linkUrls.size() + "," + linkUrls.toString());
                        return downItem(url);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "----------------------------------------》onSubscribe:开始下载:" + linkUrls.size());
                        disposable = d;

                        if (linkUrls.size() == 1) { // 虚假进度条
                            downloadProgress.setMax(10);
                            downloadProgress.setProgress(5);
                            progressRate.set("50%");
                        } else {
                            downloadProgress.setMax(linkUrls.size());
                            downloadProgress.setProgress(0);
                            progressRate.set("");
                        }

                        isUpdating.set(true);
                        canBack = false;
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        count++;
                        LogUtil.i(TAG, "onNext:count=" + count);
                        if (linkUrls.size() == 1) {
                            downloadProgress.setProgress(10);
                            progressRate.set("100%");
                        } else {
                            downloadProgress.setProgress(count);
                            float progress = (float) count / linkUrls.size();
                            if (progress >= 0.1) {
                                progressRate.set(new DecimalFormat("00%").format(progress));
                                LogUtil.i(TAG, "onNext: count=" + count + ", progress=" + progress + ", rate=" + progressRate.get());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onError:" + e.getMessage());
                        canBack = true;
                        isUpdating.set(false);
                        Toast.makeText(downloadProgress.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        if (linkUrls.size() > 1) {
                            downloadProgress.setProgress(linkUrls.size());
                        }
                        count = 0;
                        linkUrls.clear();
                        isUpdating.set(false);
                        updated(index);
                        canBack = true;
                    }
                });
    }

    private void updated(int index) {
        updateCount--;
        LogUtil.i(TAG, "updated:" + index + ",updateCount=" + updateCount);
        UpdateCenter updateCenter;
        if (index == -1) {
            for (int i = 0; i < 5; i++) {
                updateCenter = list.get(i);
                updateCenter.setStatus(1);
                list.set(i, updateCenter);
            }
            updateCount = 0;
            mRepository.updateCenterAll(list);
            statusAll.set(false);
            return;
        }
        updateCenter = list.get(index);
        updateCenter.setStatus(1);
        list.set(index, updateCenter);
        mRepository.updateCenterItem(updateCenter);
        if (updateCount == 0) {
            statusAll.set(false);
        }
    }

    private Observable<Boolean> downItem(final String url) {
        return mClient.downUrls(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            name = AppUtil.subStringLast(url, '/');
                            LogUtil.i(TAG, "name=" + name);
                            getDir(url);
                            if (name.toLowerCase().endsWith(".zip")) {
                                LogUtil.i(TAG, "解压zip");
                                if (url.toLowerCase().contains("events")) { // 同期活动，创建 001/ 002 文件夹
                                    name = AppUtil.subStringLast(url, '/');
                                    mDir = rootDir.concat(Constant.DIR_EVENT).concat("/").concat(name.replace(".zip", "")).concat("/");
                                    createFile(mDir);
                                    LogUtil.i(TAG, "events:name=" + name + ",events:mDir=" + mDir);
                                }
                                FileUtil.unpackZip(name, body.byteStream(), mDir);
                            } else {
                                LogUtil.i(TAG, "发射文件");
                                FileOutputStream fos = new FileOutputStream(new File(mDir.concat(name)));
                                fos.write(body.bytes());
                                fos.close();
                            }
                            body.close();
                        }
                        return true;
                    }
                });
    }

    private void getDir(String url) {
        mDir="";
        if (url.contains("ExhibitorInfo")) {
            mDir = rootDir.concat(Constant.DIR_EXHIBITOR);
            mDir = createFile(mDir);
        } else if (url.contains("FloorPlan")) {
            mDir = rootDir.concat(Constant.DIR_FLOOR_PLAN);
            mDir = createFile(mDir);
        } else if (url.contains("technical")) {
            mDir = rootDir.concat(Constant.DIR_SEMINAR);
            mDir = createFile(mDir);
        } else if (url.contains("events")) {
            mDir = rootDir.concat(Constant.DIR_EVENT);
            mDir = createFile(mDir);
        } else if (url.contains("travel")) {
            mDir = rootDir.concat(Constant.DIR_TRAVEL);
            mDir = createFile(mDir);
        }
    }


    private ArrayList<String> getUrls() {
        linkUrls.clear();
        if (list.get(0).getStatus() == 0) {
            LogUtil.i(TAG, "getUrls:" + 0);
            getUpdateLink0();
        }
        if (list.get(1).getStatus() == 0) {
            LogUtil.i(TAG, "getUrls:" + 1);
            getUpdateLink1();
        }
        if (list.get(2).getStatus() == 0) {
            LogUtil.i(TAG, "getUrls:" + 2);
            getUpdateLink2();
        }
        if (list.get(3).getStatus() == 0) {
            LogUtil.i(TAG, "getUrls:" + 3);
            getUpdateLink3();
        }
        if (list.get(4).getStatus() == 0) {
            LogUtil.i(TAG, "getUrls:" + 4);
            getUpdateLink4();
        }
        return linkUrls;
    }

    /**
     * 参展商链接
     *
     * @return
     */
    private ArrayList<String> getUpdateLink0() {
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_EXHIBITOR);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    /**
     * 平面图下载链接
     */
    private ArrayList<String> getUpdateLink1() {
        getMapFloors();
        int size = mMapFloors.size();
        for (int i = 0; i < size; i++) {
            linkUrls.add(urlEntity.imagePath.concat(mMapFloors.get(i).getNameCN()));
        }
        linkUrls.add(urlEntity.csvlink);
        return linkUrls;
    }

    /**
     * 通过比较最后更新时间，获取MapFloor中需要下载的项，根据FloorPlan.txt中 baseImgUrl + mapFloor.name 合成下载地址
     */
    private void getMapFloors() {
        mMapFloors.clear();
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_FLOOR_PLAN);
        mapFloorLUT = list.get(1).getLUT();

        if (floorRepository == null) {
            floorRepository = FloorRepository.getInstance();
            floorRepository.initMapFloorDao();
        }
        mMapFloors = floorRepository.getMapFloorNeedDowns(mapFloorLUT);
        LogUtil.i(TAG, "lut=" + mapFloorLUT + ",mMapFloors=" + mMapFloors.size() + "," + mMapFloors.toString());
    }

    /**
     * 技术交流会下载链接
     */
    private ArrayList<String> getUpdateLink2() {
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_SEMINAR);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    /**
     * 同期活动下载链接
     */
    private ArrayList<String> getUpdateLink3() {
        AppContent urlEntity = Parser.parseJsonFilesDirFile(AppContent.class, Constant.UC_TXT_APP_CONTENTS);
        AppContent.Pages pageEntity;
        int size = urlEntity.pages.size();
        createFile(rootDir.concat("ConcurrentEvent/"));

        LogUtil.i(TAG, "size=" + size);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {//urlEntity.htmlFilePath+pageEntity.pageID+".zip"
            pageEntity = urlEntity.pages.get(i);
            sb.append(urlEntity.htmlFilePath).append(pageEntity.pageID).append(".zip");
            linkUrls.add(sb.toString());
            sb.delete(0, sb.toString().length());
        }
        return linkUrls;
    }

    /**
     * 技术交流会链接
     *
     * @return
     */
    private ArrayList<String> getUpdateLink4() {
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_TRAVEL);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        mRepository=null;
    }


}
