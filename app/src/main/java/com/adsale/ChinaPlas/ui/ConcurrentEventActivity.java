package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityEventBinding;
import com.adsale.ChinaPlas.viewmodel.EventModel;

import org.jsoup.Connection;

/**
 * Created by Carrie on 2017/9/19.
 */

public class ConcurrentEventActivity extends BaseActivity {

    @Override
    protected void initView() {
        ActivityEventBinding binding =ActivityEventBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        EventModel mEventModel=new EventModel();
        binding.setEventModel(mEventModel);



    }

    @Override
    protected void initData() {

    }
}
