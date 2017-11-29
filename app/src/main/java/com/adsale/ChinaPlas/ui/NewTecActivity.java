package com.adsale.ChinaPlas.ui;

import android.os.Bundle;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.databinding.ActivityNewTecBinding;
import com.adsale.ChinaPlas.ui.view.SideDataView;

import java.util.ArrayList;

public class NewTecActivity extends BaseActivity {

    private ActivityNewTecBinding binding;
    private SideDataView mSideDataView;
    private NewTecRepository mRepository;
    private ArrayList<NewProductInfo> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tec);
    }

    @Override
    protected void initView() {
        binding = ActivityNewTecBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();
        mSideDataView = binding.viewSideData;
    }

    @Override
    protected void initData() {
        ArrayList<String> letters = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            letters.add(i + "a");
        }
        mSideDataView.setupSideLitter(letters);
    }

    private void getList() {
        mRepository = NewTecRepository.newInstance();
        products = mRepository.getAllProductInfoList();

    }

    public void onFilter() {

    }


}
