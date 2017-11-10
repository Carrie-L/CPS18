package com.adsale.ChinaPlas.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.MessageCenter;
import com.adsale.ChinaPlas.ui.NewsActivity;
import com.adsale.ChinaPlas.ui.NewsDtlActivity;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;

/**
 * Created by Carrie on 2017/11/10.
 */

public class IntentHelper<T> {

    private Activity activity;
    private MainIcon mainIcon;
    private MessageCenter.Message message;

    public IntentHelper(Activity activity) {
        this.activity = activity;
    }


    public void messageIntent(MessageCenter.Message message, boolean pendingIntent) {
        Intent intent = new Intent();
        switch (Integer.valueOf(message.function)) {
            case 1://展商詳情頁
                ExhibitorRepository repository = new ExhibitorRepository();
                if (!repository.isExhibitorIDExists(message.ID)) {
                    Toast.makeText(activity, activity.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
                    return;
                }
//                intent = new Intent(activity, ExhibitorDetailActivity.class);
//                intent.putExtra(Constant.COMPANY_ID, message.ID);
                break;
            case 2://新闻
                if (TextUtils.isEmpty(message.ID)) {
                    intent = new Intent(activity, NewsActivity.class);
                } else {
                    intent = new Intent(activity, NewsDtlActivity.class);
                    intent.putExtra("NewsID", message.ID);
                }
                break;

            case 3://同期活动
                intent = new Intent(activity, WebContentActivity.class);
                intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(message.ID));
                break;

            case 4://link
                intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(Constant.WEB_URL, message.ID);
                break;

            case 5://预登记
                intent = new Intent(activity, RegisterActivity.class);
                break;

            case 6://新闻详情
                intent = new Intent(activity, NewsDtlActivity.class);
                intent.putExtra("NewsID", message.ID);
                break;

            default:
                break;
        }

        //不是PendingIntent，就直接跳转
        if (!pendingIntent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必不可少
            intent.putExtra("Type", Constant.PUSH_INTENT);
            activity.startActivity(intent);
            if (AppUtil.isTablet()) {
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

    }


}
