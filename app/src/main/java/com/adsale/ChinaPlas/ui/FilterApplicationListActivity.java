package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ApplicationAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 应用分类列表
 */

public class FilterApplicationListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<ApplicationIndustry> mList = new ArrayList<>();
    private ArrayList<ExhibitorFilter> filters;

    public static final int TYPE_NEW_TEC_PRODUCT = 1001; /* 列表为 新技术产品 - 筛选 - 产品 */
    public static final int TYPE_NEW_TEC_APPLICATIONS = 1002; /* 列表为 新技术产品 - 筛选 - 应用 */

    @Override
    protected void initView() {
        View view = getLayoutInflater().inflate(R.layout.activity_filter_application, mBaseFrameLayout, true);
        recyclerView = view.findViewById(R.id.application_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void initData() {
        int type = getIntent().getIntExtra("type", 0);
        int index = getIntent().getIntExtra("index",1);
        LogUtil.i(TAG,"type="+type);
        if (getIntent().getIntExtra("type", 0) == TYPE_NEW_TEC_PRODUCT) {
            mList.add(new ApplicationIndustry("A", getString(R.string.new_tec_Product_A)));
            mList.add(new ApplicationIndustry("B", getString(R.string.new_tec_Product_B)));
        } else if (getIntent().getIntExtra("type", 0) == TYPE_NEW_TEC_APPLICATIONS) {
            NewTecRepository repository = NewTecRepository.newInstance();
            mList = repository.getApplications(mList);
        } else {
            FilterRepository mRepository = FilterRepository.getInstance();
            mRepository.initAppIndustryDao();
            mList = mRepository.getApplicationIndustries();
        }

        filters = new ArrayList<>();
        ApplicationAdapter adapter = new ApplicationAdapter(mList, filters);
        recyclerView.setAdapter(adapter);
        adapter.setIndex(index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int size = mList.size();
        ApplicationIndustry entity;
        for (int i = 0; i < size; i++) {
            entity = mList.get(i);
            entity.isSelected.set(false);
            mList.set(i, entity);
        }
        entity = null;
        mList.clear();
        finish();
    }

    private void setResultData() {
        Intent intent = new Intent();
        intent.putExtra("data", filters);
        LogUtil.i(TAG, "onDestroy::filters=" + filters.size() + "," + filters.toString());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        setResultData();
        super.onBackPressed();
    }

    @Override
    public void back() {
        setResultData();
        super.back();
    }


}
