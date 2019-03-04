package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.MenuAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.ActivityMainPadBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.ui.view.M2Dialog;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.MainViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.ui.view.HelpView.HELP_PAGE_MAIN;

/**
 * Created by Carrie on 2017/12/21.
 * 平板主界面
 */

public class PadMainActivity extends BaseActivity implements OnIntentListener {

    private MainViewModel mainViewModel;
    private ActivityMainPadBinding padBinding;
    private MainPic mainPic;
    private int scale;
    private boolean isShowPage;
    private int uc_count;
    private boolean isLogin;
    private SharedPreferences spHelpPage;
    private MenuAdapter adapter;
    private HelpView helpDialog;

    @Override
    protected void preView() {
        super.preView();
        isShowTitleBar.set(false);
        mToolbarBackgroundRes = R.drawable.main_header;
        TAG = "PadMainActivity";
        mTypePrefix = "Page_Menu";
    }

    @Override
    protected void initView() {
        padBinding = ActivityMainPadBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mainViewModel = new MainViewModel(this, this);
        padBinding.setModel(mainViewModel);
        int language = AppUtil.getCurLanguage();
        AppUtil.switchLanguage(getApplicationContext(), language);
        AppUtil.trackViewLog(185, "Page", "", "Menu");
    }

    @Override
    protected void initData() {
        isLogin = AppUtil.isLogin();
        mainViewModel.initPad(padBinding.mainTopViewPager, padBinding.rlTopBanner, padBinding.vpindicator, padBinding.ivAd, mNavViewModel);
        mainPic = mainViewModel.parseMainInfo();
        setRightBanner();
        initRecyclerView();
        mainViewModel.setTopPics();
        mainViewModel.setM2AD();

        helpPage();
//        whetherToUC();
        if (!TextUtils.isEmpty(mainViewModel.m2LargeUrl)) {
            m2UpAnimation(mainViewModel.m2LargeUrl);
        }
//        m2UpAnimation("https://o97tbiy1f.qnssl.com/advertisement/M2/phone_sc_big_2.jpg");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 必不可少。否則平板多語言會混亂
        AppUtil.switchLanguage(getApplicationContext(), AppUtil.getCurLanguage());

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


    }

    private void initRecyclerView() {
        LogUtil.i(TAG, "scale=" + scale);

        RecyclerView recyclerView = padBinding.menuRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 8 * scale;
                outRect.right = 16;
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8 * scale;
        recyclerView.setLayoutParams(params);

        ArrayList<MainIcon> largeIcons = new ArrayList<>();
        ArrayList<MainIcon> littleIcons = new ArrayList<>();
        mainViewModel.getMainIcons(largeIcons, littleIcons);
        adapter = new MenuAdapter(getApplicationContext(), largeIcons, littleIcons, this, mNavViewModel);
        recyclerView.setAdapter(adapter);
    }

    private void setRightBanner() {
        scale = (int) DisplayUtil.getScale(getApplicationContext());
        scale = scale > 1 ? 2 : 1;
        LogUtil.i(TAG, "scale=" + scale);
        int margin = (scale * 8 * 3);
        LogUtil.i(TAG, "margin=" + margin);

        int bannerHeight = (int) ((632 * AppUtil.getPadHeightRate() - margin) / 2);
        int bannerWidth = (567 * bannerHeight) / 292;
        LogUtil.i(TAG, "bannerWidth=" + bannerWidth + ",bannerHeight=" + bannerHeight);
        App.mSP_Config.edit().putInt("itemBannerHeight", bannerHeight).putInt("itemBannerWidth", bannerWidth).apply();

        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(bannerWidth, (int) (632 * AppUtil.getPadHeightRate()) - (8 * scale));
        llParams.bottomMargin = 8 * scale;
        padBinding.llBanner.setLayoutParams(llParams);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(bannerWidth, bannerHeight);
        params1.topMargin = 8 * scale;
        padBinding.ivUpPic.setLayoutParams(params1);
        padBinding.ivDownPic.setLayoutParams(params1);
        setBottomImage();
    }

    private void setBottomImage() {
        LogUtil.i(TAG, "mainPic.LeftBottomBanner.TC.BannerImage_Pad=" + mainPic.LeftBottomBanner.TC.BannerImage_Pad);
        if (App.isNetworkAvailable) {
            Glide.with(getApplicationContext()).load(Uri.parse(mNavViewModel.mCurrLang.get() == 0 ? mainPic.LeftBottomBanner.TC.BannerImage_Pad : mNavViewModel.mCurrLang.get() == 1 ? mainPic.LeftBottomBanner.EN.BannerImage_Pad : mainPic.LeftBottomBanner.SC.BannerImage_Pad)).into(padBinding.ivUpPic);
            Glide.with(getApplicationContext()).load(Uri.parse(mNavViewModel.mCurrLang.get() == 0 ? mainPic.RightBottomBanner.TC.BannerImage_Pad : mNavViewModel.mCurrLang.get() == 1 ? mainPic.RightBottomBanner.EN.BannerImage_Pad : mainPic.RightBottomBanner.SC.BannerImage_Pad)).into(padBinding.ivDownPic);
        } else {
            Glide.with(getApplicationContext()).load(String.format("file:///android_asset/MainIcon/left_%s.png", getLangType())).into(padBinding.ivUpPic);
            Glide.with(getApplicationContext()).load(String.format("file:///android_asset/MainIcon/right_%s.png", getLangType())).into(padBinding.ivDownPic);
        }
    }

