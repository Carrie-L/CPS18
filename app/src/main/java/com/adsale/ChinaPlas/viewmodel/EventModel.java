package com.adsale.ChinaPlas.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.EventAdapter;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.ui.TechnicalListActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;

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
 */

public class EventModel {
    private static final String TAG = "EventModel";
    public final ObservableArrayList<ConcurrentEvent.Pages> events = new ObservableArrayList<>();
    public final ObservableInt mClickPos = new ObservableInt(0);
    private ConcurrentEvent event;
    private DownloadClient mDownClient;
    private EventAdapter adapter;
    private ArrayList<ConcurrentEvent.Pages> mCacheList;
    private ArrayList<ConcurrentEvent.Pages> mCacheList1;
    private ArrayList<ConcurrentEvent.Pages> mCacheList2;
    private ArrayList<ConcurrentEvent.Pages> mCacheList3;
    private ArrayList<ConcurrentEvent.Pages> mCacheList4;
    private OnIntentListener mListener;

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
    }

    public void chooseDate(int dateIndex) {
        mClickPos.set(dateIndex);
        if (dateIndex == 0) { /* 全部 */
            adapter.setList(events);
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
        int size = events.size();
        for (int i = 0; i < size; i++) {
            eventItem = events.get(i);
            if (Integer.valueOf(eventItem.date) == 24) {
                mCacheList1.add(eventItem);
            } else if (Integer.valueOf(eventItem.date) == 25) {
                mCacheList2.add(eventItem);
            } else if (Integer.valueOf(eventItem.date) == 26) {
                mCacheList3.add(eventItem);
            } else if (Integer.valueOf(eventItem.date) == 27) {
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
