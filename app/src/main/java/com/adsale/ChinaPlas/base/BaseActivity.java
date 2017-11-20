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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.databinding.ActivityBaseBinding;
import com.adsale.ChinaPlas.databinding.NavHeaderBinding;
import com.adsale.ChinaPlas.databinding.ToolbarBaseBinding;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.ui.MainActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;

public abstract class BaseActivity extends AppCompatActivity implements NavViewModel.OnDrawerClickListener {
    //title bar
    public final ObservableField<String> barTitle = new ObservableField<>();
    public final ObservableBoolean isShowTitleBar = new ObservableBoolean(true);

    protected static String TAG;
    protected DrawerLayout mDrawerLayout;
    protected FrameLayout mBaseFrameLayout;
    protected DBHelper mDBHelper;
    private RecyclerView recyclerView;
    private ActivityBaseBinding mBaseBinding;
    protected NavViewModel mNavViewModel;
    private boolean isInitedDrawer;

    protected int mToolbarBackgroundRes = R.drawable.inner_header;
    protected ActionBar actionBar;
    protected int mScreenWidth;
    protected boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        mBaseFrameLayout = mBaseBinding.contentFrame;

        mDBHelper = App.mDBHelper;
        isTablet = AppUtil.isTablet();
        mNavViewModel = new NavViewModel(getApplicationContext());

        mScreenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);


        preView();

        setupToolBar();

        initView();

        if (TextUtils.isEmpty(barTitle.get())) {
            barTitle.set(getIntent().getStringExtra(Constant.TITLE));
        }


        initData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        TAG = getClass().getSimpleName();
    }

    private void initDrawer() {
        LogUtil.i(TAG, "--------- isInitedDrawer=" + isInitedDrawer);
        long startTime = System.currentTimeMillis();

        mDrawerLayout = mBaseBinding.drawerLayout;
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        NavigationView navigationView = mBaseBinding.navView;
        View navHeaderView = navigationView.getHeaderView(0);
        NavHeaderBinding navBinding = NavHeaderBinding.inflate(getLayoutInflater(), navigationView, true);
        navBinding.setNavModel(mNavViewModel);

        recyclerView = navBinding.recyclerView;

        mNavViewModel.setOnDrawerClickListener(this);

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "initDrawer spend : " + (endTime - startTime) + "ms");
    }

    private void setupDrawer() {
        mNavViewModel.onStart(recyclerView, mDrawerLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            menu();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolBar() {
        FrameLayout toolbarFrame = mBaseBinding.toolbarFrame;
        ToolbarBaseBinding toolbarBinding = ToolbarBaseBinding.inflate(getLayoutInflater(), toolbarFrame, true);
        toolbarBinding.setVariable(BR.activity, this);
        toolbarBinding.executePendingBindings();
        Toolbar toolbar = toolbarBinding.toolbar;
        toolbar.setBackgroundResource(mToolbarBackgroundRes);

        int height = (mScreenWidth * 68) / 320; /* Toolbar图片尺寸：320*68 */
        App.mSP_Config.edit().putInt(Constant.TOOLBAR_HEIGHT, height).apply();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mScreenWidth, height);
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null && !isShowTitleBar.get()) { // Main Activity
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayShowTitleEnabled(false);
        }

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
        }
    }

    public void logout(View view) {
        logout();
    }

    protected void preView() {

    }

    protected abstract void initView();

    protected abstract void initData();

    protected <T> void intent(Class<T> cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    protected void logout() {
        AppUtil.showAlertDialog(this, getString(R.string.logout_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                processLogout();
            }
        });
    }

    protected void processLogout(){
        AppUtil.putLogout();
        mNavViewModel.isLoginSuccess.set(false);
        mNavViewModel.setUpHeader();
        mNavViewModel.updateDrawerListLogin();
    }

    @Override
    public void itemClicked(String bdTJ) {

    }

    @Override
    public void languageChanged(int language, boolean inMainAty) {
        if (!inMainAty) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        LogUtil.i(TAG, "BaseAty: languageChanged=" + language + ",inMainAty=" + inMainAty);
    }

    @Override
    public void sync() {
        SyncViewModel syncViewModel = new SyncViewModel(getApplicationContext());
        syncViewModel.syncMyExhibitor();
    }

    private void menu() {
        if (!isInitedDrawer) {
            initDrawer();
            setupDrawer();
            isInitedDrawer = true;
        } else {
            LogUtil.i(TAG, "已经 isInitedDrawer=" + isInitedDrawer);
        }
        mNavViewModel.openDrawer();
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
        startActivity(intent);
        finish();
    }

    public void overridePendingTransPad() {
        if (isTablet) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

}
