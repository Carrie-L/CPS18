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

import com.adsale.ChinaPlas.adapter.MenuAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.FragmentMainBinding;
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

//    private int language;

    private NavViewModel navViewModel;
    private MainViewModel mainViewModel;
    private MainPic mainPic;
    private int menuHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i(TAG, "onActivityCreated");
        initViewModel();
        initData();
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public void setNavViewModel(NavViewModel model) {
        navViewModel = model;
        LogUtil.i(TAG, "setNavViewModel");
    }

    private void initData() {
        initRecyclerView();
        mainPic = mainViewModel.parseMainInfo();
        mainViewModel.setTopPics();
        mainViewModel.setM2AD();
        setGridMenus();
        setBottomPics();
    }

    private void setGridMenus() {
        ArrayList<MainIcon> largeIcons = new ArrayList<>();
        ArrayList<MainIcon> littleIcons = new ArrayList<>();
        mainViewModel.getMainIcons(largeIcons, littleIcons);
        MenuAdapter adapter = new MenuAdapter(getActivity(), largeIcons, littleIcons, this, navViewModel);
        recyclerView.setAdapter(adapter);
    }

    private void setBottomPics() {
//        // main_header 的高度
//        int actionBarHeight = App.mSP_Config.getInt(Constant.TOOLBAR_HEIGHT, 0);
//        int displayHeight = App.mSP_Config.getInt(Constant.DISPLAY_HEIGHT, 0);// 用 displayHeight 刚好
//        menuHeight = (mainViewModel.screenWidth * Constant.MAIN_MENU_HEIGHT * 2) / (Constant.MAIN_MENU_WIDTH * 3);
//        int aboveFixedHeight = actionBarHeight + mainViewModel.topHeight + menuHeight;
//        int bottomHeight = displayHeight - aboveFixedHeight - mainViewModel.adHeight;/* 如果有广告，再减去广告高度 */
//        LogUtil.i(TAG, "displayHeight=" + displayHeight);
//        LogUtil.i(TAG, "statusBarHeight=" + getStatusBarHeight());
//        LogUtil.i(TAG, "actionBarHeight=" + actionBarHeight);
//        LogUtil.i(TAG, "topHeight=" + mainViewModel.topHeight);
//        LogUtil.i(TAG, "menuHeight=" + menuHeight);
//        LogUtil.i(TAG, "adHeight=" + mainViewModel.adHeight);
//        LogUtil.i(TAG, "aboveFixedHeight=" + aboveFixedHeight);
//        LogUtil.i(TAG, "screenWidth=" + mainViewModel.screenWidth);
//        LogUtil.i(TAG, "bottomHeight=" + bottomHeight);
//
//        int bottomPx2Dp = DisplayUtil.px2dip(getActivity(), bottomHeight);
//        LogUtil.i(TAG, "--- bottomPx2Dp=" + bottomPx2Dp);

        //311 * 161
        int bottomHeight = (mainViewModel.screenWidth * 161) / (311 * 2);
        LogUtil.i(TAG, "bottomHeight=" + bottomHeight);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mainViewModel.screenWidth / 2, bottomHeight);
        binding.ivLeftPic.setLayoutParams(params);
        binding.ivRightPic.setLayoutParams(params);
//        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        setBottomImage();
    }

    private void setBottomImage() {
        LogUtil.i(TAG, "setBottomImage");
        Glide.with(getActivity()).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? mainPic.LeftBottomBanner.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? mainPic.LeftBottomBanner.EN.BannerImage : mainPic.LeftBottomBanner.SC.BannerImage)).into(leftPic);
        Glide.with(getActivity()).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? mainPic.RightBottomBanner.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? mainPic.RightBottomBanner.EN.BannerImage : mainPic.RightBottomBanner.SC.BannerImage)).into(rightPic);
    }

    public void refreshImages() {
        setBottomImage();
        mainViewModel.refreshImages();
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
        if (toCls != null && toCls.getSimpleName().equals("ExhibitorDetailActivity")) {
            Intent intent = new Intent(getActivity(), toCls);
            intent.putExtra(Constant.COMPANY_ID, ((adAdvertisementObj) entity).M2.getCompanyID(navViewModel.mCurrLang.get()));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Intent intent = navViewModel.newIntent(getActivity(), (MainIcon) entity);
            if (intent == null) {
                return;
            }
            startActivity(intent);
        }
    }


    /**
     * 如果有更新，跳转到更新中心页面
     */
    private void intentToUpdateCenter() {
        LogUtil.i(TAG, "intentToUpdateCenter");
        OtherRepository repository = OtherRepository.getInstance();
        repository.initUpdateCenterDao();
        int uc_count = repository.getNeedUpdatedCount();
        if (uc_count > 0) {
            Intent intent = new Intent(getActivity(), UpdateCenterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void initViewModel() {
        mainViewModel = new MainViewModel(getActivity(), this);
        binding.setModel(mainViewModel);
        mainViewModel.init(binding.mainTopViewPager, binding.vpindicator, binding.ivAd, navViewModel);
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
}
