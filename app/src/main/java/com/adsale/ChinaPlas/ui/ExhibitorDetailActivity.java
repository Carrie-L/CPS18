package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailBinding;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorDetailM5Binding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.ui.view.ExhiDtlInfoView;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.ExhibitorDtlViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import static com.adsale.ChinaPlas.App.mLogHelper;
import static com.adsale.ChinaPlas.utils.Constant.SCHEDULE_DAY01;

/**
 * [Constant.COMPANY_ID]
 * [2019/3/4 新增 来源 [from] 用于统计]
 */
public class ExhibitorDetailActivity extends BaseActivity implements OnIntentListener, ExhiDtlInfoView.OnCallPhoneListener {

    private ExhibitorDtlViewModel mViewModel;
//    private ADHelper adHelper;

    private final Integer REQUET_TAKE_PHOTO = 101;
    private final Integer REQUET_DELETE_PHOTO = 102;
    private String mPhotoPath;
    private HelpView helpDialog;
    private ActivityExhibitorDetailM5Binding binding;
    private EPO.D4 D4;
    private String companyId;

    @Override
    protected void preView() {
        super.preView();
        barTitle.set(getString(R.string.title_exhibitor_deti));
        mTypePrefix = "EPage";
    }

    @Override
    protected void initView() {
        int screenWidth = AppUtil.getScreenWidth();
        int width = isTablet ? screenWidth / 5 : (screenWidth - DisplayUtil.dip2px(getApplicationContext(), 32)) / 5;
        int height = (width * (isTablet ? 77 : 184)) / (isTablet ? 349 : 209);
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(width, height);

        companyId = getIntent().getStringExtra(Constant.COMPANY_ID);

        EPOHelper epoHelper = EPOHelper.getInstance();
        D4 = epoHelper.getItemD4(companyId);

        if (epoHelper.isAdOpen() && D4 != null && D4.isOpen == 1) {// show d4
            binding = ActivityExhibitorDetailM5Binding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
            mViewModel = new ExhibitorDtlViewModel(getApplicationContext(), binding.flDtlContent);
            bindingVariable(binding);
            mViewModel.setD4Data(D4);
            mViewModel.start(companyId, this, binding.viewstubDtlView.getViewStub(), true, this);
            binding.llButton.ivCompanyInfo.setLayoutParams(bottomParams);
            binding.llButton.ivCollect.setLayoutParams(bottomParams);
            binding.llButton.ivNote.setLayoutParams(bottomParams);
            binding.llButton.ivSchedule.setLayoutParams(bottomParams);
            binding.llButton.ivShare.setLayoutParams(bottomParams);
            showD4Pic();
        } else {
            ActivityExhibitorDetailBinding binding = ActivityExhibitorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
            mViewModel = new ExhibitorDtlViewModel(getApplicationContext(), binding.flDtlContent);
            bindingVariable(binding);
            mViewModel.start(companyId, this, binding.viewstubDtlView.getViewStub(), false, this);
            binding.llButton.ivCompanyInfo.setLayoutParams(bottomParams);
            binding.llButton.ivCollect.setLayoutParams(bottomParams);
            binding.llButton.ivNote.setLayoutParams(bottomParams);
            binding.llButton.ivSchedule.setLayoutParams(bottomParams);
            binding.llButton.ivShare.setLayoutParams(bottomParams);
        }
        AppUtil.trackViewLog(198, "EPage", "", companyId);
    }

    private void showD4Pic() {
        String d4BaseUrl = AppUtil.getAdBaseUrl() + "D4/";
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.scan_mask);
        Glide.with(getApplicationContext()).load(Uri.parse(mViewModel.exhibitor.getPhotoFileName())).apply(options).thumbnail(0.1f).into(binding.adLogo);
        Glide.with(getApplicationContext()).load(Uri.parse(d4BaseUrl + D4.products[0])).apply(options).thumbnail(0.1f).into(binding.left);
        Glide.with(getApplicationContext()).load(Uri.parse(d4BaseUrl + D4.products[1])).apply(options).thumbnail(0.1f).into(binding.center);
        Glide.with(getApplicationContext()).load(Uri.parse(d4BaseUrl + D4.products[2])).apply(options).thumbnail(0.1f).into(binding.right);

        LogUtil.i(TAG, "D4 LEFT=" + d4BaseUrl + D4.products[0]);

//        AppUtil.trackViewLog(205, "Ad", "M5", companyId);
//        AppUtil.setStatEvent(getApplicationContext(), "ViewM5", "Ad_M5_".concat(companyId));

        mLogHelper.logD4(companyId, true);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);

    }

    private void bindingVariable(ViewDataBinding binding) {
        binding.setVariable(BR.model, mViewModel);
        binding.setVariable(BR.aty, this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {
        if (HelpView.isFirstShow(HelpView.HELP_PAGE_EXHIBITOR_DTL)) {
            showHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_EXHIBITOR_DTL, HelpView.HELP_PAGE_EXHIBITOR_DTL).apply();
        }
        mViewModel.addToHistory();
        mViewModel.setActivity(this);
//        AppUtil.trackViewLog(198, "EPage", "", mViewModel.exhibitor.getCompanyID());
//        AppUtil.setStatEvent(getApplicationContext(), "EPage", "EPage_" + mViewModel.exhibitor.getCompanyID());

        String from = getIntent().getStringExtra("from");
        mLogHelper.logCompanyInfo(mViewModel.exhibitor.getCompanyID() + (TextUtils.isEmpty(from) ? "" : "_" + from));
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_Info);

    }

    private void showHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_EXHIBITOR_DTL);
        helpDialog.show(ft, "Dialog");
    }

    public void onHelpPage() {
        showHelpPage();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "entity=" + entity.toString());
        if (toCls != null) {
            LogUtil.i(TAG, "toCls.getSimpleName()=" + toCls.getSimpleName());
            if (toCls.getSimpleName().contains("ScheduleEditActivity")) {
                Intent intent = new Intent(this, toCls);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("title", getString(R.string.title_add_schedule));
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

    public void onD4Click(int position) {
//        AppUtil.trackViewLog(415, "CA", "M5", mViewModel.exhibitor.getCompanyID());
//        AppUtil.setStatEvent(getApplicationContext(), "ClickM5", "CA_M5_" + mViewModel.exhibitor.getCompanyID());

        mLogHelper.logD4(mViewModel.exhibitor.getCompanyID(), false);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_Click);

        Intent intent = new Intent(this, ImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", AppUtil.getAdBaseUrl() + "D4/" + D4.products[position - 1]);
        intent.putExtra("title", mViewModel.exhibitor.getCompanyName());
        startActivity(intent);
        overridePendingTransPad();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.getGrantResults(grantResults)) {
            showCamera();
        }
    }

    private void showCamera() {
        if (mViewModel.checkCameraPermission() && mViewModel.checkSDPermission()) {
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

    private void fromM1ToMain() {

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("FromM1", false)) {
            Intent intent = new Intent(this, isTablet ? PadMainActivity.class : MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransPad();
        } else {
            setResultForCollect();
            mViewModel.saveNoteAndRate();
        }
        super.onBackPressed();
    }

    @Override
    public void back() {
        if (getIntent().getBooleanExtra("FromM1", false)) {
            Intent intent = new Intent(this, isTablet ? PadMainActivity.class : MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransPad();
        } else {
            setResultForCollect();
            mViewModel.saveNoteAndRate();
        }
        super.back();
    }

    @Override
    public void callPhone(String number) {
        AppUtil.callPhoneIntent(this, number);
    }
}
