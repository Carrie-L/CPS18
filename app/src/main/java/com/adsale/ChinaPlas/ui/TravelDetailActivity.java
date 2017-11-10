package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.model.AgentInfo;
import com.adsale.ChinaPlas.databinding.ActivityTravelDetailBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.bumptech.glide.Glide;


/**
 * Created by new on 2016/12/1.
 * 酒店詳情
 */
public class TravelDetailActivity extends BaseActivity {
    private final String TAG = "TravelDetailActivity";
    private TextView tvTel;
    private ActivityTravelDetailBinding binding;
    private AgentInfo agentInfo;
    private String tel;

    public void initView() {
        binding = ActivityTravelDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        agentInfo = getIntent().getParcelableExtra("Info");
        binding.setInfo(agentInfo);
        binding.executePendingBindings();
        tvTel = binding.travelTel;
    }

    protected void initData() {
        Glide.with(this).load(Uri.parse(agentInfo.logoMobile)).into(binding.hotelLogo);
        onTel();
    }

    private void onTel() {
        tel = agentInfo.getTel();
        tvTel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tel.contains("/")) {
                    int telWidth = tvTel.getMeasuredWidth();
                    Paint paint = new Paint();
                    float tel1Width = DisplayUtil.sp2px(getApplicationContext(), paint.measureText(tel.split("/")[1]));
                    float splitWidth = DisplayUtil.sp2px(getApplicationContext(), paint.measureText(" / "));
                    float x = event.getX();
                    if (x <= (telWidth - tel1Width - splitWidth)) {
                        AppUtil.callPhoneIntent(TravelDetailActivity.this, tel.split("/")[0]);
                    } else {
                        AppUtil.callPhoneIntent(TravelDetailActivity.this, tel.split("/")[1]);
                    }
                } else {
                    AppUtil.callPhoneIntent(TravelDetailActivity.this, tel);
                }
                return false;
            }
        });
    }

    public void onEmail(String email) {
        AppUtil.sendEmailIntent(this, email);
    }

    public void onWebsite(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", agentInfo.getAgentName());
        intent.putExtra(Constant.WEB_URL, url);
        startActivity(intent);
        overridePendingTransPad();
    }


}
