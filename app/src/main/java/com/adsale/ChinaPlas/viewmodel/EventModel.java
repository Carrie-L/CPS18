package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarInfoDao;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.ui.FilterApplicationListActivity;
import com.adsale.ChinaPlas.ui.TechnicalListActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Comparator;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * Created by Carrie on 2017/9/19.
 * 同期活动列表
 */

public class EventModel {
    private static final String TAG = "EventModel";
    public final ObservableArrayList<ConcurrentEvent.Pages> events = new ObservableArrayList<>();
    public final ObservableField<String> filterWords = new ObservableField<>();
    public final ObservableInt mClickPos = new ObservableInt(0);
    public ConcurrentEvent event;
    private DownloadClient mDownClient;
    private EventAdapter adapter;
    private ArrayList<ConcurrentEvent.Pages> mCacheList = new ArrayList<>();
    private ArrayList<ConcurrentEvent.Pages> mCacheList0 = new ArrayList<>();
    private ArrayList<ConcurrentEvent.Pages> mCacheList1;
    private ArrayList<ConcurrentEvent.Pages> mCacheList2;
    private ArrayList<ConcurrentEvent.Pages> mCacheList3;
    private ArrayList<ConcurrentEvent.Pages> mCacheList4;
    private OnIntentListener mListener;
    private ConcurrentEvent.Pages tech;
    private ConcurrentEvent.Pages entity;

    public EventModel() {
        mCacheList1 = new ArrayList<>();
        mCacheList2 = new ArrayList<>();
        mCacheList3 = new ArrayList<>();
        mCacheList4 = new ArrayList<>();
    }

    public void onStart(OnIntentListener listener, EventAdapter adapter) {
        this.adapter = adapter;
        mListener = listener;
    }

    public void getList() {
        parseEvents();
        mCacheList.addAll(events);
        addTech();
        mCacheList0.addAll(events);
    }

    private void addTech() {
        // 隐藏技术交流会item：如果数据库里没有技术交流会数据
        boolean isSeminarEmpty = App.mDBHelper.mSeminarInfoDao.loadAll().isEmpty();
        if (isSeminarEmpty) {
            return;
        }

        for (int i = 0; i < events.size(); i++) {
            entity = events.get(i);
            if (i != 0 && !entity.date.equals(events.get(i - 1).date)) {
                tech = new ConcurrentEvent.Pages();
                tech.date = entity.date; /* 为了循环顺利进行，因此给 date 赋值,且+1.如果不加1，会死循环 */
                tech.pageID = convertToTechDateIndex(events.get(i - 1).date); /* 用于存放 dateIndex 的值 */
                tech.isTypeLabel.set(2);
                events.add(i, tech);
            }
        }
        /* all list; 前面的循环是在（第一个bar除外）每个bar的上方插入[技术交流会],因此只有3个。需要在最后再插入1个28日的。
        *  filter list: 最后一个不一定是最后一天，因此用 events.get(events.size() - 1).date .
        * */
        if (events.size() == 0) {
            return;
        }
        tech = new ConcurrentEvent.Pages();
        int correctDate = Integer.valueOf(events.get(events.size() - 1).date);
        tech.date = correctDate + 1 + ""; /* 技术交流会的date都要加1，避免死循环.因此计算实际index时，要-1. */
        tech.pageID = convertToTechDateIndex(correctDate + "");
        tech.isTypeLabel.set(2);
        events.add(tech);
    }

    /**
     * @param date 实际是 技术交流会的 date index
     */
    private String convertToTechDateIndex(String date) {
        if (date.equals("24")) {
            return "1";
        } else if (date.equals("25")) {
            return "2";
        } else if (date.equals("26")) {
            return "3";
        } else if (date.equals("27")) {
            return "4";
        }
        return "0";
    }

    public void chooseDate(int dateIndex) {
        mClickPos.set(dateIndex);
        if (dateIndex == 0) { /* 全部 */
            adapter.setList(mCacheList0);
        }
        if (dateIndex == 5) { /* 技术交流会 */
            mListener.onIntent(null, TechnicalListActivity.class);
        } else {
            if (!setDateList(dateIndex)) {
                LogUtil.e(TAG, " !! CACHE: " + dateIndex);
                filterList();
                setDateList(dateIndex);
            }
        }
    }

