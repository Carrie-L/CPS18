package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.dao.CompanyApplication;
import com.adsale.ChinaPlas.dao.CompanyProduct;
import com.adsale.ChinaPlas.dao.ConcurrentEvent;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.EventApplication;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorZone;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.Map;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.ImageViewBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.CSVHelper;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.helper.NewTecHelper;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.FileUtils;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.adsale.ChinaPlas.utils.ReleaseHelper;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.adsale.ChinaPlas.App.mLogHelper;
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
    //    private adAdvertisementObj adObj;
    private EPO.D2 D2;
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
    //    private ADHelper adHelper;
    private EPOHelper mEPOHelper;
    private ArrayList<MainIcon> icons;
    private MainIcon mainIcon;
    private Intent intent;
    private ArrayList<MainIcon> allIcons = new ArrayList<>();
    public String m2LargeUrl;
    private boolean isTablet;
    private final Integer SECOND = 5;
    private Disposable mDisposable;
    private final OtherRepository mOtherRepository;
    private ExhibitorRepository mExhibitorRepository;
    private FloorRepository mFloorRepository;
    private String mFloorDir, mEventDir;
    private CSVHelper csvHelper;
    private String newsLocalUpdateAt;

    public MainViewModel(Context mContext, OnIntentListener listener) {
        this.mContext = mContext;
        this.mIntentListener = listener;
        mRepository = MainIconRepository.getInstance();
        mOtherRepository = OtherRepository.getInstance();
        isTablet = AppUtil.isTablet();
    }

    public void init(ViewPager viewPager, LinearLayout vpindicator, ImageView adPic, NavViewModel navViewModel) {
        this.viewPager = viewPager;
        this.vpindicator = vpindicator;
        this.adPic = adPic;
        this.navViewModel = navViewModel;
        inflater = LayoutInflater.from(mContext);

        getData();
    }

    private void getData() {
        getWebContentData();
        getNews();
        getFloorPlanData();
        getCountryData();
        getEventData();
        getEventApplicationData();
        // 测试ExhibitorDataCsv
        testExhibitorDataCsv();
        downNewTecZip();
    }

    private void downNewTecZip() {
        initDownloadClient();
        LogUtil.i(TAG, "downNewTecZip");
        NewTecHelper newTecHelper = new NewTecHelper();
        newTecHelper.init();
        newTecHelper.downNewTecZip(mDClient);
    }

    public void initPad(ViewPager viewPager, RelativeLayout rlTopBanner, LinearLayout vpindicator, ImageView adPic, NavViewModel navViewModel) {
        this.viewPager = viewPager;
        this.rlTopBanner = rlTopBanner;
        this.vpindicator = vpindicator;
        this.adPic = adPic;
        this.navViewModel = navViewModel;
        inflater = LayoutInflater.from(mContext);

        getData();
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
                LogUtil.i(TAG, "banners.EN.BannerImage=" + banners.EN.BannerImage);
                if (App.isNetworkAvailable) {
                    Glide.with(mContext).load(Uri.parse(navViewModel.mCurrLang.get() == 0 ? banners.TC.BannerImage : navViewModel.mCurrLang.get() == 1 ? banners.EN.BannerImage : banners.SC.BannerImage)).into(ivTop);
                } else {
                    Glide.with(mContext).load(
                            "file:///android_asset/Menu/" + AppUtil.subStringLast(App.mLanguage.get() == 0 ? banners.TC.BannerImage : App.mLanguage.get() == 1 ? banners.EN.BannerImage : banners.SC.BannerImage, "/")
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
            if (mainIcon.getMenuSeq().split("_").length > 2) {
                littleIcons.add(mainIcon);
            } else {
                largeIcons.add(mainIcon);
            }
        }
        LogUtil.i(TAG, "littleIcons=" + littleIcons.size() + ",largeIcons=" + largeIcons.size());
    }

    public void setM2AD() {
        mEPOHelper = EPOHelper.getInstance();
        D2 = mEPOHelper.getD2();
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
        if (!mEPOHelper.isD2Open()) {
            adPic.setVisibility(View.INVISIBLE);
            return;
        }
        adPic.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(Uri.parse(mEPOHelper.getD2Banner())).into(adPic);
        m2LargeUrl = mEPOHelper.getD2BannerBig();
        LogUtil.i(TAG, "m2LargeUrl=" + m2LargeUrl);

        mLogHelper.logD2(D2.getPageID(), true);
        mLogHelper.setBaiDuLog(mContext, LogHelper.EVENT_ID_AD_VIEW);
    }

    public void onM2Click() {
        if (D2.getFunction() == 1) {
            ExhibitorRepository exhibitorRepository = ExhibitorRepository.getInstance();
            boolean isExhibitorIDExists = exhibitorRepository.isExhibitorIDExists(D2.getPageID());
            if (isExhibitorIDExists) {
                mIntentListener.onIntent(D2.getPageID(), ExhibitorDetailActivity.class);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
            }
            mLogHelper.logD2(D2.getPageID(), false);
            mLogHelper.setBaiDuLog(mContext, LogHelper.EVENT_ID_AD_Click);
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
            intent = mEPOHelper.intentAd(function, property.companyID, property.eventID, property.seminarID, property.newsID);
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

    public void updateMainIcon(ArrayList<MainIcon> list) {
        mRepository.updateOrInsertMainIcons(list);
    }

    private void getWebContentData() {
        FileUtil.createDir(App.rootDir + "ExhibitorData/");
        BmobQuery<WebContent> query = new BmobQuery<>();
        query.order("-updatedAt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String localUpdateAt = mOtherRepository.geWebContentLUT();
        LogUtil.i(TAG, "getWebContentData: localUpdateAt=" + localUpdateAt);
        if (!TextUtils.isEmpty(localUpdateAt)) {
            Date date = null;
            try {
                date = sdf.parse(localUpdateAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereGreaterThan("updatedAt", new BmobDate(date));
        }
        query.findObjects(new FindListener<WebContent>() {
            @Override
            public void done(List<WebContent> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "get WebContent::: list = " + list.size() + "," + list.toString());
                    if (list.size() == 1 && list.get(0).getUpdatedAt().equals(localUpdateAt)) {
                        return;
                    }
                    LogUtil.i(TAG, "update WebContent");
                    downloadWebContent(list);
                }

                if (e != null)

                {
                    LogUtil.i(TAG, "get WebContent e=" + e.getMessage());
                }

            }
        });
    }

    private void testExhibitorDataCsv() {
        if (ReleaseHelper.IsExhibitorDataCsvTest) {
            ArrayList<WebContent> list = new ArrayList<>();
            WebContent webContent = new WebContent("E001TEST", "ExhibitorData_201902221612.zip", "2018-11-14 14:14:21", "2019-02-22 11:33:23");
            list.add(webContent);
            LogUtil.e(TAG, "testExhibitorDataCsv");
            downloadWebContent(list);
        }
    }

    private DownloadClient mDClient;

    private void downloadWebContent(final List<WebContent> list) {
        FileUtil.createDir(App.rootDir + "WebContent/");
        LogUtil.i(TAG, "downloadWebContent: " + list.size());

        if (mDClient == null) {
            mDClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.DOWNLOAD_BASE_URL);
        }

        Observable.fromIterable(list)
                .flatMap(new Function<WebContent, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(final WebContent webContent) throws Exception {
                        LogUtil.i(TAG, "Observable<Boolean>: " + webContent.getFileName());

                        if (webContent.getFileName().equals("-")) {
                            mOtherRepository.updateOrInsertWebContent(webContent);
                            return Observable.just(false);
                        }
                        return mDClient.downWebContent(webContent.getFileName())
                                .map(new Function<retrofit2.Response<ResponseBody>, Boolean>() {
                                    @Override
                                    public Boolean apply(retrofit2.Response<ResponseBody> response) throws Exception {
                                        ResponseBody responseBody = response.body();
                                        if (responseBody != null) {
                                            boolean isUnpackSuccess = false;
                                            // update center
                                            if (webContent.getContentID().equals("E001")) {
//                                                ZSZS
//                                                isUnpackSuccess = FileUtil.unpackZip(webContent.getFileName(), responseBody.byteStream(), App.rootDir + "ExhibitorData/");
//                                                updateExhibitorData();
                                            } else if (webContent.getContentID().equals("NewTec")) {
                                                updateNewTec();
                                            } else if (webContent.getContentID().equals("Seminar")) {
                                                updateSeminar();
                                            } else if (webContent.getContentID().equals("E001TEST") && ReleaseHelper.IsExhibitorDataCsvTest) {
                                                isUnpackSuccess = FileUtil.unpackZip(webContent.getFileName(), responseBody.byteStream(), App.rootDir + "ExhibitorData/");
                                                updateExhibitorData();
                                            } else {
                                                isUnpackSuccess = FileUtil.unpackZip(webContent.getFileName(), responseBody.byteStream(), App.rootDir + "WebContent/");
                                                LogUtil.i(TAG, "解压WebContent: " + webContent.getFileName());
                                                responseBody.close();
                                            }
                                            return isUnpackSuccess;
                                        }
                                        return false;
                                    }
                                })
                                .subscribeOn(Schedulers.io());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "downloadWebContent accept: " + value);
                        if (value) {
                            mOtherRepository.updateOrInsertWebContents(list);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "downloadWebContent onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateExhibitorData() {
        AppUtil.setUpdateExhibitorSuccess(false);
        AppUtil.setExhibitorHasUpdate(true);
        initCsvHelper();
        csvHelper.initExhibitorCsvHelper();
        csvHelper.processExhibitorCsv();
    }

    private void initCsvHelper() {
        if (csvHelper == null) {
            csvHelper = new CSVHelper();
        }
    }

    private void updateNewTec() {

    }

    private void updateSeminar() {

    }

    private void getNews() {
        getQueryData(mOtherRepository.getNewsLUT(), "News", new FindListener<News>() {

            @Override
            public void done(List<News> list, BmobException e) {
                logBmobException("News", e);
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "get News::: list = " + list.size());
                    LogUtil.i(TAG, "update News");
                    mOtherRepository.updateOrInsertNews(list);
                }
            }
        });
    }

    private void getNewsData() {
        BmobQuery<News> query = new BmobQuery<>();
        query.clearCachedResult(News.class);
        query.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
        query.order("-updatedAt");
        newsLocalUpdateAt = mOtherRepository.getNewsLUT();
        LogUtil.i(TAG, "getNewsData: localUpdateAt=" + newsLocalUpdateAt);
        if (TextUtils.isEmpty(newsLocalUpdateAt)) {
            newsLocalUpdateAt = "2018-01-01 10:00:00";
        }
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(newsLocalUpdateAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }


//        String sql = "select * from News where updatedAt>" + new BmobDate(date)+" order by PublishDate desc"; // + new BmobDate(date)
//        new BmobQuery<News>().doSQLQuery(sql, new SQLQueryListener<News>() {
//            @Override
//            public void done(BmobQueryResult<News> result, BmobException e) {
//                logBmobException("News", e);
//                List<News> list = result.getResults();
//                if (list != null && list.size() > 0) {
//                    LogUtil.i(TAG, "update News： sql=" + list.size());
////                    if (list.size() == 1 && list.get(0).getUpdateDateTime().equals(newsLocalUpdateAt)) {
////                        return;
////                    }
////                    LogUtil.i(TAG, "update News ");
//                    mOtherRepository.updateOrInsertNews(list);
//                } else {
//                    LogUtil.i(TAG, "update News： sql get News  0");
//                }
//
//            }
//        });


        query.addWhereGreaterThan("updatedAt", new BmobDate(date));
        query.setLimit(500).findObjects(new FindListener<News>() {
            @Override
            public void done(List<News> list, BmobException e) {
                if (e != null) {
                    LogUtil.i(TAG, "e = " + e.getMessage());
                }
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "update News： " + list.size() + "," + list.toString());
                    if (list.size() == 1 && list.get(0).getUpdateDateTime().equals(newsLocalUpdateAt)) {
                        return;
                    }
                    LogUtil.i(TAG, "update News ");
                    mOtherRepository.updateOrInsertNews(list);
                } else {
                    LogUtil.i(TAG, "update News get News  0");
                }
            }
        });
    }

    /**
     * {@link com.adsale.ChinaPlas.ui.view.ExhiDtlInfoView} 里面的layout booth
     */
    private void getFloorPlanData() {
        mFloorRepository = FloorRepository.getInstance();
        mFloorDir = App.rootDir + "FloorPlan/";
        FileUtil.createDir(mFloorDir);
        final String mapLUT = mFloorRepository.getMapLUT();

        getQueryData(mapLUT, "Map", new FindListener<Map>() {

            @Override
            public void done(List<Map> list, BmobException e) {
                logBmobException("Map", e);
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "get Map::: list = " + list.size() + ", " + list.toString());
                    if (list.size() == 1 && list.get(0).getUpdatedAt().equals(mapLUT)) {
                        return;
                    }
                    LogUtil.i(TAG, "to down Map");
                    downloadMap(list, mapLUT);
                }
            }
        });
    }

    private void initDownloadClient() {
        if (mDClient == null) {
            mDClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.DOWNLOAD_BASE_URL);
        }
    }

    private void downloadMap(final List<Map> list, final String mapLUT) {
        initDownloadClient();
        Observable.fromIterable(list)
                .flatMap(new Function<Map, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(final Map map) throws Exception {
                        if (map.getFileName().equals("-") || map.getUpdatedAt().equals(mapLUT)) {
                            LogUtil.i(TAG, "not download map -- ");
                            mFloorRepository.updateOrInsertMap(map);
                            return Observable.just(false);  // 不下载，但还是更新/插入数据
                        }
                        return mDClient.downloadMap(map.getFileName())
                                .map(new Function<ResponseBody, Boolean>() {
                                    @Override
                                    public Boolean apply(ResponseBody responseBody) throws Exception {
                                        if (responseBody != null) {
                                            boolean isMapSaved = false;
                                            if (map.getFloorID().equals("csv")) {
                                                LogUtil.i(TAG, " download map csv ");
                                                initCsvHelper();
                                                isMapSaved = csvHelper.readFloorPlanCSV(responseBody.byteStream());
                                            } else {
                                                isMapSaved = FileUtil.writeFile(responseBody.byteStream(), mFloorDir + map.getFileName());
                                            }
                                            if (isMapSaved) {
                                                mFloorRepository.updateOrInsertMap(map);
                                            }
                                            responseBody.close();
                                            return isMapSaved;
                                        }
                                        return false;
                                    }
                                }).subscribeOn(Schedulers.io());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "download map result = " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "download map onError = " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getCountryData() {
        getQueryData(mExhibitorRepository.getCountryLUT(), "Country", new FindListener<Country>() {
            @Override
            public void done(List<Country> list, BmobException e) {
                logBmobException("Country", e);
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "get Country::: list = " + list.size());
                    LogUtil.i(TAG, "update Country");
                    mExhibitorRepository.updateOrInsertCountry(list);
                }
            }
        });
    }

    /**
     * 获取同期活动列表数据
     */
    private void getEventData() {
        final String localLUT = mOtherRepository.getEventLUT();
        BmobQuery<ConcurrentEvent> query = new BmobQuery<>();
        query.order("-EventID");
        query.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
//        query.groupby(new String[]{"EventID"});
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        checkExhibitorRepository();
        if (!TextUtils.isEmpty(localLUT)) {
            Date date = null;
            try {
                date = sdf.parse(localLUT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereGreaterThan("updatedAt", new BmobDate(date)).addWhereNotEqualTo("updatedAt", new BmobDate(date));
        }
        query.setLimit(500).findObjects(new FindListener<ConcurrentEvent>() {
            @Override
            public void done(List<ConcurrentEvent> list, BmobException e) {
                logBmobException("ConcurrentEvent", e);
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "get ConcurrentEvent::: list = " + list.size() + ", " + list.toString());
                    if (list.size() == 1 && list.get(0).getUpdatedAt().equals(localLUT)) {
                        LogUtil.i(TAG, "getEventData lut 相同，无更新");
                        return;
                    }
                    LogUtil.i(TAG, "update ConcurrentEvent");
                    mOtherRepository.updateOrInsertEvents(list);
                    startupEventPages(list);
                }
            }
        });


//        getQueryData("", "ConcurrentEvent", new FindListener<ConcurrentEvent>() {
//            @Override
//            public void done(List<ConcurrentEvent> list, BmobException e) {
//                logBmobException("ConcurrentEvent", e);
//                if (list != null && list.size() > 0) {
//                    LogUtil.i(TAG, "get ConcurrentEvent::: list = " + list.size());
//                    if (list.size() == 1 && list.get(0).getUpdatedAt().equals(localLUT)) {
//                        LogUtil.i(TAG, "getEventData lut 相同，无更新");
//                        return;
//                    }
//                    LogUtil.i(TAG, "update ConcurrentEvent");
//                    mOtherRepository.updateOrInsertEvents(list);
//                    startupEventPages(list);
//                }
//            }
//        });
    }

    /**
     * 获取同期活动行业列表数据
     */
    private void getEventApplicationData() {
        final String localLUT = mOtherRepository.getEventAppLUT();
        getQueryData("", "EventApplication", new FindListener<EventApplication>() {
            @Override
            public void done(List<EventApplication> list, BmobException e) {
                logBmobException("EventApplication", e);
                if (list != null && list.size() > 0) {
                    LogUtil.i(TAG, "get EventApplication::: list = " + list.size());
                    if (list.size() == 1 && list.get(0).getUpdatedAt().equals(localLUT)) {
                        LogUtil.i(TAG, "EventApplication lut 相同，无更新");
                        return;
                    }
                    LogUtil.i(TAG, "update EventApplication");
                    mOtherRepository.updateOrInsertEventApplications(list);
                }
            }
        });
    }

    /**
     * 根据ConcurrentEvent表字段 PageFileName ，下载同期活动详情页
     */
    private void startupEventPages(List<ConcurrentEvent> list) {
        // 去掉重复zip包的
        List<ConcurrentEvent> events = new ArrayList<>();
        int size = list.size();
        ConcurrentEvent entity;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            if (i == 0) {
                events.add(entity);
            } else if (entity.getPageFileName() != null && !entity.getPageFileName().equals(list.get(i - 1).getPageFileName())) {
                events.add(entity);
            }
        }
//        LogUtil.i(TAG, "get EventApplication::: events = " + events.size() + ", " + events.toString());
        Observable.fromIterable(events)
                .flatMap(new Function<ConcurrentEvent, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> apply(ConcurrentEvent concurrentEvent) throws Exception {
                        // 1. fileName 为空或无效地址，则不下载
                        // 2. 考虑到一种情况， 即我修改了该行除 PageFileName 之外的字段的值，而内页包没有更新，这种情况也不需要重复下载浪费资源。
                        //    因此，我在本地储存fileName名称（因为fileName中包含了时间日期）,将bmob端返回的PageFileName与本地的比较，如果前者大于后者，则需要下载更新zip包，否则无需更新。
                        String fileName = concurrentEvent.getPageFileName();
                        if (TextUtils.isEmpty(fileName)) {
                            LogUtil.i(TAG, concurrentEvent.getPageID() + " の " + fileName + " 为null, 不下载");
                            return Observable.just(false);
                        } else if (fileName.equals("-")) {
                            LogUtil.i(TAG, concurrentEvent.getPageID() + " の " + fileName + " 不下载");
                            return Observable.just(false);
                        } else if (fileName.compareTo(getEventPageFileName(concurrentEvent.getEventID())) <= 0) {
                            LogUtil.i(TAG, fileName + " の " + getEventPageFileName(concurrentEvent.getEventID()) + " の 相等，不下载");
                            return Observable.just(false);
                        }
                        mOtherRepository.updateEventIsDown(concurrentEvent.getPageID(), true); // update 用主键
                        return downloadEventPage(concurrentEvent.getPageID(), concurrentEvent.getEventID(), fileName);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "down event page onNext: " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "down event error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "down event page onComplete ");
                    }
                });
    }

    private Observable<Boolean> downloadEventPage(final String pageID, final String eventID, final String fileName) {
        initDownloadClient();
        mEventDir = App.rootDir + "ConcurrentEvent/";
        if (!new File(mEventDir).exists()) {
            new File(mEventDir).mkdir();
            AppUtil.copyCSSToConcurrent();
        }
        return mDClient.downloadEventPage(fileName)
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        boolean isUnZiped = FileUtil.unpackZip(fileName, responseBody.byteStream(), mEventDir);
                        responseBody.close();
                        LogUtil.i(TAG, "解压同期活动：" + fileName + " > " + isUnZiped);
                        mOtherRepository.updateEventIsDown(pageID, !isUnZiped);
                        setEventPageFileName(eventID, fileName);   // zip包下载解压成功，更新本地PageFileName的值。
                        return isUnZiped;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void setEventPageFileName(String id, String fileName) {
        App.mSP_EventPage.edit().putString(id, fileName).apply();
    }

    public String getEventPageFileName(String id) {
        return App.mSP_EventPage.getString(id, "");
    }

    private void checkExhibitorRepository() {
        if (mExhibitorRepository == null) {
            mExhibitorRepository = ExhibitorRepository.getInstance();
            mExhibitorRepository.initCsvDao();
        }
    }

    private <T> void getQueryData(String localUpdateAt, String tag, FindListener<T> listener) {
        BmobQuery<T> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        checkExhibitorRepository();
        if (!TextUtils.isEmpty(localUpdateAt)) {
            Date date = null;
            try {
                date = sdf.parse(localUpdateAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereGreaterThan("updatedAt", new BmobDate(date)).addWhereNotEqualTo("updatedAt", new BmobDate(date));
        }
        query.setLimit(500).findObjects(listener);
    }


    private void logBmobException(String TAG, BmobException e) {
        if (e != null) {
            LogUtil.i(TAG, "e = " + e.getMessage());
        }
    }









}
