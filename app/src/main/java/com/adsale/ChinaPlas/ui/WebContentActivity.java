package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.adsale.ChinaPlas.data.ListBindings;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.adsale.ChinaPlas.App.rootDir;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

/**
 * must intent data: [Constant.WEB_URL]
 * [Url] : e.g.:【"ConcurrentEvent/001"】,内存中的文件夹名称需与asset目录下的一致，最后不需要[/]
 */
public class WebContentActivity extends BaseActivity {

    private WebView webView;
    private String mIntentUrl;
    private Intent mIntent;
    private WebSettings settings;
    private HelpView helpDialog;
    private Disposable mEventDisposable;
    private String eventPageId;
    private ConcurrentEvent mEventTxt;

    @Override
    protected void preView() {
        super.preView();
        barTitle.set(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_web_content, mBaseFrameLayout, true);
        webView = findViewById(R.id.web_content_view);
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

        if ((mIntentUrl.toLowerCase().startsWith("http") && !checkImageUrl(mIntentUrl)) || mIntentUrl.toLowerCase().startsWith("web:")) {
            loadWebUrl();
        } else if (!mIntentUrl.contains("ConcurrentEvent")) { // 同期活动的在onResume()里另外判断
            loadLocalHtml(getHtmName());
        }

        setWebViewClient();

        mBaiduTJ = getIntent().getStringExtra(Constant.BAIDU_TJ);

        // show help
        if (mIntentUrl.contains("ConcurrentEvent")) {
            showEventHelp();
        } else if (mBaiduTJ != null && mBaiduTJ.toLowerCase().contains("hallmap")) {
            showOverallHelp();
        }
    }

    private void showOverallHelp() {
        settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);// 一定要加这句，且一定要设置为true，才会放大
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

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
                Intent intent = new Intent(getApplicationContext(), ScheduleEditActivity.class);
                ScheduleInfo entity = new ScheduleInfo();
                entity.setId(null);
                entity.setTitle(getIntent().getStringExtra("title")); // 千万不能用barTitle.get() ，会错，虽然很奇怪。
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constant.INTENT_SCHEDULE, entity);
                intent.putExtra("title", getString(R.string.title_add_schedule));
                startActivity(intent);
                overridePendingTransPad();
            }
        });

    }

    private boolean isEventHasUpdate() {
        OtherRepository repository = OtherRepository.getInstance();
        return repository.isEventCanUpdate();
    }

    private void intentToUpdateCenter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_update_event));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIntent = new Intent(getApplicationContext(), UpdateCenterActivity.class);
                startActivity(mIntent);
                intent();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIntentUrl.contains("ConcurrentEvent")) {
            // 如果是同期活动详情页，① 在sd/asset中存在，直接打开本地htm ② 本地没有，则下载数据包。
            parseEvent();
            LogUtil.i(TAG,"eventPageId="+eventPageId);
            StringBuilder sbPath = new StringBuilder();
            sbPath.append(rootDir).append(mIntentUrl).append("/").append(getHtmName());
            if (new File(sbPath.toString()).exists()) {
                LogUtil.i(TAG, "sd卡中存在：" + mIntentUrl);
                loadLocalHtml(getHtmName());
            } else if (AppUtil.isFileInAsset("ConcurrentEvent", eventPageId)) {
                loadLocalHtml(getHtmName());
            } else {
                // 更新中心中是否有更新，有，跳转到更新中心，返回时resume里打开html；无，直接下载zip包
                if (isEventHasUpdate()) {
                    LogUtil.i(TAG, "跳转到更新中心");
                    intentToUpdateCenter();
                } else {
                    LogUtil.i(TAG, "下载：" + mIntentUrl);
                    downloadSingleEventZip();
                }
            }
        }
    }

    private void parseEvent() {
        mEventTxt = Parser.parseJsonFilesDirFile(ConcurrentEvent.class, Constant.TXT_CONCURRENT_EVENT);
        int size = mEventTxt.pages.size();
        eventPageId = "";
        for (int i = 0; i < size; i++) {
            if (mIntentUrl.contains(mEventTxt.pages.get(i).pageID)) {
                eventPageId = mEventTxt.pages.get(i).pageID;
                barTitle.set(mEventTxt.pages.get(i).getTitle());
                LogUtil.i(TAG, "mIntentUrl=" + mIntentUrl + ",pageID=" + eventPageId);
                break;
            }
        }
    }

    private void downloadSingleEventZip() {
        if(mEventTxt==null){
            parseEvent();
        }
        DownloadClient client = ReRxUtils.setupRxtrofit(DownloadClient.class, mEventTxt.htmlFilePath);
        client.downloadFile(eventPageId + ".zip")
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody body = responseBodyResponse.body();
                        boolean isUnZiped = false;
                        if (body != null) {
                            LogUtil.i(TAG, "解压zip");
                            StringBuilder sbDir = new StringBuilder();
                            sbDir.append(rootDir).append(mIntentUrl).append("/");
                            createFile(rootDir+"ConcurrentEvent/");
                            createFile(sbDir.toString());
                            isUnZiped = FileUtil.unpackZip(eventPageId, body.byteStream(), sbDir.toString());
                            sbDir = null;
                        }
                        return isUnZiped;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mEventDisposable = d;
                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "zip解压结果：" + value);
                        if (value) {
                            loadLocalHtml(getHtmName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

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
    }

    private void loadWebUrl() {
        LogUtil.i(TAG, "-- loadWebUrl --");
        webView.loadUrl(mIntentUrl);
    }

    private String getHtmName() {
        return AppUtil.getName("TC.htm", "EN.htm", "SC.htm");
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
                }
                intent();
                return true;
            }
        });
    }

    private boolean startsWith(String abc) {
        return mUrl.toLowerCase().startsWith(abc);
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

    private void http() {
        mIntent = new Intent(WebContentActivity.this, WebViewActivity.class);
        mIntent.putExtra(Constant.WEB_URL, mUrl);
//                    mIntent.putExtra("title", gTitle);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventDisposable != null && !mEventDisposable.isDisposed()) {
            mEventDisposable.dispose();
        }
    }
}
