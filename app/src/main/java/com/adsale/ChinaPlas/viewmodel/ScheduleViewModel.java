package com.adsale.ChinaPlas.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * todo 当insert时插入到日历，delete时也从日历删除。
 * Created by Carrie on 2017/8/11.
 */

public class ScheduleViewModel extends BaseObservable {
    private static final String TAG = "ScheduleViewModel";
    public final ObservableArrayList<ScheduleInfo> scheduleInfos = new ObservableArrayList<>();
    public final ObservableBoolean isEmpty = new ObservableBoolean();
    public final ObservableField<String> date = new ObservableField<>(Constant.SCHEDULE_DAY01);
    private ScheduleRepository mScheduleRepository;

    public ScheduleViewModel() {
        mScheduleRepository = ScheduleRepository.getInstance();
    }

    public void onStart() {
        scheduleInfos.clear();
        ArrayList<ScheduleInfo> list = getSchedules();
        scheduleInfos.addAll(list);
        isEmpty.set(scheduleInfos.isEmpty());
        LogUtil.i(TAG, "onDateClick: date=" + date.get() + ", list.size = " + list.size() );
    }


    public void onDateClick(String d) {
        LogUtil.i(TAG,"onDateClick:"+d);
        date.set(d);
        onStart();
    }

    private ArrayList<ScheduleInfo> getSchedules() {
        return mScheduleRepository.getDateSchedules(date.get());
    }

}
