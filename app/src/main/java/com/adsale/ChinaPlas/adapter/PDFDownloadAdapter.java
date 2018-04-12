package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.helper.ProgressCallback;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.OpenFileUtil;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.ACTION_OPEN;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_DOWNLOADING;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_FINISHED;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_NS;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_PAUSING;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_RESTART;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

/**
 * Created by Carrie on 2018/1/11.
 * 文档下载中心
 * 更改存储地址为 SD卡。
 */

public class PDFDownloadAdapter extends CpsBaseAdapter<DocumentsCenter.Child> {
    private final String TAG = "PDFDownloadAdapter";
    private  ArrayList<ArrayList<DocumentsCenter.Child>> docs = new ArrayList<>();

    /* 父与子共用一个列表 */
    private ArrayList<DocumentsCenter.Child> list = new ArrayList<>();

    public final ObservableInt mClickPos = new ObservableInt(-1);
    public final ObservableBoolean isExpand = new ObservableBoolean(false);

    private ArrayList<DocumentsCenter.Child> children = new ArrayList<>();
    private ViewDataBinding mBinding;
    private DownloadClient mClient;
    private final String mDir;
    private Disposable disposable;
    private OnItemClickCallback mClickCallback;

    public PDFDownloadAdapter( ArrayList<ArrayList<DocumentsCenter.Child>> docs, ArrayList<DocumentsCenter.Child> children,Context mContext, OnItemClickCallback callback) {
        this.docs = docs;
        this.mContext = mContext;
        this.mClickCallback = callback;
        this.list = children;
        mDir = App.rootDir.concat("DocumentsPDF/");
        createFile(mDir);
//        generate();
    }

    private void generate() {
        DocumentsCenter.Child parent;
        int size = docs.size();
        for (int i = 0; i < size; i++) {
            parent = new DocumentsCenter.Child();
//            parent.FileName_SC = docs.get(i).CategoryName_SC;
//            parent.FileName_EN = docs.get(i).CategoryName_EN;
//            parent.FileName_TC = docs.get(i).CategoryName_TC;
            parent.isParent.set(true);
            list.add(parent);
        }
    }

    public void onParentItemClick(int pos) {
        LogUtil.i(TAG, "pos = " + pos + ",mClickPos=" + mClickPos.get());
        if (mClickPos.get() == -1) {
            insertChild(pos);
        } else if (mClickPos.get() == pos) {
            closeItems();
        } else {
            closeItems();
            insertChild(pos);
        }
    }

    private void insertChild(int pos) {
        isExpand.set(true);
        mClickPos.set(pos);
        children = docs.get(pos);
        list.addAll(pos + 1, children);
        notifyItemRangeInserted(pos + 1, children.size());//position + 1
    }

    private void closeItems() {
        isExpand.set(false);
        list.removeAll(children);
        notifyItemRangeRemoved(mClickPos.get() + 1, children.size());
        mClickPos.set(-1);
    }

    public void onDownload(DocumentsCenter.Child entity) {

        if (entity.downloadStatus.get() == STATUS_FINISHED) {
            LogUtil.i(TAG, "openFile");
//            openFile(entity);
            mClickCallback.onItemClick(entity, ACTION_OPEN);

        } else {
            entity.downloadStatus.set(STATUS_DOWNLOADING);
            entity.mProgress.set(0);
            entity.max.set(0);
            mClickCallback.onItemClick(entity, STATUS_DOWNLOADING);
            LogUtil.i(TAG, "download");
//            download(entity);
        }

    }


    private void openFile(DocumentsCenter.Child entity) {
        String path = mDir + AppUtil.subStringLast(entity.getFileLink(), '/');
        LogUtil.i(TAG, "path=" + path);
        Intent intent = OpenFileUtil.openFile(path);
        mContext.startActivity(intent);
    }

