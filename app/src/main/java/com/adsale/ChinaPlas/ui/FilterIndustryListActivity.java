package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.IndustryAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityFilterIndustryBinding;
import com.adsale.ChinaPlas.ui.view.SideLetter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 * 产品类别列表
 */

public class FilterIndustryListActivity extends BaseActivity implements SideLetter.OnLetterClickListener {
    private RecyclerView recyclerView;
    public final ObservableArrayList<Industry> industries = new ObservableArrayList<>();
    private ArrayList<Industry> industryCaches = new ArrayList<>();
    private FilterRepository mRepository;
    private IndustryAdapter adapter;
    private ArrayList<ExhibitorFilter> filters;
    private SideLetter sideLetter;
    private LinearLayoutManager layoutManager;
    public final ObservableField<String> dialogLetter = new ObservableField<>("");
    private RecyclerViewScrollTo mRVScrollTo;

    @Override
    protected void initView() {
        ActivityFilterIndustryBinding binding = ActivityFilterIndustryBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        sideLetter = binding.sideLetter;
        recyclerView = binding.industryRecyclerView;
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        binding.etFilterIndustry.addTextChangedListener(filterWatcher);
    }

    @Override
    protected void initData() {
        mRepository = FilterRepository.getInstance();
        mRepository.initIndustryDao();

        getIndustries();
        filters = new ArrayList<>();
        adapter = new IndustryAdapter(industries, filters);
        recyclerView.setAdapter(adapter);

        setupSideLetter();
    }

    public void setupSideLetter() {
        ArrayList<String> letters = mRepository.getIndustryLetters();
        sideLetter.setList(letters);
        sideLetter.setOnLetterClickListener(this);
        mRVScrollTo = new RecyclerViewScrollTo(layoutManager, recyclerView);
    }

    private void getIndustries() {
        industries.clear();
        if (industryCaches.isEmpty()) {
            LogUtil.i(TAG, "getIndustries:industryCaches.isEmpty()");
            industryCaches = mRepository.getIndustries(App.mLanguage.get());
        } else {
            LogUtil.i(TAG, "getIndustries:industryCaches.is not Empty()");
        }
        industries.addAll(industryCaches);
        LogUtil.i(TAG, "industryCaches=" + industryCaches.size() + "," + industryCaches.toString());
    }

    TextWatcher filterWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            industries.clear();
            industries.addAll(mRepository.getSearchIndustries(s.toString()));
            adapter.setList(industries);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        industries.clear();
        industryCaches.clear();
        finish();
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

    @Override
    public void onClick(String letter) {
        dialogLetter.set(letter);
        scrollTo(letter);
    }

    private void scrollTo(String letter) {
        int size = industries.size();
        for (int j = 0; j < size; j++) {
            if (industries.get(j).getSort().equals(letter)) {
                mRVScrollTo.scroll(j);
                break;
            }
        }
    }
}
