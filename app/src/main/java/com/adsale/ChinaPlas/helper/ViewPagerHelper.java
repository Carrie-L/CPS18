package com.adsale.ChinaPlas.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2017/12/1.
 * ViewPager通用类
 */

public class ViewPagerHelper {
    private Context mContext;
    private LinearLayout mLlPoint;
    private ViewPager mViewPager;
    private List<View> views = new ArrayList<>();
    private boolean isDotFocus;
    private int index;

    public ViewPagerHelper(Context mContext, LinearLayout mLlPoint, ViewPager mViewPager, List<View> views) {
        this.mContext = mContext;
        this.mLlPoint = mLlPoint;
        this.mViewPager = mViewPager;
        this.views = views;
    }

    public void generateView(boolean dotFocus) {
        isDotFocus = dotFocus;
        if (dotFocus) {
            setPoint(views.size());
        } else {
            setRecFocus(views.size());
        }
        AdViewPagerAdapter mPagerAdapter = new AdViewPagerAdapter(views);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPagerChangeListener());
    }

    private void setPoint(int length) {
        if (length <= 1) {
            return;
        }
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

    private void setRecFocus(int length) {
        if (length <= 1) {
            return;
        }
        // 几个点
        int width = DisplayUtil.dip2px(mContext, 18);
        LinearLayout.LayoutParams ind_params = new LinearLayout.LayoutParams(width, width);
        ind_params.setMargins(5, width, 0, 0);
        TextView textView;
        mLlPoint.removeAllViews();
        for (int i = 0; i < length; ++i) {
            textView = new TextView(mContext);
            if (i == 0) {
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.dark_gray));
            } else
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            textView.setText((i + 1) + "");
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(ind_params);
            mLlPoint.addView(textView);
            index = i;
        }
        setDotClick(length);
    }

    private void setDotClick(int length) {
        if (length == 0) {
            return;
        }
        mLlPoint.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        if (length < 2) {
            return;
        }
        mLlPoint.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        if (length < 3) {
            return;
        }
        mLlPoint.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });
    }

    private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {
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
        for (int i = 0; i < len; ++i) {
            if (isDotFocus) {
                mLlPoint.getChildAt(i).setBackgroundResource(R.drawable.dot_normal);
            } else {
                mLlPoint.getChildAt(i).setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            }
        }
        if (isDotFocus) {
            mLlPoint.getChildAt(currPos).setBackgroundResource(R.drawable.dot_focused);
        } else {
            mLlPoint.getChildAt(currPos).setBackgroundColor(mContext.getResources().getColor(R.color.dark_gray));
        }
    }


}
