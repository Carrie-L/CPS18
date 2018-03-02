package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.InterestedExhibitorAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.InterestedExhibitor;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerItemDecoration;

import java.util.ArrayList;

/**
 * 展商分布
 */
public class FloorDistributeActivity extends BaseActivity implements OnIntentListener {

    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.my_exhibt_on_map));
        getLayoutInflater().inflate(R.layout.activity_floor_distribute, mBaseFrameLayout, true);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.floor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
    }

    @Override
    protected void initData() {


//        InterestedExhibitorAdapter adapter = new InterestedExhibitorAdapter();

        ArrayList<InterestedExhibitor> list = new ArrayList<>();
        ArrayList<InterestedExhibitor> floors = new ArrayList<>();
        FloorRepository repository = FloorRepository.getInstance();
        floors = repository.getFloorIDLists();
        floors = repository.getMyExhibitorFloor(floors);
        LogUtil.i(TAG, "floors=" + floors.toString());

        InterestedExhibitorAdapter adapter = new InterestedExhibitorAdapter(floors, this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        InterestedExhibitor ie = (InterestedExhibitor) entity;
        Intent intent = new Intent(this,toCls);
        intent.putExtra("HALL",ie.floorID);
        startActivity(intent);
        overridePendingTransPad();
    }
}
