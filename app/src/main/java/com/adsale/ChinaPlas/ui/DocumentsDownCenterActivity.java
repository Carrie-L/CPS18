package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.PDFDownloadAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.databinding.ActivityDocumentsDownCenterBinding;
import com.adsale.ChinaPlas.helper.ProgressCallback;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DownloadManager;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.OpenFileUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
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
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

/**
 * 文档下载中心
 */
public class DocumentsDownCenterActivity extends BaseActivity implements OnItemClickCallback {

    private RecyclerView recyclerView;
    private DownloadClient mClient;
    private long downloadLength;
    private String mDir;
    private String mLink = "http://7x2xn9.com1.z0.glb.clouddn.com/CPS17_DxI_LKK.flv";
    private DocumentsCenter.Child dcEntity;
    private ArrayList<DocumentsCenter> list;

    @Override
    protected void initView() {
        ActivityDocumentsDownCenterBinding binding = ActivityDocumentsDownCenterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        recyclerView = binding.dcRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void initData() {

        getList();

        mDir = Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/DocumentsPDF/";
        FileUtil.createFile(Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/");
        FileUtil.createFile(mDir);
    }

    private void getList() {
        Type listType = new TypeToken<ArrayList<DocumentsCenter>>() {
        }.getType();
        list = new Gson().fromJson(FileUtil.readFilesDirFile(Constant.TXT_PDF_CENTER_INFO), listType);

        ArrayList<DocumentsCenter.Child> children = new ArrayList<>();
        ArrayList<ArrayList<DocumentsCenter.Child>> parents = new ArrayList<>();
        DocumentsCenter.Child parent;
        DocumentsCenter.Child itemChild;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            // add a parent
            parent = new DocumentsCenter.Child();
            parent.FileName_SC = list.get(i).CategoryName_SC;
            parent.FileName_EN = list.get(i).CategoryName_EN;
            parent.FileName_TC = list.get(i).CategoryName_TC;
            parent.isParent.set(true);
            children.add(parent);


            // then add its items
            int size1 = list.get(i).Items.size();
            for (int j = 0; j < size1; j++) {
                itemChild = list.get(i).Items.get(j);
                itemChild.downloadStatus.set(itemChild.getStatus(itemChild.getFileName()));
                itemChild.mProgress.set(itemChild.getProgress(itemChild.getFileName()));
                itemChild.max.set(itemChild.getMax(itemChild.getFileName()));
                if (itemChild.downloadStatus.get() == 5 && itemChild.mProgress.get() == 0 && itemChild.max.get() == 0) {
                    itemChild.downloadStatus.set(STATUS_NS);
                }
                list.get(i).Items.set(j, itemChild);
            }

            parents.add(i, list.get(i).Items);

//            children.addAll(list.get(i).Items);
        }
        LogUtil.i(TAG, "children=" + children.size() + "," + children.toString());
        LogUtil.i(TAG, "parents=" + parents.size() + "," + parents.toString());

//        PDFDownloadAdapter adapter = new PDFDownloadAdapter(list, getApplicationContext(), this);
        PDFDownloadAdapter adapter = new PDFDownloadAdapter(parents, children,getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
    }



    private boolean permissionSD() {
        boolean sdPermission = PermissionUtil.checkPermission(getApplicationContext(), PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        LogUtil.i(TAG, "sdPermission=" + sdPermission);

        if (!sdPermission) {
            LogUtil.i(TAG, "请求权限");
            PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, PMS_CODE_WRITE_SD);
        }
        return sdPermission;
    }


    @Override
    public <T> void onItemClick(T entity, int pos) {
        dcEntity = (DocumentsCenter.Child) entity;
        if (pos == STATUS_DOWNLOADING) { //download
            if (permissionSD()) {
                LogUtil.i(TAG, "download");
                dcEntity.downloadStatus.set(pos);
                dcEntity.download();
            }
        } else if (pos == STATUS_RESTART) { // restart download
            LogUtil.i(TAG, "restart download");
            bpDownload(dcEntity);
        } else if (pos == STATUS_PAUSING) {
            LogUtil.i(TAG, "STATUS_PAUSING");
//            disposable.dispose();
            dcEntity.cancel();
        } else if (pos == ACTION_OPEN) {
            openFile(dcEntity);
        }


    }

    /**
     * 直接下载
     *
     * @param entity
     */
    private void download(final DocumentsCenter.Child entity) {
        // 每个callback对应一个Item，因此不用 if(mClient==null)
        mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, NetWorkHelper.UC_BASE_URL, mCallback, entity);
        final String url = entity.getFileLink();

        mClient.largeDownload(mLink)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            LogUtil.i(TAG, "headers=" + response.headers().toString());
                            LogUtil.i(TAG, "downUrls发射文件");
                            // 用这种方式会OOM
//                            FileOutputStream fos = new FileOutputStream(new File(mDir.concat(AppUtil.subStringLast(url, '/'))));
//                            fos.write(body.bytes());
//                            fos.close();
//                            body.close();
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

    /**
     * 断点续传
     *
     * @param entity
     */
    private void bpDownload(final DocumentsCenter.Child entity) {
        final String url = entity.getFileLink();
        File file = DownloadManager.getFutureStudioIconFile();
        downloadLength = 0;
        if (file != null) {
            downloadLength = file.length();
            LogUtil.i(TAG, "length=" + downloadLength);
//            entity.mProgress.set((int) length);
//            entity.max.set((int) length);
        }
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofitProgress(DownloadClient.class, NetWorkHelper.UC_BASE_URL, mCallback, entity);
        }
        mClient.bpDownload("bytes=" + downloadLength + "-", url)//"bytes=" + length + "-",
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            Headers headers = response.headers();
//                            LogUtil.i(TAG, "headers=" + headers.get("Content-Range"));
                            LogUtil.i(TAG, "headers=" + headers.toString());
                            LogUtil.i(TAG, "发射文件");
//                            FileOutputStream fos = new FileOutputStream(new File(mDir.concat(AppUtil.subStringLast(url, '/'))), true);
//                            fos.write(body.bytes());
//                            fos.close();
//                            body.close();

                            return DownloadManager.writeFile(body, AppUtil.subStringLast(url, '/'));


                        } else {
                            LogUtil.i(TAG, "body==null");
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
                        entity.downloadStatus.set(STATUS_DOWNLOADING);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:aBoolean=" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onError:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        disposable.dispose();
                        entity.downloadStatus.set(STATUS_FINISHED);
                    }
                });
    }

    private Disposable disposable;

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

    private void openFile(DocumentsCenter.Child entity) {
        String path = App.rootDir + "DocumentsPDF/" + AppUtil.subStringLast(entity.getFileLink(), '/');
        LogUtil.i(TAG, "path=" + path);
        Intent intent = OpenFileUtil.openFile(path);
        if (intent == null) {
            Toast.makeText(getApplicationContext(), "无法打开！", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtil.getGrantResults(grantResults)) {
            // download

            if (dcEntity != null) {
                dcEntity.download();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
