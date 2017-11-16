package com.adsale.ChinaPlas.ui;

import android.content.Intent;

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
    }

    @Override
    protected void initData() {
        mViewModel.start(getIntent().getStringExtra(Constant.COMPANY_ID), this);
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
}