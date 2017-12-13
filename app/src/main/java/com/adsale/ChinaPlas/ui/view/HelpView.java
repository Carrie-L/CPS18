package com.adsale.ChinaPlas.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

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

public class HelpView extends Dialog implements View.OnClickListener {
    private Integer[] imageIds;
    private Context mContext;
    private View mView;
    private ViewPager viewPagerHelp;
    private LinearLayout mLlPoint;

    public final static int HELP_PAGE_MAIN = 0;
    public final static int HELP_PAGE_EXHIBITOR_DTL = 1;
    public final static int HELP_PAGE_EVENT_DTL = 2;
    public final static int HELP_PAGE_FLOOR_OVERALL = 3;
    public final static int HELP_PAGE_FLOOR_DTL = 4;
    public final static int HELP_PAGE_MY_EXHIBITOR = 5;
    public final static int HELP_PAGE_SCANNER = 6;
    public final static int HELP_PAGE_SCHEDULE = 7;
    public final static int HELP_PAGE_SCHEDULE_DTL = 8;
    public final static String HELP_PAGE = "HELP_PAGE_";
    /**
     * 是哪个HelpPage
     */
    private int mPageType;

    private View.OnClickListener mCloseListener;
    private AdViewPagerAdapter mPagerAdapter;

    /**
     * @param context activity
     * @param page    HELP_PAGE_MAIN|...
     */
    public HelpView(@NonNull Context context, int page) {
        super(context, R.style.transparentBgDialog);
        this.mContext = context;
        mPageType = page;
    }

    public HelpView(@NonNull Context context, int page, View.OnClickListener listener) {
        super(context, R.style.transparentBgDialog);
        this.mContext = context;
        mPageType = page;
        mCloseListener = listener;
    }

    private void initView() {
        viewPagerHelp = findViewById(R.id.helpVP);
        mLlPoint = findViewById(R.id.vpindicator);
        if (mCloseListener != null) {
            findViewById(R.id.btn_help_page_close).setOnClickListener(mCloseListener);
        } else {
            findViewById(R.id.btn_help_page_close).setOnClickListener(this);
        }
        setCancelable(false);
    }

    private void generatePage() {
        List<View> helpPages = new ArrayList<>();
        LogUtil.i(TAG, "imageIds=" + imageIds.length);
        int length = imageIds.length;
        /* 设置缓存策略为 不缓存，因为根据语言切换的不同，图片也不同，如果缓存了，切换语言后仍然会使用上一语言的图片 */
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.override(500, 888);
        requestOptions.fitCenter();
        for (int i = 0; i < length; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            Glide.with(mContext).load(imageIds[i]).apply(requestOptions).into(imageView);
            helpPages.add(imageView);
        }
        setPoint(length);
        mPagerAdapter = new AdViewPagerAdapter(helpPages);
        viewPagerHelp.setAdapter(mPagerAdapter);
        viewPagerHelp.addOnPageChangeListener(new HelpPageChangeListener());
    }

    private void setPoint(int length) {
        if (length > 1) {
            // 几个圆点
            int width = DisplayUtil.dip2px(mContext, 8);
            LinearLayout.LayoutParams ind_params = new LinearLayout.LayoutParams(width, width);
            ind_params.setMargins(width, width, 0, width * 4);
            ImageView iv;
            mLlPoint.removeAllViews();

            for (int i = 0; i < length; ++i) {
                iv = new ImageView(mContext);
                if (i == 0) {
                    iv.setBackgroundResource(R.drawable.dot_focused);
                } else
                    iv.setBackgroundResource(R.drawable.dot_normal);
                iv.setLayoutParams(ind_params);
                mLlPoint.addView(iv);
            }
        }
    }

    private void getImageIds() {
        LogUtil.i(TAG, "page=" + mPageType);
        switch (mPageType) {
            case HELP_PAGE_MAIN:
                getMenuImages();
                break;
            case HELP_PAGE_EXHIBITOR_DTL:
                imageIds = new Integer[]{R.drawable.help_exhibitordtl_0};
                break;
            case HELP_PAGE_EVENT_DTL:
                imageIds = new Integer[]{R.drawable.help_event_0};
                break;
            case HELP_PAGE_FLOOR_OVERALL:
                imageIds = new Integer[]{R.drawable.help_mapfloor_0};
                break;
            case HELP_PAGE_FLOOR_DTL:
                imageIds = new Integer[]{R.drawable.help_floordtl_0};
                break;
            case HELP_PAGE_MY_EXHIBITOR:
                imageIds = new Integer[]{R.drawable.help_myexhibitor_0};
                break;
            case HELP_PAGE_SCANNER:
                imageIds = new Integer[]{R.drawable.help_scanner_0};
                break;
            case HELP_PAGE_SCHEDULE:
                imageIds = new Integer[]{R.drawable.help_schedule_0};
                break;
            case HELP_PAGE_SCHEDULE_DTL:
                imageIds = new Integer[]{R.drawable.help_schedule_edit_0};
                break;

        }
    }

    private void getMenuImages() {
        imageIds = new Integer[]{R.drawable.help_1, R.drawable.help_2, R.drawable.help_3, R.drawable.help_4, R.drawable.help_5, R.drawable.help_6};
    }

    /**
     * 初次进入页面时，如果帮助页面没有显示过，则自动显示，否则要按？按钮才显示。
     * 这里判断是否是初次进入这个页面，如果显示过了，mSP_HP中保存pageType，肯定不等于-1.   true，则显示，false不显示
     *
     * @return
     */
    public boolean isFirstShow() {
        return App.mSP_HP.getInt(HELP_PAGE + mPageType, -1) != mPageType;
    }

    private void setHelpPageShowed() {
        App.mSP_HP.edit().putInt(HELP_PAGE + mPageType, mPageType).apply();
    }

    public boolean showPage() {
        if (isFirstShow()) {
            show();
            setHelpPageShowed();
            return true;
        }
        return false;
    }

    public void openMainHelpPage() {
        show();
        App.mSP_HP.edit().putBoolean("HELP_PAGE_OPEN_MAIN", false).apply();
    }

    @Override
    public void onClick(View v) {
        LogUtil.i(TAG, "onClick");
        cancel();
    }

    public void dismissDialog() {
        LogUtil.i(TAG, "dismissDialog");
        cancel();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_help);
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.height = App.mSP_Config.getInt(Constant.SCREEN_HEIGHT, 0);
        wl.width = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        wl.gravity = Gravity.TOP;
        window.setAttributes(wl);
        initView();
        getImageIds();
        generatePage();
    }

}
