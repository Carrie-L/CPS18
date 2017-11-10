package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.TextAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.Floor;
import com.adsale.ChinaPlas.dao.Hall;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 展馆列表
 */

public class FilterHallListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ArrayList<Floor> mList = new ArrayList<>();
    private ArrayList<ExhibitorFilter> filters;

    @Override
    protected void initView() {
        View view = getLayoutInflater().inflate(R.layout.activity_filter_hall, mBaseFrameLayout, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.hall_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void initData() {
        FilterRepository mRepository = FilterRepository.getInstance();
        mRepository.initFloorDao();
        mList = mRepository.getFloors();

        filters = new ArrayList<>();
        TextAdapter<Floor> adapter = new TextAdapter<>(mList, filters);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int size = mList.size();
        Floor entity;
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
