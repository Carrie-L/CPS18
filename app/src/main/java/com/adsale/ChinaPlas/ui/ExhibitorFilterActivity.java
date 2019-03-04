package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorFilterBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.IntentHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.ui.view.FilterView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.App.mLogHelper;

public class ExhibitorFilterActivity extends BaseActivity implements OnIntentListener {
    public final ObservableField<String> etKeyword = new ObservableField<>("");
    private FilterView industryFilterView;
    private FilterView applicationFilterView;
    private FilterView countryFilterView;
    private FilterView hallFilterView;
    private FilterView boothFilterView;
    private ArrayList<ExhibitorFilter> results = new ArrayList<>();
    private ArrayList<ExhibitorFilter> allFilters = new ArrayList<>();
    private SwitchCompat switchNewTec;
    private String[] names;
    private ImageView ivAD;

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.title_filter));
        ActivityExhibitorFilterBinding binding = ActivityExhibitorFilterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setUi(this);
        industryFilterView = binding.industryFilterView;
        applicationFilterView = binding.applicationFilterView;
        countryFilterView = binding.countryFilterView;
        hallFilterView = binding.hallFilterView;
        boothFilterView = binding.boothFilterView;
        switchNewTec = binding.switchNewTec;

        ivAD = binding.ivAd;
        showD3();
    }

    private void showD3() {
        final EPOHelper epoHelper = EPOHelper.getInstance();
        epoHelper.parseAd();
        final int index = epoHelper.count();
        if (!epoHelper.isD3Open(index)) {
            return;
        }
        ivAD.setVisibility(View.VISIBLE);
        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivAD.getLayoutParams();
        params.width = AppUtil.getScreenWidth();
        params.height = AppUtil.getCalculatedHeight(Constant.M3_WIDTH, Constant.M3_HEIGHT);
        ivAD.setLayoutParams(params);

        epoHelper.setD3Index(index);
        LogUtil.i(TAG, "SHOWM3:URL=" + epoHelper.getD3Images());
        Glide.with(getApplicationContext()).load(Uri.parse(epoHelper.getD3Images())).apply(requestOptions).into(ivAD);
        epoHelper.changeD3Count();

//        AppUtil.trackViewLog(203, "Ad", "M3", epoHelper.getD3CompanyID());
//        AppUtil.setStatEvent(getApplicationContext(), "ViewM3", "Ad_M3_" + epoHelper.getD3CompanyID());
        mLogHelper.logD3Filter(epoHelper.getD3Function(), epoHelper.getD3PageID(), true);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);

        ivAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = IntentHelper.intentToCompany(epoHelper.getD3CompanyID());
                startActivity(intent);
                overridePendingTransPad();

                mLogHelper.logD3Filter(epoHelper.getD3Function(), epoHelper.getD3PageID(), false);
                mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_Click);
            }
        });

    }

    @Override
    protected void initData() {
        names = new String[]{getString(R.string.filter_industry), getString(R.string.filter_application),
                getString(R.string.filter_country), getString(R.string.filter_hall), getString(R.string.filter_booth)};
        industryFilterView.initData(0, names[0], getResources().getDrawable(R.drawable.ic_fiter_category), this);
        applicationFilterView.initData(1, names[1], getResources().getDrawable(R.drawable.ic_fiter_industry), this);
        countryFilterView.initData(2, names[2], getResources().getDrawable(R.drawable.ic_fiter_country), this);
        hallFilterView.initData(3, names[3], getResources().getDrawable(R.drawable.ic_fiter_hall), this);
        boothFilterView.initData(4, names[4], getResources().getDrawable(R.drawable.ic_fiter_booth), this);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "onIntent::entity=" + entity);
        Intent intent = new Intent(this, toCls);
        intent.putExtra("title", names[(Integer) entity]);
        startActivityForResult(intent, (Integer) entity);
        overridePendingTransPad();
    }

    /**
     * item跳转在{@link com.adsale.ChinaPlas.ui.view.FilterView#onItemClick}
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
        ExhibitorFilter newTecFilter;

        if (!TextUtils.isEmpty(etKeyword.get().trim())) {
            newTecFilter = new ExhibitorFilter(5, "", etKeyword.get().trim());
            allFilters.add(newTecFilter);
            AppUtil.setStatEvent(getApplicationContext(), "KeySearch", "Page_KeySearch_" + etKeyword.get().trim());
            AppUtil.trackViewLog(188, "Page", "", "KeywordSearch_" + etKeyword.get().trim());
        }

        if (switchNewTec.isChecked()) {
            newTecFilter = new ExhibitorFilter(6, "C", "NewTec");
            allFilters.add(newTecFilter);
        }

//        String location=(industryId == null ? "0" : industryId) + ","
//                + (applicationId == null ? "0" : applicationId) + "," + (floorId == null ? "0" : floorId) + ","
//                + (countryId == null ? "0" : countryId);
//        LogUtil.i(TAG, "track location =" + location);
//        SystemMethod.trackLog(mContext,400,428,"AdvancedSearch","",location,"AS_"+location+"_");


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
        etKeyword.set("");
        switchNewTec.setChecked(false);
    }


}
