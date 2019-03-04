package com.adsale.ChinaPlas.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.ui.FilterApplicationListActivity;
import com.adsale.ChinaPlas.ui.TechnicalListActivity;
import com.adsale.ChinaPlas.dao.ConcurrentEvent;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobReturn;
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
    //    public final ObservableArrayList<ConcurrentEvent> events = new ObservableArrayList<>();
    public final ObservableArrayList<com.adsale.ChinaPlas.dao.ConcurrentEvent> events = new ObservableArrayList<>();
    public final ObservableField<String> filterWords = new ObservableField<>();
    public final ObservableInt mClickPos = new ObservableInt(-1);  // -1 all
    public final ObservableBoolean isFiltering = new ObservableBoolean(false);
    public ConcurrentEvent event;
    private DownloadClient mDownClient;
    private EventAdapter adapter;
    // 同期活动数组
    private ArrayList<ConcurrentEvent> mCacheList = new ArrayList<>();
    // 同期活动+技术交流会数组，全部列表
    private ArrayList<ConcurrentEvent> mCacheList0 = new ArrayList<>();
    private ArrayList<ConcurrentEvent> mCacheList00 = new ArrayList<>();  // 5.20日
    private ArrayList<ConcurrentEvent> mCacheList1;
    private ArrayList<ConcurrentEvent> mCacheList2;
    private ArrayList<ConcurrentEvent> mCacheList3;
    private ArrayList<ConcurrentEvent> mCacheList4;
    private ArrayList<ConcurrentEvent> filterListPart = new ArrayList<>();
    private OnIntentListener mListener;
    private ConcurrentEvent tech;
    private ConcurrentEvent entity;
