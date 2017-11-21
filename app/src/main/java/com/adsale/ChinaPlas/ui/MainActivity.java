package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.UpdateCenterDao;
import com.adsale.ChinaPlas.databinding.ActivityMainBinding;
import com.adsale.ChinaPlas.helper.HelpPage;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;

import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

/**
 * todo HELP PAGE:还需解决：全屏，平板
 */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainFragment mainFragment;

    private ViewStub mVS_Help;
    private View mHelpView;
    private HelpPage helpPage;
    private ImageView ivCloseHelpPage;
    private SharedPreferences spHelpPage;
    /**
     * 是否显示完第一次帮助页面，true，显示完，下次进入不再显示；false，还未显示，显示
     */
    private boolean isFirstHelpPageShowed;
    private boolean isLogin = false;
    private boolean isPadDevice;

    @Override
    protected void preView() {
        super.preView();
        TAG="MainActivity";
        mToolbarBackgroundRes = R.drawable.main_header;
        isShowTitleBar.set(false);
    }

    @Override
    protected void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mVS_Help = binding.viewPagerHelp.getViewStub();
        helpPage = new HelpPage();
        spHelpPage = getSharedPreferences("HelpPage", MODE_PRIVATE);
        isPadDevice = AppUtil.isTablet();
    }

    @Override
    protected void initData() {
        permissionSD();
        isLogin = AppUtil.isLogin();
        mNavViewModel.setMainActivity(this);
        setFragment();

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

        LogUtil.i(TAG,"=== onResume === isLogin=" +isLogin);
        LogUtil.i(TAG,"=== onResume ===  mNavViewModel.isLoginSuccess=" + mNavViewModel.isLoginSuccess.get());
        LogUtil.i(TAG,"=== onResume ===  AppUtil.isLogin()=" + AppUtil.isLogin());

        if (mNavViewModel.isLoginSuccess.get() != AppUtil.isLogin()) { // 做一个登陆状态的判断，只有在登陆状态改变时才执行以下操作
//            isLogin=AppUtil.isLogin();
            mNavViewModel.isLoginSuccess.set(AppUtil.isLogin()); /* 改变Menu的文字 */
            mNavViewModel.updateDrawerListLogin();
        }
//        firstHelpPage();
    }

    /**
     * 5个帮助页面完成了，才算是完成初始化启动
     */
    private void firstHelpPage() {
        LogUtil.i(TAG, "firstHelpPage");
        if (canShowHelpPage()) {
            if (isPadDevice) {
                helpPage.showPageMenu(getApplicationContext(), false, mMenuHelpCloseListener);
            } else {
                helpPage();
            }
        } else {

        }
    }

    /**
     * 显示帮助页面
     */
    private void helpPage() {
        if (mHelpView == null) {
            mHelpView = mVS_Help.inflate();
            helpPage.findView(mHelpView);
            ivCloseHelpPage = (ImageView) mHelpView.findViewById(R.id.btn_help_page_close);
        }
        Integer[] imageIds;
        imageIds = helpPage.getMenuImages();

        helpPage.init(this, imageIds);
        mHelpView.setVisibility(View.VISIBLE);
        ivCloseHelpPage.setOnClickListener(mMenuHelpCloseListener);
        spHelpPage.edit().putBoolean("ShowMenuHelpPage", false).apply();/*  帮助页面显示过了，设为false，下次获取ShowMenuHelpPage为true时，表示打开帮助页面 */
    }

    private View.OnClickListener mMenuHelpCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setFragment();
            if (!isFirstHelpPageShowed) {
                spHelpPage.edit().putBoolean("IsFirstHelpPageShowed", true).apply();
                intentToUpdateCenter();
            }
            if (AppUtil.isTablet()) {
//                mHelpFrame.setVisibility(View.GONE);
            } else
                mHelpView.setVisibility(View.GONE);
        }
    };

    /**
     * 如果是第一次进入主界面，或者设置中点击了帮助，则显示帮助页面
     *
     * @return isFirstHelpPageShowed
     */
    private boolean canShowHelpPage() {
        isFirstHelpPageShowed = isFirstHelpPageShowed();
        return !isFirstHelpPageShowed || spHelpPage.getBoolean("ShowMenuHelpPage", false);
    }

    private boolean isFirstHelpPageShowed() {
        //"第一次显示帮助页面"是否已经显示过了，true:显示过了；false:还没有显示过
        isFirstHelpPageShowed = spHelpPage.getBoolean("IsFirstHelpPageShowed", false);
        return isFirstHelpPageShowed;
    }

    /**
     * 如果有更新，跳转到更新中心页面
     */
    private void intentToUpdateCenter() {
        if (isFirstHelpPageShowed()) {
            int uc_count = getNeedUpdatedCount();
            LogUtil.i(TAG, "uc_count=" + uc_count);
            if (uc_count > 0) {
                Intent intent = new Intent(this, UpdateCenterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    /**
     * 获取更新中心需要下载的个数
     */
    public int getNeedUpdatedCount() {
        return App.mDBHelper.mUpdateCenterDao.queryBuilder().where(UpdateCenterDao.Properties.Status.eq(0)).list().size();
    }

}
