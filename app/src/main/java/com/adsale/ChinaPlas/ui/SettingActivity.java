package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivitySettingBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CalendarUtil;

public class SettingActivity extends BaseActivity {

    @Override
    protected void initView() {
        ActivitySettingBinding binding =ActivitySettingBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        binding.setAty(this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {

    }

//    public void onShare(){
//
//    }

    public void onLinkWebsite(){

    }

    public void onAddToCalendar(){
        CalendarUtil calendarUtil = new CalendarUtil(this);
        calendarUtil.addToCalendar();
    }

    public void onResetAll(){
        AppUtil.putLogout();


    }

    public void onPrivacy(){

    }

    public void onUseItems(){

    }

    public void onHelpPage(){

    }



}
