package com.adsale.ChinaPlas.helper;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
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
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
//            sdvHelp.setImageURI(Uri.parse("res:///" + imageIds[i]));
            setDraweeView(sdvHelp, i);

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

        for (int i = 0; i < length; i++) {
            view = inflater.inflate(R.layout.imageview, null);
            sdvHelp = (ImageView) view.findViewById(R.id.image_view);
//            sdvHelp.setImageURI(Uri.parse("res:///" + imageIds[i]));
            GlideApp.with(mContext).load(imageIds[i]).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(sdvHelp);
            setDraweeView(sdvHelp, i);
            helpPages.add(sdvHelp);
        }

        setPoint(length);

        mPagerAdapter = new AdViewPagerAdapter(helpPages);
        viewPagerHelp.setAdapter(mPagerAdapter);
        viewPagerHelp.addOnPageChangeListener(new HelpPageChangeListener());
    }

    private void setDraweeView(ImageView sdvHelp, int i) {
        //still oom

        if (AppUtil.isPadDevice(mContext)) {
//            mImageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("res:///" + imageIds[i]))
//                    .setResizeOptions(new ResizeOptions(943, 800))
//                    .build();
//             .load(Uri.parse("res:///" + imageIds[i]))
            GlideApp
                    .with(mContext)
                    .load(Uri.parse("res:///" + imageIds[i]))
                    .override(943, 800)
                    .fitCenter() // The image will be displayed completely, but might not fill the entire ImageView.
                    .into(sdvHelp);
        } else {
//            mImageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("res:///" + imageIds[i]))
//                    .setResizeOptions(new ResizeOptions(500, 888))
//                    .build();

            GlideApp
                    .with(mContext)
                    .load(Uri.parse("res:///" + imageIds[i]))
                    .override(500, 888)
                    .fitCenter() // The image will be displayed completely, but might not fill the entire ImageView.
                    .into(sdvHelp);

        }

//        mDraweeController = Fresco.newDraweeControllerBuilder()
//                .setOldController(sdvHelp.getController())
//                .setImageRequest(mImageRequest)
//                .build();
//        sdvHelp.setController(mDraweeController);

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
        imageIds = getMenuImages(language);

        if (check) {
            findView("IsFirstHelpPageShowed", imageIds, false);
        } else
            showHelpPage("IsFirstHelpPageShowed", imageIds, false);

        btn_close.setOnClickListener(listener);
        App.mSP_Config.edit().putBoolean("ShowMenuHelpPage", false).apply();
    }

    public Integer[] getMenuImages(int language) {
        if (language == 0) {
            return imageIds = new Integer[]{R.drawable.help_1_tc, R.drawable.help_2_tc, R.drawable.help_3_tc, R.drawable.help_4_tc, R.drawable.help_5_tc, R.drawable.help_6_tc};
        } else if (language == 1) {
            return imageIds = new Integer[]{R.drawable.help_1_en, R.drawable.help_2_en, R.drawable.help_3_en, R.drawable.help_4_en, R.drawable.help_5_en, R.drawable.help_6_en};
        } else {
            return imageIds = new Integer[]{R.drawable.help_1_sc, R.drawable.help_2_sc, R.drawable.help_3_sc, R.drawable.help_4_sc, R.drawable.help_5_sc, R.drawable.help_6_sc};
        }
    }

    /**
     * @param context
     * @param check   是否审核，判断是否已经启动过，第一次进入时显示，之后进入要点击？按钮才显示。true，判断；false，不判断，直接显示
     */
    public void showPageMyExhibitor(Context context, boolean check, View.OnClickListener listener) {
        mContext = context;
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_myexhibitor_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_myexhibitor_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_myexhibitor_sc_0};
        }

        if (check) {
            findView("Help_MyExhibitor", imageIds, false);
        } else
            showHelpPage("Help_MyExhibitor", imageIds, false);

        if (btn_close != null) {
            btn_close.setOnClickListener(listener);
        }
    }

    public void showPageExhibitorDtl(Context context, boolean check, View.OnClickListener listener) {
        mContext = context;
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_exhibitordtl_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_exhibitordtl_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_exhibitordtl_sc_0};
        }
        if (check)
            findView("Help_ExhibitorDtl", imageIds, false);
        else
            showHelpPage("Help_ExhibitorDtl", imageIds, false);

        if (btn_close != null) {
            btn_close.setOnClickListener(listener);
        }
    }

    public void showPageMapFloor(Context context, boolean check) {
        mContext = context;
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_mapfloor_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_mapfloor_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_mapfloor_sc_0};
        }

//        imageIds = new Integer[]{R.drawable.help_mapfloor_0};
        if (check)
            findView("Help_MapFloor", imageIds, true);
        else
            showHelpPage("Help_MapFloor", imageIds, true);
    }

    public void showPageFloorDtl(Context context, boolean check) {
        mContext = context;
//        imageIds = new Integer[]{R.drawable.help_floordtl_0};
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_floordtl_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_floordtl_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_floordtl_sc_0};
        }
        if (check)
            findView("Help_FloorDtl", imageIds, true);
        else
            showHelpPage("Help_FloorDtl", imageIds, true);
    }

    public void showPageSchedule(Context context, boolean check, final FloatingActionButton fab) {
        mContext = context;
//        imageIds = new Integer[]{R.drawable.help_schedule_0};
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_schedule_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_schedule_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_schedule_sc_0};
        }
        if (check)
            findView("Help_Schedule", imageIds, false);
        else
            showHelpPage("Help_Schedule", imageIds, false);

        if (btn_close != null) {
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setVisibility(View.VISIBLE);
                    mFrameLayout.setVisibility(View.GONE);
                    App.mSP_Config.edit().putBoolean("Help_Schedule", true).apply();
                }
            });
        }
    }

    public void showPageScheduleEdit(Context context, boolean check) {
        mContext = context;
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_schedule_edit_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_schedule_edit_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_schedule_edit_sc_0};
        }
        if (check)
            findView("Help_ScheduleEdit", imageIds, true);
        else
            showHelpPage("Help_ScheduleEdit", imageIds, true);
    }

    public void showPageEvent(Context context, boolean check, final FloatingActionButton fab) {
        mContext = context;
//        imageIds = new Integer[]{R.drawable.help_event_0};
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_event_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_event_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_event_sc_0};
        }
        if (check)
            findView("Help_Event", imageIds, false);
        else
            showHelpPage("Help_Event", imageIds, false);

        if (btn_close != null) {
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setVisibility(View.VISIBLE);
                    mFrameLayout.setVisibility(View.GONE);
                    App.mSP_Config.edit().putBoolean("Help_Event", true).apply();
                }
            });
        }
    }

    public void showPageScanner(Context context, boolean check) {
        mContext = context;
        language = AppUtil.getCurLanguage();
        if (language == 0) {
            imageIds = new Integer[]{R.drawable.help_scanner_tc_0};
        } else if (language == 1) {
            imageIds = new Integer[]{R.drawable.help_scanner_en_0};
        } else {
            imageIds = new Integer[]{R.drawable.help_scanner_sc_0};
        }
        if (check)
            findView("Help_QrCodeScanner", imageIds, true);
        else
            showHelpPage("Help_QrCodeScanner", imageIds, true);
    }

    ////newer method
    public void init2(FrameLayout frameLayout, ViewPager viewPager, LinearLayout llPoint, ImageView btn) {
        mFrameLayout = frameLayout;
        viewPagerHelp = viewPager;
        mLlPoint = llPoint;
        btn_close = btn;
    }


}
