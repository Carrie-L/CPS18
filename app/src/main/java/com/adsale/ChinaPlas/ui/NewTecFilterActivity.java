package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableField;
import android.support.v7.widget.SwitchCompat;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityNewTecFilterBinding;
import com.adsale.ChinaPlas.ui.view.FilterView;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

public class NewTecFilterActivity extends BaseActivity implements OnIntentListener{
    public final ObservableField<String> etKeyword = new ObservableField<>("");
    private FilterView industryFilterView;
    private FilterView applicationFilterView;
    private ArrayList<ExhibitorFilter> results;
    private ArrayList<ExhibitorFilter> allFilters = new ArrayList<>();
    private SwitchCompat switchNewTec;

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.title_filter));
        ActivityNewTecFilterBinding binding = ActivityNewTecFilterBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        binding.setUi(this);
        industryFilterView = binding.industryFilterView;
        applicationFilterView = binding.applicationFilterView;
        switchNewTec = binding.switchNewTec;

    }

    @Override
    protected void initData() {
        industryFilterView.initData(0, getString(R.string.filter_product), getResources().getDrawable(R.drawable.ic_fiter_category), this);
        applicationFilterView.initData(1, getString(R.string.filter_applications), getResources().getDrawable(R.drawable.ic_fiter_industry), this);
    }


    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "onIntent::entity=" + entity.toString());
        Intent intent = new Intent(this, toCls);
        if(toCls.getSimpleName().equals("FilterApplicationListActivity")){
            intent.putExtra("type",FilterApplicationListActivity.TYPE_NEW_TEC_PRODUCT);
        }else{

        }
        startActivityForResult(intent, (Integer) entity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        results = data.getParcelableArrayListExtra("data");
        LogUtil.i(TAG, "onActivityResult::results=" + results.size() + "," + results.toString());
        if (requestCode == 0) {
            industryFilterView.setList(results);
        } else if (requestCode == 1) {
            applicationFilterView.setList(results);
        }
        allFilters.addAll(results);
    }

    public void onFilter() {
        ExhibitorFilter newTecFilter = new ExhibitorFilter(5, "", etKeyword.get().trim());
        allFilters.add(newTecFilter);

        if (switchNewTec.isChecked()) {
            newTecFilter = new ExhibitorFilter(6, "NewTec", "NewTec");
            allFilters.add(newTecFilter);
        }

        Intent intent = new Intent();
        intent.putExtra("data", allFilters);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClear() {
        allFilters.clear();
        results.clear();
        industryFilterView.setList(results);
        applicationFilterView.setList(results);
        switchNewTec.setChecked(false);
    }
}
