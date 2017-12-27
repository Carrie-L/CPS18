package com.adsale.ChinaPlas.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.databinding.ActivityScheduleItemBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.viewmodel.ScheduleEditViewModel;

import static com.adsale.ChinaPlas.R.id.language;

/**
 * 日程表 add or edit
 */
public class ScheduleEditActivity extends BaseActivity implements ScheduleEditViewModel.ScheduleEditListener {

    private ScheduleEditViewModel mEditModel;

    @Override
    protected void initView() {
        ActivityScheduleItemBinding binding = ActivityScheduleItemBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mEditModel = new ScheduleEditViewModel(getApplicationContext());
        binding.setScheduleModel(mEditModel);
        mEditModel.setScheduleEditListener(this);
    }

    @Override
    protected void initData() {
        Exhibitor exhibitor = getIntent().getParcelableExtra(Constant.INTENT_EXHIBITOR);
        if (exhibitor != null) {
            barTitle.set(getString(R.string.title_add_schedule));
            mEditModel.isEdit.set(false);
            mEditModel.setCompanyId(exhibitor.getCompanyID());
            mEditModel.etStartDate.set(getIntent().getStringExtra("date"));
            mEditModel.etTitle.set(exhibitor.getCompanyName(language));
            mEditModel.etLocation.set(exhibitor.getBoothNo());
            mEditModel.etNote.set(exhibitor.getNote());
        }

        ScheduleInfo scheduleInfo = getIntent().getParcelableExtra(Constant.INTENT_SCHEDULE);
        if (scheduleInfo != null) {
            mEditModel.isEdit.set(true);
            mEditModel.setId(scheduleInfo.getId());
            mEditModel.setCompanyId(scheduleInfo.getCompanyID());
            mEditModel.etTitle.set(scheduleInfo.getTitle());
            mEditModel.etLocation.set(scheduleInfo.getLocation());
            mEditModel.etNote.set(scheduleInfo.getNote());
            mEditModel.etHour.set(String.valueOf(scheduleInfo.getHour()));
            mEditModel.etMinute.set(String.valueOf(scheduleInfo.getMinute()));
            mEditModel.etStartDate.set(scheduleInfo.getStartDate());
            mEditModel.etStartTime.set(scheduleInfo.getStartTime());
        }

        mEditModel.setFragmentManager(getFragmentManager());
        if (HelpView.isFirstShow(HelpView.HELP_PAGE_SCHEDULE_DTL)) {
            mEditModel.showHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_SCHEDULE_DTL,
                    HelpView.HELP_PAGE_SCHEDULE_DTL).apply();
        }
    }

    @Override
    public void onSameTimeSave() {
        AppUtil.showAlertDialog(this, getString(R.string.ask_schedule), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEditModel.insertOrReplace();
            }
        });
    }

    @Override
    public void toExhibitorDtl(String companyId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.COMPANY_ID, companyId);
        intent(ExhibitorDetailActivity.class, bundle);
    }

    @Override
    public void onFinish(boolean change) {
        App.mSP_Config.edit().putBoolean("ScheduleListUpdate", change).apply();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditModel.onActivityDestroyed();
    }

}
