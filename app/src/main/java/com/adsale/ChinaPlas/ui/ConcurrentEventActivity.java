package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ConcurrentEvent;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityEventBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.adsale.ChinaPlas.viewmodel.EventModel;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Carrie on 2017/9/19.
 * 同期活动
 */

public class ConcurrentEventActivity extends BaseActivity implements OnIntentListener {

    private RecyclerView recyclerView;
    private EventModel mEventModel;
    private ActivityEventBinding binding;
    private int adHeight;
    private HelpView helpDialog;
    private FloatingActionButton btnFilter;
    private Disposable mEventDisposable;


    @Override
    protected void initView() {
        binding = ActivityEventBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mEventModel = new EventModel();
        binding.setEventModel(mEventModel);
        binding.setAty(this);
        recyclerView = binding.rvEvent;
        btnFilter = binding.btnFilter;
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
//        params.topMargin = DisplayUtil.dip2px(getApplicationContext(), 48) + 2;
        params.bottomMargin = adHeight;
        cardView.setLayoutParams(params);

        EventAdapter adapter = new EventAdapter(mEventModel.events, this);
        if (AppUtil.isTablet()) {
            adapter.setItemSize(AppUtil.getScreenWidth() - cardWidth);
        }
//        adapter.setItemSize(AppUtil.getScreenWidth() - cardWidth);
        recyclerView.setAdapter(adapter);
        mEventModel.onStart(this, adapter);

//        showAd();

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

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls != null) {
            LogUtil.i(TAG, "toCls= " + toCls.getSimpleName());
        } else {
            return;
        }
        if (toCls.getSimpleName().equals("WebContentActivity")) {
            ConcurrentEvent entity1 = (ConcurrentEvent) entity;
            toEventDtl(entity1);
        } else if (toCls.getSimpleName().equals("TechnicalListActivity")) {
            //2019zszs 暂不跳转技术交流会
            Intent intent = new Intent(this, TechnicalListActivity.class);
            intent.putExtra("title", getString(R.string.title_technical_seminar));
            LogUtil.i(TAG, "跳入计较:" + mEventModel.convertToTechDateIndex(((ConcurrentEvent) entity).getDate()));
            intent.putExtra("index", mEventModel.convertToTechDateIndex(((ConcurrentEvent) entity).getDate()));
            startActivity(intent);
            overridePendingTransPad();
        } else if (toCls.getSimpleName().equals("FilterApplicationListActivity")) {
//            mEventModel.mClickPos.set(0);

            if (mEventModel.isFiltering.get()) {
                btnFilter.setBackgroundColor(getResources().getColor(R.color.home));
                btnFilter.setImageResource(R.drawable.ic_filter_blank);
            } else {
                btnFilter.setBackgroundColor(getResources().getColor(R.color.white_smoke));
                btnFilter.setImageResource(R.drawable.ic_filter_solid);
            }


            Intent intent = new Intent(this, toCls);
            intent.putExtra("title", getString(R.string.filter_application));
            intent.putExtra("MainTypeId", FilterApplicationListActivity.TYPE_EVENT_APPLICATIONS);
            startActivityForResult(intent, 1);
            overridePendingTransPad();
        }
    }

    private ConcurrentEvent mEntity;

    private void toEventDtl(ConcurrentEvent entity) {
        mEntity = entity;
        LogUtil.i(TAG, "onItemClick: id= " + entity.getPageID());
        if (entity.getPageFileName() != null && !entity.getPageFileName().equals("-")) {
            if (isEventHtmlExists()) {
                intentToWebContent();
            } else {
                downSingleEventZip();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nodata), Toast.LENGTH_SHORT).show();
        }
    }

    private void intentToWebContent() {
        Intent intent = new Intent(this, WebContentActivity.class);
        intent.putExtra("title", mEntity.getTitle());
        intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(mEntity.getEventID()));  // 用EventID打开文件夹
        intent.putExtra("ConcurrentEvent", mEntity);  // 用EventID打开文件夹
        LogUtil.i(TAG, "PageID=" + mEntity.getPageID());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }

    private boolean isEventHtmlExists() {
        StringBuilder sHtml = new StringBuilder();
        sHtml.append(App.rootDir).append("/ConcurrentEvent/").append(mEntity.getEventID()).append(AppUtil.getName("TC.html", "EN.html", "SC.html"));
        if (new File(sHtml.toString()).exists()) {
            return true;
        } else if (AppUtil.isFileInAsset("ConcurrentEvent", mEntity.getEventID())) {
            return true;
        }
        return false;
    }

    private void downSingleEventZip() {
        LogUtil.i(TAG, "都不存在，下载❤ " + mEntity.getPageFileName());
        final String eventDir = App.rootDir + "ConcurrentEvent/";
        final OtherRepository otherRepository = OtherRepository.getInstance();
        if (!new File(eventDir).exists()) {
            new File(eventDir).mkdir();
            AppUtil.copyCSSToConcurrent();
        }
        DownloadClient client = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.DOWNLOAD_BASE_URL);
        client.downloadEventPage(mEntity.getPageFileName())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        boolean isUnZiped = FileUtil.unpackZip(mEntity.getPageFileName(), responseBody.byteStream(), eventDir);
                        responseBody.close();
                        LogUtil.i(TAG, "解压同期活动：" + mEntity.getPageFileName() + " > " + isUnZiped);
                        otherRepository.updateEventIsDown(mEntity.getPageID(), !isUnZiped);
                        mEntity.setEventPageFileName();   // zip包下载解压成功，更新本地PageFileName的值。
                        return isUnZiped;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mEventDisposable = d;
                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "zip解压结果：" + value);
                        if (value) {
                            intentToWebContent();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "downSingleEventZip: onError= " + e.getMessage());
                        Toast.makeText(getApplicationContext(), getString(R.string.nodata), Toast.LENGTH_SHORT).show();
                        dispose();
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });

    }

    private void dispose() {
        if (mEventDisposable != null && !mEventDisposable.isDisposed()) {
            mEventDisposable.dispose();
        }
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


}
