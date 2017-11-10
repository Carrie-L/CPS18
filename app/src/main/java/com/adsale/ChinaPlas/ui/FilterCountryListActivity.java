package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.CountryAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityFilterCountryBinding;
import com.adsale.ChinaPlas.ui.view.SideLetter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.R.id.language;

/**
 * Created by Carrie on 2017/10/17.
 * 国家列表
 */

public class FilterCountryListActivity extends BaseActivity implements SideLetter.OnLetterClickListener {
    private RecyclerView recyclerView;
    private ArrayList<Country> mList = new ArrayList<>();
    private ArrayList<ExhibitorFilter> filters;
    public final ObservableField<String> dialogLetter = new ObservableField<>("");
    private RecyclerViewScrollTo mRVScrollTo;
    private SideLetter sideLetter;
    private FilterRepository mRepository;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        int size = mList.size();
        Country entity;
        for (int i = 0; i < size; i++) {
            entity = mList.get(i);
            entity.selected.set(false);
            mList.set(i, entity);
        }
        entity = null;
        mList.clear();
        finish();
    }

    @Override
    protected void initView() {
        ActivityFilterCountryBinding binding = ActivityFilterCountryBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        recyclerView = binding.countryRecyclerView;
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        sideLetter = binding.sideLetter;
    }

    @Override
    protected void initData() {
        mRepository = FilterRepository.getInstance();
        mRepository.initCountryDao();
        mList = mRepository.getCountries(App.mLanguage.get());

        filters = new ArrayList<>();
        CountryAdapter adapter = new CountryAdapter(mList, filters);
        recyclerView.setAdapter(adapter);

        setupSideLetter();
    }

    public void setupSideLetter() {
        ArrayList<String> letters = mRepository.getCountryLetters();
        sideLetter.setList(letters);
        sideLetter.setOnLetterClickListener(this);
        mRVScrollTo = new RecyclerViewScrollTo(layoutManager, recyclerView);
    }

    @Override
    public void onClick(String letter) {
        dialogLetter.set(letter);
        scrollTo(letter);
    }

    private void scrollTo(String letter) {
        int size = mList.size();
        for (int j = 0; j < size; j++) {
            if (mList.get(j).getSort().equals(letter)) {
                mRVScrollTo.scroll(j);
                break;
            }
        }
    }

    private void setResultData() {
        Intent intent = new Intent();
        intent.putExtra("data", filters);
        LogUtil.i(TAG, "onDestroy::filters=" + filters.size() + "," + filters.toString());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        setResultData();
        super.onBackPressed();
    }

    @Override
    public void back() {
        setResultData();
        super.back();
    }




}
