package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

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

import java.util.ArrayList;

/**
 * Logo: https://o97tbiy1f.qnssl.com/advertisement/M6/230525/phone_logo_1.jpg
 * Header: https://o97tbiy1f.qnssl.com/advertisement/M6/230525/phone_header_1.jpg
 * Banner(dtl): https://o97tbiy1f.qnssl.com/advertisement/M6/banner/phone_17016en_20170413.jpg
 * <p>
 * StatService.onEvent(getApplicationContext(), "Technical", "Date" + date);
 * SystemMethod.trackViewLog(getApplicationContext(), 302, "Technical", "", "Date" + date);
 * <p>
 * SystemMethod.setStatEvent(getActivity(), "ViewM6", "M6" + "_Date" + (16 + index) + m6B.companyID[index], currLang);
 * <p>
 * <p>
 * logo显示的判断：公司ID 是否在txt的CompanyID[]中，存在，则显示所有该公司的技术交流会logo.
 * 上午下午时间段都要置顶。
 * </>
 */
public class TechnicalListActivity extends BaseActivity implements OnIntentListener {
    private TechViewModel model;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void initView() {
        ActivityTechnicalListBinding binding = ActivityTechnicalListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        model = new TechViewModel(getApplicationContext(), binding.ivAd);
        binding.setTechModel(model);
        binding.executePendingBindings();
        recyclerView = binding.rvSeminar;
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void initData() {
        String date = getIntent().getStringExtra("index");
        LogUtil.i(TAG, "");
        TechAdapter adapter;
        if (!TextUtils.isEmpty(date)) {
            LogUtil.i(TAG, "0DATE=" + date);
            int pos = Integer.valueOf(date);
            model.getPartList(pos, true);
            adapter = new TechAdapter(getApplicationContext(), model.mSeminars, this);
        } else { // 显示全部列表
            LogUtil.i(TAG, "1DATE=" + date);
            adapter = new TechAdapter(getApplicationContext(), model.getList(), this);
            model.showM6V2(0);
        }
        recyclerView.setAdapter(adapter);
        model.onStart(this, adapter);
        AppUtil.trackViewLog(302, "Technical", "", "Date24");

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int firstVisibleItemPos, lastVisibleItemPos;
            int size = model.headerItemPositions.size();

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 当上滑时，进入日期范围就显示那天的广告，如：从26号上滑到25号，则进入25号区域就显示广告
                // 当下滑时，最上方的item过了，就显示那天的广告，如：最上方为25号，则底部banner显示25号的广告
                if (dy >= 0) { // 下滑
                    firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition();
                    if (model.headerItemPositions.contains(firstVisibleItemPos)) {
                        model.showM6V2(model.convertDateTimeToIndex(model.mSeminars.get(firstVisibleItemPos).getDate(), model.mSeminars.get(firstVisibleItemPos).getTime()));
                    }
                } else { // 上滑
                    lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = 0; i < size; i++) {
                        if (model.headerItemPositions.get(i) == lastVisibleItemPos) {
                            lastVisibleItemPos = model.headerItemPositions.get(i == 0 ? i : i - 1);
                            model.showM6V2(model.convertDateTimeToIndex(model.mSeminars.get(lastVisibleItemPos).getDate(), model.mSeminars.get(lastVisibleItemPos).getTime()));
                            break;
                        }
                    }
                }
            }
        });


    }


    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent;
        if (toCls == null) {
            // on bottom banner click
            intent = new Intent(this, TechSeminarDtlActivity.class);
            intent.putExtra(Constant.INTENT_SEMINAR_DTL_ID, (String) entity);
        } else if (toCls.getSimpleName().equals("WebContentActivity")) {
            intent = new Intent(getApplicationContext(), WebContentActivity.class);
            intent.putExtra(Constant.WEB_URL, "WebContent/MI00000064");
            intent.putExtra(Constant.TITLE, getString(R.string.uc_floorplan));
        } else {
            // on item click
            intent = new Intent(this, toCls);
            intent.putExtra("Info", (SeminarInfo) entity);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }
}
