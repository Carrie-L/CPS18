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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;
import static com.adsale.ChinaPlas.utils.Constant.MAIN_TOP_BANNER_HEIGHT;
import static com.adsale.ChinaPlas.utils.Constant.MAIN_TOP_BANNER_HEIGHT_PAD;
import static com.adsale.ChinaPlas.utils.Constant.MAIN_TOP_BANNER_WIDTH;
import static com.adsale.ChinaPlas.utils.Constant.PAD_CONTENT_WIDTH;


/**
 * Created by Ponyo on 2017/8/5.
 * Main
 */

public class MainViewModel {
    private final String TAG = "MainViewModel";
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
    private ImageView ivTop;
    private ImageView ivPoint;
    private RelativeLayout rlTopBanner;
    private ADHelper adHelper;
    private ArrayList<MainIcon> icons;
    private MainIcon mainIcon;
    private Intent intent;
    private ArrayList<MainIcon> allIcons = new ArrayList<>();
    public String m2LargeUrl;
    private boolean isTablet;
    private final Integer SECOND = 5;
    private Disposable mDisposable;

    public MainViewModel(Context mContext, OnIntentListener listener) {
        this.mContext = mContext;
        this.mIntentListener = listener;
        mRepository = MainIconRepository.getInstance();
        isTablet = AppUtil.isTablet();
    }

    public void init(ViewPager viewPager, LinearLayout vpindicator, ImageView adPic, NavViewModel navViewModel) {
        this.viewPager = viewPager;
        this.vpindicator = vpindicator;
        this.adPic = adPic;
        this.navViewModel = navViewModel;
        inflater = LayoutInflater.from(mContext);
    }

    public void initPad(ViewPager viewPager, RelativeLayout rlTopBanner, LinearLayout vpindicator, ImageView adPic, NavViewModel navViewModel) {
        this.viewPager = viewPager;
        this.rlTopBanner = rlTopBanner;
        this.vpindicator = vpindicator;
        this.adPic = adPic;
        this.navViewModel = navViewModel;
        inflater = LayoutInflater.from(mContext);
    }

    public MainPic parseMainInfo() {
        mainPic = Parser.parseJsonFilesDirFile(MainPic.class, Constant.TXT_MAIN_PIC_INFO);
        App.mSP_Config.edit().putString("MainIconBaseUrl", mainPic.IconPath).apply();
        return mainPic;
    }

