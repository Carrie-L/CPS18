package com.adsale.ChinaPlas.data.model;

import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.support.annotation.NonNull;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.helper.ProgressCallback;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DownloadManager;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_DOWNLOADING;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_FINISHED;

/**
 * Created by Carrie on 2017/10/31.
 * 文档下载中心
 */

public class DocumentsCenter {
    private static final String TAG = "DocumentsCenter";

    public String CategoryName_SC;
    public String CategoryName_EN;
    public String CategoryName_TC;
    public ArrayList<Child> Items;

    public void setItems(ArrayList<Child> items) {
        Items = items;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public static class Child {
        public String FileName_SC;
        public String FileName_EN;
        public String FileName_TC;

        public String Cover_SC;

        public String FileLink_EN;
        public String FileLink_SC;
        public String FileLink_TC;

        public String FileSize_SC;
        public String FileSize_TC;
        public String FileSize_EN;

        /*  自定义variable  */
        public final ObservableBoolean isParent = new ObservableBoolean(false);
        public final static Integer STATUS_NS = -1;
        public final static Integer STATUS_PAUSING = 0;
        public final static Integer STATUS_DOWNLOADING = 1;
        public final static Integer STATUS_FINISHED = 2;
        public final static Integer STATUS_RESTART = 3;
        public final static Integer ACTION_OPEN = 4;
        public final static Integer STATUS_BACK_DOWNLOADING = 5; // 后台正在下载，但中途退出了下载中心，再次进入下载中心，此时的状态是：在下载中，但是没法取得disposable等值

        public final ObservableInt mProgress = new ObservableInt(0);
        public final ObservableInt max = new ObservableInt(0);

        /**
         * -1:未开始下载；0：点击下载，暂停中；1：正在下载; 2:下载完成
         */
        public final ObservableInt downloadStatus = new ObservableInt(STATUS_NS);


        public String getFileName() {
            return AppUtil.getName(FileName_TC, FileName_EN, FileName_SC);
        }

        public String getFileSize() {
            return AppUtil.getName(FileSize_TC, FileSize_EN, FileSize_SC);
        }

        public String getFileLink() {
            return AppUtil.getName(FileLink_TC, FileLink_EN, FileLink_SC);
        }

        @Override
        public String toString() {
            return "Child{" +
                    "FileName_SC='" + FileName_SC + '\'' +
                    ", FileName_EN='" + FileName_EN + '\'' +
                    ", FileName_TC='" + FileName_TC + '\'' +
                    ", Cover_SC='" + Cover_SC + '\'' +
                    ", FileLink_EN='" + FileLink_EN + '\'' +
                    ", FileLink_SC='" + FileLink_SC + '\'' +
                    ", FileLink_TC='" + FileLink_TC + '\'' +
                    ", FileSize_SC='" + FileSize_SC + '\'' +
                    ", FileSize_TC='" + FileSize_TC + '\'' +
                    ", FileSize_EN='" + FileSize_EN + '\'' +
                    '}';
        }


        private String mLink = "http://7x2xn9.com1.z0.glb.clouddn.com/CPS17_DxI_LKK.flv";

        public void cancel() {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }

            File file = DownloadManager.getFutureStudioIconFile();
            if (file != null && file.exists()) {
                file.delete();
            }

        }

        public void download() {
            // 每个callback对应一个Item，因此不用 if(mClient==null)
            DownloadClient mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, NetWorkHelper.UC_BASE_URL, mCallback, this);
            final String url = getFileLink();

            mClient.largeDownload(mLink)
                    .map(new Function<Response<ResponseBody>, Boolean>() {
                        @Override
                        public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                            ResponseBody body = response.body();
                            if (body != null) {
                                LogUtil.i(TAG, "headers=" + response.headers().toString());
                                LogUtil.i(TAG, "downUrls发射文件:"+getFileName());
                                // 用这种方式会OOM
//                            FileOutputStream fos = new FileOutputStream(new File(mDir.concat(AppUtil.subStringLast(url, '/'))));
//                            fos.write(body.bytes());
//                            fos.close();
//                            body.close();


                                saveDownloadInfo();


                                return DownloadManager.writeFile(body, AppUtil.subStringLast(url, '/'));
                            }
                            return true;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            LogUtil.i(TAG, "----------------------------------------》onSubscribe:开始下载");
                            disposable = d;
                            downloadStatus.set(STATUS_DOWNLOADING);
                        }

                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            LogUtil.i(TAG, "onNext:aBoolean=" + aBoolean);


                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            LogUtil.i(TAG, "onNext:" + e.getMessage());

                            downloadStatus.set(STATUS_NS);
                            saveDownloadInfo();

                            disposable.dispose();
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.i(TAG, "onComplete");

                            downloadStatus.set(STATUS_FINISHED);
                            saveDownloadInfo();

                            disposable.dispose();

                        }
                    });
        }

        private void saveDownloadInfo(){
            // 保存下载信息,方便下次进来时显示进度条
            setProgress(getFileName(), mProgress.get());
            setMax(getFileName(), max.get());
            setStatus(getFileName(), downloadStatus.get());
        }

        private Disposable disposable;
        private SharedPreferences.Editor editor = App.mSP_DownloadCenter.edit();

        private ProgressCallback mCallback = new ProgressCallback() {

            @Override
            public <T> void onProgress(long progress, long total, boolean done, T entity) {
//                LogUtil.i(TAG, "progress=" + progress + ",total=" + total);

                if(done){
                    LogUtil.i(TAG,"ProgressCallback:"+done+","+((Child)entity).getFileName());
                }

                ((DocumentsCenter.Child) entity).mProgress.set((int) progress);
                ((DocumentsCenter.Child) entity).max.set((int) total);

                int pro = (int) (((float) progress / (float) total) * 100);
                if (pro % 10 == 0) {
//                    LogUtil.i(TAG, "onProgress: entity = " + ((DocumentsCenter.Child) entity).getFileName() + ",progress=" + pro);
                }


            }
        };


        /**
         * 一系列 status \ progress\ max 的setter getter 操作，都是为了在再次进入下载中心时，得到原先的下载信息
         * @param fileName
         * @param status
         */

        public void setStatus(String fileName, int status) {
            if (status == STATUS_DOWNLOADING) {
                // 为了让 item_child 将文字显示为“下载中”,且不显示进度条
                status = STATUS_BACK_DOWNLOADING;
            }
            editor.putInt(fileName + "_status", status == STATUS_DOWNLOADING ? STATUS_BACK_DOWNLOADING : status).apply(); //
        }

        public int getStatus(String fileName) {
            return App.mSP_DownloadCenter.getInt(fileName + "_status", STATUS_NS);
        }

        public void setProgress(String fileName, int progress) {
            editor.putInt(fileName + "_progress", progress).apply();
        }

        public int getProgress(String fileName) {
            return App.mSP_DownloadCenter.getInt(fileName + "_progress", 0);
        }

        public void setMax(String fileName, int max) {
            editor.putInt(fileName + "_max", max).apply();
        }

        public int getMax(String fileName) {
            return App.mSP_DownloadCenter.getInt(fileName + "_max", 0);
        }

    }

    @Override
    public String toString() {
        return "DocumentsCenter{" +
                "CategoryName_SC='" + CategoryName_SC + '\'' +
                ", CategoryName_EN='" + CategoryName_EN + '\'' +
                ", CategoryName_TC='" + CategoryName_TC + '\'' +
                ", Items=" + Items +
                '}';
    }
}
