package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

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
 *
 * todo add:    SystemMethod.trackViewLog(this, 302, "Technical", "", "Date"+(24+currIndex));
 *              StatService.onEvent(getApplicationContext(), "Technical", "Date" + date);
                SystemMethod.trackViewLog(getApplicationContext(), 302, "Technical", "", "Date" + date);

 todo M6 Log:   SystemMethod.trackViewLog(getActivity(), 206, "Ad", "M6" + "_Date" + (16 + index), m6B.companyID[index]);// 统计广告出现次数
                SystemMethod.setStatEvent(getActivity(), "ViewM6", "M6" + "_Date" + (16 + index) + m6B.companyID[index], currLang);

 */
public class TechnicalListActivity extends BaseActivity implements OnIntentListener {

    private TechViewModel model;
    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        ActivityTechnicalListBinding binding = ActivityTechnicalListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        model = new TechViewModel(getApplicationContext(),binding.ivAd);
        binding.setTechModel(model);
        binding.executePendingBindings();
        recyclerView = binding.rvSeminar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        /* todo 这个广告不是M3广告，跳转到同期活动某一页面 */
        ADHelper adHelper = new ADHelper(this);
//        adHelper.isShowM6anner()
        adHelper.getM6HeaderUrl(0);
    }

    private void showM6(){

    }

    @Override
    protected void initData() {
        String date = getIntent().getStringExtra("index");
        TechAdapter adapter;
        if(!TextUtils.isEmpty(date)){
            LogUtil.i(TAG,"DATE="+date);
            int pos = Integer.valueOf(date);
            model.getPartList(pos,true);
            adapter = new TechAdapter(getApplicationContext(), model.mSeminars, this);
        }else{
            adapter = new TechAdapter(getApplicationContext(), model.getList(), this);
        }
        recyclerView.setAdapter(adapter);
        model.onStart(this, adapter);
        AppUtil.trackViewLog(302, "Technical", "", "Date24");
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = new Intent(this, toCls);
        intent.putExtra("Info", (SeminarInfo) entity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }
}
