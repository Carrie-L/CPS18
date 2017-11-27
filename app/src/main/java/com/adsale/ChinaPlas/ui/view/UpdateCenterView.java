package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.MapFloor;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.model.AppContent;
import com.adsale.ChinaPlas.data.model.UpdateCenterUrl;
import com.adsale.ChinaPlas.databinding.ItemUpdateCenterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.File;
import java.io.FileOutputStream;
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
import static com.adsale.ChinaPlas.R.array.urls;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

public class UpdateCenterView extends RelativeLayout {
    private final String TAG="UpdateCenterView";
    public final ObservableField<UpdateCenter> updateCenter = new ObservableField<>();
    //	public final ObservableField<String> itemName=new ObservableField<>();
    public final ObservableField<String> lastUpdateTime = new ObservableField<>();
//	public final ObservableInt index=new ObservableInt();
    /**
     * 是否有更新，是 true，否 false
     */
//	public final ObservableBoolean hasUpdate = new ObservableBoolean();
    /**
     * Update按钮是否可点击
     */
    public boolean isClickable;
    private Context mContext;
    private ItemUpdateCenterBinding binding;
    private UpdateCenterUrl urlEntity;
    private ArrayList<UpdateCenter> ucArrayList = new ArrayList<>();
    private ArrayList<String> linkUrls = new ArrayList<>();
    private ArrayList<MapFloor> mMapFloors = new ArrayList<>();
    /**
     * 从ScanFile获取的平面图的最新更新时间，存储在UpdateCenter表中
     */
    private String mapFloorLUT;
    /**
     * 是否允许返回：当前正在下载，不允许，false; 否则，true
     */
    public static boolean canBack = true;
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
    protected ProgressBar downloadProgress;
    public final ObservableFloat progress = new ObservableFloat(0);
    private int count;

    public UpdateCenterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public UpdateCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UpdateCenterView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        binding = ItemUpdateCenterBinding.inflate(LayoutInflater.from(context), this, true);
        binding.setView(this);
        downloadProgress=binding.downloadProgress;
    }

    public void setData(UpdateCenter entity) {
        updateCenter.set(entity);
        binding.setObj(updateCenter.get());
        binding.executePendingBindings();
        lastUpdateTime.set(String.format(mContext.getString(R.string.uc_last_update_time), updateCenter.get().getLUT()));
    }

    public void onUpdate(long index) {
        LogUtil.i(TAG, "onUpdate:id=" + updateCenter.get().getId() + ",index=" + index);
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.UC_BASE_URL);
        }
        linkUrls.clear();
        String dir = "";
        if (index == 1) { // ExhibitorData
            getUpdateLink0();
//            dir = rootDir.concat(Constant.DIR_EXHIBITOR);
//            dir = createFile(dir);
//            downOneZip(urlEntity.link, dir);
            downFloorPlan();
        } else if (index == 3) { // Technical Seminar
//            dir = rootDir.concat(Constant.DIR_SEMINAR);
//            dir = createFile(dir);
            getUpdateLink2();
//            downOneZip(urlEntity.link, dir);
            downFloorPlan();
        } else if (index == 5) { // Travel Info
//            dir = rootDir.concat(Constant.DIR_TRAVEL);
//            dir = createFile(dir);
            getUpdateLink4();
//            downOneZip(urlEntity.link, dir);
            downFloorPlan();
        } else if (index == 2) { // Floor Plan
//            dir = rootDir.concat(Constant.DIR_FLOOR_PLAN);
//            dir = createFile(dir);
            getUpdateLink1();
//            downFloorPlan(urlEntity.csvlink,dir);
            downFloorPlan();
        } else if (index == 4) { // Concurrent Event
//            dir = rootDir.concat(Constant.DIR_EVENT);
//            dir = createFile(dir);
            getUpdateLink3();
            downFloorPlan();
        }
    }

    public void onUpdateAll(ArrayList<UpdateCenter> list) {
        ucArrayList = list;
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.UC_BASE_URL);
        }
        getUrls();
        LogUtil.i(TAG, "onUpdateAll:" + linkUrls.size() + "," + linkUrls.toString());
        downFloorPlan();
    }

    /**
     * 参展商链接
     *
     * @return
     */
    private ArrayList<String> getUpdateLink0() {
//        createFile(rootDir.concat(Constant.DIR_EXHIBITOR));
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_EXHIBITOR);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    private void downExhibitor() {
        final String exhibitorDir = rootDir.concat("ExhibitorInfo/");
        createFile(exhibitorDir);
        mClient.downExhibitorCSVs(linkUrls.get(0))
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            FileUtil.unpackZip("20170614.zip", body.byteStream(), exhibitorDir);
                            body.close();
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                    }
                });
    }

    /**
     * ExhibitorInfo.txt - ExhibitorInfo/20170614.zip
     * SeminarInfo.txt - technicalSeminar/technical_20170427.zip
     * TravelInfo.txt - travel/travel0508.zip
     *
     * @param url link
     */
    private void downOneZip(String url, final String dir) {
        final String zip = AppUtil.subStringLast(url, '/');
//        final String dir = rootDir.concat(AppUtil.subStringMiddle(url, ".com/", "/")).concat("/");
        LogUtil.i(TAG, "zip=" + zip);
//        createFile(dir);

        mClient.downExhibitorCSVs(url)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            FileUtil.unpackZip(zip, body.byteStream(), dir);
                            body.close();
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                    }
                });
    }

    private void downFloorPlan() {
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
                        return downMapPic(url);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        downloadProgress.setVisibility(VISIBLE);
                        LogUtil.i(TAG, "----------------------------------------》onSubscribe:开始下载");
                        downloadProgress.setMax(linkUrls.size());
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        count ++;
                        LogUtil.i(TAG, "onNext:count=" + count);
                        downloadProgress.setProgress(count);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        downloadProgress.setProgress(linkUrls.size());
                        downloadProgress.setVisibility(GONE);
                        count=0;
                        updated();
                    }
                });
    }

    private void updated(){
        updateCenter.get().setStatus(1);
    }

    private Observable<Boolean> downMapPic(final String url) {
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
                                    mDir="";
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
                        LogUtil.i(TAG, "linkUrls=" + linkUrls.size() + "," + linkUrls.toString());
                        return true;
                    }
                });
    }

    private void getDir(String url){
        if(url.contains("ExhibitorInfo")){
            mDir = rootDir.concat(Constant.DIR_EXHIBITOR);
            mDir = createFile(mDir);
        }else  if(url.contains("FloorPlan")){
            mDir = rootDir.concat(Constant.DIR_FLOOR_PLAN);
            mDir = createFile(mDir);
        }else  if(url.contains("technical")){
            mDir = rootDir.concat(Constant.DIR_SEMINAR);
            mDir = createFile(mDir);
        }else  if(url.contains("events")){
            mDir = rootDir.concat(Constant.DIR_EVENT);
            mDir = createFile(mDir);
        }else  if(url.contains("travel")){
            mDir = rootDir.concat(Constant.DIR_TRAVEL);
            mDir = createFile(mDir);
        }
    }


    private ArrayList<String> getUrls() {
        linkUrls.clear();
        getUpdateLink0();
        getUpdateLink1();
        getUpdateLink2();
        getUpdateLink3();
        getUpdateLink4();
        return linkUrls;
    }

    private Observable<Boolean> downloadItem(String url) {
        final String zip = AppUtil.subStringLast(url, '/');
//        String dir = AppUtil.subStringMiddle(url, ".com/", "/");

        final String dir = rootDir.concat(AppUtil.subStringMiddle(url, ".com/", "/")).concat("/");
        LogUtil.i(TAG, "zip=" + zip + ",dir=" + dir);
        createFile(dir);

        return mClient.downUrls(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            LogUtil.i(TAG, "downloadItem:apply");
                            FileUtil.unpackZip(zip, body.byteStream(), dir);
                            body.close();
                        }
                        return true;
                    }
                });
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
        LogUtil.i(TAG, "floor plan urls=" + urls);
        return linkUrls;
    }

    /**
     * 通过比较最后更新时间，获取MapFloor中需要下载的项，根据FloorPlan.txt中 baseImgUrl + mapFloor.name 合成下载地址
     */
    private void getMapFloors() {
        mMapFloors.clear();
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_FLOOR_PLAN);
        mapFloorLUT = updateCenter.get().getLUT();
        LogUtil.i(TAG, "mapFloorLUT=" + mapFloorLUT);

        if (floorRepository == null) {
            floorRepository = FloorRepository.getInstance();
            floorRepository.initMapFloorDao();
        }
        mMapFloors = floorRepository.getMapFloorNeedDowns(mapFloorLUT);
        LogUtil.i(TAG, "lut=" + mapFloorLUT + ",mMapFloors=" + mMapFloors.size() + "," + mMapFloors.toString());

