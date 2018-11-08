package com.adsale.ChinaPlas.data.model;

import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.helper.ProgressCallback;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.adsale.ChinaPlas.viewmodel.DownloadCenterViewModel;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


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
        public int id;
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


        public String getFileLink() {
            return AppUtil.getName(FileLink_TC, FileLink_EN, FileLink_SC);
        }

        public String getFileName() {
            return AppUtil.getName(FileName_TC, FileName_EN, FileName_SC);
        }

        public int getId() {
            return id;
        }

        /*  自定义variable  */
        public final ObservableBoolean isParent = new ObservableBoolean(false);
        public final static Integer STATUS_NS = -1;
        public final static Integer STATUS_DOWNLOADING = 1;
        public final static Integer STATUS_FINISHED = 2;
        public final static Integer ACTION_OPEN = 4;

        public final ObservableInt mProgress = new ObservableInt(0);
        public final ObservableInt max = new ObservableInt(0);

        public static final String PDF_LINK = "https://www.chinaplasonline.com/CPS17/Files/PDF/ShowDaily/CPS17_Intl_Day1.pdf";

        /**
         * -1:未开始下载；0：点击下载，暂停中；1：正在下载; 2:下载完成
         */
        public final ObservableInt downloadStatus = new ObservableInt(STATUS_NS);

        public void cancel() {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }

            File file = FileUtil.getPDFFile();
            if (file != null && file.exists()) {
                file.delete();
            }

            mViewModel.clearEntity(id);

        }

        private DownloadCenterViewModel mViewModel;

        public void download(DownloadCenterViewModel viewModel) {
            mViewModel = viewModel;
            // 每个callback对应一个Item，因此不用 if(mClient==null)
            DownloadClient mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, "https://www.chinaplas.com/", mCallback, this);
            mClient.largeDownload(getFileLink())
                    .map(new Function<Response<ResponseBody>, Boolean>() {
                        @Override
                        public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                            ResponseBody body = response.body();
                            if (body != null) {
                                LogUtil.i(TAG, "headers=" + response.headers().toString());
                                LogUtil.i(TAG, "downUrls发射文件:" + getFileName());
                                return FileUtil.writeFile(body, AppUtil.subStringLast(getFileLink(), '/'));
                            }
                            return false;
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
                        public void onNext(@NonNull Boolean success) {
                            if (success) {
                                downloadStatus.set(STATUS_FINISHED);
                                AppUtil.setPDFDownStatus(id, STATUS_FINISHED);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            LogUtil.i(TAG, "onError:" + e.getMessage());

                            downloadStatus.set(STATUS_NS);
                            disposable.dispose();
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.i(TAG, "onComplete");

                            mViewModel.clearEntity(id);
                            disposable.dispose();


                        }
                    });
        }

        private Disposable disposable;
        private SharedPreferences.Editor editor = App.mSP_DownloadCenter.edit();

        private ProgressCallback mCallback = new ProgressCallback() {

            @Override
            public <T> void onProgress(long progress, long total, boolean done, T entity) {
//                LogUtil.i(TAG, "progress=" + progress + ",total=" + total);
                if (done) {
                    LogUtil.i(TAG, "ProgressCallback:" + done + "," + ((Child) entity).getFileName());
                }
                mProgress.set((int) progress);
                max.set((int) total);
            }
        };

        @Override
        public String toString() {
            return "Child{" +
                    "id=" + id +
                    ", FileName_SC='" + FileName_SC + '\'' +
                    ", FileLink_SC='" + FileLink_SC + '\'' +
                    ", isParent=" + isParent +
                    '}';
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
