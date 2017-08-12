package com.adsale.ChinaPlas.ui;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.databinding.ActivityScheduleBinding;
import com.adsale.ChinaPlas.viewmodel.ScheduleViewModel;

import java.util.ArrayList;

import static android.R.attr.id;

public class ScheduleActivity extends BaseActivity{

    private ScheduleViewModel mScheduleModel;
    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        ActivityScheduleBinding binding = ActivityScheduleBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mScheduleModel = new ScheduleViewModel(getApplicationContext());
        binding.setScheduleModel(mScheduleModel);
        binding.setView(this);
        recyclerView = binding.rvSchedule;
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        ScheduleAdapter adapter = new ScheduleAdapter(new ArrayList<ScheduleInfo>(0), this);
        recyclerView.setAdapter(adapter);
    }

    public void onItemClick(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);
        intent(ScheduleEditActivity.class,bundle);
    }

    public void onAddClick(){
        intent(ExhibitorAllListActivity.class);
    }

    private void toAddSchedule(){
        Bundle bundle = new Bundle();
        bundle.putInt("dateIndex",mScheduleModel.dateIndex.get());
        intent(ScheduleEditActivity.class,bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScheduleModel.onStart(mScheduleModel.dateIndex.get());
    }

}
