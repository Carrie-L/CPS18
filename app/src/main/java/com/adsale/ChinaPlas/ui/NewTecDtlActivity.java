package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityNewTecDtlBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewTecViewModel;
import com.bumptech.glide.Glide;

public class NewTecDtlActivity extends BaseActivity implements OnIntentListener {

    private NewProductInfo entity;
    private NewTecViewModel viewModel;

    @Override
    protected void initView() {
        entity = getIntent().getParcelableExtra("obj");
        barTitle.set(entity.getProductName());
        ActivityNewTecDtlBinding binding = ActivityNewTecDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setObj(entity);
        binding.executePendingBindings();
        viewModel = new NewTecViewModel(getApplicationContext(), binding.newTecFrame, entity, this);
        binding.setModel(viewModel);
        LogUtil.i(TAG, "entity.LogoImageLink=" + entity.LogoImageLink);
        if (!TextUtils.isEmpty(entity.LogoImageLink)) {
            Glide.with(getApplicationContext()).load(entity.LogoImageLink).into(binding.ivLogo);
        } else {
            binding.ivLogo.setVisibility(View.GONE);
        }
        binding.tvDes.setText(entity.getDescription().replace("\\n", "\n"));

    }

    @Override
    protected void initData() {
        LogUtil.i(TAG, "ENTITY = " + entity.toString());
        viewModel.setupTop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls == null) {
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("url", (String) entity);
            startActivity(intent);
            overridePendingTransPad();
        } else if (toCls.getSimpleName().equals("FloorDetailActivity")) {
            String booth = (String) entity;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < booth.length(); i++) {
                char c = booth.charAt(i);
                if (Character.isLetter(c)) {
                    LogUtil.i(TAG, c + " is a letter : ");
                    break;
                }
                sb.append(c);
            }
            sb.append("H");
            LogUtil.i(TAG, "sb=" + sb.toString());
            Intent intent = new Intent(getApplicationContext(), FloorDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("BOOTH", booth);
            intent.putExtra("HALL", sb.toString());
            startActivity(intent);
            overridePendingTransPad();
        } else if (toCls.getSimpleName().equals("ExhibitorDetailActivity")) {
            Intent intent = new Intent(getApplicationContext(), ExhibitorDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constant.COMPANY_ID, (String) entity);
            startActivity(intent);
            overridePendingTransPad();
        }

    }
}
