package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.ui.ExhibitorFilterActivity;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static android.R.id.list;
import static android.R.transition.move;
import static android.content.ContentValues.TAG;

/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorViewModel {
    public final ObservableBoolean noData = new ObservableBoolean(true);
    public final ObservableField<String> etFilter = new ObservableField<>();
    public final ObservableField<String> indexText = new ObservableField<>();
    public final ObservableArrayList<Exhibitor> exhibitors = new ObservableArrayList<>();
    public final ObservableArrayList<SideLetter> letters = new ObservableArrayList<>();
    /**
     * 当前是否以拼音字母排序，true是；false hall排序。
     */
    public final ObservableBoolean isSortAZ = new ObservableBoolean(true);

    private ArrayList<Exhibitor> mExhibitorCaches = new ArrayList<>();
    private ArrayList<SideLetter> mSideCaches = new ArrayList<>();

    private Context mContext;
    private ExhibitorRepository mExhibitorRepo;

    public boolean move = false;
    public LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private boolean isSmothScroller = false;
    private ArrayList<Exhibitor> searchTemps = new ArrayList<>();

    public ExhibitorViewModel(Context mContext, ExhibitorRepository repository) {
        this.mContext = mContext;
        this.mExhibitorRepo = repository;
    }

    public ArrayList<Exhibitor> getAllExhibitors() {
        exhibitors.clear();
        LogUtil.i("---> getAllExhibitors before ", "mExhibitorCaches=" + mExhibitorCaches.size() + ",exhibitors=" + exhibitors.size());
        if (mExhibitorCaches.isEmpty()) {
            exhibitors.addAll(mExhibitorRepo.getData());
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
    }

    public void setLayoutManager(LinearLayoutManager layoutManager, RecyclerView recyclerView) {
        mLayoutManager = layoutManager;
        this.recyclerView = recyclerView;
    }

    public void getAllLetters() {
        letters.clear();
        if (mSideCaches.size() > 0) {
            letters.addAll(mSideCaches);
        } else {
            letters.addAll(mExhibitorRepo.getAllExhiLetters());
            mSideCaches.addAll(letters);
        }
        LogUtil.i(TAG, "letters=" + letters.size() + ",letters=" + letters.toString());
    }

    public void scrollTo(String letter) {
        int size = exhibitors.size();
        for (int j = 0; j < size; j++) {
            if (exhibitors.get(j).getSort().equals(letter)) {
                if (isSmothScroller) {
                    smoothScroller(recyclerView, j);
                } else {
                    scroll(recyclerView, j);
                }
                break;
            }
        }
    }

    /**
     * 定位到当前位置，并置顶
     *
     * @param recyclerView
     * @param position     <font color="#f97798">如点击了排序列表的C，position为C在展商列表中的位置</font>
     * @return void
     * @version 创建时间：2016年7月22日 上午11:24:51
     */
    public void smoothScroller(RecyclerView recyclerView, final int position) {
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
//		LogUtil.i(TAG, "position=" + position + ",firstItem=" + firstItem + ",lastItem=" + lastItem);

        if (position <= firstItem) {// 当要定位的项在当前可见第一项的前面(比如当前第一位在C，要去A)，则可直接移动到最前并置顶
            recyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            int top = recyclerView.getChildAt(position - firstItem).getTop();
//			LogUtil.i(TAG, "top=" + top);
            recyclerView.smoothScrollBy(0, top);
        } else {// 如果要显示的项在当前最后一项的后面，（如当前最后一项为D，要去E），则移动，并置顶显示
            recyclerView.smoothScrollToPosition(position);
            move = true;
        }

        //滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    move = false;
                    int i = position - mLayoutManager.findFirstVisibleItemPosition();
                    if (i >= 0 && i < recyclerView.getChildCount()) {
                        int top = recyclerView.getChildAt(i).getTop();
                        recyclerView.smoothScrollBy(0, top);
                    }
                }
            }
        });
    }

    public void scroll(RecyclerView recyclerView, final int position) {
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
//		LogUtil.i(TAG, "position=" + position + ",firstItem=" + firstItem + ",lastItem=" + lastItem);

        if (position <= firstItem) {// 当要定位的项在当前可见第一项的前面(比如当前第一位在C，要去A)，则可直接移动到最前并置顶
            recyclerView.scrollToPosition(position);
        } else if (position <= lastItem) {
            int top = recyclerView.getChildAt(position - firstItem).getTop();
//			LogUtil.i(TAG, "top=" + top);
            recyclerView.smoothScrollBy(0, top);
        } else {// 如果要显示的项在当前最后一项的后面，（如当前最后一项为D，要去E），则移动，并置顶显示
            recyclerView.scrollToPosition(position);
            move = true;
        }

        //滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    move = false;
                    int i = position - mLayoutManager.findFirstVisibleItemPosition();
                    if (i >= 0 && i < recyclerView.getChildCount()) {
                        int top = recyclerView.getChildAt(i).getTop();
                        recyclerView.smoothScrollBy(0, top);
                    }
                }
            }
        });
    }

    public void onImgFilter() {
        Intent intent =new Intent(mContext, ExhibitorFilterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    public void onSortAZ() {
        isSortAZ.set(true);
    }

    public void onSortHall() {
        isSortAZ.set(false);
    }


}
