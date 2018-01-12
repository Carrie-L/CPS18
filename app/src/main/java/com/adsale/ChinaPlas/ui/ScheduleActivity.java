package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.databinding.ActivityScheduleBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ScheduleViewModel;

import static com.adsale.ChinaPlas.utils.Constant.INTENT_SCHEDULE;

public class ScheduleActivity extends BaseActivity {

    private ScheduleViewModel mScheduleModel;
    private RecyclerView recyclerView;
    private HelpView helpDialog;

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

        if (HelpView.isFirstShow(HelpView.HELP_PAGE_SCHEDULE)) {
            showHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_SCHEDULE,
                    HelpView.HELP_PAGE_SCHEDULE).apply();
        }
    }

    public void onItemClick(ScheduleInfo entity) {
        AppUtil.trackViewLog( 192, "Page", entity.getCompanyID(), "ScheduleInfo");
        Intent intent = new Intent(this, ScheduleEditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(INTENT_SCHEDULE, entity);
        intent.putExtra("title",getString(R.string.edit_schedule));
        startActivity(intent);
        overridePendingTransPad();
    }

    public void onAddClick() {
        Intent intent = new Intent(this, ExhibitorAllListActivity.class);
        intent.putExtra("date", mScheduleModel.date.get());
        startActivity(intent);
        overridePendingTransPad();
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
        showHelpPage();
    }

    private void showHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_SCHEDULE);
        helpDialog.show(ft, "Dialog");
    }
}
