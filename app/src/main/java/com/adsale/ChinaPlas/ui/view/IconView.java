package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.ViewExhibitorNavItemBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DisplayUtil;

public class IconView extends RelativeLayout {
    private static final String TAG = "IconView";
    private TextView tvName;
    private ImageView ivRight;
    private TextView below;
    private RelativeLayout rlIconView;

    private Context mContext;
    private Animation animation;
    private final int DEFAULT_HEIGHT = 48;

    public final ObservableInt leftIcon = new ObservableInt();
    public final ObservableField<String> text = new ObservableField<>();
    public final ObservableBoolean isLineVisible=new ObservableBoolean(true);
    public final ObservableField<OnClickListener> clickListener=new ObservableField<>();

    private ViewExhibitorNavItemBinding binding;

    public IconView(Context context) {
        super(context);
        init(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        binding = ViewExhibitorNavItemBinding.inflate(LayoutInflater.from(context),this,true);
        binding.setIconView(this);
        View view = binding.getRoot();

        tvName=binding.textNvaName;
        rlIconView =binding.rlIconView;
        below = binding.textNvaSyncTime;
        ivRight = binding.ivRight;

//		setDefaultHeight();
    }

    public void setIconText(int resIcon,int resStr){
        leftIcon.set(resIcon);
        text.set(mContext.getString(resStr));
    }

    public void setIconText(int resIcon,String resStr){
        leftIcon.set(resIcon);
        text.set(resStr);
    }

    public void setRLGone() {
        rlIconView.setVisibility(View.GONE);
    }

    public void setTextColor(int color) {
        tvName.setTextColor(color);
    }

    public void setBelowVisible() {
        below.setVisibility(View.VISIBLE);
    }

    public void setLineVisible(boolean visible) {
        isLineVisible.set(visible);
    }

    /**
     * 設置默認文字和顏色 ，文字為：All，顏色為灰色R.color.grey
     */
    public void setDefaultBelowTextAndColor() {
        below.setVisibility(View.VISIBLE);
        below.setText("ALL");
        below.setTextColor(getContext().getResources().getColor(R.color.grey));
    }

    /**
     * 設置默認文字和顏色
     */
    public void setDefaultBelowTextAndColor(String text, int color) {
        below.setVisibility(View.VISIBLE);
        below.setText(text);
        below.setTextColor(color);
    }

    /**
     * 名称下方的文字
     */
    public void setBelowText(String text) {
        below.setVisibility(View.VISIBLE);
        below.setText(text);
    }

    /**
     * 設置文字和顏色 ，顏色為紅色R.color.primaryRedColor
     */
    public void setBelowTextAndColor(String text) {
        below.setVisibility(View.VISIBLE);
        below.setText(text);
        below.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
    }

    public void setBelowText(int resStr) {
        below.setVisibility(View.VISIBLE);
        below.setText(resStr);
    }

    public void setBelowColor(int color) {
        below.setVisibility(View.VISIBLE);
        below.setTextColor(color);
    }

    /**
     * 给TextView设置下划线
     */
    public void setTextUnderLine() {
        AppUtil.setUnderLine(tvName);
    }

    /**
     * 给TextView设置下划线，并自定义颜色
     */
    public void setTextUnderLineColor() {
        AppUtil.setUnderLineColor(tvName, getResources().getColor(R.color.home));
    }

    public void setOnTextClickListener(OnClickListener listener) {
        tvName.setOnClickListener(listener);
    }

    public void setIvRightVisible() {
        ivRight.setImageResource(R.drawable.ic_right_arrow);
        ivRight.setVisibility(View.VISIBLE);
    }

    public void setIvRightResource(int resId) {
        ivRight.setImageResource(resId);
    }

    public void setIvRightVisible(int resId) {
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(resId);
    }

    public void setIvRightGone() {
        ivRight.setVisibility(View.GONE);
    }

    public void setIvRightClickListener(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
    }

    public void setHeight(int height) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        rlIconView.setLayoutParams(params);
    }

    public void setDefaultHeight() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, DEFAULT_HEIGHT));
        rlIconView.setLayoutParams(params);
    }

    /**
     * 箭头旋转动画
     *
     * @param down 要点击箭头往下，则执行往下的动画；要点击箭头返回原位向右，则执行向右的动画。
     */
    public void arrowRotate(boolean down) {
        if (down) {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.arrow_rotate_down);
        } else {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.arrow_rotate_right);
        }
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);
        ivRight.startAnimation(animation);
    }

}