    private void filterList() {
        mCacheList1.clear();
        mCacheList2.clear();
        mCacheList3.clear();
        mCacheList4.clear();
        ConcurrentEvent.Pages eventItem;
        int size = mCacheList0.size();
        for (int i = 0; i < size; i++) {
            eventItem = mCacheList0.get(i);
            if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.date) == 24) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.date) == 25)) {
                mCacheList1.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.date) == 25) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.date) == 26)) {
                mCacheList2.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.date) == 26) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.date) == 27)) {
                mCacheList3.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.date) == 27) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.date) == 28)) {
                mCacheList4.add(eventItem);
            }
        }
    }

    private boolean setDateList(int dateIndex) {
        if (dateIndex == 1 && mCacheList1.size() > 0) {
            LogUtil.i(TAG, "CACHE: " + dateIndex + ":" + mCacheList1.size());
            adapter.setList(mCacheList1);
        } else if (dateIndex == 2 && mCacheList2.size() > 0) {
            LogUtil.i(TAG, "CACHE: " + dateIndex + ":" + mCacheList2.size());
            adapter.setList(mCacheList2);
        } else if (dateIndex == 3 && mCacheList3.size() > 0) {
            LogUtil.i(TAG, "CACHE: " + dateIndex + ":" + mCacheList3.size());
            adapter.setList(mCacheList3);
        } else if (dateIndex == 4 && mCacheList4.size() > 0) {
            LogUtil.i(TAG, "CACHE: " + dateIndex + ":" + mCacheList4.size());
            adapter.setList(mCacheList4);
        } else {
            return false;
        }
        return true;
    }

    private void parseEvents() {
        event = Parser.parseJsonFilesDirFile(ConcurrentEvent.class, Constant.TXT_CONCURRENT_EVENT);
        events.addAll(event.pages);
        LogUtil.i(TAG, "events1=" + events.size() + "," + events.toString());

        AppUtil.sort(events, new Comparator<ConcurrentEvent.Pages>() {
            @Override
            public int compare(ConcurrentEvent.Pages o1, ConcurrentEvent.Pages o2) {
                if (o1.date.compareTo(o2.date) == 0) {
                    return Integer.valueOf(o1.sort).compareTo(Integer.valueOf(o2.sort));
                } else {
                    return o1.date.compareTo(o2.date);
                }
            }
        });

        LogUtil.i(TAG, "events2=" + events.size() + "," + events.toString());
    }

    /**
     * 从选择的应用分类中，筛选出符合结果的同期活动,多个选择的话为交集
     */
    public void filterEvent(ArrayList<String> filters) {
        int size = mCacheList.size();
        int filterSize = filters.size();
        events.clear();
        ArrayList<String> appIds;

        for (int i = 0; i < size; i++) {
            appIds = mCacheList.get(i).applications;
            for (int j = 0; j < filterSize; j++) {
                if (!appIds.contains(filters.get(j))) { /* 有1个不满足就break */
                    break;
                }
                if (j == filterSize - 1) { /* 说明 appIds 里包含 filters 里所有id */
                    events.add(mCacheList.get(i));
                }
            }
        }
        LogUtil.i(TAG, "after filter>>> events=" + events.size() + "," + events.toString());

        addTech();
        adapter.setList(events);
    }

    public void onFilter() {
        mListener.onIntent(null, FilterApplicationListActivity.class);
    }

    public void onRefresh() {
        filterWords.set("");
        events.clear();
        events.addAll(mCacheList0);
        adapter.setList(events);
    }

    private void downEventZip() {
        final String zipPath = App.rootDir.concat("ConcurrentEvent/");
        String abc = "abc".concat(".zip");
        LogUtil.i(TAG, "ABC=" + abc);
        FileUtil.createFile(zipPath);
        mDownClient = ReRxUtils.setupRxtrofit(DownloadClient.class, event.htmlFilePath);
        Observable.fromIterable(events)
                .flatMap(new Function<ConcurrentEvent.Pages, Observable<Boolean>>() {

                    @Override
                    public Observable<Boolean> apply(@NonNull final ConcurrentEvent.Pages pages) throws Exception {
                        return mDownClient.downloadFile(pages.pageID.concat(".zip"))
                                .map(new Function<Response<ResponseBody>, Boolean>() {
                                    @Override
                                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                                        return FileUtil.writeZipToMemory(responseBodyResponse, pages.pageID.concat(".zip"), zipPath.concat(pages.pageID).concat("/"));
                                    }
                                })
                                .subscribeOn(Schedulers.io());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    private long startTime;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        startTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.e(TAG, "onError: e =" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        long endTime = System.currentTimeMillis();
                        LogUtil.i(TAG, "down zip spend time: " + (endTime - startTime) + " ms");
                    }
                });


    }


}