    public void onRestart(DocumentsCenter.Child entity, int pos) {
        int status = entity.downloadStatus.get();
        LogUtil.i(TAG, "onRestart: status=" + status);
        if (status == STATUS_DOWNLOADING) {
            // pause
//            disposable.dispose();
            entity.downloadStatus.set(STATUS_PAUSING);
            mClickCallback.onItemClick(entity, STATUS_PAUSING);

        } else {
            // continue download
//            bpDownload(entity);
            entity.downloadStatus.set(STATUS_DOWNLOADING);
            mClickCallback.onItemClick(entity, STATUS_RESTART);


        }


    }

    public void onDelete(DocumentsCenter.Child entity) {
        entity.downloadStatus.set(STATUS_NS);
        entity.cancel();
    }

//    public final ObservableInt mProgress = new ObservableInt(0);
//    public final ObservableInt max = new ObservableInt(0);

    private ProgressCallback mCallback = new ProgressCallback() {

        @Override
        public <T> void onProgress(long progress, long total, boolean done, T entity) {
            LogUtil.i(TAG, "progress=" + progress + ",total=" + total);

            ((DocumentsCenter.Child) entity).mProgress.set((int) progress);
            ((DocumentsCenter.Child) entity).max.set((int) total);

            int pro = (int) (((float) progress / (float) total) * 100);
            if (pro % 10 == 0) {
                LogUtil.i(TAG, "onProgress: entity = " + ((DocumentsCenter.Child) entity).getFileName() + ",progress=" + pro);
            }


        }
    };

    private void download(final DocumentsCenter.Child entity) {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, NetWorkHelper.UC_BASE_URL, mCallback, entity);
        }
        final String url = entity.getFileLink();

        mClient.downUrls(url)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            LogUtil.i(TAG, "downUrls发射文件");
                            FileOutputStream fos = new FileOutputStream(new File(mDir.concat(AppUtil.subStringLast(url, '/'))));
                            fos.write(body.bytes());
                            fos.close();
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
                        LogUtil.i(TAG, "----------------------------------------》onSubscribe:开始下载");
                        disposable = d;
                        entity.downloadStatus.set(STATUS_DOWNLOADING);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:aBoolean=" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        disposable.dispose();
                        entity.downloadStatus.set(STATUS_FINISHED);
                    }
                });


    }

    private void bpDownload(final DocumentsCenter.Child entity) {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, NetWorkHelper.UC_BASE_URL, mCallback, entity);
        }
        final String url = entity.getFileLink();

        File file = new File(mDir.concat(AppUtil.subStringLast(url, '/')));
        long length = file.length();
        LogUtil.i(TAG, "length=" + length);

        mClient.bpDownload("bytes=" + length + "-", url)//"bytes=" + length + "-",
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            Headers headers = response.headers();
                            LogUtil.i(TAG, "headers=" + headers.toString());

                            LogUtil.i(TAG, "发射文件");
                            FileOutputStream fos = new FileOutputStream(new File(mDir.concat(AppUtil.subStringLast(url, '/'))), true);
                            fos.write(body.bytes());
                            fos.close();
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
                        LogUtil.i(TAG, "----------------------------------------》onSubscribe:开始下载");
                        disposable = d;
                        entity.downloadStatus.set(STATUS_DOWNLOADING);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:aBoolean=" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        disposable.dispose();
                        entity.downloadStatus.set(STATUS_FINISHED);
                    }
                });


    }


    @Override
    protected void bindVariable(ViewDataBinding binding) {
        mBinding = binding;
        binding.setVariable(BR.pdfAdapter, this);
        super.bindVariable(binding);
    }

    private DocumentsCenter.Child mEntity;

    @Override
    protected Object getObjForPosition(int position) {
        mBinding.setVariable(BR.pos, position);
        mBinding.executePendingBindings();
        mEntity = list.get(position);
        mEntity.mProgress.set(mEntity.getProgress(mEntity.getFileName()));
        mEntity.max.set(mEntity.getMax(mEntity.getFileName()));
        mEntity.downloadStatus.set(mEntity.getStatus(mEntity.getFileName()));

        LogUtil.i(TAG,"downloadStatus="+mEntity.getStatus(mEntity.getFileName()));

        return mEntity;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if (list.get(position).isParent.get()) {
            return R.layout.item_documents_list;
        }
        return R.layout.item_docoments_child;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
