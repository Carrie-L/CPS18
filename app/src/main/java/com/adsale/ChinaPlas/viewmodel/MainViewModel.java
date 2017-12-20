package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.ImageViewBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.adsale.ChinaPlas.utils.Constant.MAIN_TOP_BANNER_HEIGHT;
import static com.adsale.ChinaPlas.utils.Constant.MAIN_TOP_BANNER_WIDTH;


/**
 * Created by Ponyo on 2017/8/5.
 * Main
 */

public class MainViewModel {
    private Context mContext;
    private MainIconRepository mRepository;
    private NavViewModel navViewModel;
    private MainPic mainPic;

    private ViewPager viewPager;
    private LinearLayout vpindicator;
    private LinearLayout.LayoutParams ind_params;

    /**
     * 设置默认banner图片的尺寸
     */
    private final Integer TOP_PIC_WIDTH = 320;
    private final Integer TOP_PIC_HEIGHT = 141;
    public int screenWidth;
    public int topHeight;
    public int adHeight;
    private adAdvertisementObj adObj;
    private ImageView adPic;
    private OnIntentListener mIntentListener;
    private AdViewPagerAdapter viewPagerAdapter;
    private List<View> topPics;
    private LayoutInflater inflater;
    private MainPic.Banners banners;
    private int topBannerSize;
    public ObservableBoolean isCountDownShow = new ObservableBoolean(false);
    private ImageView ivTop;
    private ImageView ivPoint;
    private ADHelper adHelper;
    private ArrayList<MainIcon> icons;
    private MainIcon mainIcon;
    private Intent intent;
    private ArrayList<MainIcon> allIcons = new ArrayList<>();
    public String m2LargeUrl;

    public MainViewModel(Context mContext, OnIntentListener listener) {
        this.mContext = mContext;
        this.mIntentListener = listener;
        mRepository = MainIconRepository.getInstance();
    }

    public void init(ViewPager viewPager, LinearLayout vpindicator, ImageView adPic, NavViewModel navViewModel) {
        this.viewPager = viewPager;
        this.vpindicator = vpindicator;
        this.adPic = adPic;
        this.navViewModel = navViewModel;
        inflater = LayoutInflater.from(mContext);
    }

    public MainPic parseMainInfo() {
        mainPic = Parser.parseJsonFilesDirFile(MainPic.class, Constant.TXT_MAIN_PIC_INFO);
        return mainPic;
    }

    public void setTopPics() {
        topPics = new ArrayList<>();
        topBannerSize = mainPic.TopBanners.size();
        screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        /* top banner 图片默认尺寸设为：647*281 */
        topHeight = (screenWidth * MAIN_TOP_BANNER_HEIGHT) / MAIN_TOP_BANNER_WIDTH;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, topHeight);
        params.topMargin = 0;
        viewPager.setLayoutParams(params);

        if (topBannerSize > 1) {
             /* 设置单个小圆点尺寸 */
            int width = DisplayUtil.dip2px(mContext, 8);
            ind_params = new LinearLayout.LayoutParams(width, width);
            ind_params.setMargins(width, 0, 0, width);/* 左 上 右 下 */
             /* 设置LLPoint高度 */
            RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(screenWidth, topHeight);
            vpindicator.setLayoutParams(llParams);
        }

        generateTopPics();
        setLlPoints();

        viewPagerAdapter = new AdViewPagerAdapter(topPics);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private void setLlPoints() {
        for (int i = 0; i < topBannerSize; ++i) {
            /* 小圆点 */
            if (topBannerSize > 1) {
                ivPoint = new ImageView(mContext);
                if (i == 0) {
                    ivPoint.setBackgroundResource(R.drawable.dot_focused);
                } else {
                    ivPoint.setBackgroundResource(R.drawable.dot_normal);
                }
                ivPoint.setLayoutParams(ind_params);
                vpindicator.addView(ivPoint);
            }
        }
    }

    private void generateTopPics() {
        for (int i = 0; i < topBannerSize; ++i) {
            /* banner图片 */
            banners = mainPic.TopBanners.get(i);
            ImageViewBinding binding = ImageViewBinding.inflate(inflater, null);
            RelativeLayout rlTopBanner = binding.rlBanner;
            binding.setModel(this);
            binding.setPos(i);
            ivTop = binding.imageView;

            LogUtil.i(TAG, "banners.TC.BannerImage=" + banners.TC.BannerImage);

            Glide.with(mContext).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? banners.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? banners.EN.BannerImage : banners.SC.BannerImage)).into(ivTop);

