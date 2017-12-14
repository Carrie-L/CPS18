package com.adsale.ChinaPlas.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.databinding.ActivityMainBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;

import static com.adsale.ChinaPlas.ui.view.HelpView.HELP_PAGE_MAIN;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

/**
 * todo 平板
 */
public class MainActivity extends BaseActivity {

    private MainFragment mainFragment;
    private SharedPreferences spHelpPage;
    private boolean isLogin = false;
    private int uc_count;
    private boolean isShowPage;
    private HelpView helpView;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.i(TAG,"onWindowFocusChanged:hasFocus="+hasFocus);
    }

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
    }

    @Override
    protected void initData() {
        permissionSD();
        isLogin = AppUtil.isLogin();
        mNavViewModel.setMainActivity(this);
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
            mNavViewModel.updateDrawerListLogin();
        }

        LogUtil.i(TAG, "onResume: App.mLanguage=" + App.mLanguage.get());
        LogUtil.i(TAG, "onResume: mNavViewModel.mCurrLang=" + mNavViewModel.mCurrLang.get());

        if (App.mLanguage.get() != mNavViewModel.mCurrLang.get()) {
            mNavViewModel.mCurrLang.set(App.mLanguage.get());
            mNavViewModel.updateLanguage();
            mainFragment.refreshImages();
            AppUtil.switchLanguage(getApplicationContext(), App.mLanguage.get());
        }

        updateCenterCount();

        boolean isOpenMainHelpPage = spHelpPage.getBoolean("HELP_PAGE_OPEN_MAIN", false);
        if (isOpenMainHelpPage) {
            helpView.openMainHelpPage();
        }


//        firstHelpPage();
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
        helpView = new HelpView(this, HELP_PAGE_MAIN, mMenuHelpCloseListener);
        isShowPage = helpView.showPage();
    }

    private View.OnClickListener mMenuHelpCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            helpView.dismissDialog();
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
