package com.adsale.ChinaPlas.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.databinding.ViewExhiDtlInfoBinding;
import com.adsale.ChinaPlas.ui.FloorDetailActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String content = mExhibitor.getBoothNo();
        if (content.contains("/")) {// W5K21/E7D05/N5S51
            final String[] boothNos = content.split("/");
            LogUtil.i("ExhibitorInfoView", "boothNos=" + Arrays.toString(boothNos));
            // .Resources$NotFoundException: Resource ID #0x0
//            AlertDialog.Builder builder = new AlertDialog.Builder(binding.getRoot().getContext());
//            builder.setItems(boothNos, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    LogUtil.i("ExhibitorInfoView", "click:" + boothNos[which] + "..getHallNo=" + mExhibitor.getHallNo()+", mExhibitor.getBoothNo()");
//                    Intent intent = new Intent(mContext, FloorDetailActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("BOOTH",  boothNos[which]);
//                    intent.putExtra("HALL", mExhibitor.getHallNo());
//                    mContext.startActivity(intent);
//                    if (AppUtil.isTablet()) {
//                        ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    }
//                }
//            }).show();
        }else{
            Intent intent = new Intent(mContext, FloorDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("BOOTH", mExhibitor.getBoothNo());
            intent.putExtra("HALL", mExhibitor.getHallNo());
            intent.putExtra("FromCls", 1);
            mContext.startActivity(intent);
            if (AppUtil.isTablet()) {
                ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }




    }

    public void onEmail() {
        AppUtil.sendEmailIntent(mContext, mExhibitor.getEmail());
    }

    public interface OnCallPhoneListener{
        void callPhone(String number);
    }

    private OnCallPhoneListener mListener;

    public void setOnCallPhoneListener(OnCallPhoneListener listener){
        mListener = listener;
    }

    public void onTel() {
        if(mListener!=null){
            mListener.callPhone(mExhibitor.getTel());
        }
//        AppUtil.callPhoneIntent(binding.tvTel.getContext(), mExhibitor.getTel());
    }

    public void onWebsite() {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
     * @param toDown 要点击箭头往下，则执行往下的动画；要点击箭头返回原位向右，则执行向右的动画。
     */
    public void arrowRotate(boolean toDown) {
        Animation animation;
        if (toDown) {
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
