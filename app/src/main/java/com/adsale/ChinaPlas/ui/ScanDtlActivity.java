package com.adsale.ChinaPlas.ui;

import android.os.Bundle;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityScanDtlBinding;
import com.adsale.ChinaPlas.databinding.ActivityScannerBinding;
import com.adsale.ChinaPlas.utils.AESCrypt;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NCardViewModel;
import com.adsale.ChinaPlas.viewmodel.ScannerViewModel;

import java.security.GeneralSecurityException;

import io.reactivex.Observable;

public class ScanDtlActivity extends BaseActivity {

    private NCardViewModel viewModel;
    private String deviceId;

    @Override
    protected void initView() {
        ActivityScanDtlBinding binding = ActivityScanDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        viewModel = new NCardViewModel(getApplicationContext());
        binding.setViewModel(viewModel);
        binding.setView(this);
    }

    @Override
    protected void initData() {
        String result = getIntent().getStringExtra("result");
        if (!result.isEmpty()) {
            result = result.split(":")[1];
            try {
                result = AESCrypt.decrypt(AESCrypt.AESPASSWORD, result);
                viewModel.getNCInfo(result);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i(TAG, "result= " + result);
    }

    public void callPhone(){

    }

    public void sendEmail(){

    }

    public void add() {

    }

    public void back() {
        finish();
    }
}
