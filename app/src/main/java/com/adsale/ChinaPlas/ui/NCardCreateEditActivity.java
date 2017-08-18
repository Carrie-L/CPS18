package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityNcardCreateEditBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.viewmodel.NCardViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.name;

/**
 * 名片创建和编辑
 * todo 获取device id
 */
public class NCardCreateEditActivity extends BaseActivity implements NCardViewModel.OnNCSavedListener {

    private ActivityNcardCreateEditBinding binding;
    private NCardViewModel viewModel;

    @Override
    protected void initView() {
        binding = ActivityNcardCreateEditBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        viewModel = new NCardViewModel(getApplicationContext());
        binding.setNcModel(viewModel);
    }

    @Override
    protected void initData() {
        viewModel.setOnNCSavedListener(this);
        viewModel.onStart();
    }

    @Override
    public void setSelection() {
        binding.etCompany.setSelection(viewModel.company.get().length());
        binding.etName.setSelection(viewModel.name.get().length());
        binding.etTitle.setSelection(viewModel.title.get().length());
        binding.etPhone1.setSelection(viewModel.phone1.get().length());
        binding.etPhone2.setSelection(viewModel.phone2.get().length());
        binding.etEmail.setSelection(viewModel.email.get().length());
        binding.etQq.setSelection(viewModel.qq.get().length());
        binding.etWeChat.setSelection(viewModel.weChat.get().length());
        binding.etTitle.setSelection(viewModel.title.get().length());
    }

    @Override
    public boolean checkNotNull() {
        if (viewModel.company.get()==null||viewModel.company.get().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_input_company), Toast.LENGTH_SHORT).show();
            binding.etCompany.requestFocus();
            return false;
        }

        // 姓名
        if (viewModel.name.get()==null||viewModel.name.get().trim().equals("")) {
            Toast.makeText(getApplicationContext(),getString(R.string.toast_input_name), Toast.LENGTH_SHORT).show();
            binding.etName.requestFocus();
            return false;
        }

        String check = "[a-zA-Z.\u4e00-\u9fa5\\s]+";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(viewModel.name.get());
        if (!matcher.matches()) {
            AppUtil.showAlertDialog(this, getString(R.string.toast_input_name_1), null);
            binding.etName.requestFocus();
            return false;
        }

        // 职位
        if (TextUtils.isEmpty(viewModel.title.get())) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_input_title), Toast.LENGTH_SHORT).show();
            binding.etTitle.requestFocus();
            return false;
        }

        // 区号
        if (TextUtils.isEmpty(viewModel.phone1.get())) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_input_country), Toast.LENGTH_SHORT).show();
            binding.etPhone1.requestFocus();
            return false;
        }

        // 电话
        if (TextUtils.isEmpty(viewModel.phone2.get())) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_input_phone_2), Toast.LENGTH_SHORT).show();
            binding.etPhone2.requestFocus();
            return false;
        }

        // 邮箱
        if (TextUtils.isEmpty(viewModel.email.get())) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_input_email), Toast.LENGTH_SHORT).show();
            binding.etEmail.requestFocus();
            return false;
        }

//            check="^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        check = "[\\w\\-.]+@([\\w-]+\\.)+[a-z]{2,3}";

        regex = Pattern.compile(check);
        matcher = regex.matcher(viewModel.email.get());
        if (!matcher.matches()) {
            AppUtil.showAlertDialog(this, getString(R.string.register_msg004), null);
            binding.etEmail.requestFocus();
            return false;
        }

//        deviceId = SystemMethod.getDeviceId(getApplicationContext());
//        Log.d("TAG", "deviceIddeviceIddeviceIddeviceId:" + deviceId);

        return true;
    }

    @Override
    public void saved() {
        Intent intent = new Intent(this,NCardActivity.class);
        startActivity(intent);
        finish();
    }


}
