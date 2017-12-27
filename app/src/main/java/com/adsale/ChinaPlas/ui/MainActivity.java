package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.databinding.ActivityMainBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import static com.adsale.ChinaPlas.ui.view.HelpView.HELP_PAGE_MAIN;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

/**
 */
public class MainActivity extends BaseActivity {

    private MainFragment mainFragment;
    private SharedPreferences spHelpPage;
    private boolean isLogin = false;
    private int uc_count;
    private boolean isShowPage;
    private HelpView helpDialog;

    @Override
    protected void preView() {
        super.preView();
        TAG = "MainActivity";
        mToolbarBackgroundRes = R.drawable.main_header;
        isShowTitleBar.set(false);
    }

    @Override
    protected void initView() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        spHelpPage = getSharedPreferences("HelpPage", MODE_PRIVATE);
        int language = AppUtil.getCurLanguage();
        AppUtil.switchLanguage(getApplicationContext(), language);
    }

    @Override
    protected void initData() {
        permissionSD();
        isLogin = AppUtil.isLogin();
        setFragment();
        helpPage();

        OtherRepository repository = OtherRepository.getInstance();
        repository.initUpdateCenterDao();
        uc_count = repository.getNeedUpdatedCount();
        if (!isShowPage) {
            intentToUpdateCenter();
        }
    }

    private void setFragment() {
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            getFragmentManager().beginTransaction().add(R.id.contentFrame, mainFragment).commit();
            mainFragment.setNavViewModel(mNavViewModel);
        }
    }

    private void permissionSD() {
        boolean sdPermission = PermissionUtil.checkPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        LogUtil.i(TAG, "sdPermission=" + sdPermission);

        if (!sdPermission) {
            LogUtil.i(TAG, "请求权限");
            PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, PMS_CODE_WRITE_SD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "=== onResume === isLogin=" + isLogin);
        LogUtil.i(TAG, "=== onResume ===  mNavViewModel.isLoginSuccess=" + mNavViewModel.isLoginSuccess.get());
        LogUtil.i(TAG, "=== onResume ===  AppUtil.isLogin()=" + AppUtil.isLogin());

        if (mNavViewModel.isLoginSuccess.get() != AppUtil.isLogin()) { // 做一个登陆状态的判断，只有在登陆状态改变时才执行以下操作
            mNavViewModel.isLoginSuccess.set(AppUtil.isLogin()); /* 改变Menu的文字 */
        }

        LogUtil.i(TAG, "onResume: App.mLanguage=" + AppUtil.getCurLanguage());
        LogUtil.i(TAG, "onResume: mNavViewModel.mCurrLang=" + mNavViewModel.mCurrLang.get());

        if (AppUtil.getCurLanguage() != mNavViewModel.mCurrLang.get()) {
            mNavViewModel.mCurrLang.set(AppUtil.getCurLanguage());
            mNavViewModel.updateLanguage();
            mainFragment.refreshImages();
            AppUtil.switchLanguage(getApplicationContext(), AppUtil.getCurLanguage());
        }

        updateCenterCount();

        boolean isOpenMainHelpPage = spHelpPage.getBoolean("HELP_PAGE_OPEN_MAIN", false);
        if (isOpenMainHelpPage) {
            showHelpPage();
            helpDialog.openMainHelpPage();
        }

        // 如果點擊Home按鈕，則將Menu的子按鈕都關閉
        boolean isPressHome = App.mSP_Config.getBoolean("HOME", false);
        if (isPressHome) {
            mainFragment.closeLitterMenu();
            App.mSP_Config.edit().putBoolean("HOME", false).apply();
        }
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
        if (HelpView.isFirstShow(HELP_PAGE_MAIN)) {
            showHelpPage();
            isShowPage=true;
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
            helpDialog.dismiss();
            intentToUpdateCenter();
        }
    };

    /**
     * 如果有更新，跳转到更新中心页面
     */
    private void intentToUpdateCenter() {
        LogUtil.i(TAG, "intentToUpdateCenter");
        if (uc_count > 0) {
            Intent intent = new Intent(this, UpdateCenterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void sendCrashLog() {
        final LoginClient client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);

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
