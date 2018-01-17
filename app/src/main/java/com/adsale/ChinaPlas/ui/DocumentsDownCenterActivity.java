package com.adsale.ChinaPlas.ui;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.PDFDownloadAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.databinding.ActivityDocumentsDownCenterBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * 文档下载中心
 */
public class DocumentsDownCenterActivity extends BaseActivity {

    private RecyclerView recyclerView;


    @Override
    protected void initView() {
        ActivityDocumentsDownCenterBinding binding = ActivityDocumentsDownCenterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        recyclerView = binding.dcRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void initData() {

        getList();


    }

    private void getList() {
        Type listType = new TypeToken<ArrayList<DocumentsCenter>>() {
        }.getType();
        ArrayList<DocumentsCenter> list = new Gson().fromJson(FileUtil.readFilesDirFile(Constant.TXT_PDF_CENTER_INFO), listType);

        ArrayList<DocumentsCenter.Child> children = new ArrayList<>();
        DocumentsCenter.Child parent;
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
            children.addAll(list.get(i).Items);
        }
        LogUtil.i(TAG, "children=" + children.size() + "," + children.toString());

        PDFDownloadAdapter adapter = new PDFDownloadAdapter(list, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }


}
