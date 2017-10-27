package com.adsale.ChinaPlas.ui;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.MenuAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.databinding.FragmentMainBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.MainViewModel;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnIntentListener {

    private FragmentMainBinding binding;
    private ImageView leftPic;
    private ImageView rightPic;
    private RecyclerView recyclerView;

    private int language;


    private NavViewModel navViewModel;
    private MainViewModel mainViewModel;
    private MainPic mainPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        initData();
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private void initView() {
        recyclerView = binding.menuRecyclerView;
        leftPic = binding.ivLeftPic;
        rightPic = binding.ivRightPic;
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {
        language = AppUtil.getCurLanguage();
        initRecyclerView();
        mainPic = mainViewModel.parseMainInfo();
        mainViewModel.setTopPics();
        mainViewModel.setM2AD();
        setBottomPics();
        setGridMenus();
    }

    private void setGridMenus() {
        ArrayList<MainIcon> largeIcons = new ArrayList<>();
        ArrayList<MainIcon> littleIcons = new ArrayList<>();
        mainViewModel.getMainIcons(largeIcons, littleIcons);
        MenuAdapter adapter = new MenuAdapter(getActivity(), largeIcons, littleIcons, this);
        recyclerView.setAdapter(adapter);
    }

    private void setBottomPics() {
        // main_header 的高度
        int actionBarHeight = App.mSP_Config.getInt(Constant.TOOLBAR_HEIGHT, 0);
        int displayHeight = App.mSP_Config.getInt(Constant.DISPLAY_HEIGHT, 0);
        int menuHeight = (mainViewModel.screenWidth * 90 * 2) / (100 * 3);// menu图片尺寸：100*90. 这里计算的是两行menu的高度
        int aboveFixedHeight = actionBarHeight + mainViewModel.topHeight + menuHeight + getStatusBarHeight();// 不知道为什么加个getStatusBarHeight正合适
        int bottomHeight = displayHeight - aboveFixedHeight - mainViewModel.adHeight;/* 如果有广告，再减去广告高度 */
        LogUtil.i(TAG, "actionBarHeight=" + actionBarHeight);
        LogUtil.i(TAG, "menuHeight=" + (mainViewModel.screenWidth / 3));
        LogUtil.i(TAG, "topHeight=" + mainViewModel.topHeight);
        LogUtil.i(TAG, "screenWidth=" + mainViewModel.screenWidth);
        LogUtil.i(TAG, "bottomHeight=" + bottomHeight);
        LogUtil.i(TAG, "aboveFixedHeight=" + aboveFixedHeight);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mainViewModel.screenWidth, bottomHeight);
        binding.llBottom.setLayoutParams(params);
        Glide.with(getActivity()).load(Uri.parse(language == 0 ? mainPic.LeftBottomBanner.TC.BannerImage : language == 1 ? mainPic.LeftBottomBanner.EN.BannerImage : mainPic.LeftBottomBanner.SC.BannerImage)).into(leftPic);
        Glide.with(getActivity()).load(Uri.parse(language == 0 ? mainPic.RightBottomBanner.TC.BannerImage : language == 1 ? mainPic.RightBottomBanner.EN.BannerImage : mainPic.RightBottomBanner.SC.BannerImage)).into(rightPic);
    }

    private int getStatusBarHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        LogUtil.e(TAG, "状态栏-方法1:" + statusBarHeight);
        return statusBarHeight;
    }


    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = navViewModel.newIntent(getActivity(), (MainIcon) entity);
        if (intent == null) {
            return;
        }
        startActivity(intent);
    }

    private void initViewModel() {
        mainViewModel = new MainViewModel(getActivity(), this);
        navViewModel = new NavViewModel(getActivity());
        binding.setModel(mainViewModel);
        mainViewModel.init(binding.mainTopViewPager, binding.vpindicator, binding.ivAd);
    }
}
