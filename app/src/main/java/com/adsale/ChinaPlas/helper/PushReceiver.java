package com.adsale.ChinaPlas.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.model.MessageCenter;
import com.adsale.ChinaPlas.ui.LoadingActivity;
import com.adsale.ChinaPlas.ui.MainActivity;
import com.adsale.ChinaPlas.ui.PadMainActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private SimpleDateFormat sdf;
    private String dateTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtil.i(TAG, "[MyReceiver] onReceive - " + intent.getAction()
                + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle
                    .getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.i(TAG, "[MyReceiver] 接收Registration Id : " + regId);

            // send the Registration Id to your server...
            AppUtil.setJPUSHRegId(regId);

            //2016.9.22 删除掉ksoap后注释
//			new MasterWSHelper(context).RegAndroidDTToDataBase(regId);

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                .getAction())) {
            LogUtil.i(TAG,
                    "[MyReceiver] 接收到推送下来的自定义消息: "
                            + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            openNotification(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle
                    .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            //[MyReceiver] 用户点击打开了通知:message=null,extras={"ID":"215180","function":"1"},title=国际橡塑展,alert=CPS2017 APP将在2017年1月1日上线！
            LogUtil.d(TAG, "[MyReceiver] 用户点击打开了通知:\nmessage=" + bundle.getString(JPushInterface.EXTRA_MESSAGE) + ",\nextras=" + bundle.getString(JPushInterface.EXTRA_EXTRA)
                    + ",\ntitle=" + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE) + ",\nalert=" + bundle.getString(JPushInterface.EXTRA_ALERT));

            //发送LOG和TJ
            String subMsg = bundle.getString(JPushInterface.EXTRA_ALERT);
            if (subMsg != null && subMsg.length() > 5) {
                subMsg = bundle.getString(JPushInterface.EXTRA_ALERT).substring(0, 5);
            }
            AppUtil.trackViewLog(411, "Push", "", subMsg);
            AppUtil.setStatEvent(context, "Push", "Push_" + subMsg);

            //解析附加字段Json内容
            MessageCenter.Message message = Parser.parseJson(MessageCenter.Message.class, bundle.getString(JPushInterface.EXTRA_EXTRA));
            LogUtil.d(TAG, "message=" + message.toString());

            //跳转到指定页面
            if (TextUtils.isEmpty(message.function)) {
                boolean isAppRunning = isAppRunning(context, "com.adsale.ChinaPlas");
                LogUtil.i(TAG, "isAppRunning=" + isAppRunning);
                if (isAppRunning) { // 如果App正在运行，则直接在当前页面；否则启动App
                    return;
                }
                Intent intent2 = new Intent(context, LoadingActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.putExtra("Type", Constant.PUSH_INTENT);
                context.startActivity(intent2);
            } else {
                AppUtil.messageIntent(context, message, false);
            }
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
                .getAction())) {
            boolean connected = intent.getBooleanExtra(
                    JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction()
                    + " connected state change to " + connected);
        } else {
            LogUtil.i(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    // 自定义通知
    private void openNotification(Context context, Bundle bundle) {

        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        dateTime = sdf.format(new Date());
        LogUtil.i("Splash", "-Splash--dateTime----" + dateTime);
        // :LocalNotification trackingName: LNotification_201506221425_tc_iOS
//		SystemMethod.trackPushLog(context, 412, "PNotification", "", "",
//				dateTime);

        AppUtil.trackViewLog(412, "PNotification", "", dateTime);

        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        String APPName = context.getString(R.string.app_name);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, AppUtil.isTablet() ? PadMainActivity.class : MainActivity.class), 0);

        NotificationCompat.Builder oBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(APPName)
                .setStyle(
                        new NotificationCompat.BigTextStyle().bigText(message));
        Uri uri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        oBuilder.setSound(uri);
        oBuilder.setContentText(message);
        oBuilder.setContentIntent(contentIntent);
        Notification notification = oBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(new Random().nextInt(), notification);
    }

    /**
     * 方法描述：判断某一应用是否正在运行
     * Created by cafeting on 2017/2/4.
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    private boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
