package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ApplicationAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.baidu.mobstat.StatService;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 应用分类列表
 */

public class FilterApplicationListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<Application> mList = new ArrayList<>();
    private ArrayList<ExhibitorFilter> filters;

    public static final String TYPE_NEW_TEC_PRODUCT = "PRD"; /* 列表为 新技术产品 - 筛选 - 产品 */
    public static final String TYPE_NEW_TEC_APPLICATIONS = "APT"; /* 列表为 新技术产品 - 筛选 - 应用 */
    public static final String TYPE_NEW_TEC_THEMATIC = "THS"; /* 列表为 新技术产品 - 筛选 - 主题专集 */
    public static final String TYPE_EVENT_APPLICATIONS = "EAP"; /* 列表为 同期活动 - 筛选 - 应用 */

    @Override
    protected void preView() {
        super.preView();
        isChangeTitleHomeIcon = true;
        mTypePrefix = "Page_SearchByApplication";
    }

    @Override
    protected void initView() {
        View view = getLayoutInflater().inflate(R.layout.activity_filter_application, mBaseFrameLayout, true);
        recyclerView = view.findViewById(R.id.application_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

    @Override
    protected void initData() {
        int index = getIntent().getIntExtra("index", 1);
        String type = getIntent().getStringExtra("MainTypeId");
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(TYPE_EVENT_APPLICATIONS)) { // 同期活动
                OtherRepository otherRepo = OtherRepository.getInstance();
                mList = otherRepo.getEventApplications();
            } else { // 新技术产品
                NewTecRepository repository = NewTecRepository.newInstance();
                mList = repository.getNewTecFilterList(mList, getIntent().getStringExtra("MainTypeId"));
            }
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
        Application entity;
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
        if (filters.size() > 0) {
            App.mLogHelper.eventLog(403, "FilterE", "Application", App.mLogHelper.getFiltersName(filters));
            StatService.onEvent(getApplicationContext(), "FilterE", App.mLogHelper.getTrackingName());
        }
        Intent intent = new Intent();
        intent.putExtra("data", filters);
        LogUtil.i(TAG, "onDestroy::filters=" + filters.size() + "," + filters.toString());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        filters.clear();
        setResultData();
        super.onBackPressed();
    }

    @Override
    public void back() {
        filters.clear();
        setResultData();
        super.back();
    }

    @Override
    protected void onReplaceHomeClick() {
        super.onReplaceHomeClick();
        setResultData();
        finish();
    }
}
