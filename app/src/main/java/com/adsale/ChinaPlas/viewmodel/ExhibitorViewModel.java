package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorViewModel {
    /* ------------------------------- Exhibitor All list -------------------------------------- */
    public final ObservableBoolean noData = new ObservableBoolean(true);
    public final ObservableField<String> etFilter = new ObservableField<>();
    public final ObservableField<String> indexText = new ObservableField<>();
    public final ObservableArrayList<Exhibitor> exhibitors = new ObservableArrayList<>();
    public final ObservableArrayList<SideLetter> letters = new ObservableArrayList<>();

    private ArrayList<Exhibitor> mExhibitorCaches = new ArrayList<>();

    private Context mContext;
    private ExhibitorRepository mExhibitorRepo;

    public ExhibitorViewModel(Context mContext, ExhibitorRepository repository) {
        this.mContext = mContext;
        this.mExhibitorRepo = repository;
    }

    public void getAllExhibitors() {
        exhibitors.clear();
        LogUtil.i("---> getAllExhibitors before ","mExhibitorCaches="+mExhibitorCaches.size()+",exhibitors=" +exhibitors.size());
        if (mExhibitorCaches.isEmpty()) {
            exhibitors.addAll(mExhibitorRepo.getData());
            mExhibitorCaches.addAll(exhibitors);
        }else{
            exhibitors.addAll(mExhibitorCaches);
        }
        LogUtil.i("getAllExhibitors after <---- ","mExhibitorCaches="+mExhibitorCaches.size()+",exhibitors=" +exhibitors.size());

        noData.set(exhibitors.isEmpty());
    }

    public void resetList() {
        LogUtil.i("---> resetList before ","mExhibitorCaches="+mExhibitorCaches.size()+",exhibitors=" +exhibitors.size());
        exhibitors.addAll(mExhibitorCaches);
        LogUtil.i("resetList after <---- ","mExhibitorCaches="+mExhibitorCaches.size()+",exhibitors=" +exhibitors.size());
    }

    public void search(){

    }

    public void getAllLetters(){
        letters.addAll(mExhibitorRepo.getAllExhiLetters());
        LogUtil.i(TAG,"letters="+letters.size()+",letters="+letters.toString());
    }


}
