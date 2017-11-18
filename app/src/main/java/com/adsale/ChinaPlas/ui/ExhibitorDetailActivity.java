package com.adsale.ChinaPlas.ui;

import android.content.Intent;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ExhibitorDtlViewModel;

public class ExhibitorDetailActivity extends BaseActivity implements OnIntentListener {

    private ExhibitorDtlViewModel mViewModel;

    @Override
    protected void initView() {
        ActivityExhibitorDetailBinding binding = ActivityExhibitorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mViewModel = new ExhibitorDtlViewModel(getApplicationContext(), binding.flDtlContent);
        binding.setModel(mViewModel);
        mViewModel.start(getIntent().getStringExtra(Constant.COMPANY_ID), this,binding.viewstubDtlView.getViewStub());
    }

    @Override
    protected void initData() {

    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "entity=" + entity.toString());

//        Intent intent = new Intent(this,ExhibitorAllListActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

//        if (toCls.getSimpleName().contains("NewTecDtlActivity")) {
//
//        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean updated = App.mSP_Config.getBoolean("ScheduleListUpdate", false);
        LogUtil.i(TAG, "onResume::updated=" + updated);
        if (updated) {
            mViewModel.updateSchedule();
            App.mSP_Config.edit().putBoolean("ScheduleListUpdate", false).apply();// must.
        }
    }
}
