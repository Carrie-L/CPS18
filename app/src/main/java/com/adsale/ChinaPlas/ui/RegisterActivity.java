package com.adsale.ChinaPlas.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.RegPicViewBinding;
import com.adsale.ChinaPlas.databinding.RegWebBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.viewmodel.RegisterViewModel;

import java.io.File;

public class RegisterActivity extends BaseActivity {

    private RegWebBinding regWebBinding;
    private RegPicViewBinding regPicBinding;
    private boolean isLogin;

    @Override
    protected void initView() {
//       View mContentFrame= getLayoutInflater().inflate(R.layout.activity_register,mBaseFrameLayout);
        RegisterViewModel registerViewModel = new RegisterViewModel();

        isLogin = AppUtil.isLogin();
        if(isLogin){
            regPicBinding = RegPicViewBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
            regPicBinding.setRegModel(registerViewModel);
        }else{
            regWebBinding = RegWebBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
            regWebBinding.setRegModel(registerViewModel);
        }



    }


    @Override
    protected void initData() {
        if(isLogin){
            processPicView();
        }else{
            processWebView();
        }
    }

    public void processPicView(){
        ImageView ivRegisted = regPicBinding.ivRegisted;
        ivRegisted.setImageURI(Uri.fromFile(new File(App.memoryFileDir+"/reg.png")));
//        ivRegisted.setImageBitmap();
    }

    public void processWebView(){

    }

}
