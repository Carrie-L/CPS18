package com.adsale.ChinaPlas.helper;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.glide.GlideApp;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by new on 2016/11/16.
 */
public class HelpPage {
    private final String TAG = "HelpPage";
    private Context mContext;

    private View mHelpView;
    private FrameLayout mFrameLayout;
    private ViewPager viewPagerHelp;
    private LinearLayout mLlPoint;
    private ImageView btn_close;
    private boolean showed;
    private Integer[] imageIds;
    private List<View> helpPages;
    private View view;
    private ImageView sdvHelp;
    private LayoutInflater inflater;
    private int language;
    private AdViewPagerAdapter mPagerAdapter;


    public void findView(View view) {
        viewPagerHelp = (ViewPager) view.findViewById(R.id.helpVP);
        mLlPoint = (LinearLayout) view.findViewById(R.id.vpindicator);
    }

    public void findView(final String key, Integer[] imageIds, boolean btnClose) {
        showed = App.mSP_Config.getBoolean(key, false);
        if (!showed) {
            showHelpPage(key, imageIds, btnClose);
        }
    }

    public void showHelpPage(final String key, Integer[] imageIds, boolean btnClose) {
        if (mFrameLayout == null) {
            LogUtil.e(TAG, "mFrameLayout==null");
        }
        mFrameLayout.setVisibility(View.VISIBLE);

        init(imageIds);

        if (btnClose) {
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFrameLayout.setVisibility(View.GONE);
                    App.mSP_Config.edit().putBoolean(key, true).apply();
//                    mFrameLayout=null;
                    sdvHelp = null;
                }
            });
        }
    }

    public void destroyHelpPage() {
        if (mHelpView != null) {
            mHelpView.setVisibility(View.GONE);
            mHelpView = null;
        }

        if (mFrameLayout != null) {
            mFrameLayout.setVisibility(View.GONE);
            mFrameLayout = null;
        }
    }

    public void init(Integer[] imageIds) {
        List<View> helpPages = new ArrayList<>();
        int length = imageIds.length;
        inflater = LayoutInflater.from(mContext);

        for (int i = 0; i < length; i++) {
            view = inflater.inflate(R.layout.imageview, null);
            sdvHelp = (ImageView) view.findViewById(R.id.image_view);
            Glide.with(mContext).load(imageIds[i]).into(sdvHelp);
//            sdvHelp.setImageURI(Uri.parse("res:///" + imageIds[i]));
//            setDraweeView(sdvHelp, i);

            //replace with glide
//            Glide.with(mContext).load(imageIds[i]).centerCrop().crossFade().into(sdvHelp);

            helpPages.add(sdvHelp);
        }
        setPoint(length);
        mPagerAdapter = new AdViewPagerAdapter(helpPages);
        viewPagerHelp.setAdapter(mPagerAdapter);
        viewPagerHelp.addOnPageChangeListener(new HelpPageChangeListener());
    }

    public void init(Context context, Integer[] imageIds) {
        mContext = context;
        helpPages = new ArrayList<>();
        int length = imageIds.length;
        inflater = LayoutInflater.from(mContext);
        /* 设置缓存策略为 不缓存，因为根据语言切换的不同，图片也不同，如果缓存了，切换语言后仍然会使用上一语言的图片 */
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);

        for (int i = 0; i < length; i++) {
            view = inflater.inflate(R.layout.imageview, null);
            sdvHelp = (ImageView) view.findViewById(R.id.image_view);
            Glide.with(mContext).load(imageIds[i]).apply(requestOptions).into(sdvHelp);
            helpPages.add(sdvHelp);
        }
        setPoint(length);
        mPagerAdapter = new AdViewPagerAdapter(helpPages);
        viewPagerHelp.setAdapter(mPagerAdapter);
        viewPagerHelp.addOnPageChangeListener(new HelpPageChangeListener());
    }

    private void setDraweeView(ImageView sdvHelp, int i) {
        if (AppUtil.isPadDevice(mContext)) {
            GlideApp
                    .with(mContext)
                    .load(Uri.parse("res:///" + imageIds[i]))
                    .override(943, 800)
                    .fitCenter() // The image will be displayed completely, but might not fill the entire ImageView.
                    .into(sdvHelp);
        } else {
            GlideApp
                    .with(mContext)
                    .load(imageIds[i])
                    .override(500, 888)
                    .fitCenter() // The image will be displayed completely, but might not fill the entire ImageView.
                    .into(sdvHelp);

        }
    }

    private void setPoint(int length) {
        if (length > 1) {
            // 几个圆点
            int width = DisplayUtil.dip2px(mContext, 8);
            LinearLayout.LayoutParams ind_params = new LinearLayout.LayoutParams(width, width);
            ind_params.setMargins(width, width, 0, width * 2);
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

    public class HelpPageChangeListener implements ViewPager.OnPageChangeListener {
        private int mCurrItem = 0;
        private int scroll = 0;

        @Override
        public void onPageScrollStateChanged(int pos) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            scroll = arg2;
        }

        // 监听页面改变事件来改变viewIndicator中的指示图片
        @Override
        public void onPageSelected(int arg0) {
            mCurrItem = arg0;
            showDot(arg0);
        }
    }

    private void showDot(int currPos) {
        int len = mLlPoint.getChildCount();
        for (int i = 0; i < len; ++i)
            mLlPoint.getChildAt(i).setBackgroundResource(R.drawable.dot_normal);
        mLlPoint.getChildAt(currPos).setBackgroundResource(R.drawable.dot_focused);
    }


    public void showPageMenu(Context context, boolean check, View.OnClickListener listener) {
        mContext = context;
        language = AppUtil.getCurLanguage();
        imageIds = getMenuImages();

        if (check) {
            findView("IsFirstHelpPageShowed", imageIds, false);
        } else
            showHelpPage("IsFirstHelpPageShowed", imageIds, false);

        btn_close.setOnClickListener(listener);
        App.mSP_Config.edit().putBoolean("ShowMenuHelpPage", false).apply();
    }

    public Integer[] getMenuImages() {
        return imageIds = new Integer[]{R.drawable.help_1, R.drawable.help_2, R.drawable.help_3, R.drawable.help_4, R.drawable.help_5, R.drawable.help_6};
    }



}
