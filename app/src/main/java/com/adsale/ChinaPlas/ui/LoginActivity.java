package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.view.View;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityLoginBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void initView() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
    }

    @Override
    protected void initData() {
        viewModel = new LoginViewModel(getApplicationContext());
        binding.setLoginViewModel(viewModel);
        binding.setActivity(this);
    }

    public void toRegister(View view){
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }




}