    public void setTopPics() {
        topPics = new ArrayList<>();
        topBannerSize = mainPic.TopBanners.size();
        screenWidth = AppUtil.getScreenWidth();
        /* top banner 图片默认尺寸设为：647*281 */
        topHeight = isTablet ? (screenWidth * MAIN_TOP_BANNER_HEIGHT_PAD) / PAD_CONTENT_WIDTH :
                (screenWidth * MAIN_TOP_BANNER_HEIGHT) / MAIN_TOP_BANNER_WIDTH;
        LogUtil.i(TAG, "screenWidth=" + screenWidth + ",topHeight = " + (screenWidth * MAIN_TOP_BANNER_HEIGHT_PAD) / PAD_CONTENT_WIDTH);
        if (isTablet) {
            topHeight = (screenWidth * MAIN_TOP_BANNER_HEIGHT_PAD) / PAD_CONTENT_WIDTH;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, topHeight);
            rlTopBanner.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, topHeight);
            params.topMargin = 0;
            viewPager.setLayoutParams(params);
        }
        App.mSP_Config.edit().putInt("topHeight", topHeight).apply();

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

        if (viewPagerAdapter.getCount() > 1) {
            rollAuto();
        }
    }

    private void setLlPoints() {
        for (int i = 0; i < topBannerSize; ++i) {
            /* 小圆点 */
            if (topBannerSize > 1) {
                ivPoint = new ImageView(mContext);
                ivPoint.setBackgroundResource(i == 0 ? R.drawable.dot_focused : R.drawable.dot_normal);
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
            LogUtil.i(TAG, "banners.TC.BannerImage_pad=" + banners.TC.BannerImage_Pad);
            if (isTablet) {
                LogUtil.i(TAG, "banners.TC.BannerImage=" + banners.TC.BannerImage_Pad);
                if (App.isNetworkAvailable) {
                    Glide.with(mContext).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? banners.TC.BannerImage_Pad : navViewModel.mCurrLang.get() == 1 ? banners.EN.BannerImage_Pad : banners.SC.BannerImage_Pad)).into(ivTop);
                } else {
                    Glide.with(mContext).load(
                            "file:///android_asset/Menu/tablet_top_" + i + "_.jpg"
//                             + AppUtil.subStringLast(App.mLanguage.get() == 0 ? banners.TC.BannerImage_Pad : App.mLanguage.get() == 1 ? banners.EN.BannerImage_Pad : banners.SC.BannerImage_Pad, "/")
                            ).into(ivTop);
                    LogUtil.i(TAG, "TOP BANNER ASSET = " + String.format(Locale.getDefault(), "file:///android_asset/mENU/" + AppUtil.subStringLast(App.mLanguage.get() == 0 ? banners.TC.BannerImage_Pad : App.mLanguage.get() == 1 ? banners.EN.BannerImage_Pad : banners.SC.BannerImage_Pad, "/"), getLangType(), i));
                }
            } else {
                LogUtil.i(TAG, "banners.TC.BannerImage=" + banners.TC.BannerImage);
                if (App.isNetworkAvailable) {
                    Glide.with(mContext).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? banners.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? banners.EN.BannerImage : banners.SC.BannerImage)).into(ivTop);
                } else {
                    Glide.with(mContext).load(
                            "file:///android_asset/Menu/phone_top_%d_.jpg"
//                            "file:///android_asset/Menu/" + AppUtil.subStringLast(App.mLanguage.get() == 0 ? banners.TC.BannerImage : App.mLanguage.get() == 1 ? banners.EN.BannerImage : banners.SC.BannerImage, "/")
                           ).into(ivTop);
                    LogUtil.i(TAG, "TOP BANNER ASSET = " + String.format(Locale.getDefault(), "file:///android_asset/Menu/" + AppUtil.subStringLast(App.mLanguage.get() == 0 ? banners.TC.BannerImage : App.mLanguage.get() == 1 ? banners.EN.BannerImage : banners.SC.BannerImage, "/"), getLangType(), i));
                }
            }
            if (i == 0) {// 第一张图加倒计时
                long diff = AppUtil.getShowCountDown();
                if (diff > 0) {
                    TextView tvCDD = binding.tvCdd;
                    tvCDD.setText(String.valueOf(diff));
                    binding.rvCountdown.setVisibility(View.VISIBLE);
                }
            }
            topPics.add(rlTopBanner);
        }
    }

    /**
     * 每5s 自动切换到下一张
     */
    private void rollAuto() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(SECOND) // up to SECOND items
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long v) throws Exception {
                        return SECOND - v;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mDisposable != null && mDisposable.isDisposed()) {
                            mDisposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        destroy();
//                        LogUtil.i(TAG, "onComplete：-------------> 倒计时结束,切换下一张");
                        int currPagerItem = viewPager.getCurrentItem();
                        if (currPagerItem == (viewPagerAdapter.getCount() - 1)) {
                            currPagerItem = 0;
                        } else {
                            currPagerItem++;
                        }
                        viewPager.setCurrentItem(currPagerItem);
                        rollAuto(); // 轮播
                    }
                });
    }

    private String getLangType() {
        return App.mLanguage.get() == 0 ? "tc" : App.mLanguage.get() == 1 ? "en" : "sc";
    }

    public void refreshImages() {
        topPics.clear();
        generateTopPics();
        viewPagerAdapter.setList(topPics, true);
        showM2();
    }

    public void getMainIcons(ArrayList<MainIcon> largeIcons, ArrayList<MainIcon> littleIcons) {
        icons = mRepository.getMenus();
//        LogUtil.i(TAG, "icons=" + icons.size() + "," + icons.toString());
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
        if (!isTablet) {
            adHeight = (screenWidth * 100) / 640;
        } else {
            adHeight = (screenWidth * 140) / 1024;//1840
        }
        LogUtil.i(TAG, "adHeight=" + adHeight);
        LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(screenWidth, adHeight);
        adPic.setLayoutParams(adParams);
        showM2();
    }

    private void showM2() {
        if (!adHelper.isAdOpen() || Integer.valueOf(adObj.M2.version) <= 0
                || (App.mLanguage.get() == 0 && adObj.M2.action_companyID_tc.equals("0"))
                || (App.mLanguage.get() == 1 && adObj.M2.action_companyID_en.equals("0"))
                || (App.mLanguage.get() == 2 && adObj.M2.action_companyID_sc.equals("0"))) {
            adPic.setVisibility(View.INVISIBLE);
            return;
        }
        StringBuilder m2Url = new StringBuilder();
        m2Url.append(adObj.Common.baseUrl).append(adObj.M2.filepath).append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(AppUtil.getLanguageType(navViewModel.mCurrLang.get())).append("_");
        adPic.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(Uri.parse(m2Url.toString().concat(adObj.M2.version).concat(adObj.M2.format))).into(adPic);
        m2LargeUrl = m2Url.toString().concat(adObj.M2.image2).concat(adObj.M2.version).concat(adObj.M2.format);
        LogUtil.i(TAG, "m2LargeUrl=" + m2LargeUrl);
        LogUtil.i(TAG, "m2Url=" + m2Url.toString());

        AppUtil.trackViewLog(202, "Ad", "M2", adObj.M2.getCompanyID(AppUtil.getCurLanguage()));
        AppUtil.setStatEvent(mContext, "ViewM2", "Ad_M2_" + adObj.M2.getCompanyID(AppUtil.getCurLanguage()));
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
            return;
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

    public void destroy() {
        if (mDisposable != null && mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

//    public void pasue() {
//        LogUtil.i(TAG, "-------pasue()------");
//        if (mDisposable != null && mDisposable.isDisposed()) {
//            mDisposable.dispose();
//        }
//    }
//
//    public void resume() {
//        LogUtil.i(TAG, "-------resume()------");
//        rollAuto();
//    }


}