    private String getLangType() {
        return App.mLanguage.get() == 0 ? "tc" : App.mLanguage.get() == 1 ? "en" : "sc";
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        LogUtil.i(TAG, "=== onResume === isLogin=" + isLogin);
        LogUtil.i(TAG, "=== onResume ===  mNavViewModel.isLoginSuccess=" + mNavViewModel.isLoginSuccess.get());
        LogUtil.i(TAG, "=== onResume ===  AppUtil.isLogin()=" + AppUtil.isLogin());

        if (mNavViewModel.isLoginSuccess.get() != AppUtil.isLogin()) { // 做一个登陆状态的判断，只有在登陆状态改变时才执行以下操作
            mNavViewModel.isLoginSuccess.set(AppUtil.isLogin()); /* 改变Menu的文字 */
        }

        LogUtil.i(TAG, "onResume: App.mLanguage=" + AppUtil.getCurLanguage());
        LogUtil.i(TAG, "onResume: mNavViewModel.mCurrLang=" + mNavViewModel.mCurrLang.get());

        if (AppUtil.getCurLanguage() != mNavViewModel.mCurrLang.get()) {
            mNavViewModel.updateLanguage();
            refreshImages();
            AppUtil.switchLanguage(getApplicationContext(), App.mLanguage.get());
        }
        mNavViewModel.mCurrLang.set(App.mLanguage.get());

        updateCenterCount();

        boolean isOpenMainHelpPage = spHelpPage.getBoolean("HELP_PAGE_OPEN_MAIN", false);
        if (isOpenMainHelpPage) {
            showHelpPage();
            helpDialog.openMainHelpPage();
        }

        // 如果點擊Home按鈕，則將Menu的子按鈕都關閉
        boolean isPressHome = App.mSP_Config.getBoolean("HOME", false);
        if (isPressHome) {
            adapter.mClickPos.set(-1);
            App.mSP_Config.edit().putBoolean("HOME", false).apply();
        }
    }

    public void refreshImages() {
        setBottomImage();
        mainViewModel.refreshImages();
    }

    /**
     * 更新侧边栏下载中心数据，如有
     */
    private void updateCenterCount() {
        int ucCount = App.mSP_Config.getInt("UC_COUNT", 0);
        LogUtil.i(TAG, "uc_count=" + uc_count + ",ucCount=" + ucCount);
        if (uc_count != ucCount) {
            mNavViewModel.refreshUpdateCount(ucCount);
        }
    }

    /**
     * 显示帮助页面
     */
    private void helpPage() {
        spHelpPage = getSharedPreferences("HelpPage", MODE_PRIVATE);
        if (HelpView.isFirstShow(HELP_PAGE_MAIN)) {
            showHelpPage();
            isShowPage = true;
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_MAIN, HelpView.HELP_PAGE_MAIN).apply();
        }
    }

    private void showHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HELP_PAGE_MAIN);
        helpDialog.setCloseListener(mMenuHelpCloseListener);
        helpDialog.show(ft, "Dialog");
    }

    private View.OnClickListener mMenuHelpCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.i(TAG, "mMenuHelpCloseListener");
            helpDialog.dismiss();
            intentToUpdateCenter();
            isShowPage = false;
        }
    };

    /**
     * 检查是否有更新，如有，则跳转到更新中心
     */
    private void whetherToUC() {
        OtherRepository repository = OtherRepository.getInstance();
        repository.initUpdateCenterDao();
        uc_count = repository.getNeedUpdatedCount();
        if (!isShowPage) {
            intentToUpdateCenter();
        }
    }

    /**
     * 如果有更新，跳转到更新中心页面
     */
    private void intentToUpdateCenter() {
        LogUtil.i(TAG, "intentToUpdateCenter");
        if (uc_count > 0) {
            Intent intent = new Intent(this, UpdateCenterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransPad();
        }
    }

    private void m2UpAnimation(String url) {
        M2Dialog dialog = new M2Dialog(this);
        dialog.setUrl(url);
        dialog.show();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls != null && toCls.getSimpleName().equals("ExhibitorDetailActivity")) {
            Intent intent = new Intent(getApplicationContext(), toCls);
            intent.putExtra(Constant.COMPANY_ID, ((adAdvertisementObj) entity).M2.getCompanyID(mNavViewModel.mCurrLang.get()));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransPad();
        } else {
            Intent intent = mNavViewModel.newIntent(this, (MainIcon) entity);
            if (intent == null) {
                return;
            }
            startActivity(intent);
            overridePendingTransPad();
        }

    }

    private void exit() {
        try {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(getString(R.string.EXIT));
            ad.setMessage(getString(R.string.EXIT_MSG));
            ad.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {// ??锟斤 ?锟介 ?
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    App.mDBHelper.db.close();
                    App.mDBHelper = null;
                    finish();
                    System.exit(0);
                }
            });
            ad.setNegativeButton(getString(R.string.cancel), null);
            ad.show();
        } catch (Exception e) {
            System.exit(0);
        }
    }

    @Override
    public void back() {
        exit();
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}
