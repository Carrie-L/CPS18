package com.adsale.ChinaPlas.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.databinding.ViewExhiDtlInfoBinding;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.animation;
import static com.adsale.ChinaPlas.R.drawable.arrow;
import static com.adsale.ChinaPlas.R.id.language;

/**
 * Created by Carrie on 2017/11/15.
 * 展商详情页 - 公司资料
 * todo booth click
 */

public class ExhiDtlInfoView extends RelativeLayout {

    private ViewExhiDtlInfoBinding binding;
    private Exhibitor mExhibitor;
    private Context mContext;

    public final ObservableBoolean arrowDown = new ObservableBoolean(true);  /* 默认箭头朝下 */

    public ExhiDtlInfoView(Context context) {
        super(context);
        init(context);
    }

    public ExhiDtlInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExhiDtlInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        binding = ViewExhiDtlInfoBinding.inflate(LayoutInflater.from(context), this, true);
        binding.setView(this);
    }

    public void setData(Exhibitor exhibitor) {
        mExhibitor = exhibitor;
        binding.setObj(exhibitor);
        binding.executePendingBindings();
        if (!TextUtils.isEmpty(mExhibitor.getDescription())) {
            arrowRotate(true);
        }
    }

    public void onBooth() {

    }

    public void onEmail() {
        AppUtil.sendEmailIntent(mContext, mExhibitor.getEmail());
    }


    public void onTel() {
        AppUtil.callPhoneIntent(binding.getView().getContext(), mExhibitor.getTel());
    }

    public void onWebsite() {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.WEB_URL, checkUrl(mExhibitor.getWebsite()));
        intent.putExtra("title", mExhibitor.getCompanyName(language));
        mContext.startActivity(intent);
        if (AppUtil.isTablet()) {
            ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void onDesc() {
        arrowRotate(!arrowDown.get());
        arrowDown.set(!arrowDown.get());

    }

    /**
     * 箭头旋转动画
     *
     * @param down 要点击箭头往下，则执行往下的动画；要点击箭头返回原位向右，则执行向右的动画。
     */
    public void arrowRotate(boolean down) {
        Animation animation;
        if (down) {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.arrow_rotate_down);
        } else {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.arrow_rotate_right);
        }
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);
        binding.ivArrow.startAnimation(animation);
    }

    public String checkUrl(String url) {
        if (url.toLowerCase().startsWith("www.")) {
            url = "http://" + url;
        }
        Pattern pattern = Pattern.compile("http://[\\w\\.\\-/:]+");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            url = matcher.group();
        }
        return url;
    }


}
