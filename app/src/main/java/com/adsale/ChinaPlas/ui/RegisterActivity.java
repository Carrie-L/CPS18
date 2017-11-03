package com.adsale.ChinaPlas.ui;

import android.webkit.WebView;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityRegisterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.viewmodel.RegisterViewModel;

public class RegisterActivity extends BaseActivity {
    private RegisterViewModel mRegModel;
    private ActivityRegisterBinding binding;

    @Override
    protected void initView() {
        binding = ActivityRegisterBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        mRegModel = new RegisterViewModel(getApplicationContext());
        binding.setRegModel(mRegModel);
    }

    @Override
    protected void initData() {
        WebView webView =binding.regWeb;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        mRegModel.start(webView,binding.ivRegisted,binding.progressBar);


    }





}
