package com.adsale.ChinaPlas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ExhibitorAdapter;
import com.adsale.ChinaPlas.adapter.SideAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.SideBar;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorAllListBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener;
import com.adsale.ChinaPlas.viewmodel.ExhibitorViewModel;

import java.util.ArrayList;


/**
 * 全部参展商列表
 */
public class ExhibitorAllListActivity extends BaseActivity {
    private ExhibitorViewModel mExhibitorModel;
    private ActivityExhibitorAllListBinding binding;
    private ExhibitorRepository mRepository;
    private RecyclerView rvExhibitors;
    private int dateIndex = -1;

    @Override
    protected void initView() {
        binding = ActivityExhibitorAllListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);

    }

    @Override
    protected void initData() {
        mRepository = ExhibitorRepository.getInstance();
        mExhibitorModel = new ExhibitorViewModel(getApplicationContext(), mRepository);
        binding.setExhibitorModel(mExhibitorModel);

        dateIndex = getIntent().getIntExtra("dateIndex", 0);

        setExhibitorList();
        setSideList();


        EditText etFilter = binding.editFilter;
        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.i(TAG, "onTextChanged: s=" + s.toString() + ",start=" + start + ",before=" + before + ",count=" + count);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.i(TAG, "beforeTextChanged: s=" + s.toString() + ",start=" + start + ",after=" + after + ",count=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.i(TAG, "afterTextChanged: s=" + s.toString());

                if (TextUtils.isEmpty(s.toString())) {
                    mExhibitorModel.resetList();
                } else {
                    mExhibitorModel.search(s.toString().trim());
                }
            }
        });

    }

    private void setExhibitorList() {
        rvExhibitors = binding.rvKs;
        rvExhibitors.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvExhibitors.setLayoutManager(layoutManager);

        ExhibitorAdapter adapter = new ExhibitorAdapter(this, new ArrayList<Exhibitor>(0));
        rvExhibitors.setAdapter(adapter);

        mExhibitorModel.getAllExhibitors();

        mExhibitorModel.setLayoutManager(layoutManager, rvExhibitors);

        onItemClick();

    }

    private void setSideList() {
        RecyclerView rvSideBar = binding.rvSideBar;
        rvSideBar.setHasFixedSize(true);
        rvSideBar.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        int height = App.mScreenHeight;
        int searchEditHeight = DisplayUtil.dip2px(getApplicationContext(), 48);
        int bannerHeight = DisplayUtil.dip2px(getApplicationContext(), 56);
        int sideHeight = height - searchEditHeight - bannerHeight;
        LogUtil.i(TAG, "searchEditHeight=" + searchEditHeight + ",bannerHeight=" + bannerHeight + ", sideHeight=" + sideHeight);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rvSideBar.getLayoutParams();
        params.height = sideHeight;
        rvSideBar.setLayoutParams(params);


        SideAdapter adapter = new SideAdapter(this, new ArrayList<SideBar>(0), sideHeight);
        rvSideBar.setAdapter(adapter);

        mExhibitorModel.getAllLetters();
    }

    private void onItemClick() {
        rvExhibitors.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rvExhibitors, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onItemClick(View view, int position) {
                if (dateIndex!=-1) {
                    Intent intent = new Intent(ExhibitorAllListActivity.this,ScheduleEditActivity.class);
                    intent.putExtra(Constant.EXHIBITOR, mExhibitorModel.exhibitors.get(position));
                    intent.putExtra("dateIndex",dateIndex);
                    startActivity(intent);
                    finish();
                }

            }
        }));
    }

    public void onLetterClick(String letter) {
        Toast.makeText(this, letter, Toast.LENGTH_SHORT).show();

        mExhibitorModel.scrollTo(letter);

    }

}
