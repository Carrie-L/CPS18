package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.databinding.ActivityEventBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.EventModel;

import static android.R.attr.id;


/**
 * Created by Carrie on 2017/9/19.
 * 同期活动
 */

public class ConcurrentEventActivity extends BaseActivity implements OnIntentListener {

    private RecyclerView recyclerView;
    private EventModel mEventModel;

    @Override
    protected void initView() {
        ActivityEventBinding binding = ActivityEventBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mEventModel = new EventModel();
        binding.setEventModel(mEventModel);

        recyclerView = binding.rvEvent;

    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mEventModel.getList();
        EventAdapter adapter = new EventAdapter(mEventModel.events, this);
        recyclerView.setAdapter(adapter);
        mEventModel.onStart(this, adapter);

    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls.getSimpleName().equals("WebContentActivity")) {
            LogUtil.i(TAG, "onItemClick: id= " + id);
            Intent intent = new Intent(this, WebContentActivity.class);
            ConcurrentEvent.Pages pages = (ConcurrentEvent.Pages) entity;
            intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(pages.pageID));
            intent.putExtra("title", pages.getTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransPad();
        } else {
            Intent intent = new Intent(this, toCls);
            intent.putExtra("title", getString(R.string.title_technical_seminar));
            startActivity(intent);
            overridePendingTransPad();
        }
    }
}
