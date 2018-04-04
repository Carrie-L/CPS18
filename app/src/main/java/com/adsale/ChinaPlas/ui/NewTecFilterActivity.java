package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityNewTecFilterBinding;
import com.adsale.ChinaPlas.ui.view.FilterView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import java.util.ArrayList;

public class NewTecFilterActivity extends BaseActivity implements OnIntentListener {
    private FilterView industryFilterView;
    private FilterView applicationFilterView;
    private FilterView thematicFilterView;
    private ArrayList<ExhibitorFilter> results=new ArrayList<>();
    private ArrayList<ExhibitorFilter> allFilters = new ArrayList<>();

    @Override
    protected void initView() {
        TAG = "NewTecFilterActivity";
        barTitle.set(getString(R.string.title_filter));
        ActivityNewTecFilterBinding binding = ActivityNewTecFilterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setUi(this);
        binding.executePendingBindings();
        industryFilterView = binding.industryFilterView;
        applicationFilterView = binding.applicationFilterView;
        thematicFilterView = binding.thematicFilterView;

        binding.highLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(NetWorkHelper.MOBILE_EXHI_HIGHLIGHTS, AppUtil.getUrlLangType(App.mLanguage.get()))));
                startActivity(intent);
                overridePendingTransPad();
            }
        });

    }

    @Override
    protected void initData() {
        industryFilterView.initData(0, getString(R.string.filter_product), getResources().getDrawable(R.drawable.ic_fiter_category), this);
        applicationFilterView.initData(1, getString(R.string.filter_applications), getResources().getDrawable(R.drawable.ic_fiter_industry), this);
        thematicFilterView.initData(5, getString(R.string.filter_topic_collection), getResources().getDrawable(R.drawable.ic_fiter_new_tec), this);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        int index =  Integer.valueOf(entity.toString());
        LogUtil.i(TAG, "onIntent::entity=" + index);
        Intent intent = new Intent(this, FilterApplicationListActivity.class);
        if (index == 0) {
            LogUtil.i(TAG, "》产品");
            intent.putExtra("MainTypeId", FilterApplicationListActivity.TYPE_NEW_TEC_PRODUCT);
            intent.putExtra("title",getString(R.string.title_product_category));
        } else if (index == 1) {
            LogUtil.i(TAG, "》行业");
            intent.putExtra("MainTypeId", FilterApplicationListActivity.TYPE_NEW_TEC_APPLICATIONS);
            intent.putExtra("title",getString(R.string.title_application));
        }else if (index == 5) {
            LogUtil.i(TAG, "》主题专辑");
            intent.putExtra("MainTypeId", FilterApplicationListActivity.TYPE_NEW_TEC_THEMATIC);
            intent.putExtra("title",getString(R.string.filter_topic_collection));
        }
        intent.putExtra("index", index);
        startActivityForResult(intent, (Integer) entity);
        overridePendingTransPad();
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
        } else if (requestCode == 5) {
            thematicFilterView.setList(results);
        }
        allFilters.addAll(results);
    }

    public void onFilter() {
        Intent intent = new Intent();
        intent.putExtra("data", allFilters);
        setResult(RESULT_OK, intent);
        finish();
    }

    // 跳到mobile website
    public void onHighLight(){
        LogUtil.i(TAG, "》展会亮点");


    }

    public void onClear() {
        allFilters.clear();
        results.clear();
        industryFilterView.setList(results);
        applicationFilterView.setList(results);
    }
}
