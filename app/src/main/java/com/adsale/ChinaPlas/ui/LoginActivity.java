package com.adsale.ChinaPlas.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityLoginBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.LoginViewModel;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;

public class LoginActivity extends BaseActivity implements LoginViewModel.OnLoginFinishListener{

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void preView() {
        super.preView();
        barTitle.set(getIntent().getStringExtra(Constant.TITLE));
        LogUtil.i(TAG,"title="+barTitle.get());
    }

    @Override
    protected void initView() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
    }

    @Override
    protected void initData() {
        viewModel = new LoginViewModel(getApplicationContext());
        binding.setLoginViewModel(viewModel);
        binding.setActivity(this);

        viewModel.setOnLoginFinishListener(this);
    }

    public void toRegister(View view){
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    public void login(boolean bool) {
        LogUtil.i(TAG,"login: bool="+bool);
        if(bool){
            toMyAccount();
        }
    }

    private void toMyAccount(){
        Intent intent = new Intent(getApplicationContext(),MyAccountActivity.class);
        intent.putExtra("LoginSync",true);
        startActivity(intent);
        finish();
    }
}
