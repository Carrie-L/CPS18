package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.databinding.ActivityEventBinding;
import com.adsale.ChinaPlas.helper.OnCpsItemClickListener;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.EventModel;

import org.jsoup.Connection;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/9/19.
 * 同期活动
 */

public class ConcurrentEventActivity extends BaseActivity implements OnCpsItemClickListener,OnIntentListener{

    private RecyclerView recyclerView;
    private EventModel mEventModel;

    @Override
    protected void initView() {
        ActivityEventBinding binding =ActivityEventBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        mEventModel = new EventModel();
        binding.setEventModel(mEventModel);

        recyclerView = binding.rvEvent;

    }

    @Override
    protected void initData() {

        mEventModel.setOnCpsItemClickListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mEventModel.getList();
        EventAdapter adapter = new EventAdapter(mEventModel.events,mEventModel);
        recyclerView.setAdapter(adapter);
        mEventModel.onStart(this,adapter);

    }

    @Override
    public void onItemClick(String id) {
        LogUtil.i(TAG,"onItemClick: id= "+id);
        Intent intent=new Intent(this,WebContentActivity.class);
        intent.putExtra("Url", "ConcurrentEvent/".concat(id));
//        intent.putExtra(Constant.TITLE, mainIcon.getTitle());
        startActivity(intent);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = new Intent(this,toCls);
        startActivity(intent);
    }
}
