package com.adsale.ChinaPlas.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.DocumentsItemTestAdapter;
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
    }

    @Override
    protected void initData() {

        getList();


    }

    private void getList() {
        Type listType = new TypeToken<ArrayList<DocumentsCenter>>() {
        }.getType();
        ArrayList<DocumentsCenter> list = new Gson().fromJson(FileUtil.readFilesDirFile(Constant.TXT_PDF_CENTER_INFO), listType);

        LogUtil.i(TAG, "list=" + list.size() + "," + list.toString());
        DocumentsItemTestAdapter adapter = new DocumentsItemTestAdapter(list, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
