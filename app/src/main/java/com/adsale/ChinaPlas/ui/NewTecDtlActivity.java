package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.databinding.ActivityNewTecDtlBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewTecViewModel;

public class NewTecDtlActivity extends BaseActivity {

    private NewProductInfo entity;
    private NewTecViewModel viewModel;

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.title_new_tec_dtl));
        ActivityNewTecDtlBinding binding = ActivityNewTecDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        entity = getIntent().getParcelableExtra("obj");
        binding.setObj(entity);
        binding.executePendingBindings();

        viewModel = new NewTecViewModel(getApplicationContext(), binding.newTecFrame, entity);
    }

    @Override
    protected void initData() {
        LogUtil.i(TAG, "ENTITY = " + entity.toString());
        viewModel.setupTop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
