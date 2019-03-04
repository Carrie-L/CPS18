package com.adsale.ChinaPlas.ui.view;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.ui.PadMainActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Carrie on 2017/11/7.
 * 自定义Help Page Dialog
 * <p>
 * use:
 * <p>1. first enter show
 * <br>HelpView  helpView = new HelpView(this, HelpView.HELP_PAGE_MY_EXHIBITOR);
 * <br>helpView.showPage();
 * <p>
 * <p>2. click show
 * <br>helpView.show();
 */

public class HelpView extends DialogFragment implements View.OnClickListener {
    private Integer[] imageIds;
    private View mView;
    private ViewPager viewPagerHelp;
    private LinearLayout mLlPoint;
    private final static String TAG = "HelpView";

    public final static int HELP_PAGE_MAIN = 0;
    public final static int HELP_PAGE_EXHIBITOR_DTL = 1;
    public final static int HELP_PAGE_EVENT_DTL = 2;
    public final static int HELP_PAGE_FLOOR_OVERALL = 3;
    public final static int HELP_PAGE_FLOOR_DTL = 4;
    public final static int HELP_PAGE_MY_EXHIBITOR = 5;
    public final static int HELP_PAGE_SCANNER = 6;
    public final static int HELP_PAGE_SCHEDULE = 7;
    public final static int HELP_PAGE_SCHEDULE_DTL = 8;
    public final static int HELP_PAGE_EVENT_LIST = 9;
    public final static int HELP_PAGE_EXHIBITOR_LIST = 10;
    public final static int HELP_PAGE_NEW_TEC_LIST = 11;
    public final static int HELP_PAGE_NAMECARD_LIST = 12;
    public final static int HELP_PAGE_MY_NAMECARD = 13;

    public final static String HELP_PAGE = "HELP_PAGE_";
    /**
     * 是哪个HelpPage
     */
    private int mPageType;

    private View.OnClickListener mCloseListener;
    private Window window;
    private int language;

    public HelpView() {
    }

    /**
     * @param page HELP_PAGE_MAIN|...
     */
    public static HelpView newInstance(int page) {
        LogUtil.i(TAG, "newInstance: page=" + page);
        HelpView hv = new HelpView();
        Bundle args = new Bundle();
        args.putInt("page", page);
        hv.setArguments(args);
        return hv;
    }

    public void setCloseListener(View.OnClickListener listener) {
        mCloseListener = listener;
    }

    private void initView() {
        viewPagerHelp = mView.findViewById(R.id.helpVP);
        mLlPoint = mView.findViewById(R.id.vpindicator);
        if (mCloseListener != null) {
            mView.findViewById(R.id.btn_help_page_close).setOnClickListener(mCloseListener);
        } else {
            mView.findViewById(R.id.btn_help_page_close).setOnClickListener(this);
        }
        setCancelable(false);

        int height = AppUtil.getScreenHeight();
        int width = AppUtil.isTablet() ? (1024 * height / 767) : AppUtil.getScreenWidth();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mView.findViewById(R.id.frameLayout_help).setLayoutParams(params);
        viewPagerHelp.setLayoutParams(params);
    }

