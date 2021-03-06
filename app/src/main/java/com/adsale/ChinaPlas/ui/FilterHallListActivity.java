package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.HallAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Map;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.baidu.mobstat.StatService;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 展馆列表
 */

public class FilterHallListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ArrayList<Map> mList = new ArrayList<>();
    private ArrayList<ExhibitorFilter> filters;

    @Override
    protected void preView() {
        super.preView();
        isChangeTitleHomeIcon = true;
        mTypePrefix = "Page_SearchByHall";
    }

    @Override
    protected void initView() {
        View view = getLayoutInflater().inflate(R.layout.activity_filter_hall, mBaseFrameLayout, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.hall_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

    @Override
    protected void initData() {
        FilterRepository mRepository = FilterRepository.getInstance();
        mRepository.initFloorDao();
        mList = mRepository.getFloorsWiithExhibitor();

        filters = new ArrayList<>();
        HallAdapter adapter = new HallAdapter(mList, filters);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int size = mList.size();
        Map entity;
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
            App.mLogHelper.eventLog(403, "FilterE", "Hall", App.mLogHelper.getFiltersName(filters));
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
