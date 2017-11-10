package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableField;
import android.support.v7.widget.SwitchCompat;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorFilterBinding;
import com.adsale.ChinaPlas.ui.view.FilterView;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

public class ExhibitorFilterActivity extends BaseActivity implements OnIntentListener {
    public final ObservableField<String> etKeyword = new ObservableField<>("");
    private FilterView industryFilterView;
    private FilterView applicationFilterView;
    private FilterView countryFilterView;
    private FilterView hallFilterView;
    private FilterView boothFilterView;
    private ArrayList<ExhibitorFilter> results;
    private ArrayList<ExhibitorFilter> allFilters = new ArrayList<>();
    private SwitchCompat switchNewTec;

    @Override
    protected void initView() {
        ActivityExhibitorFilterBinding binding = ActivityExhibitorFilterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setUi(this);
        industryFilterView = binding.industryFilterView;
        applicationFilterView = binding.applicationFilterView;
        countryFilterView = binding.countryFilterView;
        hallFilterView = binding.hallFilterView;
        boothFilterView = binding.boothFilterView;
        switchNewTec = binding.switchNewTec;
    }

    @Override
    protected void initData() {
        industryFilterView.initData(0, getString(R.string.filter_industry), getResources().getDrawable(R.drawable.ic_fiter_category), this);
        applicationFilterView.initData(1, getString(R.string.filter_application), getResources().getDrawable(R.drawable.ic_fiter_industry), this);
        countryFilterView.initData(2, getString(R.string.filter_country), getResources().getDrawable(R.drawable.ic_fiter_country), this);
        hallFilterView.initData(3, getString(R.string.filter_hall), getResources().getDrawable(R.drawable.ic_fiter_hall), this);
        boothFilterView.initData(4, getString(R.string.filter_booth), getResources().getDrawable(R.drawable.ic_fiter_booth), this);


    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "onIntent::entity=" + entity);
        Intent intent = new Intent(this, toCls);
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
        } else if (requestCode == 2) {
            countryFilterView.setList(results);
        } else if (requestCode == 3) {
            hallFilterView.setList(results);
        } else if (requestCode == 4) {
            boothFilterView.setList(results);
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
        countryFilterView.setList(results);
        hallFilterView.setList(results);
        boothFilterView.setList(results);
        switchNewTec.setChecked(false);
    }


}
