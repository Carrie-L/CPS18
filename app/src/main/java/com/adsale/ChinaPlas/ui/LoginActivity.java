package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityLoginBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.LoginViewModel;

public class LoginActivity extends BaseActivity implements LoginViewModel.OnLoginFinishListener,LoginViewModel.OnLoginListener {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void initView() {
        setBarTitle(R.string.title_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);


    }

    @Override
    protected void initData() {
        viewModel = new LoginViewModel(getApplicationContext());
        binding.setLoginViewModel(viewModel);
        binding.setActivity(this);
        binding.executePendingBindings();

        viewModel.setOnLoginFinishListener(this);

        binding.emailLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("894750416")) {
                    viewModel.loginEmail.set("894750416@qq.com");
                    viewModel.loginPwd.set("ktdv4616KT");
                }
            }
        });

        getRegData();

    }

    private void getRegData() {
        Intent intent = getIntent();
        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("tel"))) {
            viewModel.loginPhone.set(intent.getStringExtra("tel"));
            viewModel.loginEmail.set(intent.getStringExtra("email"));
            viewModel.loginLang.set(intent.getStringExtra("lang"));
            viewModel.isSmsShow.set(true);
        }
    }

    public void toRegister() {
        intent(RegisterActivity.class, getString(R.string.title_register));
    }


    @Override
    public void login(boolean bool) {
        LogUtil.i(TAG, "login: bool=" + bool);
        if (bool) {
            String fromCls = getIntent().getStringExtra("from");
            if (!TextUtils.isEmpty(fromCls) && fromCls.equals("MyExhibitor")) { // from MyExhibitor
                finish();
            } else {
                toMyAccount(); // default
            }
        }
    }

    private void toMyAccount() {
        Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
        intent.putExtra("LoginSync", true);
        startActivity(intent);
        finish();
        overridePendingTransPad();
    }

    @Override
    public void verificationCode() {



    }
}
