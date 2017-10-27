package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.TechAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityTechnicalListBinding;
import com.adsale.ChinaPlas.viewmodel.TechViewModel;

public class TechnicalListActivity extends BaseActivity implements OnIntentListener {

    private TechViewModel model;
    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        ActivityTechnicalListBinding binding = ActivityTechnicalListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        model = new TechViewModel(getApplicationContext());
        binding.setTechModel(model);
        binding.executePendingBindings();
        recyclerView = binding.rvSeminar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void initData() {
        TechAdapter adapter = new TechAdapter(getApplicationContext(), model.getList(), this);
        recyclerView.setAdapter(adapter);

        model.onStart(this, adapter);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = new Intent(this, toCls);
        intent.putExtra("Info", (SeminarInfo) entity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
