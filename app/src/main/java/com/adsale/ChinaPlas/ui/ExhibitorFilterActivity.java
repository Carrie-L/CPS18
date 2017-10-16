package com.adsale.ChinaPlas.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;

public class ExhibitorFilterActivity extends BaseActivity {

    private ActivityExhibitorFilterBindinig bindinig;

    @Override
    protected void initView() {
        bindinig = DataBindingUtil.setContentView(this, R.layout.activity_exhibitor_filter);

    }

    @Override
    protected void initData() {

    }
}
