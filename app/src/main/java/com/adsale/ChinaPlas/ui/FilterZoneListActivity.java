package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.TextAdapter;
import com.adsale.ChinaPlas.adapter.ZoneAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 展商专区
 */

public class FilterZoneListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ArrayList<Zone> mList = new ArrayList<>();
    private ArrayList<ExhibitorFilter> filters;

    @Override
    protected void preView() {
        super.preView();
        isChangeTitleHomeIcon = true;
        mTypePrefix="Page_SearchByZone";
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
        FilterRepository mRepository = FilterRepository.getInstance();
        mList = mRepository.getZones();

        filters = new ArrayList<>();
        ZoneAdapter adapter = new ZoneAdapter(mList, filters);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int size = mList.size();
        Zone entity;
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
