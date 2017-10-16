package com.adsale.ChinaPlas.ui;

import android.databinding.DataBindingUtil;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityDownloadCenterBinding;
import com.adsale.ChinaPlas.viewmodel.DownCenterViewModel;

/**
 * Created by Carrie on 2017/10/16.
 */

public class DownloadCenterActivity extends BaseActivity {

    @Override
    protected void initView() {
        ActivityDownloadCenterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_download_center);
        DownCenterViewModel model = new DownCenterViewModel();
        binding.setDownModel(model);
    }

    @Override
    protected void initData() {

    }

}
