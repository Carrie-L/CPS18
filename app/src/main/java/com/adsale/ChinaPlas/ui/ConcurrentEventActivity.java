package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityEventBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.EventModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.R.attr.id;


/**
 * Created by Carrie on 2017/9/19.
 * 同期活动
 */

public class ConcurrentEventActivity extends BaseActivity implements OnIntentListener {

    private RecyclerView recyclerView;
    private EventModel mEventModel;
    private ActivityEventBinding binding;
    private ConcurrentEvent.AdInfo adInfo;

    @Override
    protected void initView() {
        binding = ActivityEventBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mEventModel = new EventModel();
        binding.setEventModel(mEventModel);
        binding.setAty(this);
        recyclerView = binding.rvEvent;
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));

        mEventModel.getList();
        EventAdapter adapter = new EventAdapter(mEventModel.events, this);
        recyclerView.setAdapter(adapter);
        mEventModel.onStart(this, adapter);

        showAd();

    }

    public void showAd() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivAd.getLayoutParams();
        int screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        params.width = screenWidth;
        params.height = AppUtil.getCalculatedHeight(Constant.M3_WIDTH, Constant.M3_HEIGHT);
        binding.ivAd.setLayoutParams(params);

        adInfo = mEventModel.event.AdInfo;
        Glide.with(getApplicationContext()).load(adInfo.getImageUrl()).into(binding.ivAd);
    }

    public void onAdClick() {
        toEventDtl(adInfo.getEventID(), "");
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls.getSimpleName().equals("WebContentActivity")) {
            ConcurrentEvent.Pages pages = (ConcurrentEvent.Pages) entity;
            toEventDtl(pages.pageID, pages.getTitle());
        } else if (toCls.getSimpleName().equals("TechnicalListActivity")) {
            Intent intent = new Intent(this, toCls);
            intent.putExtra("title", getString(R.string.title_technical_seminar));
            intent.putExtra("index", (String) entity);
            startActivity(intent);
            overridePendingTransPad();
        } else if (toCls.getSimpleName().equals("FilterApplicationListActivity")) {
            mEventModel.mClickPos.set(0);
            Intent intent = new Intent(this, toCls);
            intent.putExtra("title", getString(R.string.filter_application));
            startActivityForResult(intent, 1);
            overridePendingTransPad();
        }
    }

    private void toEventDtl(String eventId, String title) {
        LogUtil.i(TAG, "onItemClick: id= " + eventId);
        Intent intent = new Intent(this, WebContentActivity.class);
        intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(eventId));
        if (TextUtils.isEmpty(title)) {
            ArrayList<ConcurrentEvent.Pages> pages = mEventModel.event.pages;
            int size = pages.size();
            for (int i = 0; i < size; i++) {
                if (pages.get(i).pageID.equals(eventId)) {
                    title = pages.get(i).getTitle();
                    break;
                }
            }
        }
        intent.putExtra("title", title);
        LogUtil.i(TAG, "title=" + title);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "onActivityResult:requestCode=" + requestCode);

        ArrayList<ExhibitorFilter> filters = data.getParcelableArrayListExtra("data");
        LogUtil.i(TAG, "onActivityResult::filters=" + filters.size() + "," + filters.toString());

        int size = filters.size();
        String words = "";
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            words = words.concat(filters.get(i).filter);
            ids.add(filters.get(i).id);
            if (ids.size() < size) {
                words = words.concat("、");
            }
        }
        LogUtil.i(TAG, "words=" + words);
        mEventModel.filterWords.set(words);
        mEventModel.filterEvent(ids);
    }

}
