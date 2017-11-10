package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.HotelAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.model.AgentInfo;
import com.adsale.ChinaPlas.helper.CSVHelper;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener;

import java.util.ArrayList;

public class TravelInfoActivity extends BaseActivity {

    private Intent intent;
    private final String TAG = "TravelInfoActivity";
    protected RecyclerView recyclerView;
    private ArrayList<AgentInfo> hrInfos;

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_travel_info, mBaseFrameLayout);
        recyclerView = (RecyclerView) findViewById(R.id.travel_recycler_view);
    }

    @Override
    protected void initData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        CSVHelper csvHelper = new CSVHelper(getApplicationContext());
        final ArrayList<AgentInfo> list = csvHelper.getHotelDetailsCsv();
        HotelAdapter adapter = new HotelAdapter(list, this);
        recyclerView.setAdapter(adapter);
        hrInfos = new ArrayList<>();
        hrInfos = csvHelper.getHrHotelDetailCSV();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {


            @Override
            public void onItemClick(View view, int position) {
                if (App.mLanguage.get() == 1 && list.get(position).titleENG.contains("Endorsed")) {
                    intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra("title", list.get(position).titleENG);
                    intent.putExtra("WebUrl", list.get(position).getWebsiteENG());
                } else {
                    intent = new Intent(getApplicationContext(), TravelDetailActivity.class);
                    intent.putExtra("title", getString(R.string.title_travel_info));
                    if (list.get(position).titleENG.contains("Official Travel Agency") || list.get(position).titleSC.contains("推介酒店")) {
                        intent.putExtra("Info", hrInfos.get(0));
                    } else {
                        intent.putExtra("Info", list.get(position));
                    }
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransPad();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }


}
