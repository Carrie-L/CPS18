package com.adsale.ChinaPlas.base;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.databinding.ActivityBaseBinding;
import com.adsale.ChinaPlas.databinding.NavHeaderBinding;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.ui.MainActivity;
import com.adsale.ChinaPlas.ui.PadMainActivity;
import com.adsale.ChinaPlas.ui.ScannerActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;
import com.baidu.mobstat.StatService;

import io.reactivex.disposables.Disposable;

/**
 * todo [LinearLayout   android:id="@+id/language"] 一删掉，Databinding 就报错。
 */
public abstract class BaseActivity extends AppCompatActivity implements NavViewModel.OnDrawerClickListener {
    //title bar
    public final ObservableField<String> barTitle = new ObservableField<>();
    public final ObservableBoolean isShowTitleBar = new ObservableBoolean(true);

    protected String TAG;
    private int mPadRvHeight;
    private boolean hasMeasured;
    protected DrawerLayout mDrawerLayout;
    private RelativeLayout mPadLayout;
    private NavigationView navigationView;
    protected FrameLayout mBaseFrameLayout;
    protected DBHelper mDBHelper;
    private RecyclerView recyclerView;
    private ActivityBaseBinding mBaseBinding;
    protected NavViewModel mNavViewModel;
    private boolean isInitedDrawer;

    protected int mToolbarBackgroundRes = R.drawable.inner_header;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected boolean isTablet;
    protected String mBaiduTJ;
    private String eventName;
    protected String mTypePrefix;
    private Window window;
    protected boolean isChangeTitleHomeIcon = false;

//    protected int mHomeIconRes = R.drawable.ic_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        mBaseBinding.setActivity(this);
        mBaseBinding.executePendingBindings();
        mBaseFrameLayout = mBaseBinding.contentFrame;

        mDBHelper = App.mDBHelper;
        isTablet = AppUtil.isTablet();
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mNavViewModel = NavViewModel.getInstance(getApplication());
        setContentWidth();
        preView();

        LogUtil.i(TAG, "mScreenWidth=" + mScreenWidth);
        LogUtil.i(TAG, "mScreenHeight=" + mScreenHeight);
        initToolbar();
        initView();

        if (TextUtils.isEmpty(barTitle.get())) {
            barTitle.set(getIntent().getStringExtra(Constant.TITLE));
        }