    private void generatePage() {
        List<View> helpPages = new ArrayList<>();
        LogUtil.i(TAG, "imageIds=" + imageIds.length);
        int length = imageIds.length;
//        /* 设置缓存策略为 不缓存，因为根据语言切换的不同，图片也不同，如果缓存了，切换语言后仍然会使用上一语言的图片 */
        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA);
//        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
//        requestOptions.skipMemoryCache(true);
        for (int i = 0; i < length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity()).load(imageIds[i]).apply(requestOptions).into(imageView);
            helpPages.add(imageView);
        }
        setPoint(length);
        AdViewPagerAdapter mPagerAdapter = new AdViewPagerAdapter(helpPages);
        viewPagerHelp.setAdapter(mPagerAdapter);
        viewPagerHelp.addOnPageChangeListener(new HelpPageChangeListener());
    }

    private void setPoint(int length) {
        if (length > 1) {
            // 几个圆点
            int width = DisplayUtil.dip2px(getActivity(), 8);
            LinearLayout.LayoutParams ind_params = new LinearLayout.LayoutParams(width, width);
            ind_params.setMargins(width, width, 0, width * 2);
            ImageView iv;
            mLlPoint.removeAllViews();

            for (int i = 0; i < length; ++i) {
                iv = new ImageView(getActivity());
                if (i == 0) {
                    iv.setBackgroundResource(R.drawable.dot_focused);
                } else
                    iv.setBackgroundResource(R.drawable.dot_normal);
                iv.setLayoutParams(ind_params);
                mLlPoint.addView(iv);
            }
        }
    }

    private void getImageIdsSC() {
        if (AppUtil.isTablet()) {
            getImageIdsSCPad();
            return;
        }
        switch (mPageType) {
            case HELP_PAGE_MAIN:
                getMenuImages();
                break;
            case HELP_PAGE_EXHIBITOR_DTL:
                imageIds = new Integer[]{R.drawable.help_exhibitordtl_0_sc};
                break;
            case HELP_PAGE_EVENT_DTL:
                imageIds = new Integer[]{R.drawable.help_event_0_sc};
                break;
            case HELP_PAGE_EVENT_LIST:
                imageIds = new Integer[]{R.drawable.help_eventlist_0_sc};
                break;
            case HELP_PAGE_FLOOR_OVERALL:
                imageIds = new Integer[]{R.drawable.help_mapfloor_0_sc};
                break;
            case HELP_PAGE_FLOOR_DTL:
                imageIds = new Integer[]{R.drawable.help_floordtl_0_sc};
                break;
            case HELP_PAGE_MY_EXHIBITOR:
                imageIds = new Integer[]{R.drawable.help_myexhibitor_0_sc};
                break;
            case HELP_PAGE_SCANNER:
                imageIds = new Integer[]{R.drawable.help_scanner_0_sc};
                break;
            case HELP_PAGE_SCHEDULE:
                imageIds = new Integer[]{R.drawable.help_schedule_0_sc};
                break;
            case HELP_PAGE_SCHEDULE_DTL:
                imageIds = new Integer[]{R.drawable.help_schedule_edit_0_sc};
                break;
            case HELP_PAGE_EXHIBITOR_LIST:
                imageIds = new Integer[]{R.drawable.help_exhibitorlist_0_sc};
                break;
            case HELP_PAGE_NAMECARD_LIST:
                imageIds = new Integer[]{R.drawable.help_namecardlist_0_sc};
                break;
            case HELP_PAGE_MY_NAMECARD:
                imageIds = new Integer[]{R.drawable.help_mynamecard_0_sc};
                break;
            case HELP_PAGE_NEW_TEC_LIST:
                imageIds = new Integer[]{R.drawable.help_newtec_0_sc};
                break;
        }
    }

    private void getImageIdsSCPad() {
        switch (mPageType) {
            case HELP_PAGE_MAIN:
                getMenuImages();
                break;
            case HELP_PAGE_EXHIBITOR_DTL:
                imageIds = new Integer[]{R.drawable.help_exhibitordtl_0_sc_pad};
                break;
            case HELP_PAGE_EVENT_DTL:
                imageIds = new Integer[]{R.drawable.help_event_0_sc_pad};
                break;
            case HELP_PAGE_EVENT_LIST:
                imageIds = new Integer[]{R.drawable.help_eventlist_0_sc_pad};
                break;
            case HELP_PAGE_FLOOR_OVERALL:
//                imageIds = new Integer[]{R.drawable.help_ma};
                break;
            case HELP_PAGE_FLOOR_DTL:
                imageIds = new Integer[]{R.drawable.help_floordtl_0_sc_pad};
                break;
            case HELP_PAGE_MY_EXHIBITOR:
                imageIds = new Integer[]{R.drawable.help_myexhibitor_0_sc_pad};
                break;
            case HELP_PAGE_SCANNER:
                imageIds = new Integer[]{R.drawable.help_scanner_0_sc_pad};
                break;
            case HELP_PAGE_SCHEDULE:
                imageIds = new Integer[]{R.drawable.help_schedule_0_sc_pad};
                break;
            case HELP_PAGE_SCHEDULE_DTL:
                imageIds = new Integer[]{R.drawable.help_schedule_edit_0_sc_pad};
                break;
            case HELP_PAGE_EXHIBITOR_LIST:
                imageIds = new Integer[]{R.drawable.help_exhibitorlist_0_sc_pad};
                break;
            case HELP_PAGE_NAMECARD_LIST:
                imageIds = new Integer[]{R.drawable.help_namecardlist_0_sc_pad};
                break;
            case HELP_PAGE_MY_NAMECARD:
                imageIds = new Integer[]{R.drawable.help_mynamecard_0_sc_pad};
                break;
            case HELP_PAGE_NEW_TEC_LIST:
                imageIds = new Integer[]{R.drawable.help_newtec_0_sc_pad};
                break;

        }
    }

    private void getImageIdsTC() {

        if(AppUtil.isTablet()&& mPageType == HELP_PAGE_EVENT_LIST){
            getImageIdsTCPad();
            return;
        }

        switch (mPageType) {
            case HELP_PAGE_MAIN:
                getMenuImages();
                break;
            case HELP_PAGE_EXHIBITOR_DTL:
                imageIds = new Integer[]{R.drawable.help_exhibitordtl_0_tc};
                break;
            case HELP_PAGE_EVENT_DTL:
                imageIds = new Integer[]{R.drawable.help_event_0_tc};
                break;
            case HELP_PAGE_EVENT_LIST:
                imageIds = new Integer[]{R.drawable.help_eventlist_0_tc};
                break;
            case HELP_PAGE_FLOOR_OVERALL:
                imageIds = new Integer[]{R.drawable.help_mapfloor_0_tc};
                break;
            case HELP_PAGE_FLOOR_DTL:
                imageIds = new Integer[]{R.drawable.help_floordtl_0_tc};
                break;
            case HELP_PAGE_MY_EXHIBITOR:
                imageIds = new Integer[]{R.drawable.help_myexhibitor_0_tc};
                break;
            case HELP_PAGE_SCANNER:
                imageIds = new Integer[]{R.drawable.help_scanner_0_tc};
                break;
            case HELP_PAGE_SCHEDULE:
                imageIds = new Integer[]{R.drawable.help_schedule_0_tc};
                break;
            case HELP_PAGE_SCHEDULE_DTL:
                imageIds = new Integer[]{R.drawable.help_schedule_edit_0_tc};
                break;
            case HELP_PAGE_EXHIBITOR_LIST:
                imageIds = new Integer[]{R.drawable.help_exhibitorlist_0_tc};
                break;
            case HELP_PAGE_NAMECARD_LIST:
                imageIds = new Integer[]{R.drawable.help_namecardlist_0_tc};
                break;
            case HELP_PAGE_MY_NAMECARD:
                imageIds = new Integer[]{R.drawable.help_mynamecard_0_tc};
                break;
            case HELP_PAGE_NEW_TEC_LIST:
                imageIds = new Integer[]{R.drawable.help_newtec_0_tc};
                break;
        }
    }

    private void getImageIdsTCPad() {
        switch (mPageType) {
            case HELP_PAGE_MAIN:
                getMenuImages();
                break;

            case HELP_PAGE_EVENT_LIST:
                imageIds = new Integer[]{R.drawable.help_eventlist_0_tc_pad};
                break;

            case HELP_PAGE_EXHIBITOR_LIST:
                imageIds = new Integer[]{R.drawable.help_exhibitorlist_0_tc_pad};
                break;
            case HELP_PAGE_NAMECARD_LIST:
                imageIds = new Integer[]{R.drawable.help_namecardlist_0_tc_pad};
                break;
            case HELP_PAGE_MY_NAMECARD:
                imageIds = new Integer[]{R.drawable.help_mynamecard_0_tc_pad};
                break;
            case HELP_PAGE_NEW_TEC_LIST:
                imageIds = new Integer[]{R.drawable.help_newtec_0_tc_pad};
                break;
        }
    }

    private void getImageIdsEN() {
        LogUtil.i(TAG, "getImageIdsEN");
        switch (mPageType) {
            case HELP_PAGE_MAIN:
                getMenuImages();
                break;
            case HELP_PAGE_EXHIBITOR_DTL:
                imageIds = new Integer[]{R.drawable.help_exhibitordtl_0_en};
                break;
            case HELP_PAGE_EVENT_DTL:
                imageIds = new Integer[]{R.drawable.help_event_0_en};
                break;
            case HELP_PAGE_EVENT_LIST:
                imageIds = new Integer[]{R.drawable.help_eventlist_0_en};
                break;
            case HELP_PAGE_FLOOR_OVERALL:
                imageIds = new Integer[]{R.drawable.help_mapfloor_0_en};
                break;
            case HELP_PAGE_FLOOR_DTL:
                imageIds = new Integer[]{R.drawable.help_floordtl_0_en};
                break;
            case HELP_PAGE_MY_EXHIBITOR:
                imageIds = new Integer[]{R.drawable.help_myexhibitor_0_en};
                break;
            case HELP_PAGE_SCANNER:
                imageIds = new Integer[]{R.drawable.help_scanner_0_en};
                break;
            case HELP_PAGE_SCHEDULE:
                imageIds = new Integer[]{R.drawable.help_schedule_0_en};
                break;
            case HELP_PAGE_SCHEDULE_DTL:
                imageIds = new Integer[]{R.drawable.help_schedule_edit_0_en};
                break;
            case HELP_PAGE_EXHIBITOR_LIST:
                imageIds = new Integer[]{R.drawable.help_exhibitorlist_0_en};
                break;
            case HELP_PAGE_NAMECARD_LIST:
                imageIds = new Integer[]{R.drawable.help_namecardlist_0_en};
                break;
            case HELP_PAGE_MY_NAMECARD:
                imageIds = new Integer[]{R.drawable.help_mynamecard_0_en};
                break;
            case HELP_PAGE_NEW_TEC_LIST:
                imageIds = new Integer[]{R.drawable.help_newtec_0_en};
                break;
        }
        LogUtil.i(TAG, "imageIds=" + imageIds.length+","+ Arrays.toString(imageIds));
    }

    private void getMenuImages() {
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_1_tc, R.drawable.help_2_tc, R.drawable.help_3_tc, R.drawable.help_4_tc};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_1_en, R.drawable.help_2_en, R.drawable.help_3_en, R.drawable.help_4_en};
        } else {
            imageIds = new Integer[]{R.drawable.help_1_sc, R.drawable.help_2_sc, R.drawable.help_3_sc, R.drawable.help_4_sc};
        }
    }

    /**
     * 初次进入页面时，如果帮助页面没有显示过，则自动显示，否则要按？按钮才显示。
     * 这里判断是否是初次进入这个页面，如果显示过了，mSP_HP中保存pageType，肯定不等于-1.   true，则显示，false不显示
     *
     * @return
     */
    public static boolean isFirstShow(int pageType) {
        LogUtil.i(TAG, "pageType=" + pageType + ",SP pageTyp=" + App.mSP_HP.getInt(HELP_PAGE + pageType, -1));
//        App.mSP_Config.edit().putBoolean("isM2Popup",true).apply(); /*  第一次显示帮助页面，此时也会显示M2Big图片。因此在有更新且跳转到更新中心后，返回时将不再显示M2Big */
        return App.mSP_HP.getInt(HELP_PAGE + pageType, -1) != pageType;
    }

    private void setHelpPageShowed() {
        LogUtil.i(TAG, "setHelpPageShowed:mPageType=" + mPageType);
        App.mSP_HP.edit().putInt(HELP_PAGE + mPageType, mPageType).apply();
    }

    public boolean showPage() {
        setHelpPageShowed();
        return true;
    }

    public void openMainHelpPage() {
        App.mSP_HP.edit().putBoolean("HELP_PAGE_OPEN_MAIN", false).apply();
    }

    @Override
    public void onClick(View v) {
        LogUtil.i(TAG, "onClick");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(getResources().getColor(R.color.home_nav_bar));
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }


        dismiss();


    }

    private class HelpPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int pos) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        // 监听页面改变事件来改变viewIndicator中的指示图片
        @Override
        public void onPageSelected(int arg0) {
            showDot(arg0);
        }
    }

    private void showDot(int currPos) {
        int len = mLlPoint.getChildCount();
        for (int i = 0; i < len; ++i)
            mLlPoint.getChildAt(i).setBackgroundResource(R.drawable.dot_normal);
        mLlPoint.getChildAt(currPos).setBackgroundResource(R.drawable.dot_focused);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageType = getArguments().getInt("page");
        setStyle(STYLE_NO_TITLE, R.style.transparentDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.page_help, container, false);
        initView();
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            getImageIdsTC();
        } else if (language == 1) {
            getImageIdsEN();
        } else {
            getImageIdsSC();
        }
        generatePage();
        return mView;
    }

    private void setPadWindow() {
        window = getDialog().getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);// 加了这个就闪现一个顶部黑条, 不加7.1平板帮助页就无法全屏显示
        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (AppUtil.isTablet()) {
            setPadWindow();
        } else {
            setPhoneWindow();
        }
    }


    private void setPhoneWindow() {
        window = getDialog().getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.height = AppUtil.getScreenHeight();
        wl.width = AppUtil.getScreenWidth();
        wl.gravity = Gravity.TOP;
        window.setAttributes(wl);
        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


}
