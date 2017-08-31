package com.adsale.ChinaPlas.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityNcardListBinding;
import com.adsale.ChinaPlas.viewmodel.NCardViewModel;

public class NCardListActivity extends BaseActivity {

    private NCardViewModel viewModel;
    private ActivityNcardListBinding binding;

    @Override
    protected void initView() {
        binding = ActivityNcardListBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        viewModel = new NCardViewModel(getApplicationContext());
        binding.setViewModel(viewModel);

    }

    @Override
    protected void initData() {
        viewModel.onListStart();

        EditText etFilter=binding.etSearch;
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    viewModel.resetList();
                } else {
                    viewModel.search(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
