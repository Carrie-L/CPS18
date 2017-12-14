package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailBinding;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailM5Binding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.ExhibitorDtlViewModel;

import static com.adsale.ChinaPlas.utils.Constant.SCHEDULE_DAY01;

/**
 * [Constant.COMPANY_ID]
 */
public class ExhibitorDetailActivity extends BaseActivity implements OnIntentListener {

    private ExhibitorDtlViewModel mViewModel;
    private HelpView helpView;
    private ADHelper adHelper;

    private final Integer REQUET_TAKE_PHOTO = 101;
    private final Integer REQUET_DELETE_PHOTO = 102;

    @Override
    protected void preView() {
        super.preView();
        barTitle.set(getString(R.string.title_exhibitor_deti));
    }

    @Override
    protected void initView() {
        int screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        int width = (screenWidth - DisplayUtil.dip2px(getApplicationContext(), 32)) / 5;
        int height = (width * 184) / 209;
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(width, height);

        String companyId = getIntent().getStringExtra(Constant.COMPANY_ID);
        adHelper = new ADHelper(this);
        int M5Index = adHelper.isM5Show(companyId);
        if (M5Index == -1) {// not show m5
            ActivityExhibitorDetailBinding binding = ActivityExhibitorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
            mViewModel = new ExhibitorDtlViewModel(getApplicationContext(), binding.flDtlContent);
            bindingVariable(binding);
            mViewModel.start(companyId, this, binding.viewstubDtlView.getViewStub(), false);
            binding.llButton.ivCompanyInfo.setLayoutParams(bottomParams);
            binding.llButton.ivCollect.setLayoutParams(bottomParams);
            binding.llButton.ivNote.setLayoutParams(bottomParams);
            binding.llButton.ivSchedule.setLayoutParams(bottomParams);
            binding.llButton.ivShare.setLayoutParams(bottomParams);
        } else {
            ActivityExhibitorDetailM5Binding binding = ActivityExhibitorDetailM5Binding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
            mViewModel = new ExhibitorDtlViewModel(getApplicationContext(), binding.flDtlContent);
            bindingVariable(binding);
            mViewModel.setM5Description(adHelper.getAdObj().M5.description.get(M5Index).getDescription(App.mLanguage.get()));
            mViewModel.start(companyId, this, binding.viewstubDtlView.getViewStub(), true);
            binding.llButton.ivCompanyInfo.setLayoutParams(bottomParams);
            binding.llButton.ivCollect.setLayoutParams(bottomParams);
            binding.llButton.ivNote.setLayoutParams(bottomParams);
            binding.llButton.ivSchedule.setLayoutParams(bottomParams);
            binding.llButton.ivShare.setLayoutParams(bottomParams);
            adHelper.showM5(M5Index, binding.m5Left, binding.m5Center, binding.m5Right, binding.m5Logo);
        }
    }

    private void bindingVariable(ViewDataBinding binding) {
        binding.setVariable(BR.model, mViewModel);
        binding.setVariable(BR.aty, this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {
        helpView = new HelpView(this, HelpView.HELP_PAGE_EXHIBITOR_DTL);
        helpView.showPage();
        mViewModel.addToHistory();
        mViewModel.setActivity(this);
    }

    public void onHelpPage() {
        helpView.show();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "entity=" + entity.toString());
        if (toCls != null) {
            LogUtil.i(TAG, "toCls.getSimpleName()=" + toCls.getSimpleName());
            if (toCls.getSimpleName().contains("ScheduleEditActivity")) {
                Intent intent = new Intent(this, toCls);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constant.INTENT_EXHIBITOR, (Exhibitor) entity);
                intent.putExtra("date", SCHEDULE_DAY01);
                startActivity(intent);
            } else if (toCls.getSimpleName().contains("NewTecDtlActivity")) {
                Intent intent = new Intent(this, NewTecDtlActivity.class);
                intent.putExtra("obj", (NewProductInfo) entity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (toCls.getSimpleName().contains("ImageActivity")) {
                Intent intent = new Intent(this, toCls);
                intent.putExtra("url", (String) entity);
                intent.putExtra("del", true);
                intent.putExtra("title", barTitle.get());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUET_DELETE_PHOTO);
            } else if (toCls.getSimpleName().contains("TakePhotoActivity")) {
                Intent intent = new Intent(this, TakePhotoActivity.class);
                intent.putExtra("absPath", (String) entity);
                startActivityForResult(intent, REQUET_TAKE_PHOTO);
            }
            overridePendingTransPad();
        }
    }

    public void onM5Click(int position) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", adHelper.getM5ProductUrl(position));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.PMS_CODE_CAMERA && PermissionUtil.getGrantResults(grantResults)) {
            mViewModel.showCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUET_TAKE_PHOTO) {
            mViewModel.photoSuccess(data.getStringExtra("path"));
        } else if (requestCode == REQUET_DELETE_PHOTO) {
            mViewModel.deletePhoto(data.getStringExtra("path"));
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

    private void setResultForCollect() {
        Intent intent = new Intent();
        intent.putExtra("isCollected", mViewModel.isCollected.get());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        setResultForCollect();
        mViewModel.saveNoteAndRate();
        super.onBackPressed();
    }

    @Override
    public void back() {
        setResultForCollect();
        mViewModel.saveNoteAndRate();
        super.back();
    }
}
