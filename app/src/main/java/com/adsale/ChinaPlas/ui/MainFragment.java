package com.adsale.ChinaPlas.ui;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.MenuAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.FragmentMainBinding;
import com.adsale.ChinaPlas.ui.view.M2Dialog;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.MainViewModel;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnIntentListener {

    private FragmentMainBinding binding;
    private ImageView leftPic;
    private ImageView rightPic;
    private RecyclerView recyclerView;

    public NavViewModel navViewModel;
    private MainViewModel mainViewModel;
    private MainPic mainPic;
    private int menuHeight;
    private int navHeight;
    private MenuAdapter adapter;

    public void getMainData(){








    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        initView();
        binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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

    public MainFragment() {

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
        calNavigationBar();
        setBottomPics();

        getMainIcon();
    }

    private void getMainIcon(){
        BmobQuery<MainIcon> query = new BmobQuery<>();
        query.addWhereGreaterThan("updatedAt","2018-09-11 14:43:52");
        query.findObjects(new FindListener<MainIcon>() {
            @Override
            public void done(List<MainIcon> list, BmobException e) {
                LogUtil.i(TAG,"getMainIcon::: list = "+list.size()+","+list.toString());



            }
        });










    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isM2Popup = App.mSP_Config.getBoolean("isM2Popup", false);
        LogUtil.i(TAG, "MainFragment:onResume:isM2Popup=" + isM2Popup);
        if (!isM2Popup && !TextUtils.isEmpty(mainViewModel.m2LargeUrl)) {
            m2UpAnimation(mainViewModel.m2LargeUrl);
            App.mSP_Config.edit().putBoolean("isM2Popup", true).apply();
        }
//        mainViewModel.resume();
    }

    private void m2UpAnimation(String url) {
        M2Dialog dialog = new M2Dialog(getActivity());
        dialog.setUrl(url);
        dialog.show();
    }

    private void calNavigationBar() {
        boolean hasMenuKey = ViewConfiguration.get(getActivity()).hasPermanentMenuKey();
        LogUtil.i(TAG, "hasMenuKey=" + hasMenuKey);
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        LogUtil.i(TAG, "hasBackKey=" + hasBackKey);
        if (!hasMenuKey) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            navHeight = resources.getDimensionPixelSize(resourceId);
            LogUtil.i(TAG, "navigation_bar_height=" + navHeight);
        }
    }

    private void setGridMenus() {
        menuHeight = (mainViewModel.screenWidth * Constant.MAIN_MENU_HEIGHT * 2) / (Constant.MAIN_MENU_WIDTH * 3);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mainViewModel.screenWidth, menuHeight);
        recyclerView.setLayoutParams(params);

        ArrayList<MainIcon> largeIcons = new ArrayList<>();
        ArrayList<MainIcon> littleIcons = new ArrayList<>();
        mainViewModel.getMainIcons(largeIcons, littleIcons);
        adapter = new MenuAdapter(getActivity(), largeIcons, littleIcons, this, navViewModel);
        recyclerView.setAdapter(adapter);
    }

    private void setBottomPics() {
        //311 * 161
        int bottomHeight = (mainViewModel.screenWidth * 161) / (311 * 2);
        LogUtil.i(TAG, "bottomHeight=" + bottomHeight);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mainViewModel.screenWidth / 2, bottomHeight);
        binding.ivLeftPic.setLayoutParams(params);
        binding.ivRightPic.setLayoutParams(params);
        setBottomImage();
    }





    private void setBottomImage() {
        LogUtil.i(TAG, "bottom banners =" + mainPic.LeftBottomBanner.EN.BannerImage + ", " + mainPic.RightBottomBanner.EN.BannerImage);
        LogUtil.i(TAG, "bottom banners =" + mainPic.LeftBottomBanner.EN.BannerImage_Pad + ", " + mainPic.RightBottomBanner.EN.BannerImage_Pad);
        if (App.isNetworkAvailable) {
            Glide.with(getActivity()).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? mainPic.LeftBottomBanner.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? mainPic.LeftBottomBanner.EN.BannerImage : mainPic.LeftBottomBanner.SC.BannerImage)).into(leftPic);
            Glide.with(getActivity()).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? mainPic.RightBottomBanner.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? mainPic.RightBottomBanner.EN.BannerImage : mainPic.RightBottomBanner.SC.BannerImage)).into(rightPic);
        } else {
            Glide.with(getActivity()).load(String.format("file:///android_asset/Menu/left_%s.png", getLangType())).into(leftPic);
            Glide.with(getActivity()).load(String.format("file:///android_asset/Menu/right_%s.png", getLangType())).into(rightPic);
        }
    }

    private String getLangType() {
        return App.mLanguage.get() == 0 ? "tc" : App.mLanguage.get() == 1 ? "en" : "sc";
    }

    public void refreshImages() {
        setBottomImage();
        mainViewModel.refreshImages();
    }

    /**
     * when click [HOME] button, set little menus invisible
     */
    public void closeLitterMenu() {
        adapter.mClickPos.set(-1);
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

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i("MainFragment", "-------onPause()------");
//        mainViewModel.pasue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainViewModel.destroy();
    }



}
