package com.adsale.ChinaPlas.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.ActivityCompat;

import com.adsale.ChinaPlas.R;

import java.util.Calendar;
import java.util.TimeZone;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static com.adsale.ChinaPlas.utils.AppUtil.showAlertDialog;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PERMISSION_READ_CALENDAR;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_READ_CALENDAR;

/**
 * Created by Carrie on 2017/11/9.
 */

public class CalendarUtil {
    private final String TAG = "CalendarUtil";
    private final String ACCOUNT_NAME = "ChinaPlas@gmail.com";
    private final String CALENDAR_DISPLAY_NAME = "ChinaPlas18";
    private final int YEAR = 2018;
    private final int MONTH = 3; // 月份-1
    private final int START_DAY = 24;
    private Activity activity;

    public CalendarUtil(Activity activity) {
        this.activity = activity;
    }

    public void addToCalendar() {
        try {
            if (!PermissionUtil.checkPermission(activity, PERMISSION_READ_CALENDAR)) {
                PermissionUtil.requestPermission(activity, PERMISSION_READ_CALENDAR, PMS_CODE_READ_CALENDAR);
                return;
            }
            ContentValues event, values;
            Calendar calendar;
            Uri newEvent;
            ContentResolver cr;
            long start01, end01, id;
            int calId = getCalendarId();
            for (int i = 0; i < 4; i++) {
                event = new ContentValues();
                event.put(Events.TITLE, activity.getString(R.string.notes));
                event.put(Events.DESCRIPTION, activity.getString(R.string.calendar_description));
                event.put(Events.EVENT_LOCATION, activity.getString(R.string.calendar_location));
                event.put(Events.CALENDAR_ID, calId);

                calendar = Calendar.getInstance();
                calendar.set(YEAR, MONTH, START_DAY + i, 9, 30);
                start01 = calendar.getTime().getTime();
                calendar.set(YEAR, MONTH, START_DAY + i, 17, 00);// 11.30
                end01 = calendar.getTime().getTime();

                event.put(Events.DTSTART, start01);
                event.put(Events.DTEND, end01);
                event.put(Events.HAS_ALARM, 1);
                event.put(EXTRA_EVENT_ALL_DAY, 0);
                event.put(Events.STATUS, 1);
                event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                event.put(Events.GUESTS_CAN_MODIFY, true);//参与者能否修改事件  2016/10/28

                newEvent = activity.getContentResolver().insert(Events.CONTENT_URI, event);
                id = Long.parseLong(newEvent.getLastPathSegment());
                values = new ContentValues();
                values.put(Reminders.EVENT_ID, id);
                values.put(Reminders.MINUTES, 1 * 60 * 24);// 设置为提前一天提醒
                values.put(Reminders.EVENT_ID, id);
                values.put(Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

                cr = activity.getContentResolver(); // 为刚才新添加的event添加reminder
                cr.insert(Reminders.CONTENT_URI, values); // 调用这个方法返回值是一个Uri
            }
            AppUtil.showAlertDialog(activity, R.string.addToCalendar_success,
                    R.string.ok, -1, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtil.showAlertDialog(activity, R.string.addToCalendar_fail,
                    R.string.ok, -1, null, null);
        }

    }

    private int getCalendarId() {
        ContentResolver cr = activity.getContentResolver();
        Cursor userCursor = cr.query(Calendars.CONTENT_URI, null, null, null, null);
        int calId = 0;
        if (userCursor != null && userCursor.getCount() > 0) {
            //先获取用户日历账户，如果没有，则初始化添加账户
            while (userCursor.moveToNext()) {
                if (userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME)).equals(ACCOUNT_NAME)) {
                    calId = userCursor.getInt(userCursor.getColumnIndex("_id"));
                    break;
                }
            }
            userCursor.close();
        }
        if (calId == 0) {
            calId = initCalendars2(activity);
        }
        LogUtil.i(TAG, "calId=" + calId);
        return calId;
    }

    /**
     * 添加账户
     *
     * @param context
     */
    private int initCalendars2(Context context) {//插入成功
        int calId = 3;
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put("_id", calId);
        value.put(CalendarContract.Calendars.NAME, "CPS");
        value.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
        value.put(Calendars.ACCOUNT_TYPE, "com.adsale.ChinaPlas");//"com.android.exchange"
        value.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_DISPLAY_NAME);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME).appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.adsale.ChinaPlas")
                .build();
        context.getContentResolver().insert(calendarUri, value);
        return calId;
    }

}
