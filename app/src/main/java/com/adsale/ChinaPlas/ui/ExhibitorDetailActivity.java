package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.viewmodel.ExhibitorDtlViewModel;

public class ExhibitorDetailActivity extends BaseActivity {

    private ExhibitorDtlViewModel mViewModel;

    @Override
    protected void initView() {
        ActivityExhibitorDetailBinding binding =ActivityExhibitorDetailBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        mViewModel = new ExhibitorDtlViewModel(getApplicationContext(),binding.flDtlContent);
        binding.setModel(mViewModel);
    }

    @Override
    protected void initData() {
        mViewModel.start(getIntent().getStringExtra(Constant.COMPANY_ID));
    }
}