//    public boolean isSeminarEmpty;

    public EventModel() {
        mCacheList00 = new ArrayList<>();
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
        getEvents();
        mCacheList.addAll(events);
//        addTech();
        mCacheList0.addAll(events);
    }

    private void addTech() {
        // 隐藏技术交流会item：如果数据库里没有技术交流会数据
//        isSeminarEmpty = App.mDBHelper.mSeminarInfoDao.loadAll().isEmpty();
//        if (isSeminarEmpty) {
//            return;
//        }
        for (int i = 0; i < events.size(); i++) {
            entity = events.get(i);
            if (i != 0 && !entity.getDate().equals(events.get(i - 1).getDate())) {
                tech = new ConcurrentEvent();
                tech.setDate(entity.getDate()); /* 为了循环顺利进行，因此给 date 赋值,且+1.如果不加1，会死循环 */
                tech.setPageID(convertToTechDateIndex(events.get(i - 1).getDate())); /* 用于存放 dateIndex 的值 */
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
        tech = new ConcurrentEvent();
        int correctDate = Integer.valueOf(events.get(events.size() - 1).getDate());
        tech.setDate(correctDate + 1 + ""); /* 技术交流会的date都要加1，避免死循环.因此计算实际index时，要-1. */
        tech.setPageID(convertToTechDateIndex(correctDate + ""));
        tech.isTypeLabel.set(2);
        events.add(tech);
    }

    /**
     * @param date 实际是 技术交流会的 date index
     */
    public String convertToTechDateIndex(String date) {
        if (date.equals(Constant.DAY01 + "")) {
            return "0";
        } else if (date.equals(Constant.DAY02 + "")) {
            return "2";
        } else if (date.equals(Constant.DAY03 + "")) {
            return "4";
        } else if (date.equals(Constant.DAY04 + "")) {
            return "6";
        }
        return "0";
    }

    public void chooseDate(int dateIndex) {
        mClickPos.set(dateIndex);
        filterListPart.clear();
        if (isFiltering.get()) {  // 筛选状态下
            if (dateIndex == -1) { /* 全部 */
                adapter.setList(events);
            } else if (0 <= dateIndex && dateIndex <= 4) {
                int size = events.size();
                for (int i = 0; i < size; i++) {
                    if (Integer.valueOf(events.get(i).getDate()) - 20 == dateIndex) {
                        filterListPart.add(events.get(i));
                    }
                }
                adapter.setList(filterListPart);
            }
            return;
        }
        // 未筛选状态下
        if (dateIndex == -1) { /* 全部 */
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
        mCacheList00.clear();
        mCacheList1.clear();
        mCacheList2.clear();
        mCacheList3.clear();
        mCacheList4.clear();
        ConcurrentEvent eventItem;
        int size = mCacheList0.size();
        for (int i = 0; i < size; i++) {
            eventItem = mCacheList0.get(i);
            if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.getDate()) == 20) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY01)) {
                mCacheList00.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY01) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY02)) {
                mCacheList1.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY02) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY03)) {
                mCacheList2.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY03) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY04)) {
                mCacheList3.add(eventItem);
            } else if ((eventItem.isTypeLabel.get() != 2 && Integer.valueOf(eventItem.getDate()) == Constant.DAY04) || (eventItem.isTypeLabel.get() == 2 && Integer.valueOf(eventItem.getDate()) == (Constant.DAY04 + 1))) {
                mCacheList4.add(eventItem);
            }
        }
    }

    private boolean setDateList(int dateIndex) {
        if (dateIndex == 0 && mCacheList1.size() > 0) {
            LogUtil.i(TAG, "CACHE: " + dateIndex + ":" + mCacheList1.size());
            adapter.setList(mCacheList00);
        } else if (dateIndex == 1 && mCacheList1.size() > 0) {
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

    private void getEvents() {
        OtherRepository otherRepository = OtherRepository.getInstance();
        events.addAll(otherRepository.getEvents());
        LogUtil.i(TAG, "getEvents: >> list = " + events.size());

        AppUtil.sort(events, new Comparator<ConcurrentEvent>() {
            @Override
            public int compare(ConcurrentEvent o1, ConcurrentEvent o2) {
                if (o1.getDate().compareTo(o2.getDate()) == 0) {
                    return o1.getSeq().compareTo(o2.getSeq());
                } else {
                    return o1.getDate().compareTo(o2.getDate());
                }
            }
        });
    }

    /**
     * 从选择的应用分类中，筛选出符合结果的同期活动,多个选择的话为交集
     */
    public void filterEvent(ArrayList<String> filters) {
        int size = mCacheList.size();
        int filterSize = filters.size();
        events.clear();
//        ArrayList<String> appIds = new ArrayList<>();
        LogUtil.i(TAG, "filterEvent: size = " + size);

        for (int i = 0; i < size; i++) {
            String applications = mCacheList.get(i).getInApplications();
            if (TextUtils.isEmpty(applications)) {
                LogUtil.i(TAG, "空 app id， return");
                return;
            } else if (applications.equals("ALL")) {
                events.add(mCacheList.get(i));
                LogUtil.i(TAG, "ALL  : " + mCacheList.get(i).getPageID() + ",events =" + events.size());
            } else {
                if (!applications.contains(",")) {
                    if (filterSize == 1 && applications.equals(filters.get(0))) {
                        events.add(mCacheList.get(i));
                        LogUtil.i(TAG, "ONE  : " + mCacheList.get(i).getPageID() + ",events =" + events.size());
                    }
                } else {
                    for (int j = 0; j < filterSize; j++) {
                        for (String appId : applications.split(",")) {
                            if (!appId.equals(filters.get(j))) {
                                LogUtil.i(TAG, "有1个不满足就break appId=" + appId + ", filterId=" + filters.get(j));
                                break;
                            }
                            LogUtil.i(TAG, "满足");
                        }
                        if (j == filterSize - 1) { /* 说明 appIds 里包含 filters 里所有id */
                            events.add(mCacheList.get(i));
                        }
                    }
                }
            }
        }
        LogUtil.i(TAG, "after filter>>> events=" + events.size() + "," + events.toString());
        adapter.setList(events);
    }

    public void onFilter() {
        isFiltering.set(true);
        LogUtil.i(TAG, "mClickPos=" + mClickPos.get());
        mClickPos.set(mClickPos.get());
        mListener.onIntent(null, FilterApplicationListActivity.class);
    }

    public void onRefresh() {
        mClickPos.set(-1);
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
        mDownClient = ReRxUtils.setupRxtrofit(DownloadClient.class, event.getPageFileName());
        Observable.fromIterable(events)
                .flatMap(new Function<ConcurrentEvent, Observable<Boolean>>() {

                    @Override
                    public Observable<Boolean> apply(@NonNull final ConcurrentEvent pages) throws Exception {
                        return mDownClient.downloadFile(pages.getPageID().concat(".zip"))
                                .map(new Function<Response<ResponseBody>, Boolean>() {
                                    @Override
                                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                                        return FileUtil.writeZipToMemory(responseBodyResponse, pages.getPageID().concat(".zip"), zipPath.concat(pages.getPageID()).concat("/"));
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
