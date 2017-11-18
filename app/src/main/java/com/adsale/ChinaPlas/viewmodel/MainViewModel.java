package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.FragmentMainBinding;
import com.adsale.ChinaPlas.databinding.ImageViewBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by Ponyo on 2017/8/5.
 * Main
 */

public class MainViewModel {
    private Context mContext;
    private MainIconRepository mRepository;
    private MainPic mainPic;
    private int language;

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

    public MainViewModel(Context mContext, OnIntentListener listener) {
        this.mContext = mContext;
        this.mIntentListener = listener;
        mRepository = MainIconRepository.getInstance();
        language = App.mLanguage.get();
    }

    public void init(ViewPager viewPager, LinearLayout vpindicator, ImageView adPic) {
        this.viewPager = viewPager;
        this.vpindicator = vpindicator;
        this.adPic = adPic;
    }

    public MainPic parseMainInfo() {
        mainPic = Parser.parseJsonFilesDirFile(MainPic.class, Constant.TXT_MAIN_PIC_INFO);
        return mainPic;
    }

    public void setMainInfo(MainPic mainInfo) {
        mainPic = mainInfo;
    }

    public void setTopPics() {
        List<View> topPics = new ArrayList<>();
        MainPic.Banners banners;
        int size = mainPic.TopBanners.size();
        screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        /* top banner 图片默认尺寸设为：647*281 */
        topHeight = (screenWidth * TOP_PIC_HEIGHT) / TOP_PIC_WIDTH;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, topHeight);
        params.topMargin = 0;
        viewPager.setLayoutParams(params);

        ImageView ivTop, ivPoint;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (size > 1) {
             /* 设置单个小圆点尺寸 */
            int width = DisplayUtil.dip2px(mContext, 8);
            ind_params = new LinearLayout.LayoutParams(width, width);
            ind_params.setMargins(width, 0, 0, width);/* 左 上 右 下 */
             /* 设置LLPoint高度 */
            RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(screenWidth, topHeight);
            vpindicator.setLayoutParams(llParams);
        }

        for (int i = 0; i < size; ++i) {
            /* banner图片 */
            banners = mainPic.TopBanners.get(i);
            ImageViewBinding binding = ImageViewBinding.inflate(inflater, null);
            binding.setModel(this);
            binding.setPos(i);
            ivTop = binding.imageView;
            Glide.with(mContext).load(Uri.parse(language == 0 ? banners.TC.BannerImage : language == 1 ? banners.EN.BannerImage : banners.SC.BannerImage)).into(ivTop);
            topPics.add(ivTop);

            /* 小圆点 */
            if (size > 1) {
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

        AdViewPagerAdapter viewPagerAdapter = new AdViewPagerAdapter(topPics);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(mPageChangeListener);
    }

    public void getMainIcons(ArrayList<MainIcon> largeIcons, ArrayList<MainIcon> littleIcons) {
        ArrayList<MainIcon> icons = mRepository.getMenus();
        LogUtil.i(TAG, "icons=" + icons.size() + "," + icons.toString());
        int size = icons.size();
        MainIcon mainIcon;
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
        ADHelper adHelper = new ADHelper(mContext);
        adObj = adHelper.getAdObj();
        if(adHelper.isAdOpen()){
             /* M2广告图片尺寸：640*100 */
            adHeight = (screenWidth * 100) / 640;
            LogUtil.i(TAG, "adHeight=" + adHeight);
            LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(screenWidth, adHeight);
            adPic.setLayoutParams(adParams);
            adHelper.showM2(adPic);
        }

    }

    public void onM2Click() {
        Toast.makeText(mContext, adObj.M2.getCompanyID(language), Toast.LENGTH_SHORT).show();
//        mIntentListener.onIntent(adObj,null);
    }

    public void onTopPicClick(int index) {
        LogUtil.i(TAG, "onTopPicClick: pos=" + index + ", companyID= " + mainPic.TopBanners.get(index).SC.companyID_sc);
//        mIntentListener.onIntent(mainPic.TopBanners.get(index),null);
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
