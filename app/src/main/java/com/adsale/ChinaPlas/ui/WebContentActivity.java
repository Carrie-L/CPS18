package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.adsale.ChinaPlas.dao.ConcurrentEvent;
import com.bumptech.glide.Glide;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.adsale.ChinaPlas.App.mLogHelper;
import static com.adsale.ChinaPlas.App.rootDir;

/**
 * must intent data: [Constant.WEB_URL]
 * [Url] : e.g.:【"ConcurrentEvent/001"】,内存中的文件夹名称需与asset目录下的一致，最后不需要[/]
 * <p>
 * optional:  同期活动页：[ConcurrentEvent] entity
 */
public class WebContentActivity extends BaseActivity {

    private WebView webView;
    private String mIntentUrl;
    private Intent mIntent;
    private WebSettings settings;
    private HelpView helpDialog;
    private Disposable mEventDisposable;
    private String eventPageId;
    private ConcurrentEvent mEvent;
    private OtherRepository otherRepository;
    private ImageView ivAD;
    private EPOHelper mEPOHelper;

    @Override
    protected void preView() {
        super.preView();
        barTitle.set(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_web_content, mBaseFrameLayout, true);
        webView = findViewById(R.id.web_content_view);
        ivAD = findViewById(R.id.iv_ad);
        TAG = "WebContentActivity";
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        webView.setHapticFeedbackEnabled(false);
    }

    @Override
    protected void initData() {
        final Intent intent = getIntent();
        mIntentUrl = intent.getStringExtra(Constant.WEB_URL);
        LogUtil.i(TAG, "mIntentUrl=" + mIntentUrl);

        //open pdf
        if (mIntentUrl.startsWith("PDF:")) {
            openPDF();
        } else if ((mIntentUrl.toLowerCase().startsWith("http") && !checkImageUrl(mIntentUrl)) || mIntentUrl.toLowerCase().startsWith("web:")) {
            loadWebUrl();
        } else if (!mIntentUrl.contains("ConcurrentEvent")) { // 2018: 同期活动的在onResume()里另外判断   // 2019:改为不在resume中。去年的因为要跳转到更新中心，所以需要在onResume中显示。今年无需跳转，所以直接在start里显示
            loadLocalHtml(getHtmName());
        }

        setWebViewClient();

        mBaiduTJ = getIntent().getStringExtra(Constant.BAIDU_TJ);

        // show help
        if (mIntentUrl.contains("ConcurrentEvent")) {
            LogUtil.e(TAG, "show ConcurrentEvent");
            showEventHelp();
            // 显示详情页面
            eventPageId = mIntentUrl.replace("ConcurrentEvent/", "");
            LogUtil.i(TAG, "eventPageId=" + eventPageId);
            toEventDtl();
        } else if (mBaiduTJ != null && (mBaiduTJ.toLowerCase().contains("hallmap") || mBaiduTJ.toLowerCase().contains("floorplan"))) {
            showOverallHelp();
        }
    }

    private void openPDF() {
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        webView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + mIntentUrl.replace("PDF:", ""));
    }

    private void showOverallHelp() {
        settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);// 一定要加这句，且一定要设置为true，才会放大
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        showD5Overall();

        LogUtil.i(TAG, "showOverallHelp");
        if (mBaiduTJ.toLowerCase().contains("withouthelp")) {
            return;
        }

        ImageView ivHelp = findViewById(R.id.iv_help);
        ivHelp.setVisibility(View.VISIBLE);

        if (HelpView.isFirstShow(HelpView.HELP_PAGE_FLOOR_OVERALL)) {
            showOverallHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_FLOOR_OVERALL, HelpView.HELP_PAGE_FLOOR_OVERALL).apply();
        }

        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverallHelpPage();
            }
        });
    }

    private void showEventHelp() {
        ImageView ivHelp = findViewById(R.id.iv_help);
        ivHelp.setVisibility(View.VISIBLE);
        ImageView ivAdd = findViewById(R.id.fab_add);
        ivAdd.setVisibility(View.VISIBLE);

        if (HelpView.isFirstShow(HelpView.HELP_PAGE_EVENT_DTL)) {
            showEventHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_EVENT_DTL, HelpView.HELP_PAGE_EVENT_DTL).apply();
        }

        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventHelpPage();
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEvent == null) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ScheduleEditActivity.class);
                ScheduleInfo entity = new ScheduleInfo();
                entity.setId(null);
