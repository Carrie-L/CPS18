package com.adsale.ChinaPlas.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
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
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;
import com.baidu.mobstat.StatService;

/**
 * todo [LinearLayout   android:id="@+id/language"] 一删掉，Databinding 就报错。
 */
public abstract class BaseActivity extends AppCompatActivity implements NavViewModel.OnDrawerClickListener {
    //title bar
    public final ObservableField<String> barTitle = new ObservableField<>();
    public final ObservableBoolean isShowTitleBar = new ObservableBoolean(true);

    protected String TAG;
    protected DrawerLayout mDrawerLayout;
    protected FrameLayout mBaseFrameLayout;
    protected DBHelper mDBHelper;
    private RecyclerView recyclerView;
    private ActivityBaseBinding mBaseBinding;
    protected NavViewModel mNavViewModel;
    private boolean isInitedDrawer;

    protected int mToolbarBackgroundRes = R.drawable.inner_header;
    protected int mScreenWidth;
    protected boolean isTablet;
    protected String mBaiduTJ;
    private String eventName;
    protected String mTypePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        mBaseBinding.setActivity(this);
        mBaseBinding.executePendingBindings();
        mBaseFrameLayout = mBaseBinding.contentFrame;

        mDBHelper = App.mDBHelper;
        isTablet = AppUtil.isTablet();
        mNavViewModel = new NavViewModel(getApplicationContext());

        mScreenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);

        preView();
        initToolbar();
        initView();

        if (TextUtils.isEmpty(barTitle.get())) {
            barTitle.set(getIntent().getStringExtra(Constant.TITLE));
        }


        initData();
        setBaiDuTJ();
        initDrawer();
    }

    protected void setBarTitle(int resId) {
        if (TextUtils.isEmpty(barTitle.get())) {
            barTitle.set(getString(resId));
        }
    }

    private void initDrawer() {
        mDrawerLayout = mBaseBinding.drawerLayout;
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

    protected void setOnDrawerClickListener() {
        LogUtil.i(TAG, "- setOnDrawerClickListener - ");
        if (mNavViewModel.mDrawerListener == null) {
            mNavViewModel.setOnDrawerClickListener(this);
        }
    }

    private void setupDrawer() {
        LogUtil.i(TAG, "--------- isInitedDrawer=" + isInitedDrawer);
        long startTime = System.currentTimeMillis();

        NavigationView navigationView = mBaseBinding.navView;
        NavHeaderBinding navBinding = NavHeaderBinding.inflate(getLayoutInflater(), navigationView, true);
        navBinding.setNavModel(mNavViewModel);

        recyclerView = navBinding.recyclerView;

        setOnDrawerClickListener();

        mNavViewModel.onStart(recyclerView, mDrawerLayout);

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "initDrawer spend : " + (endTime - startTime) + "ms");


    }

    private void initToolbar() {
        RelativeLayout rlToolbar = mBaseBinding.layoutTitleBar.rlToolbar;
        rlToolbar.setBackgroundResource(mToolbarBackgroundRes);

        int height = (mScreenWidth * 68) / 320; /* Toolbar图片尺寸：320*68 */
        App.mSP_Config.edit().putInt(Constant.TOOLBAR_HEIGHT, height).apply();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, height);
        rlToolbar.setLayoutParams(params);

        setStatusBar();
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if(isShowTitleBar.get()){
                window.setNavigationBarColor(getResources().getColor(R.color.home_transparent));
            }else{
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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
    }

    @Override
    public void login() {
        mNavViewModel.isLoginSuccess.set(false);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
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
                if (!className.equals("MainActivity")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        mNavViewModel.updateDrawerListLogin();
    }

    protected void processLogin() {
        AppUtil.putLogin();
        mNavViewModel.isLoginSuccess.set(true);
        mNavViewModel.setUpHeader();
        mNavViewModel.updateDrawerListLogin();
    }

    @Override
    public void languageChanged(int language, boolean inMainAty) {
        LogUtil.i("BaseAty", "languageChanged: " + language);
        if (!inMainAty) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
        }
        LogUtil.i(TAG, "BaseAty: languageChanged=" + language + ",inMainAty=" + inMainAty);
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
        openDrawer();
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void onMenu() {
        menu();
    }


    public void back() {
        finish();
        overridePendingTransPad();
    }

    public void home() {
        Intent intent = new Intent(this, MainActivity.class);
        App.mSP_Config.edit().putBoolean("HOME", true).apply();
        startActivity(intent);
        finish();
    }

    public void overridePendingTransPad() {
        if (isTablet) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    /**
     * 百度统计
     */
    private void setBaiDuTJ() {
        mBaiduTJ = getIntent().getStringExtra(Constant.BAIDU_TJ);
        if (mBaiduTJ != null) {
            eventName = "Page_".concat(mBaiduTJ).concat("_").concat(AppUtil.getLanguageType()).concat("_Android");
        }
        if (mTypePrefix != null) {
            eventName = setEventName(mTypePrefix);
        }
    }

    /**
     * @param prefix 前缀，如“Page_NewsLink”,"EPage_220098"
     * @return String
     */
    public String setEventName(String prefix) {
        return prefix.concat("_").concat(AppUtil.getLanguageType()).concat("_Android");
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, eventName);
        LogUtil.e(TAG, "this=" + this + "..onResume_eventName=" + eventName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, eventName);
    }
}
