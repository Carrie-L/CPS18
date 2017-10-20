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
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorViewModel {
    private final String TAG="ExhibitorViewModel";
    public final ObservableBoolean noData = new ObservableBoolean(true);
    public final ObservableField<String> etFilter = new ObservableField<>();
    public final ObservableField<String> dialogLetter = new ObservableField<>();
    public final ObservableArrayList<Exhibitor> exhibitors = new ObservableArrayList<>();
    /**
     * 当前是否以拼音字母排序，true是；false hall排序。
     */
    public final ObservableBoolean isSortAZ = new ObservableBoolean(true);

    private ArrayList<Exhibitor> mExhibitorCaches = new ArrayList<>();
    public ArrayList<String> letters = new ArrayList<>();
    private ArrayList<String> mSideCaches = new ArrayList<>();

    private Context mContext;
    private ExhibitorRepository mExhibitorRepo;

    public boolean move = false;
    private RecyclerViewScrollTo mRVScrollTo;
    private ExhibitorAdapter adapter;
    private ArrayList<Exhibitor> searchTemps = new ArrayList<>();
    private OnIntentListener mListener;

    public ExhibitorViewModel(Context mContext, ExhibitorRepository repository, OnIntentListener listener) {
        this.mContext = mContext;
        this.mExhibitorRepo = repository;
        this.mListener = listener;
    }

    public ArrayList<Exhibitor> getAllExhibitors() {
        exhibitors.clear();
        LogUtil.i("---> getAllExhibitors before ", "mExhibitorCaches=" + mExhibitorCaches.size() + ",exhibitors=" + exhibitors.size());
        if (mExhibitorCaches.isEmpty()) {
            exhibitors.addAll(mExhibitorRepo.getDataTest());
            mExhibitorCaches.addAll(exhibitors);
        } else {
            exhibitors.addAll(mExhibitorCaches);
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
        exhibitors.addAll(mExhibitorRepo.getExhibitorSearchResults(searchTemps, text));
        letters.addAll(mExhibitorRepo.getSearchedLetters(text));
        adapter.setList(exhibitors);
    }

    public void setLayoutManager(RecyclerViewScrollTo scrollTo, ExhibitorAdapter adapter) {
        mRVScrollTo = scrollTo;
        this.adapter=adapter;
    }

    public void getAllLetters() {
        letters.clear();
        if (mSideCaches.size() > 0) {
            letters.addAll(mSideCaches);
        } else {
            letters.addAll(mExhibitorRepo.getSideList(mExhibitorRepo.getAllSideSQL()));
            mSideCaches.addAll(letters);
        }
        LogUtil.i(TAG, "letters=" + letters.size() + ",letters=" + letters.toString());
    }




    public void onImgFilter() {
        mListener.onIntent(null,ExhibitorFilterActivity.class);
    }


    public void onSortAZ() {
        isSortAZ.set(true);
    }

    public void onSortHall() {
        isSortAZ.set(false);
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
