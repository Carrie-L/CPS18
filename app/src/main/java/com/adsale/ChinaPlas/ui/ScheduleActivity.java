package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.databinding.ActivityScheduleBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ScheduleViewModel;

import java.util.ArrayList;

import static android.R.attr.id;
import static com.adsale.ChinaPlas.utils.Constant.INTENT_SCHEDULE;

public class ScheduleActivity extends BaseActivity {

    private ScheduleViewModel mScheduleModel;
    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        ActivityScheduleBinding binding = ActivityScheduleBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mScheduleModel = new ScheduleViewModel();
        binding.setScheduleModel(mScheduleModel);
        binding.setView(this);
        recyclerView = binding.rvSchedule;
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        mScheduleModel.onStart();
        ScheduleAdapter adapter = new ScheduleAdapter(mScheduleModel.scheduleInfos, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    public void onItemClick(ScheduleInfo entity) {
        Intent intent = new Intent(this, ScheduleEditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(INTENT_SCHEDULE, entity);
        startActivity(intent);
    }

    public void onAddClick() {
        Intent intent = new Intent(this, ExhibitorAllListActivity.class);
        intent.putExtra("date", mScheduleModel.date.get());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean updated = App.mSP_Config.getBoolean("ScheduleListUpdate", false);
        LogUtil.i(TAG, "onResume::updated=" + updated);
        if (updated) {
            mScheduleModel.onStart();
            App.mSP_Config.edit().putBoolean("ScheduleListUpdate", false).apply();// must.
        }
    }

    public void onHelpPage() {
        HelpView helpView = new HelpView(this, HelpView.HELP_PAGE_SCHEDULE);
        helpView.show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==RESULT_OK&&requestCode==Constant.REQUEST_CODE_ADD_SCHEDULE){
//            Exhibitor exhibitor = data.getParcelableExtra(Constant.EXHIBITOR);
//            mScheduleModel.setCompanyId(exhibitor.getCompanyID());
//            mScheduleModel.etTitle.set(exhibitor.getCompanyName());
//            mScheduleModel.etLocation.set(exhibitor.getBoothNo());
//        }
//    }
}
