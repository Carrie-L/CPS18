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
import com.adsale.ChinaPlas.utils.LogUtil;

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
//                ExhibitorRepository repository = new ExhibitorRepository();
//                if (!repository.isExhibitorIDExists(message.ID)) {
//                    Toast.makeText(activity, activity.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                intent = new Intent(activity, ExhibitorDetailActivity.class);
//                intent.putExtra(Constant.COMPANY_ID, message.ID);

                intent = intentToCompany(message.ID);

                break;
            case 2://新闻
                intent = intentToNews(message.ID);
                break;

            case 3://同期活动
                intent = intentToCurEvent(message.ID);
                break;

            case 4://link
                intent = intentToWebView(message.ID);
                break;

            case 5://预登记
                intent = new Intent(activity, RegisterActivity.class);
                break;

            case 6://新闻详情
                intent = intentToNews(message.ID);
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


    public static Intent intentAd(int function, String companyId, String pageId) {
        LogUtil.i("IntentHelper", "intentAd: function=" + function + ",companyId=" + companyId + ",pageId=" + pageId);
        switch (function) {
            case 1: // 展商详情
                return intentToCompany(pageId);
            case 2: // 同期活动详情
                return intentToCurEvent(pageId);
            case 3: // 技术交流会详情
                return intentToSeminar(pageId, companyId);
            case 4: // 新闻详情
                return intentToNews(pageId);
            case 5: // baiduTJ

                break;
            case 6: // link   pageId 为 url
                return IntentHelper.intentToWebView(pageId);
            case 10: // mapDetail
                return intentToMapDetail(pageId);
            case 99:
                return intentToWebContent(pageId);
        }
        return new Intent();
    }

    public static Intent intentToMapDetail(String pageId) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_MAP_DETAIL);
        intent.putExtra("BOOTH", pageId.split("-")[0]);
        intent.putExtra("HALL", pageId.split("-")[1]);
        return null;
    }

    public static Intent intentToWebView(String pageId) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_WEB_VIEW);
        intent.putExtra(Constant.WEB_URL, pageId);
        return null;
    }

    public static Intent intentToWebContent(String pageId) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_WEB_CONTENT);
        intent.putExtra(Constant.WEB_URL, "WebContent/" + pageId);
        return intent;
    }

    public static Intent intentToCompany(String adCompanyId) {
        Intent intent = new Intent();
        ExhibitorRepository repository = ExhibitorRepository.getInstance();
        if (!repository.isExhibitorIDExists(adCompanyId)) {
            LogUtil.i("IntentHelper", adCompanyId + " 不存在啦");
//            Toast.makeText(mContext, mContext.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            LogUtil.i("IntentHelper", ",companyID=" + adCompanyId);
//            intent = new Intent(mContext, ExhibitorDetailActivity.class);
            intent.setAction(Constant.ACTION_EXHIBITOR_DTL);
            intent.putExtra("CompanyID", adCompanyId);
            intent.putExtra("title", Constant.TITLE_EXHIBITOR_DTL);
            intent.putExtra("from", "other");

            return intent;
        }
    }

    public static Intent intentToCurEvent(String eventId) {
        Intent intent = new Intent(Constant.ACTION_WEB_CONTENT);
//        intent = new Intent(mContext, WebContentActivity.class);
        intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(eventId));
        intent.putExtra("PageID", eventId);
        intent.putExtra("title", Constant.TITLE_EVENT);
        return intent;
    }

    public static Intent intentToSeminar(String seminarId, String adCompanyId) {
        Intent intent = new Intent();
//        intent = new Intent(mContext, TechSeminarDtlActivity.class);
        intent.setAction(Constant.ACTION_SEMINAR_INFO);
        intent.putExtra(Constant.INTENT_SEMINAR_DTL_ID, seminarId);
        LogUtil.i("IntentHelper", "M:seminarID=" + seminarId);
        intent.putExtra("title", Constant.TITLE_SEMINAR);
        intent.putExtra("adCompanyID", adCompanyId);
        return intent;
    }

    public static Intent intentToNews(String newsId) {
        Intent intent = new Intent();
        if (TextUtils.isEmpty(newsId)) {
            intent.setAction(Constant.ACTION_NEWS);
        } else {
            intent.setAction(Constant.ACTION_NEWS_DTL);
            intent.putExtra("ID", newsId);
        }
        intent.putExtra("title", Constant.TITLE_NEWS);
        return intent;
    }


}
