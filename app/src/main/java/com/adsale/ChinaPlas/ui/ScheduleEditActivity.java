package com.adsale.ChinaPlas.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityScheduleItemBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ScheduleViewModel;

/**
 * 日程表 add or edit
 */
public class ScheduleEditActivity extends BaseActivity implements ScheduleViewModel.ScheduleEditListenr {

    private ScheduleViewModel mScheduleModel;

    @Override
    protected void initView() {
        ActivityScheduleItemBinding binding = ActivityScheduleItemBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);

        Bundle bundle = getIntent().getExtras();
        long id = bundle.getLong("id");
        int dateIndex = bundle.getInt("dateIndex");

        if (id == 0) {
            mScheduleModel = new ScheduleViewModel(getApplicationContext(), dateIndex);
        } else {
            mScheduleModel = new ScheduleViewModel(getApplicationContext(), id);
        }
        binding.setScheduleModel(mScheduleModel);
    }

    @Override
    protected void initData() {
        mScheduleModel.setScheduleEditListener(this);
    }

    @Override
    public void onSameTimeSave() {
        AppUtil.showAlertDialog(this, getString(R.string.ask_schedule), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScheduleModel.insert();
            }
        });
    }

    @Override
    public void toExhibitorDtl(String companyId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.COMPANY_ID, companyId);
        intent(ExhibitorDtlActivity.class, bundle);
    }


    @Override
    public void onFinish() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScheduleModel.onActivityDestroyed();
    }
}
