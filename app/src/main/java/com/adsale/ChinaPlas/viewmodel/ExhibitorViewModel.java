package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.adsale.ChinaPlas.adapter.ExhibitorAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.ExhibitorFilterActivity;
import com.adsale.ChinaPlas.ui.view.SideLetter;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Carrie on 2017/8/12.
 * Exhibitor All List ViewModel
 */

public class ExhibitorViewModel {
    private final String TAG = "ExhibitorViewModel";
    public final ObservableBoolean noData = new ObservableBoolean(true);
    public final ObservableField<String> etFilter = new ObservableField<>();
    public final ObservableField<String> dialogLetter = new ObservableField<>();
    public final ObservableArrayList<Exhibitor> exhibitors = new ObservableArrayList<>();
    /**
     * 当前是否以拼音字母排序，true是；false hall排序。
     */
    public final ObservableBoolean isSortAZ = new ObservableBoolean(true);

    public ArrayList<String> letters = new ArrayList<>();
    private ArrayList<Exhibitor> mExhibitorCaches = new ArrayList<>();
    private ArrayList<String> mSideCaches = new ArrayList<>();
    /**
     * 以Hall排序列表缓存
     */
    private ArrayList<Exhibitor> mExhibitorHallCaches = new ArrayList<>();
    /**
     * 以Hall排序SideLetter缓存
     */
    private ArrayList<String> mSideHallCaches = new ArrayList<>();

    private Context mContext;
    private ExhibitorRepository mExhibitorRepo;

    private RecyclerViewScrollTo mRVScrollTo;
    private ExhibitorAdapter adapter;
    private SideLetter sideLetter;
    private ArrayList<Exhibitor> searchTemps = new ArrayList<>();
    private OnIntentListener mListener;

    public ExhibitorViewModel(Context mContext, ExhibitorRepository repository, OnIntentListener listener) {
        this.mContext = mContext;
        this.mExhibitorRepo = repository;
        this.mListener = listener;
    }

    public ArrayList<Exhibitor> getAllExhibitors() {
        exhibitors.clear();
        letters.clear();
        LogUtil.i("---> getAllExhibitors before ", "mExhibitorCaches=" + mExhibitorCaches.size() + ",exhibitors=" + exhibitors.size());
        if (mExhibitorCaches.isEmpty()) {
            exhibitors.addAll(mExhibitorRepo.getAllExhibitors(letters));
            mExhibitorCaches.addAll(exhibitors);
            mSideCaches.addAll(letters);
        } else {
            exhibitors.addAll(mExhibitorCaches);
            letters.addAll(mSideCaches);
        }
        LogUtil.i("getAllExhibitors after <---- ", "mExhibitorCaches=" + mExhibitorCaches.size() + ",exhibitors=" + exhibitors.size());

        noData.set(exhibitors.isEmpty());

        return exhibitors;
    }

    public void resetList() {
        exhibitors.clear();
        exhibitors.addAll(mExhibitorCaches);

        letters.clear();
        letters.addAll(mSideCaches);
    }

    public void search(String text) {
        exhibitors.clear();
        letters.clear();
        searchTemps.clear();

        searchTemps.addAll(mExhibitorCaches);
        exhibitors.addAll(mExhibitorRepo.getExhibitorSearchResults(searchTemps, letters, text));
        adapter.setList(exhibitors);
    }

    public void setLayoutManager(RecyclerViewScrollTo scrollTo, ExhibitorAdapter adapter, SideLetter sideLetter) {
        mRVScrollTo = scrollTo;
        this.adapter = adapter;
        this.sideLetter=sideLetter;
    }

    public void refreshSideLetter(){
        sideLetter.setList(letters);
        sideLetter.refresh();
    }

    public void onImgFilter() {
        mListener.onIntent(null, ExhibitorFilterActivity.class);
    }

    public void resetCache() {
        mExhibitorCaches.clear();
        mExhibitorCaches.addAll(exhibitors);
        mSideCaches.clear();
        mSideCaches.addAll(letters);
    }

    public void onSortAZ() {
        isSortAZ.set(true);
        exhibitors.clear();
        letters.clear();
        exhibitors.addAll(mExhibitorCaches);
        letters.addAll(mSideCaches);
        adapter.setList(exhibitors);
        refreshSideLetter();
    }

    /**
     * todo 还剩返回的筛选列表的 Hall排序
     */
    public void onSortHall() {
        isSortAZ.set(false);
        exhibitors.clear();
        letters.clear();
        if (mExhibitorHallCaches.isEmpty()) {
            mExhibitorHallCaches.addAll(mExhibitorRepo.sortByHallList(mSideHallCaches));
        }
        LogUtil.i(TAG, "mExhibitorHallCaches= " + mExhibitorHallCaches.size());
        exhibitors.addAll(mExhibitorHallCaches);
        letters.addAll(mSideHallCaches);
        adapter.setList(exhibitors);
        refreshSideLetter();
    }

    public void scrollTo(String letter) {
        int size = exhibitors.size();
        for (int j = 0; j < size; j++) {
            if (exhibitors.get(j).getSort().equals(letter)) {
                mRVScrollTo.scroll(j);
                break;
            }
        }
    }
}
