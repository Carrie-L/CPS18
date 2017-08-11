package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.id;
import static android.R.id.list;


/**
 * Created by Carrie on 2017/8/11.
 */

public class ScheduleViewModel extends BaseObservable {
    private static final String TAG = "ScheduleViewModel";
    /*Schedule List*/
    public final ObservableArrayList<ScheduleInfo> scheduleInfos = new ObservableArrayList<>();
    public final ObservableField<String> noSchedules = new ObservableField<>();
    public final ObservableBoolean isEmpty = new ObservableBoolean();
    public final ObservableInt dateIndex = new ObservableInt(0);

    /*Schedule Edit*/
    public final ObservableField<String> etTitle = new ObservableField<>();
    public final ObservableField<String> etLocation = new ObservableField<>();
    public final ObservableField<String> etStartDate = new ObservableField<>();
    public final ObservableField<String> etStartTime = new ObservableField<>("09:00");
    public final ObservableField<String> etNote = new ObservableField<>();
    public final ObservableField<String> etHour = new ObservableField<>("0");
    public final ObservableField<String> etMinute = new ObservableField<>("15");
    public final ObservableBoolean isEdit = new ObservableBoolean();

    private Context mContext;
    private ScheduleRepository mScheduleRepository;
    private long mId;
    private ScheduleInfo mScheduleInfo;

    public ScheduleViewModel(Context context) {
        this.mContext = context.getApplicationContext();
        mScheduleRepository = ScheduleRepository.getInstance();
        noSchedules.set(context.getString(R.string.no_schedule));
    }

    //由 list 的 item 点击而来
    public ScheduleViewModel(Context context, long id) {
        this.mContext = context.getApplicationContext();
        this.mId = id;
        mScheduleRepository = ScheduleRepository.getInstance();
        mScheduleInfo = mScheduleRepository.getItemData(mId);
        if (mScheduleInfo != null) {
            LogUtil.i(TAG, "mScheduleInfo=" + mScheduleInfo.toString());
            setupEditView();
        }
        etStartDate.set(toStrDate());
        LogUtil.i(TAG, " etStartDate=" + etStartDate.get());
    }

    //由 add 按钮点击而来
    public ScheduleViewModel(Context context, int index) {
        this.mContext = context.getApplicationContext();
        dateIndex.set(index);
        mScheduleRepository = ScheduleRepository.getInstance();
        etStartDate.set(toStrDate());
        LogUtil.i(TAG, " etStartDate=" + etStartDate.get());
    }

    private void setupEditView() {
        etTitle.set(mScheduleInfo.getTitle());
        etLocation.set(mScheduleInfo.getLocation());
        etStartDate.set(mScheduleInfo.getStartTime().split(" ")[0]);
        etStartTime.set(mScheduleInfo.getStartTime().split(" ")[1]);
        etMinute.set(mScheduleInfo.getLength() + "");
        etHour.set(mScheduleInfo.getAllday() + "");
        etNote.set(mScheduleInfo.getNote());
    }

    public void onStart(int date) {
        dateIndex.set(date);
        scheduleInfos.clear();
        ArrayList<ScheduleInfo> list = getSchedules();
        scheduleInfos.addAll(list);
        isEmpty.set(scheduleInfos.isEmpty());
        LogUtil.i(TAG, "onDateClick: date=" + date + ", list.size = " + list.size() + ", etStartDate=" + etStartDate.get());
    }


    public void onDateClick(int date) {
        etStartDate.set(toStrDate());
        onStart(date);
    }

    private ArrayList<ScheduleInfo> getSchedules() {
        return mScheduleRepository.getDateSchedules(toStrDate());
    }

    private String toStrDate() {
        return dateIndex.get() == 0 ? Constant.SCHEDULE_DAY01 : dateIndex.get() == 1 ? Constant.SCHEDULE_DAY02 : dateIndex.get() == 2 ? Constant.SCHEDULE_DAY03 : Constant.SCHEDULE_DAY04;
    }

    public void delete() {

    }

    public void save() {
        if (TextUtils.isEmpty(etTitle.get())) {
            Toast.makeText(mContext, R.string.not_title, Toast.LENGTH_SHORT).show();
            return;
        }
        //Long id, String Title, String Note, String Location, String CompanyID, String StartTime, Integer Length, Integer Allday,String event_CId
        String companyId = "";

        mScheduleInfo = new ScheduleInfo(mId == 0 ? null : mId, etTitle.get(), etNote.get(), etLocation.get(), companyId, etStartDate.get() + " " + etStartTime.get(), Integer.valueOf(etMinute.get()), Integer.valueOf(etHour.get()), "");
        if (mId == 0) {
            insertSchedule();
        } else {
            updateSchedule();
        }
    }

    public void datePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                etStartDate.set(AppUtil.dateToString(calendar.getTime()));
                LogUtil.i(TAG, "datePicker: " + etStartDate.get());
            }
        }, year, month, day);
        dateDialog.show();
    }

    public void timePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timeDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                etStartTime.set(AppUtil.timeToString(calendar.getTime()));
                LogUtil.i(TAG, "timePicker: " + etStartTime.get());
            }
        }, mHour, mMinute, true);
        timeDialog.show();
    }

    private void insertSchedule() {
        mScheduleRepository.insertItemData(mScheduleInfo);
        LogUtil.i(TAG, "insertSchedule");
    }

    private void updateSchedule() {
        mScheduleRepository.updateItemData(mScheduleInfo);
        LogUtil.i(TAG, "updateSchedule");
    }

    public void insertSchedule(ScheduleInfo scheduleInfo) {
        mScheduleRepository.insertItemData(scheduleInfo);
    }

    public void saveSchedule() {

    }

    public void deleteSchedule() {

    }


}
