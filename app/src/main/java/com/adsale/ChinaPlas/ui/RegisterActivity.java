package com.adsale.ChinaPlas.ui;

import android.net.Uri;
import android.webkit.WebView;
import android.widget.ImageView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityRegisterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.viewmodel.RegisterViewModel;

import java.io.File;

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
        mRegModel.start(webView,binding.ivRegisted,binding.progressBar);

        boolean isLogin = AppUtil.isLogin();
        if(isLogin){
            mRegModel.showPicView();
        }else{
            mRegModel.showWebView();
        }

    }





}