        initData();
        setBaiDuTJ();
        if (isTablet) {
            initDrawerPad();
        } else
            initDrawer();
    }

    private void setContentWidth() {
        mScreenWidth = AppUtil.getScreenWidth();
        mScreenHeight = AppUtil.getScreenHeight();
    }

    protected void setBarTitle(int resId) {
        if (TextUtils.isEmpty(barTitle.get())) {
            barTitle.set(getString(resId));
        }
    }

    private void initDrawer() {
        mDrawerLayout = mBaseBinding.drawerLayout;
        navigationView = mBaseBinding.navView;
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                LogUtil.i(TAG, "onDrawerOpened");
                openDrawer();
            }
        });
    }

    private void initDrawerPad() {
        mPadLayout = mBaseBinding.rlPad;
        navigationView = mBaseBinding.navView;
        getLeftHeight();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mScreenWidth, mScreenHeight);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        mPadLayout.setLayoutParams(layoutParams);
    }

    protected void setOnDrawerClickListener() {
        LogUtil.i(TAG, "- setOnDrawerClickListener - ");
        if (mNavViewModel.mDrawerListener == null) {
            mNavViewModel.setOnDrawerClickListener(this);
        }
    }

    private void setupDrawer() {
        LogUtil.i(TAG, "--------- isInitedDrawer=" + isInitedDrawer);
        long startTime = System.currentTimeMillis();

        NavHeaderBinding navBinding = NavHeaderBinding.inflate(getLayoutInflater(), navigationView, true);
        navBinding.setNavModel(mNavViewModel);
        recyclerView = navBinding.recyclerView;
        setOnDrawerClickListener();
        if (isTablet) {
            navigationView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
            mNavViewModel.onStart(recyclerView, null);
        } else {
            mNavViewModel.onStart(recyclerView, mDrawerLayout);
        }

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "initDrawer spend : " + (endTime - startTime) + "ms");
    }

    private void initToolbar() {
        RelativeLayout rlToolbar = mBaseBinding.layoutTitleBar.rlToolbar;
        rlToolbar.setBackgroundResource(mToolbarBackgroundRes);
        int toolbarHeight;
        if (AppUtil.isTablet()) {
            toolbarHeight = (mScreenWidth * Constant.PAD_HEADER_HEIGHT) / Constant.PAD_CONTENT_WIDTH;
        } else {
            toolbarHeight = (mScreenWidth * Constant.PHONE_HEADER_HEIGHT) / Constant.PHONE_HEADER_WIDTH; /* Toolbar图片尺寸：320*68 */
        }
        App.mSP_Config.edit().putInt(Constant.TOOLBAR_HEIGHT, toolbarHeight).apply();
        if (isTablet) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mScreenWidth, toolbarHeight);
            rlToolbar.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, toolbarHeight);
            rlToolbar.setLayoutParams(params);
        }
        setStatusBar();
        mBaseBinding.layoutTitleBar.txtBarHome.setImageResource(isChangeTitleHomeIcon ? R.drawable.btn_ok : R.drawable.ic_home);
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);/* 20180126添加  */
            window.setStatusBarColor(Color.TRANSPARENT);
            if (isShowTitleBar.get()) {
                window.setNavigationBarColor(getResources().getColor(R.color.home_transparent));
            } else {
                window.setNavigationBarColor(getResources().getColor(R.color.home_nav_bar));
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }


        }
    }

    public void logout(View view) {
        logout();
    }

    protected void preView() {
        TAG = getClass().getSimpleName();
    }

    protected abstract void initView();

    protected abstract void initData();

    protected <T> void intent(Class<T> cls, String title) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", title);
        startActivity(intent);
        overridePendingTransPad();
    }

    protected <T> void intent(Class<T> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransPad();
    }

    @Override
    public void login() {
        mNavViewModel.isLoginSuccess.set(false);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransPad();
    }

    protected void logout() {
        final String className = getClass().getSimpleName();
        LogUtil.i(TAG, "className=" + className);

        AppUtil.showAlertDialog(this, getString(R.string.logout_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                processLogout();
                LogUtil.i(TAG, "TAG=" + TAG);
                if ((!isTablet && !className.equals("MainActivity")) || ((isTablet && !className.equals("PadMainActivity")))) {
                    Intent intent = new Intent(getApplicationContext(), isTablet ? PadMainActivity.class : MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransPad();
                }
            }
        });
    }

    protected void processLogout() {
        AppUtil.putLogout();
        mNavViewModel.isLoginSuccess.set(false);
        mNavViewModel.isLoginStatusChanged.set(true);
        mNavViewModel.setUpHeader();
    }

    @Override
    public void sync() {
        SyncViewModel syncViewModel = new SyncViewModel(getApplicationContext());
        syncViewModel.syncMyExhibitor();
    }

    private void openDrawer() {
        if (!isInitedDrawer) {
            setupDrawer();
            isInitedDrawer = true;
        } else {
            LogUtil.i(TAG, "已经 isInitedDrawer=" + isInitedDrawer);
        }
        mNavViewModel.openDrawer();
    }

    private void menu() {
        if (isTablet) {
            LogUtil.i(TAG, "navigationView :" + navigationView.getVisibility() + "");
            if (navigationView.getVisibility() == View.VISIBLE) {
                LogUtil.i(TAG, "animatorClose");
                animatorClose();
            } else {
                navigationView.setVisibility(View.VISIBLE);
                openDrawer();
                animatorOpen();
                LogUtil.i(TAG, "animatorOpen");
            }
        } else {
            openDrawer();
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        AppUtil.trackViewLog(184, "Page", "", "SlideMenu");
    }

    public void onMenu() {
        menu();
    }


    public void back() {
        finish();
        overridePendingTransPad();
    }

    public void home() {
        if (isChangeTitleHomeIcon) {
            onReplaceHomeClick();
        } else {
            Intent intent = new Intent(this, isTablet ? PadMainActivity.class : MainActivity.class);
            App.mSP_Config.edit().putBoolean("HOME", true).apply();
            startActivity(intent);
            finish();
            overridePendingTransPad();
        }
    }

    /**
     * 替换掉 HOME icon 后的点击行为
     */
    protected void onReplaceHomeClick() {

    }

    public void overridePendingTransPad() {
        if (isTablet) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppUtil.createConfigurationContext(base));
    }

    /**
     * 百度统计
     */
    private void setBaiDuTJ() {
        mBaiduTJ = getIntent().getStringExtra(Constant.BAIDU_TJ);
        LogUtil.i(TAG, "setBaiDuTJ:mBaiduTJ=" + mBaiduTJ);

        if (mBaiduTJ != null) {
            eventName = "Page_".concat(mBaiduTJ).concat("_").concat(AppUtil.getLanguageType()).concat("_Android");
        }
        if (mTypePrefix != null) {
            eventName = setEventName(mTypePrefix);
        }
        LogUtil.i(TAG, "setBaiDuTJ:eventName=" + eventName);
    }

    /**
     * @param prefix 前缀，如“Page_NewsLink”,"EPage_220098"
     * @return String
     */
    public String setEventName(String prefix) {
        return prefix.concat("_").concat(AppUtil.getLanguageType()).concat("_Android");
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (AppUtil.isTablet()) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        AppUtil.switchLanguage(getApplicationContext(), AppUtil.getCurLanguage());
//        mNavViewModel.mCurrLang.set(AppUtil.getCurLanguage());
//    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, eventName);
        LogUtil.e(TAG, "this=" + this + "..onResume_eventName=" + eventName);

        LogUtil.i("onResume", "SP LANGUAGE = " + AppUtil.getCurLanguage() + ", App.mLanguage=" + App.mLanguage.get());

    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, eventName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.i(TAG, "onRequestPermissionsResult: requestCode=" + requestCode);
        if (requestCode == PermissionUtil.PMS_CODE_CAMERA && PermissionUtil.getGrantResults(grantResults)) {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivity(intent);
            overridePendingTransPad();
        }
    }

    private void getLeftHeight() {
        ViewTreeObserver observer = navigationView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!hasMeasured) {
                    mPadRvHeight = navigationView.getHeight();
                    Log.i(TAG, "mPadRvHeight=" + mPadRvHeight);
                    hasMeasured = true;
                }
            }
        });
    }

    private void animatorOpen() {
        navigationView.setVisibility(View.VISIBLE);
        LogUtil.e(TAG, "mPadRvHeight=" + mPadRvHeight);
        ValueAnimator animator = createDropAnimator(0, mPadRvHeight);
        animator.setDuration(500);
        animator.start();
        navigationView.requestLayout();
    }

    private void animatorClose() {
        int orgHeight = navigationView.getHeight();
        LogUtil.e(TAG, "orgHeight=" + orgHeight);
        ValueAnimator animator = createDropAnimator(orgHeight, 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                navigationView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private ValueAnimator createDropAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) navigationView.getLayoutParams();
                layoutParams.height = value;
                navigationView.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

}
