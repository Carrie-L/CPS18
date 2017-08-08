package com.adsale.ChinaPlas.base;

import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.DrawerAdapter;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.databinding.ActivityBaseBinding;
import com.adsale.ChinaPlas.databinding.NavHeaderBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.LoginViewModel;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.adsale.ChinaPlas.R.id.language;


public abstract class BaseActivity extends AppCompatActivity {
    protected static String TAG;
    protected DrawerLayout mDrawerLayout;
    protected FrameLayout mBaseFrameLayout;
    protected DBHelper mDBHelper;
    private RecyclerView recyclerView;
    private ActivityBaseBinding mBaseBinding;
    private NavViewModel mNavViewModel;
    private boolean isInitedDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        mBaseFrameLayout = mBaseBinding.contentFrame;

        mDBHelper = App.mDBHelper;
        TAG = getClass().getSimpleName();

        setupToolBar();

        preView();
        initView();
        initData();
    }

    private void initDrawer() {
        LogUtil.i(TAG, "isInitedDrawer=" + isInitedDrawer);
        mDrawerLayout = mBaseBinding.drawerLayout;
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        NavigationView navigationView = mBaseBinding.navView;
        View navHeaderView = navigationView.getHeaderView(0);
        NavHeaderBinding navBinding = NavHeaderBinding.inflate(getLayoutInflater(), navigationView, true);

        mNavViewModel = new NavViewModel(getApplicationContext());
        navBinding.setNavViewModel(mNavViewModel);

        recyclerView = navBinding.recyclerView;
    }

    private void setupDrawer() {
        mNavViewModel.onStart(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!isInitedDrawer) {
                initDrawer();
                setupDrawer();
                isInitedDrawer = true;
            } else {
                LogUtil.i(TAG, "已经 isInitedDrawer=" + isInitedDrawer);
            }
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolBar() {
        setSupportActionBar(mBaseBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    protected void preView() {

    }

    protected abstract void initView();

    protected abstract void initData();


}
