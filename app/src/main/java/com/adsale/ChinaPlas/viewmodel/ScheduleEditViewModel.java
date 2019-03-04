package com.adsale.ChinaPlas.viewmodel;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.Calendar;

/**
 * Created by Carrie on 2017/10/11.
 */

public class ScheduleEditViewModel {
    private final String TAG = "ScheduleEditViewModel";
    public final ObservableField<String> etTitle = new ObservableField<>("");
    public final ObservableField<String> etLocation = new ObservableField<>("");
    public final ObservableField<String> etStartDate = new ObservableField<>("");
    public final ObservableField<String> etStartTime = new ObservableField<>("09:00");
    public final ObservableField<String> etNote = new ObservableField<>("");
    public final ObservableField<String> etHour = new ObservableField<>("0");
    public final ObservableField<String> etMinute = new ObservableField<>("15");
    /*是 edit 还是 add. true: edit; false: add */
    public final ObservableBoolean isEdit = new ObservableBoolean();

    private Context mContext;
    private ScheduleRepository mRepository;
    /**
     * id starts from 1, so if mId == 0, means adding a new schedule.
     */
    private long mId = 0;
    public String mCompanyId;
    private FragmentManager mFragmentManager;
    private HelpView helpDialog;

    public ScheduleEditViewModel(Context mContext) {
        this.mContext = mContext;
        mRepository = ScheduleRepository.getInstance();
    }

    public void setCompanyId(String companyId) {
        mCompanyId = companyId;
    }

    public void setId(long id) {
        this.mId = id;
        LogUtil.i(TAG, "mId=" + mId);
    }

    /**
     * 当 scheduleInfo.getId() 为空时，返回一个数值
     * @return
     */
    public int getId() {
        return mRepository.getScheduleCounts() + 1;
    }

    public void onDelete() {
        mRepository.deleteItemData(mId);
        if (mEditListener != null) {
            mEditListener.onFinish(true);
        }
    }

    /**
     * ScheduleInfo: [Long ScheduleID, String CompanyID, String Title, String Location, Integer DayIndex, String StartTime, Integer Hour, Integer Minute, String Note]
     */
    public void onSave() {
        if (etTitle.get().isEmpty()) {
            Toast.makeText(mContext, mContext.getString(R.string.not_title), Toast.LENGTH_LONG).show();
            return;
        }
        if (mId == 0) {// add
            LogUtil.i(TAG, "isSameTimeSchedule：" + mId);
            isSameTimeSchedule();
        } else {// update
            LogUtil.i(TAG, "insertOrReplace：" + mId);
            insertOrReplace();
//            AppUtil.trackViewLog(418, "US", etStartDate.get(), mCompanyId);
//            AppUtil.setStatEvent(mContext, "UpdateSchedule", "US_" + etStartDate.get() + "_" + mCompanyId);
            LogUtil.i(TAG, "etStartDate=" + etStartDate.get());

        }
    }

    /**
     * 当相同时间储存了一个议程时，提醒用户
     */
    private void isSameTimeSchedule() {
        boolean isSameTimeSchedule = mRepository.isSameTimeSchedule(etStartDate.get(), etStartTime.get());
        LogUtil.i(TAG, "isSameTimeSchedule=" + isSameTimeSchedule);
        if (isSameTimeSchedule && mEditListener != null) {
            mEditListener.onSameTimeSave();
        } else {
            insertOrReplace();
        }
    }

    public void insertOrReplace() {
        ScheduleInfo scheduleInfo = new ScheduleInfo(mId == 0 ? null : mId, etTitle.get(), etNote.get(), etLocation.get(), mCompanyId, etStartDate.get(), etStartTime.get(), Integer.valueOf(etHour.get()), Integer.valueOf(etMinute.get()), "");
        LogUtil.i(TAG, "onSave::scheduleInfo= " + scheduleInfo.toString());
        mRepository.insertOrReplaceItemData(scheduleInfo);
        if (mEditListener != null) {
            mEditListener.onFinish(true);
        }
    }

    public void onHelpPage(View view) {
        showHelpPage();
    }

    public void setFragmentManager(FragmentManager fm) {
        mFragmentManager = fm;
    }

    public void showHelpPage() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_SCHEDULE_DTL);
        helpDialog.show(ft, "Dialog");
    }

    public void showPage() {
        helpDialog.showPage();
    }

    public void onClickExhibitor() {
        if (mEditListener != null) {
            mEditListener.toExhibitorDtl(mCompanyId);
        }
    }

    /**
     * @param view
     */
    public void onDateClick(View view) {
        int year = Integer.valueOf(Constant.SCHEDULE_DAY01.split("-")[0]);
        int month = Integer.valueOf(Constant.SCHEDULE_DAY01.split("-")[1]);
        int minDay = Integer.valueOf(Constant.SCHEDULE_DAY01.split("-")[2]); // 开始日
        int maxDay = Integer.valueOf(Constant.SCHEDULE_DAY_END.split("-")[2]); // 结束日
        int currDay = Integer.valueOf(etStartDate.get().split("-")[2]);
        LogUtil.e(TAG, "minDay=" + minDay);
        LogUtil.e(TAG, "maxDay=" + maxDay);
        LogUtil.e(TAG, "year=" + year);
        LogUtil.e(TAG, "month=" + month);
        LogUtil.e(TAG, "currDay=" + currDay);

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                etStartDate.set(AppUtil.dateToString(calendar.getTime()));
                LogUtil.i(TAG, " datePicker: " + etStartDate.get());
            }
        }, year, month, currDay);

        //set minDate and maxDate
        DatePicker datePicker = dateDialog.getDatePicker();
        calendar.set(year, month - 1, minDay, 0, 0);//月份需减1
        long minDate = calendar.getTimeInMillis();
        calendar.set(year, month - 1, maxDay, 23, 59, 59);
        long maxDate = calendar.getTimeInMillis();
        datePicker.setMinDate(minDate);
        datePicker.setMaxDate(maxDate);
        datePicker.updateDate(year, month - 1, currDay);
        dateDialog.show();
    }

    public void onTimeClick(View view) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(AppUtil.stringToDate(etStartTime.get()));
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

    public interface ScheduleEditListener {
        void onSameTimeSave();

        void toExhibitorDtl(String companyId);

        void onFinish(boolean change);
    }

    public void setScheduleEditListener(ScheduleEditListener listener) {
        mEditListener = listener;
    }

    private ScheduleEditListener mEditListener;

    public void onActivityDestroyed() {
        mEditListener = null;
        mRepository = null;
    }


}
