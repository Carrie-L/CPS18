package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ExhibitorDtlViewModel;

import static com.adsale.ChinaPlas.utils.Constant.SCHEDULE_DAY01;

/**
 * [Constant.COMPANY_ID]
 */
public class ExhibitorDetailActivity extends BaseActivity implements OnIntentListener {

    private ExhibitorDtlViewModel mViewModel;
    private HelpView helpView;

    @Override
    protected void preView() {
        super.preView();
        barTitle.set(getString(R.string.title_exhibitor_deti));
    }

    @Override
    protected void initView() {
        ActivityExhibitorDetailBinding binding = ActivityExhibitorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mViewModel = new ExhibitorDtlViewModel(getApplicationContext(), binding.flDtlContent);
        binding.setModel(mViewModel);
        binding.setAty(this);
        binding.executePendingBindings();

        mViewModel.start(getIntent().getStringExtra(Constant.COMPANY_ID), this, binding.viewstubDtlView.getViewStub());

        int screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        int width = (screenWidth - DisplayUtil.dip2px(getApplicationContext(), 32)) / 5;
        int height = (width * 184) / 209;

        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(width, height);
        binding.llButton.ivCompanyInfo.setLayoutParams(bottomParams);
        binding.llButton.ivCollect.setLayoutParams(bottomParams);
        binding.llButton.ivNote.setLayoutParams(bottomParams);
        binding.llButton.ivSchedule.setLayoutParams(bottomParams);
        binding.llButton.ivShare.setLayoutParams(bottomParams);
    }

    @Override
    protected void initData() {
        helpView = new HelpView(this, HelpView.HELP_PAGE_EXHIBITOR_DTL);
        helpView.showPage();
        mViewModel.addToHistory();
    }

    public void onHelpPage(){
        helpView.show();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "entity=" + entity.toString());


//        Intent intent = new Intent(this,ExhibitorAllListActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

        if (toCls != null) {
            LogUtil.i(TAG, "toCls.getSimpleName()=" + toCls.getSimpleName());
            if (toCls.getSimpleName().contains("ScheduleEditActivity")) {
                Intent intent = new Intent(this, toCls);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constant.INTENT_EXHIBITOR, (Exhibitor) entity);
                intent.putExtra("date", SCHEDULE_DAY01);
                startActivity(intent);
            }
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean updated = App.mSP_Config.getBoolean("ScheduleListUpdate", false);
        LogUtil.i(TAG, "onResume::updated=" + updated);
        if (updated) {
            mViewModel.updateSchedule();
            App.mSP_Config.edit().putBoolean("ScheduleListUpdate", false).apply();// must.
        }
    }
}
