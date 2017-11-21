package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.adsale.ChinaPlas.adapter.MyExhibitorAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.ui.view.SideLetter;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/19.
 */

public class MyExhibitorViewModel implements SideLetter.OnLetterClickListener, SyncViewModel.SyncCallback {
    public final ObservableField<String> dialogLetter = new ObservableField<>();
    public final ObservableBoolean isNoData = new ObservableBoolean();
    public ArrayList<Exhibitor> mExhibitors = new ArrayList<>();
    public ArrayList<String> mLetters = new ArrayList<>();

    private Context mContext;
    private SideLetter mSideLetter;
    private RecyclerViewScrollTo mScrollTo;
    private ExhibitorRepository mRepository;
    private MyExhibitorAdapter mAdapter;
    private SyncViewModel syncViewModel;

    public MyExhibitorViewModel(Context context) {
        this.mContext = context;

    }

    public void start(SideLetter sideLetter, RecyclerViewScrollTo scrollTo) {
        this.mSideLetter = sideLetter;
        this.mScrollTo = scrollTo;
        mRepository = ExhibitorRepository.getInstance();
        getMyExhibitors();
        setupSideLetter();
    }

    public void setAdapter(MyExhibitorAdapter adapter) {
        if (mAdapter == null) {
            this.mAdapter = adapter;
        }
    }

    private void getMyExhibitors() {
        mExhibitors.clear();
        mLetters.clear();
        mExhibitors = mRepository.getMyExhibitors(mLetters);
        isNoData.set(mExhibitors.isEmpty());
    }

    private void setupSideLetter() {
        mSideLetter.setList(mLetters);
        mSideLetter.setOnLetterClickListener(this);
    }

    private void refreshUI() {
        mAdapter.setList(mExhibitors);
        mSideLetter.setList(mLetters);
        mSideLetter.invalidate();
    }

    public void sync() {
        if (syncViewModel == null) {
            syncViewModel = new SyncViewModel(mContext);
            syncViewModel.setSyncCallback(this);
        }
        syncViewModel.syncMyExhibitor();
    }

    @Override
    public void onClick(String letter) {
        dialogLetter.set(letter);
        scrollTo(letter);
    }

    private void scrollTo(String letter) {
        int size = mExhibitors.size();
        for (int j = 0; j < size; j++) {
            if (mExhibitors.get(j).getSort().equals(letter)) {
                mScrollTo.scroll(j);
                break;
            }
        }
    }

    @Override
    public void sync(boolean success) {
        if (success) {
            getMyExhibitors();
            refreshUI();
        }
    }
}