//        if (!new File(App.rootDir + "FloorPlan/").exists()) {
//            new File(App.rootDir + "FloorPlan/").mkdir();
//        }
    }


    /**
     * 技术交流会下载链接
     */
    private ArrayList<String> getUpdateLink2() {
//        createFile(rootDir.concat(Constant.DIR_SEMINAR));
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_SEMINAR);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    /**
     * 同期活动下载链接
     */
    private ArrayList<String> getUpdateLink3() {
//        createFile(rootDir.concat(Constant.DIR_EVENT));
        AppContent urlEntity = Parser.parseJsonFilesDirFile(AppContent.class, Constant.UC_TXT_APP_CONTENTS);
        AppContent.Pages pageEntity;
        int size = urlEntity.pages.size();
        createFile(App.rootDir.concat("ConcurrentEvent/"));

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
//        createFile(rootDir.concat(Constant.DIR_TRAVEL));
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_TRAVEL);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    // HTTP method annotation is required (e.g., @GET, @POST, etc.).
    private void downAll() {
//        mClient.downUrls("")
//                .flatMap(new Function<UpdateCenter, Observable<String>>() {
//                    @Override
//                    public Observable<String> apply(@NonNull UpdateCenter updateCenter) throws Exception {
//                        return Observable.fromIterable(getUrls());
//                    }
//                })
//                .flatMap(new Function<String, Observable<Boolean>>() {
//                    @Override
//                    public Observable<Boolean> apply(@NonNull String s) throws Exception {
//                        return downloadItem(s);
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Boolean>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        LogUtil.i(TAG, "onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Boolean aBoolean) {
//                        LogUtil.i(TAG, "onNext:" + aBoolean);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        LogUtil.i(TAG, "onNext:" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtil.i(TAG, "onComplete");
//                    }
//                });
    }

}
