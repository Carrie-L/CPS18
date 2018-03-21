package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
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
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.data.model.PayResult;
import com.adsale.ChinaPlas.ui.ImageActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CalendarUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
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
    private static final Integer IMG_WIDTH = 600;
    private static final Integer IMG_HEIGHT = 1240;
    private Context mContext;
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
    private Disposable mDisposable2;
    private Disposable mImgDisposable;
    private String imgUrl;

    public RegisterViewModel(Context context, CalendarUtil calendarUtil,OnIntentListener listener) {
        this.mContext = context;
        this.calendarUtil = calendarUtil;
        this.mIntentListener = listener;
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
        sp_reg = mContext.getSharedPreferences("Prereg", MODE_PRIVATE);

        if (isLoginOrReged.get()) {
            showPicView();
        } else if (getPingPay() && !isLoginOrReged.get()) { // Ping++返回的值表示付款成功了，但sp里表示还没登录，则请求服务器，获取返回status
            LogUtil.i(TAG, "付款了，但是还没请求服务器");
            getPostStatus();
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
//                        createPayment();
                        mWebCallback.createPay();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.e(TAG, "mDisposable0_onError:" + e.getMessage());
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

    private void paySuccess() {
        setPingPay(false); // 重置状态
        mSP_Login.edit().putBoolean(Constant.IS_LOGIN, true).apply();
        downConfirmImage();
        show();
        AppUtil.trackViewLog(420, "PS", "", "");
        AppUtil.setStatEvent(mContext, "PreregSuccess", "PS");
    }

    /**
     * 支付完成后（result code = success），主动发送guid到我们的服务器上，查询是否支付成功。
     * status 不为0 则为支付成功。
     * https://epayment.adsale-marketing.com.cn/vreg/PreregMobileSMS/APPSelectPay
     * post参数：{
     * guid = 7AF7D22992C544258A8904ECDE716B8C;
     * }
     */
    public void getPostStatus() {
        LogUtil.i(TAG, "getPostStatus:" + sp_reg.getString("g_guid", ""));
        initClient();
        RequestBody requestBody = new FormBody.Builder().add("guid", sp_reg.getString("g_guid", "")).build();  //  sp_reg.getString("g_guid", "")
        mClient.confirmPay(requestBody).map(new Function<ResponseBody, Boolean>() {
            @Override
            public Boolean apply(ResponseBody responseBody) throws Exception {
                if (responseBody != null) {
                    String payResult = responseBody.string();
                    LogUtil.i(TAG, "payResult=" + payResult);
                    PayResult entity = Parser.parseJson(PayResult.class, payResult);
                    LogUtil.i(TAG, "entity=" + entity.toString());
                    return entity.Status != null && !"0".equals(entity.Status);
                }
                return false;
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable1 = d;
                    }

                    @Override
                    public void onNext(Boolean value) {
                        LogUtil.i(TAG, "value=" + value);
                        if (value) {
                            paySuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, "mDisposable1_onError:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mDisposable1.dispose();
                    }
                });
    }

    private void downLoginImage() {
        LogUtil.i(TAG, "downLoginImage");
        initClient();
        mClient.regGetData(NetWorkHelper.getRegRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EmailVisitorData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mImgDisposable = d;
                    }

                    @Override
                    public void onNext(EmailVisitorData value) {
                        imgUrl = value.VisitorData.RegImageName;
                        LogUtil.i(TAG, "EmailVisitorData: " + imgUrl);
                        AppUtil.setRegImgUrl(imgUrl);
                        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(IMG_WIDTH, IMG_HEIGHT);
                        Glide.with(mContext).load(imgUrl).apply(options).into(mImageView);

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "onError:" + e.getMessage());
                        mImgDisposable.dispose();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void downConfirmImage() {
        initClient();
        mClient.downConfirmImg(NetWorkHelper.REGISTER_CONFIRM_IMG_URL.concat(regImgName()))
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull ResponseBody responseBody) throws Exception {
                        return AppUtil.saveFileOutput(mContext, regImgName(), responseBody.bytes());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable2 = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        sp_reg.edit().putBoolean("RegImg", aBoolean).apply();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.e(TAG, "mDisposable2_onError:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mDisposable2.dispose();
                        showPicView();
                    }
                });
    }

    private String regImgName() {
        return sp_reg.getString("p_image", "");
    }

    private void showPicView() {

        imgFile = new File(App.filesDir + regImgName());

        LogUtil.i(TAG, "showPicView:" + imgFile.getAbsolutePath());

        if (!TextUtils.isEmpty(regImgName())) {

            LogUtil.i(TAG, "imgFile.exists()");

            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(IMG_WIDTH, IMG_HEIGHT);// 保存在本地的图片，由于文件名都是reg.png，为了防止重置后图片仍显示之前的缓存图片，因此设置不缓存
            Glide.with(mContext).load(imgFile).apply(options).into(mImageView);
            imgUrl = imgFile.getAbsolutePath();

        } else if (!TextUtils.isEmpty(AppUtil.getRegImgUrl())) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(IMG_WIDTH, IMG_HEIGHT);
            Glide.with(mContext).load(AppUtil.getRegImgUrl()).apply(options).into(mImageView);
            imgUrl = AppUtil.getRegImgUrl();

        } else if (!TextUtils.isEmpty(AppUtil.getUserEmail())) { /* 登录之后，得到email data,显示其中的Image url */
            LogUtil.i(TAG, "!! imgFile.exists()");
            downLoginImage();
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

        AppUtil.trackViewLog(420, "PR", "", "");
        AppUtil.setStatEvent(mContext, "PreregReset", "PR");
        showWebView();
    }

    private OnIntentListener mIntentListener;
    public void onImageClick(){
        mIntentListener.onIntent(imgUrl, ImageActivity.class);
    }

    public void addToCalendar() {
        LogUtil.i(TAG, "addToCalendar");
        calendarUtil.addToCalendar();
    }

    public String getInvoiceUrl() {
        return String.format(NetWorkHelper.REGISTER_INVOICE_URL, AppUtil.getUrlLangType(AppUtil.getCurLanguage()), sp_reg.getString("p_image", ""));
    }

    public String getCharge() {
        return sp_reg.getString("charge", "");
    }

    public void setPingPay(boolean bool) {
        sp_reg.edit().putBoolean("PingPay", bool).apply();
    }

    public boolean getPingPay() {
        return sp_reg.getBoolean("PingPay", false);
    }

    public interface OnWebViewLoadCallback {
        void load(String url);

        void createPay();
    }

    private OnWebViewLoadCallback mWebCallback;

    public void setOnWebViewLoadCallback(OnWebViewLoadCallback callback) {
        mWebCallback = callback;
    }

    public void dispose() {
        if (mDisposable0 != null && !mDisposable0.isDisposed()) {
            mDisposable0.dispose();
        }
        if (mDisposable1 != null && !mDisposable1.isDisposed()) {
            mDisposable1.dispose();
        }
        if (mDisposable2 != null && !mDisposable2.isDisposed()) {
            mDisposable2.dispose();
        }
//        if (mImgDisposable != null && !mImgDisposable.isDisposed()) {
//            mImgDisposable.dispose();
//        }
    }

}
