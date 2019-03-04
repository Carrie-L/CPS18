package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.HistoryExhibitorAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.ui.view.CpsRecyclerView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by new on 2016/10/13.
 * 歷史記錄
 */
public class ExhibitorHistoryActivity extends BaseActivity {
    protected static final String TAG = "ExhibitorHistoryActivity";
    private ArrayList<HistoryExhibitor> histories = new ArrayList<>();
    private ArrayList<HistoryExhibitor> todayHistories = new ArrayList<>();
    private ArrayList<HistoryExhibitor> yesterdayHistories = new ArrayList<>();
    private ArrayList<HistoryExhibitor> pastHistories = new ArrayList<>();
    protected CpsRecyclerView mRecyclerView;

    private HistoryExhibitorAdapter adapter;

    /**
     * 是否经历跳转
     */
    private boolean isIntent = false;
    private ExhibitorRepository mRepository;

    public void initView() {
        getLayoutInflater().inflate(R.layout.activity_history_exhibitor, mBaseFrameLayout);
        barTitle.set(getString(R.string.title_history_exhibitor));
        mRecyclerView = findViewById(R.id.recycler_view2);
    }

    public void initData() {
        mRepository = ExhibitorRepository.getInstance();
        generateList();

//        adapter = new ExhibitorHistoryAdapter(getApplicationContext(), histories);
        adapter = new HistoryExhibitorAdapter(histories);
        mRecyclerView.setCpsAdapter(adapter);
        mRecyclerView.setOnItemClickListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onItemClick(View view, int position) {
                isIntent = true;

                Intent intent = new Intent(getApplicationContext(), ExhibitorDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("CompanyID", histories.get(position).getCompanyID());
                intent.putExtra("from", "exhibitorHistory");
                startActivity(intent);
                overridePendingTransPad();
            }
        });
    }

    private void generateList() {
        histories.clear();
        todayHistories.clear();
        yesterdayHistories.clear();
        pastHistories.clear();

        LogUtil.i(TAG,"today="+AppUtil.getTodayDate().trim());
        LogUtil.i(TAG,"today="+AppUtil.getYesterdayDate().trim());

        todayHistories = mRepository.getHistoryFrequency(AppUtil.getTodayDate().trim(), todayHistories, 1);
        yesterdayHistories = mRepository.getHistoryFrequency(AppUtil.getYesterdayDate().trim(), yesterdayHistories, 0);
        pastHistories = mRepository.getHistoryFrequency(AppUtil.getYesterdayDate().trim(), pastHistories, -1);

        histories.addAll(todayHistories);
        histories.addAll(yesterdayHistories);
        histories.addAll(pastHistories);

        LogUtil.i(TAG, "histories=$$$$$$=" + histories.size());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isIntent) {
            generateList();
            adapter.setList(histories);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroy() {
        todayHistories.clear();
        todayHistories = null;
        yesterdayHistories.clear();
        yesterdayHistories = null;
        pastHistories.clear();
        pastHistories = null;
        histories.clear();
        histories = null;
        super.onDestroy();
    }

}