//                entity.setTitle(getIntent().getStringExtra("title")); // 千万不能用barTitle.get() ，会错，虽然很奇怪。
                entity.setTitle(mEvent.getTitle());
                entity.setLocation(mEvent.getVenue());
                entity.setStartDate(mEvent.getYearDate());
                String duration = mEvent.getDuration();
                if (duration != null && duration.contains("-") && duration.length() > 1) {
                    entity.setStartTime(mEvent.getDuration().split("-")[0]);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constant.INTENT_SCHEDULE, entity);
                intent.putExtra("title", getString(R.string.title_add_schedule));
                startActivity(intent);
                overridePendingTransPad();
            }
        });
    }

    private void showEventHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_EVENT_DTL);
        helpDialog.show(ft, "Dialog");
    }

    private void showOverallHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_FLOOR_OVERALL);
        helpDialog.show(ft, "Dialog");
    }

    /**
     * 总览平面图底部广告 D5
     */
    private void showD5Overall() {
        LogUtil.i(TAG, "showD5Overall");
        mEPOHelper = EPOHelper.getInstance();
        if (!mEPOHelper.isD5Open()) {
            return;
        }
        ivAD.setVisibility(View.VISIBLE);
        final int index = mEPOHelper.getD5ShowIndex();
        LogUtil.i(TAG, "getD5ShowIndex= " + index);
        Glide.with(getApplicationContext()).load(Uri.parse(mEPOHelper.getD5Banner(index))).into(ivAD);
        mEPOHelper.setD5ShowIndex(index);

        mLogHelper.logD5(mEPOHelper.getD5CompanyID(index), true);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);

        ivAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogHelper.logD5(mEPOHelper.getD5CompanyID(index), false);
                mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);

                Intent intent = new Intent(getApplicationContext(), ExhibitorDetailActivity.class);
                intent.putExtra(Constant.COMPANY_ID, mEPOHelper.getD5CompanyID(index));
                intent.putExtra("from", "D5");
                startActivity(intent);
                overridePendingTransPad();
            }
        });
    }

    private boolean checkImageUrl(String url) {
        return url.endsWith("jpg") || url.endsWith("png");
    }

    private void loadLocalHtml(String htmlName) {
        StringBuilder sb = new StringBuilder();
        if (new File(App.rootDir.concat(mIntentUrl)).exists()) {
            sb.append("file://").append(App.rootDir).append(mIntentUrl).append("/").append(htmlName);
        } else {
            sb.append("file:///android_asset/").append(mIntentUrl).append("/").append(htmlName);
        }
        webView.loadUrl(sb.toString());
        LogUtil.i(TAG, "loadLocalHtml= " + sb.toString());

        showD7();
    }

    private void loadWebUrl() {
        LogUtil.i(TAG, "-- loadWebUrl --");
        webView.loadUrl(mIntentUrl);
    }

    private String getHtmName() {
        return AppUtil.getName("TC.html", "EN.html", "SC.html");
    }

    private String mUrl;

    private void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mUrl = url;
                LogUtil.i(TAG, "shouldOverrideUrlLoading: url= " + url);

                if (checkImageUrl(url)) {
                    toImageAty();
                } else if (startsWith("prereg://")) {
                    toRegisterAty();
                } else if (startsWith("web")) {
                    web();
                    return true;
                } else if (endsWith("pdf")) {
                    pdf();
                    return true;
                } else if (endsWith("mp4")) {
                    mp4();
                    return true;
                } else if (startsWith("http")) {
                    http();
                } else if (startsWith("mailto")) {
                    mailTo();
                    return true;
                } else if (startsWith("tel:")) {
                    callPhone();
                    return true;
                } else if (url.startsWith("floor://")) { // 平面图总览 —— 点击楼层，显示相应html
                    loadLocalHtml(url.replace("floor://", ""));
                    return true;
                } else if (url.startsWith("hall://")) {
                    mIntent = new Intent(getApplicationContext(), FloorDetailActivity.class);
                    mIntent.putExtra("HALL", url.replace("hall://", ""));
                } else if (url.startsWith("events://0")) {
                    mIntent = new Intent(getApplicationContext(), ConcurrentEventActivity.class);
                } else if (url.startsWith("eventinfo://")) {
                    eventPageId = url.replace("eventinfo://", "");
                    LogUtil.i(TAG, "eventinfo://eventPageId=" + eventPageId);
                    toEventDtl();
                    return true;
                } else if (url.startsWith("technical://")) {
                    mIntent = new Intent(getApplicationContext(), TechnicalListActivity.class);
                } else if (url.startsWith("exhibitor://")) {
                    mIntent = new Intent(getApplicationContext(), ExhibitorAllListActivity.class);
                } else if (url.startsWith("schedule://")) {
                    mIntent = new Intent(getApplicationContext(), ScheduleActivity.class);
                } else if (url.startsWith("news://")) {
                    mIntent = new Intent(getApplicationContext(), NewsDtlActivity.class);
                } else if (url.startsWith("travel://")) {
                    mIntent = new Intent(getApplicationContext(), TravelInfoActivity.class);
                } else if (url.startsWith("login://0")) {
                    if (AppUtil.isLogin()) {
                        mIntent = new Intent(getApplicationContext(), UserInfoActivity.class);
                    } else {
                        mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    }
                } else if (url.startsWith("CMS://")) {
                    mIntentUrl = "WebContent/" + url.replace("CMS://", "");
                    loadLocalHtml(getHtmName());
                    return true;
                }
                intent();
                return true;
            }
        });
    }

    private void toEventDtl() {
        loadLocalHtml(getHtmName());

        ConcurrentEvent entity = getIntent().getParcelableExtra("ConcurrentEvent");
        if (entity != null) {
            mLogHelper.logEventInfo(entity.getDate() + "_" + entity.getEventID());
        } else {
            mLogHelper.logEventInfo(eventPageId);
        }
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_Info);


    }

    private boolean startsWith(String abc) {
        return mUrl.toLowerCase().startsWith(abc);
    }

    private boolean endsWith(String abc) {
        return mUrl.toLowerCase().endsWith(abc);
    }

    private void toImageAty() {
        mIntent = new Intent(WebContentActivity.this, ImageActivity.class);
        mIntent.putExtra("url", mUrl);
        mIntent.putExtra("title", barTitle.get());
    }

    private void toRegisterAty() {
        mIntent = new Intent(WebContentActivity.this, RegisterActivity.class);
    }

    private void web() {
        mUrl = mUrl.replace("web://", "http://");
        LogUtil.i(TAG, "startsWith(\"web_————url=" + mUrl);
        Uri uri = Uri.parse(mUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void mp4() {
        Uri uri = Uri.parse(mUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void pdf() {
        LogUtil.i(TAG, "startsWith(\"pdf————url=" + mUrl);
        Uri uri = Uri.parse(mUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void http() {
        mIntent = new Intent(WebContentActivity.this, WebViewActivity.class);
        mIntent.putExtra(Constant.WEB_URL, mUrl);
        mIntent.putExtra("title", barTitle.get());
    }

    private void mailTo() {
        mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.setData(Uri.parse(mUrl));
        mIntent.setType("plain/text");
        mIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mUrl.replaceFirst("mailto:", "").trim()});
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        try {
            startActivity(mIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.exception_toast_email), Toast.LENGTH_SHORT).show();
        }
    }

    private void callPhone() {
        try {
            mIntent = new Intent(Intent.ACTION_DIAL);
            mIntent.setData(Uri.parse(mUrl));
            startActivity(mIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.exception_toast_phone), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void intent() {
        if (mIntent == null) {
            return;
        }
        LogUtil.e(TAG, "intent()");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mIntent);
        overridePendingTransPad();
    }

    private Integer D7_INDEX = -1;

    private void showD7() {
        LogUtil.i(TAG, "showD7:mIntentUrl= " + mIntentUrl);
        if (mIntentUrl.contains("MI00000077")) { // 展会概览
            D7_INDEX = 1;
        } else if (mIntentUrl.equals("WebContent/7")) { // 如何到达展馆
            D7_INDEX = 2;
        } else if (mIntentUrl.contains("MI00000073")) { // 旅游住宿
            D7_INDEX = 3;
        } else if (mIntentUrl.contains("MI00000076")) { // 展馆设施
            D7_INDEX = 5;
        } else if (mIntentUrl.contains("MI00000078")) { // 参展小贴士
            D7_INDEX = 6;
        }
        LogUtil.i(TAG, "showD7:D7_INDEX= " + D7_INDEX);
        final EPOHelper epoHelper = EPOHelper.getInstance();
        if (!epoHelper.isD7Open(D7_INDEX)) {
            return;
        }
        ivAD.setVisibility(View.VISIBLE);
        epoHelper.setD7ViewLog(D7_INDEX, getApplicationContext());
        Glide.with(getApplicationContext()).load(epoHelper.getD7Image(D7_INDEX)).into(ivAD);
        ivAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.adsale.ChinaPlas.data.model.EPO.D7 D7 = epoHelper.getItemD7(D7_INDEX);
                Intent intent = epoHelper.intentAd(D7.function, D7.companyID, D7.PageID);
                if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                    epoHelper.setD7ClickLog(D7_INDEX, getApplicationContext());
                    startActivity(intent);
                    overridePendingTransPad();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventDisposable != null && !mEventDisposable.isDisposed()) {
            mEventDisposable.dispose();
        }
    }
}
