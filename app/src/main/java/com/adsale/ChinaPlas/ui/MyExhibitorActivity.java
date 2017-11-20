package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.MyExhibitorAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityMyExhibitorBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DividerItemDecoration;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;
import com.adsale.ChinaPlas.viewmodel.MyExhibitorViewModel;

public class MyExhibitorActivity extends BaseActivity implements OnIntentListener{


    private MyExhibitorViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ActivityMyExhibitorBinding binding;
    private MyExhibitorAdapter adapter;

    @Override
    protected void initView() {
        binding = ActivityMyExhibitorBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        viewModel = new MyExhibitorViewModel(getApplicationContext());
        binding.setModel(viewModel);
        binding.executePendingBindings();
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        recyclerView = binding.myExhibitorRv;
        initRecyclerView();
        RecyclerViewScrollTo mRVScrollTo = new RecyclerViewScrollTo(layoutManager, recyclerView);
        viewModel.start(binding.sideLetter, mRVScrollTo);

        adapter = new MyExhibitorAdapter(viewModel.mExhibitors,this);
        recyclerView.setAdapter(adapter);
    }

    public void onSync() {
        viewModel.setAdapter(adapter);
        viewModel.sync();
    }

    public void onHelpPage() {

    }

    public void onHallMap() {
        intent(FloorDistributeActivity.class);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
       Intent intent = new Intent(this, ExhibitorDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.COMPANY_ID, ((Exhibitor) entity).getCompanyID());
        startActivity(intent);
        overridePendingTransPad();
    }
}