            if (i == 0) {// 第一张图加倒计时
                TextView tvCDD = binding.tvCdd;
                long diff = AppUtil.getShowCountDown();
                tvCDD.setText(String.valueOf(diff));
                isCountDownShow.set(diff != 0);
            }
            topPics.add(rlTopBanner);
        }
    }

    public void refreshImages() {
        topPics.clear();
        generateTopPics();
        viewPagerAdapter.setList(topPics, true);
        showM2();
    }

    public void getMainIcons(ArrayList<MainIcon> largeIcons, ArrayList<MainIcon> littleIcons) {
        icons = mRepository.getMenus();
        LogUtil.i(TAG, "icons=" + icons.size() + "," + icons.toString());
        int size = icons.size();
        for (int i = 0; i < size; i++) {
            mainIcon = icons.get(i);
            if (mainIcon.getMenuList().split("_").length > 2) {
                littleIcons.add(mainIcon);
            } else {
                largeIcons.add(mainIcon);
            }
        }
    }

    public void setM2AD() {
        adHelper = new ADHelper(mContext);
        adObj = adHelper.getAdObj();
           /* M2广告图片尺寸：640*100 */
        adHeight = (screenWidth * 100) / 640;
        LogUtil.i(TAG, "adHeight=" + adHeight);
        LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(screenWidth, adHeight);
        adPic.setLayoutParams(adParams);
        if (adHelper.isAdOpen()) {
            showM2();
        }
    }

    private void showM2() {
        if (Integer.valueOf(adObj.M2.version) <= 0) {
            return;
        }
        StringBuilder m2Url = new StringBuilder();
        m2Url.append(adObj.Common.baseUrl).append(adObj.M2.filepath).append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(AppUtil.getLanguageType(navViewModel.mCurrLang.get())).append("_");
        adPic.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(Uri.parse(m2Url.toString().concat(adObj.M2.version).concat(adObj.M2.format))).into(adPic);
        m2LargeUrl = m2Url.toString().concat(adObj.M2.image2).concat(adObj.M2.version).concat(adObj.M2.format);

        AppUtil.trackViewLog(mContext, 202, "Ad", "M2", adObj.M2.getCompanyID(App.mLanguage.get()));
        AppUtil.setStatEvent(mContext, "ViewM2", "Ad_M2_" + adObj.M2.getCompanyID(App.mLanguage.get()));
    }

    public void onM2Click() {
        ExhibitorRepository exhibitorRepository = ExhibitorRepository.getInstance();
        boolean isExhibitorIDExists = exhibitorRepository.isExhibitorIDExists(adObj.M2.getCompanyID(navViewModel.mCurrLang.get()));
        if (isExhibitorIDExists) {
            mIntentListener.onIntent(adObj, ExhibitorDetailActivity.class);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
        }
    }

    public void onTopPicClick(int index) {
        LogUtil.i(TAG, "onTopPicClick: pos=" + index + ", companyID= " + mainPic.TopBanners.get(index).SC.companyID);
        MainPic.Property property = navViewModel.mCurrLang.get() == 0 ? mainPic.TopBanners.get(index).TC
                : navViewModel.mCurrLang.get() == 1 ? mainPic.TopBanners.get(index).EN : mainPic.TopBanners.get(index).SC;
        bannerIntent(property);
    }

    public void onLeftClick(View view) {
        MainPic.Property property = navViewModel.mCurrLang.get() == 0 ? mainPic.LeftBottomBanner.TC
                : navViewModel.mCurrLang.get() == 1 ? mainPic.LeftBottomBanner.EN : mainPic.LeftBottomBanner.SC;
        bannerIntent(property);
    }

    public void onRightClick() {
        MainPic.Property property = navViewModel.mCurrLang.get() == 0 ? mainPic.RightBottomBanner.TC
                : navViewModel.mCurrLang.get() == 1 ? mainPic.RightBottomBanner.EN : mainPic.RightBottomBanner.SC;
        bannerIntent(property);
    }

    private void bannerIntent(MainPic.Property property) {
        int function = Integer.valueOf(property.Function);
        LogUtil.i(TAG, "function=" + function);
        if (function < 5) {
            adHelper.intentAd(function, property.companyID,
                    property.eventID, property.seminarID, property.newsID);
        } else if (function == 5) {  // function = 5, 根據 baiduTJ 值跳轉
            if (allIcons.isEmpty()) {
                allIcons = mRepository.getAllIcons();
            }
            int size = allIcons.size();
            for (int i = 0; i < size; i++) {
                mainIcon = allIcons.get(i);
                if (mainIcon.getBaiDu_TJ().trim().equals(property.InnerPage.trim())) {
                    LogUtil.i(TAG, "InnerPage=" + property.InnerPage);
                    break;
                }
            }
            intent = navViewModel.newIntent((Activity) mContext, mainIcon);
        } else if (function == 6) {   // function = 6， link
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(property.Link));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (intent == null) {
            return;
        }
        mContext.startActivity(intent);
        if (AppUtil.isTablet()) {
            ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int len = vpindicator.getChildCount();
            for (int i = 0; i < len; ++i)
                vpindicator.getChildAt(i).setBackgroundResource(R.drawable.dot_normal);
            vpindicator.getChildAt(position).setBackgroundResource(R.drawable.dot_focused);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
