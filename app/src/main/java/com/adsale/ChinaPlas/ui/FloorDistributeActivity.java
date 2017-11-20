package com.adsale.ChinaPlas.ui;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.FloorRepository;

/**
 * 展商分布
 */
public class FloorDistributeActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.my_exhibt_on_map));
        getLayoutInflater().inflate(R.layout.activity_floor_distribute,mBaseFrameLayout,true);
        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.floor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {


//        FloorDistributeAdapter adapter = new FloorDistributeAdapter();


    }

    private void getFloors(){
        FloorRepository repository = FloorRepository.getInstance();

    }
}
