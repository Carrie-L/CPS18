package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.PDFDownloadAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.databinding.ActivityDocumentsDownCenterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.DownloadCenterViewModel;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.ACTION_OPEN;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.PDF_LINK;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_DOWNLOADING;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_FINISHED;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

/**
 * 文档下载中心
 */
public class DocumentsDownCenterActivity extends BaseActivity implements OnItemClickCallback {

    private RecyclerView recyclerView;
    private String mDir;

    private DocumentsCenter.Child dcEntity;
    private ArrayList<DocumentsCenter> list;
    private DownloadCenterViewModel downViewModel;

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
        downViewModel = DownloadCenterViewModel.getInstance(getApplication());

        getList();

        mDir = Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/DocumentsPDF/";
        FileUtil.createFile(Environment.getExternalStorageDirectory() + "/com.adsale.ChinaPlas/");
        FileUtil.createFile(mDir);
    }

    private void getList() {
        Type listType = new TypeToken<ArrayList<DocumentsCenter>>() {
        }.getType();
        list = Parser.getGson().fromJson(FileUtil.readFilesDirFile(Constant.TXT_PDF_CENTER_INFO), listType);

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

                if (AppUtil.getPDFDownStatus(itemChild.getId()) == STATUS_FINISHED) {
                    itemChild.downloadStatus.set(STATUS_FINISHED);
                } else {
                    try {
                        itemChild = downViewModel.getDownloadingEntity(itemChild.getId());
                    } catch (NullPointerException e) {
                        LogUtil.i(TAG, "not find its download entity.");
                    }
                }
                list.get(i).Items.set(j, itemChild);

            }
            parents.add(i, list.get(i).Items);
        }
        LogUtil.i(TAG, "children=" + children.size() + "," + children.toString());
        LogUtil.i(TAG, "parents=" + parents.size() + "," + parents.toString());

        PDFDownloadAdapter adapter = new PDFDownloadAdapter(parents, children, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
    }

    private boolean permissionSD() {
        boolean sdPermission = PermissionUtil.checkPermission(getApplicationContext(), PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        LogUtil.i(TAG, "sdPermission=" + sdPermission);

        if (!sdPermission) {
            PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, PMS_CODE_WRITE_SD);
        }
        return sdPermission;
    }


    @Override
    public <T> void onItemClick(T entity, int pos) {
        dcEntity = (DocumentsCenter.Child) entity;
        if (pos == STATUS_DOWNLOADING) { //download
            if (permissionSD()) {
                startDownload();
            }
        } else if (pos == ACTION_OPEN) {
            openFile(dcEntity);
        }
    }

    private void startDownload() {
        if (dcEntity == null) {
            return;
        }
        dcEntity.downloadStatus.set(STATUS_DOWNLOADING);
        dcEntity.mProgress.set(0);
        dcEntity.max.set(0);
        downViewModel.add(dcEntity);
        dcEntity.download(downViewModel);
    }

    private void openFile(DocumentsCenter.Child entity) {
        String path = mDir + AppUtil.subStringLast(entity.getFileLink(), '/');
        LogUtil.i(TAG, "path=" + path);

        Intent intent = new Intent(getApplicationContext(), WebContentActivity.class);
        intent.putExtra(Constant.WEB_URL, "PDF:" + path);
        intent.putExtra(Constant.TITLE, entity.getFileName());
        startActivity(intent);
        overridePendingTransPad();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.getGrantResults(grantResults)) {
            startDownload();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
