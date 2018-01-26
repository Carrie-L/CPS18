package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CalendarUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;
import static com.adsale.ChinaPlas.App.mSP_Login;


/**
 * <p>
 * Created by Carrie on 2017/8/10.
 */

public class RegisterViewModel {
    private static final String TAG = "RegisterViewModel";
    private Activity activity;
    private WebView mWebView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public final ObservableBoolean isLoginOrReged = new ObservableBoolean(false);
    private DownloadClient mClient;
    private SharedPreferences sp_reg;
    private Disposable mDisposable0;
    private Disposable mDisposable1;
    private CalendarUtil calendarUtil;
    private File imgFile;

    public RegisterViewModel(Activity activity) {
        this.activity = activity;
    }

    public void start(WebView wv, ImageView iv, ProgressBar pb) {
        mWebView = wv;
        mImageView = iv;
        mProgressBar = pb;
        show();
    }

    public void show() {
        isLoginOrReged.set(AppUtil.isLogin());
        LogUtil.i(TAG, "isLoginOrReged=" + isLoginOrReged.get());
        sp_reg = activity.getSharedPreferences("Prereg", MODE_PRIVATE);
        if (isLoginOrReged.get()) {
            showPicView();
        } else {
            showWebView();
        }
    }

    private void showWebView() {
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.loadUrl(String.format(NetWorkHelper.Register_URL, AppUtil.getUrlLangType(AppUtil.getCurLanguage())));
        setProgressClient();
    }

    private void setProgressClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (mProgressBar.getVisibility() == View.GONE) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private class MyWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            super.shouldOverrideUrlLoading(view, url);
            LogUtil.i(TAG, "shouldOverrideUrlLoading:" + url);

            if (url.contains("PayAPPjump")) {
                getJSValue(view);
            } else if (url.contains("QuickPreRegResult")) {
                view.evaluateJavascript("document.getElementById('QuickPreRegResult').src;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        LogUtil.i(TAG, "shouldOverrideUrlLoading_QuickPreRegResult: " + s);
                        view.loadUrl(TextUtils.isEmpty(s) ? url : s);
                    }
                });
            } else {
                view.loadUrl(url);
            }

            return true;
        }


    }

    private void getJSValue(final WebView view) {
        view.evaluateJavascript("document.getElementById('p_payMethod').value+','+document.getElementById('g_guid').value+','+document.getElementById('p_lang').value+','+document.getElementById('p_image').value;", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "shouldOverrideUrlLoading_onReceiveValue:value=" + value);
                value = value.replaceAll("\"", "");
                sp_reg.edit().putString("p_payMethod", value.split(",")[0])
                        .putString("g_guid", value.split(",")[1])
                        .putString("p_lang", value.split(",")[2])
                        .putString("p_image", value.split(",")[3]).apply();
                LogUtil.i(TAG, "p_payMethod=" + sp_reg.getString("p_payMethod", ""));
                LogUtil.i(TAG, "g_guid=" + sp_reg.getString("g_guid", ""));
                LogUtil.i(TAG, "p_lang=" + sp_reg.getString("p_lang", ""));
                LogUtil.i(TAG, "p_image=" + sp_reg.getString("p_image", ""));
                createPay(view);
            }
        });
    }

    private String getRequestJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payMethod", sp_reg.getString("p_payMethod", ""))
                    .put("guid", sp_reg.getString("g_guid", ""))
                    .put("lang", sp_reg.getString("p_lang", ""));
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void createPay(final WebView view) {
        String json = getRequestJson();
        if (TextUtils.isEmpty(json)) {
            return;
        }
        LogUtil.e(TAG, "json=" + json);
        initClient();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mClient.getRegCharge(requestBody)
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(@NonNull ResponseBody responseBody) throws Exception {
                        return responseBody.string();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable0 = d;
                        //加载确认信
//                        view.loadUrl(String.format(NetWorkHelper.REGISTER_CONFIRM_URL, AppUtil.getUrlLangType(AppUtil.getCurLanguage()), sp_reg.getString("p_image", "")));
                    }

                    @Override
                    public void onNext(@NonNull String response) {
                        Log.i(TAG, "onNext:" + response);
                        sp_reg.edit().putString("charge", response).apply();
                            /* 调起支付 */
                        createPayment();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i(TAG, "onError:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                        mDisposable0.dispose();
                    }
                });


    }

    private void initClient() {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.REGISTER_BASE_URL);
        }
    }

    public void createPayment() {
        Pingpp.createPayment(activity, sp_reg.getString("charge", ""));
    }

    public void paySuccess() {
//        mWebView.loadUrl(String.format(NetWorkHelper.REGISTER_CONFIRM_URL, AppUtil.getUrlLangType(AppUtil.getCurLanguage()), sp_reg.getString("p_image", "")));
        mSP_Login.edit().putBoolean(Constant.IS_LOGIN, true).apply();
        downConfirmImage();
        show();
        AppUtil.trackViewLog( 420, "PS", "", "");
        AppUtil.setStatEvent(activity, "PreregSuccess", "PS");
    }

    private void downConfirmImage() {
        initClient();
        mClient.downConfirmImg(NetWorkHelper.REGISTER_CONFIRM_IMG_URL.concat(regImgName()))
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull ResponseBody responseBody) throws Exception {
                        return AppUtil.saveFileOutput(activity, regImgName(), responseBody.bytes());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable1 = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        sp_reg.edit().putBoolean("RegImg", aBoolean).apply();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mDisposable1.dispose();
                        showPicView();
                    }
                });
    }

    private String regImgName() {
        return sp_reg.getString("p_image", "");
    }

    private void showPicView() {
        imgFile = new File(App.filesDir.concat(regImgName()));
        if (imgFile.exists()) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
            Glide.with(activity).load(imgFile).apply(options).into(mImageView);
        } else {
            LogUtil.e(TAG, "reg图片不存在，下载");
            downConfirmImage();
        }

        showRegText();
    }

    public final ObservableField<String> tvHeader = new ObservableField<>();
    public final ObservableField<String> tvFooter = new ObservableField<>();

    private void showRegText() {
        OtherRepository repository = OtherRepository.getInstance();
        repository.initWebContentDao();
        tvHeader.set(repository.getPreHeader(AppUtil.getCurLanguage()));
        tvFooter.set(repository.getPreFooter(AppUtil.getCurLanguage()));
    }

    public void reset() {
        imgFile = new File(App.filesDir.concat(regImgName()));
        if (imgFile.exists()) {
            boolean deleteImg = imgFile.delete();
            LogUtil.e(TAG, "reset: reg img is exists, so delete it." + deleteImg + ", path=" + App.filesDir.concat(regImgName()));
        }
        AppUtil.putLogout();
        isLoginOrReged.set(false);
        App.mSP_Login.edit().putBoolean("IsPreUser", false).apply();

        AppUtil.trackViewLog( 420, "PR", "", "");
        AppUtil.setStatEvent(activity, "PreregReset", "PR");
        showWebView();
    }

    public void addToCalendar() {
        LogUtil.i(TAG, "addToCalendar");
        addCalendar();
    }

    public void addCalendar() {
        if (calendarUtil == null) {
            calendarUtil = new CalendarUtil(activity);
        }
        calendarUtil.addToCalendar();
    }

    public String getInvoiceUrl() {
        return String.format(NetWorkHelper.REGISTER_INVOICE_URL, AppUtil.getUrlLangType(AppUtil.getCurLanguage()), sp_reg.getString("p_image", ""));
    }

}
