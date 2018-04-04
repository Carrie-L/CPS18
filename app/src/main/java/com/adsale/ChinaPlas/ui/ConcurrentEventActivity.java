package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.AppContent;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityEventBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
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
    private int adHeight;
    private HelpView helpDialog;

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

        CardView cardView = binding.llLeftBtn;
        int cardWidth = 0;
        if (AppUtil.isTablet()) {
            cardWidth = (int) (AppUtil.getScreenWidth() * 0.17f);
        } else {
//            cardWidth = AppUtil.getScreenWidth() - (520 * DisplayUtil.dip2px(getApplicationContext(), 140) / 232);
            cardWidth = (int) (AppUtil.getScreenWidth() * 0.2f);
        }
        LogUtil.i(TAG, "cardWidth=" + cardWidth);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cardWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.topMargin = DisplayUtil.dip2px(getApplicationContext(), 48) + 2;
        params.bottomMargin = adHeight;
        cardView.setLayoutParams(params);

        EventAdapter adapter = new EventAdapter(mEventModel.events, this);
//        if(AppUtil.isTablet()){
//            adapter.setItemSize(AppUtil.getScreenWidth()-cardWidth);
//        }
        adapter.setItemSize(AppUtil.getScreenWidth() - cardWidth);
        adapter.setItemSize(AppUtil.getScreenWidth() - cardWidth);
        recyclerView.setAdapter(adapter);
        mEventModel.onStart(this, adapter);

        showAd();

        if (HelpView.isFirstShow(HelpView.HELP_PAGE_EVENT_LIST)) {
            showHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_EVENT_LIST, HelpView.HELP_PAGE_EVENT_LIST).apply();
        }

        binding.ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpPage();
            }
        });


    }

    private void showHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_EVENT_LIST);
        helpDialog.show(ft, "Dialog");
    }

    public void showAd() {
        adInfo = mEventModel.event.AdInfo;
        if (adInfo.version.equals("0")) {
            binding.ivAd.setVisibility(View.GONE);
            return;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivAd.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        adHeight = AppUtil.getCalculatedHeight(Constant.M3_WIDTH, Constant.M3_HEIGHT);
        params.height = adHeight;
        binding.ivAd.setLayoutParams(params);
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
            Intent intent = new Intent(this, TechnicalListActivity.class);
            intent.putExtra("title", getString(R.string.title_technical_seminar));
            LogUtil.i(TAG, "跳入计较:" + mEventModel.convertToTechDateIndex(((ConcurrentEvent.Pages) entity).date));
            intent.putExtra("index", mEventModel.convertToTechDateIndex(((ConcurrentEvent.Pages) entity).date));
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
        LogUtil.i(TAG, "onItemClick: id= " + eventId + ",title=" + title);
        Intent intent = new Intent(this, WebContentActivity.class);
        intent.putExtra("title", title);
        intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(eventId));
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
        if (size == 0) {
            return;
        }
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

    private void downEventZips() {

    }

}
