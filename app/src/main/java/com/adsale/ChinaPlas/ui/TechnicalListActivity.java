package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.TechAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityTechnicalListBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.TechViewModel;

/**
 * Logo: https://o97tbiy1f.qnssl.com/advertisement/M6/230525/phone_logo_1.jpg
 * Header: https://o97tbiy1f.qnssl.com/advertisement/M6/230525/phone_header_1.jpg
 * Banner(dtl): https://o97tbiy1f.qnssl.com/advertisement/M6/banner/phone_17016en_20170413.jpg
 * <p>
 * todo add:    SystemMethod.trackViewLog(this, 302, "Technical", "", "Date"+(24+currIndex));
 * StatService.onEvent(getApplicationContext(), "Technical", "Date" + date);
 * SystemMethod.trackViewLog(getApplicationContext(), 302, "Technical", "", "Date" + date);
 * <p>
 * todo M6 Log:   SystemMethod.trackViewLog(getActivity(), 206, "Ad", "M6" + "_Date" + (16 + index), m6B.companyID[index]);// 统计广告出现次数
 * SystemMethod.setStatEvent(getActivity(), "ViewM6", "M6" + "_Date" + (16 + index) + m6B.companyID[index], currLang);
 */
public class TechnicalListActivity extends BaseActivity implements OnIntentListener {
    private TechViewModel model;
    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        ActivityTechnicalListBinding binding = ActivityTechnicalListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        model = new TechViewModel(getApplicationContext(), binding.ivAd);
        binding.setTechModel(model);
        binding.executePendingBindings();
        recyclerView = binding.rvSeminar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void initData() {
        String date = getIntent().getStringExtra("index");
        LogUtil.i(TAG,"");
        TechAdapter adapter;
        if (!TextUtils.isEmpty(date)&&!date.equals("0")) {
            LogUtil.i(TAG, "DATE=" + date);
            int pos = Integer.valueOf(date);
            model.getPartList(pos, true);
            adapter = new TechAdapter(getApplicationContext(), model.mSeminars, this);
        } else {
            adapter = new TechAdapter(getApplicationContext(), model.getList(), this);
            model.showM6(0);
        }
        recyclerView.setAdapter(adapter);
        model.onStart(this, adapter);
        AppUtil.trackViewLog(302, "Technical", "", "Date24");
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent;
        if (toCls == null) {
            intent = new Intent(this, TechSeminarDtlActivity.class);
            intent.putExtra("ID", (String) entity);
        } else if(toCls.getSimpleName().equals("WebContentActivity")){
            intent = new Intent(getApplicationContext(), WebContentActivity.class);
            intent.putExtra(Constant.WEB_URL, "WebContent/MI00000064");
            intent.putExtra(Constant.TITLE,getString(R.string.uc_floorplan));
        }
        else {
            intent = new Intent(this, toCls);
            intent.putExtra("Info", (SeminarInfo) entity);
//            intent.putExtra(Constant.TITLE,getString(R.string.uc_floorplan));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }
}
