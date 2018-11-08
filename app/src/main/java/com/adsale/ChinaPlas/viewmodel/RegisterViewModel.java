package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.data.model.PayResult;
import com.adsale.ChinaPlas.ui.ImageActivity;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CalendarUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;
import static com.adsale.ChinaPlas.App.mSP_Config;
import static com.adsale.ChinaPlas.App.mSP_Login;
import static com.adsale.ChinaPlas.helper.LoadingReceiver.LOADING_ACTION;
import static com.adsale.ChinaPlas.utils.NetWorkHelper.MY_CHINAPLAS_URL;
import static com.adsale.ChinaPlas.utils.NetWorkHelper.Register_URL;

/**
 * <p>
 * Created by Carrie on 2017/8/10.
 */

public class RegisterViewModel {
    private static final String TAG = "RegisterViewModel";
    private static final String CONFIRM_PAGE_NAME = "Wed Nov 07 18:05:45 GMT+08:00 2018confirm.jpg";
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
    private String imgUrl;

    private final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CPS19/";
    private final String mImgPath = rootPath + CONFIRM_PAGE_NAME;

    public RegisterViewModel(Context context, CalendarUtil calendarUtil, OnIntentListener listener) {
        this.mContext = context;
        this.calendarUtil = calendarUtil;
        this.mIntentListener = listener;
    }

    public void start(WebView wv, ImageView iv, ProgressBar pb) {
        mWebView = wv;
        mImageView = iv;
        mProgressBar = pb;
    }

    public void show() {
        isLoginOrReged.set(AppUtil.isLogin());
        LogUtil.i(TAG, "isLoginOrReged=" + isLoginOrReged.get());
        sp_reg = mContext.getSharedPreferences("Prereg", MODE_PRIVATE);


        // just for test screen
//        isLoginOrReged.set(false);
//        getPostStatus();


        if (isLoginOrReged.get()) {
            LogUtil.i(TAG, " showPicView()");
            showPicView();
        } else if (getPingPay() && !isLoginOrReged.get()) { // Ping++返回的值表示付款成功了，但sp里表示还没登录，则请求服务器，获取返回status
            LogUtil.i(TAG, "付款了，但是还没请求服务器");
            LogUtil.i(TAG, " getPostStatus()");
            getPostStatus();
        } else {
            LogUtil.i(TAG, " showWebView()");
            showWebView();
        }
    }

    private void showWebView() {
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.loadUrl(Register_URL);
        setProgressClient();
    }

    private void showConfirmView() {
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                LogUtil.i(TAG, "showConfirmView - onPageStarted: " + url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.i(TAG, "-------准备截图啦------" + url);
                captureScreen();
            }
        });
        mWebView.loadUrl(String.format(NetWorkHelper.CONFIRM_URL, getGUID()));
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
                LogUtil.i(TAG, "PayAPPjump");
                getJSValue(view);
            } else if (url.contains("QuickPreRegResult")) {
                LogUtil.i(TAG, "QuickPreRegResult");
                view.evaluateJavascript("document.getElementById('QuickPreRegResult').src;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        LogUtil.i(TAG, "shouldOverrideUrlLoading_QuickPreRegResult: " + s);
                        view.loadUrl(TextUtils.isEmpty(s) ? url : s);
                    }
                });
            } else {
                if (url.contains("MyChinaplasForApp")) {
//                getJSValueMyCPS(view);
                    LogUtil.i(TAG, "跳转到MYCHINAPLAS" + url);
                    view.loadUrl(MY_CHINAPLAS_URL);
                } else if (url.contains("langtmp")) {

                } else {
                    LogUtil.i(TAG, "else  url ");
                    view.loadUrl(url);
                }
            }
            return true;
        }
// 2018.05.10 注：VISA支付出现问题

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

//            if (mSaveConfirmPage) {
//
//                LogUtil.i(TAG,"-------准备截图啦------");
//
//                captureScreen();
//
//            }

        }
    }

    private void getJSValue(final WebView view) {
        view.evaluateJavascript("document.getElementById('p_payMethod').value+','+document.getElementById('g_guid').value+','+document.getElementById('p_lang').value;", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                LogUtil.e(TAG, "shouldOverrideUrlLoading_onReceiveValue:value=" + value);

                value = value.replaceAll("\"", "");
                sp_reg.edit().putString("p_payMethod", value.split(",")[0])
                        .putString("g_guid", value.split(",")[1])
                        .putString("p_lang", value.split(",")[2])
//                        .putString("p_image", value.split(",")[3])
                        .apply();
                LogUtil.i(TAG, "p_payMethod=" + sp_reg.getString("p_payMethod", ""));
                LogUtil.i(TAG, "g_guid=" + sp_reg.getString("g_guid", ""));
                LogUtil.i(TAG, "p_lang=" + sp_reg.getString("p_lang", ""));
//                LogUtil.i(TAG, "p_image=" + sp_reg.getString("p_image", ""));
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

    private String getGUID() {
        return sp_reg.getString("g_guid", "");
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
                    }

                    @Override
                    public void onNext(@NonNull String response) {
                        Log.i(TAG, "onNext:" + response);
                        sp_reg.edit().putString("charge", response).apply();
                            /* 调起支付 */
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
        LogUtil.i(TAG, "paySuccess");
        setPingPay(false); // 重置状态
        mSP_Login.edit().putBoolean(Constant.IS_LOGIN, true).apply();
        showConfirmView();
        AppUtil.trackViewLog(420, "PS", "", "");
        AppUtil.setStatEvent(mContext, "PreregSuccess", "PS");
    }

    private Disposable mCountDownDisposable;

    /**
     * 支付完成后（result code = success），主动发送guid到我们的服务器上，查询是否支付成功。
     * status 不为0 则为支付成功。
     * https://epayment.adsale-marketing.com.cn/vreg/PreregMobileSMS/APPSelectPay
     * post参数：{
     * guid = 7AF7D22992C544258A8904ECDE716B8C;
     * }
     * <p>
     * status会出现 有时支付成功但返回0的情况，处理方式是：每隔5s钟请求一次。
     */
    public void getPostStatus() {
        LogUtil.i(TAG, "getPostStatus: guid=" + getGUID());
        initClient();
        RequestBody requestBody = new FormBody.Builder().add("guid", getGUID()).build();  //  sp_reg.getString("g_guid", "")
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
                        } else {
                            // 失败则每5s请求一次
                            countDownRequestStatus();

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

    private static final Integer COUNT_DOWN_SECOND = 2;

    private void countDownRequestStatus() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(COUNT_DOWN_SECOND) // up to 5 items
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long v) throws Exception {
                        return COUNT_DOWN_SECOND - v;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mCountDownDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        LogUtil.i(TAG, "返回0，再次请求状态  " + aLong);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "----------倒计时结束，开始请求------------");
                        mCountDownDisposable.dispose();
                        getPostStatus();

                    }
                });
    }


    private void captureScreen() {
        Bitmap bitmap = getWebViewBitmap(mContext, mWebView);

        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();

        LogUtil.i(TAG, "imgWidth = " + imgWidth + ", imgHeight= " + imgHeight);

        sp_reg.edit().putInt("imgWidth", imgWidth).putInt("imgHeight", imgHeight).apply();


        File file = new File(rootPath);
        file.mkdir();

        File imgFile = new File(mImgPath);


//        File file = new File(App.filesDir + CONFIRM_PAGE_NAME);
        LogUtil.i(TAG, "path = " + mImgPath);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 获取 WebView 视图截图 * @param context * @param view * @return
     */
    private Bitmap getWebViewBitmap(Context context, WebView view) {
        if (null == view) return null;
        view.scrollTo(0, 0);
        view.buildDrawingCache(true);
        view.setDrawingCacheEnabled(true);
        view.setVerticalScrollBarEnabled(false);
        Bitmap b = getViewBitmapWithoutBottom(view);
        // 可见高度
        int vh = view.getHeight();
        // 容器内容实际高度
        int th = (int) (view.getContentHeight() * view.getScale());
        Bitmap temp = null;
        if (th > vh) {
            int w = getScreenWidth(context);
            int absVh = vh - view.getPaddingTop() - view.getPaddingBottom();
            do {
                int restHeight = th - vh;
                if (restHeight <= absVh) {
                    view.scrollBy(0, restHeight);
                    vh += restHeight;
                    temp = getViewBitmap(view);
                } else {
                    view.scrollBy(0, absVh);
                    vh += absVh;
                    temp = getViewBitmapWithoutBottom(view);
                }
                b = mergeBitmap(vh, w, temp, 0, view.getScrollY(), b, 0, 0);
            } while (vh < th);
        }
        // 回滚到顶部
        view.scrollTo(0, 0);
        view.setVerticalScrollBarEnabled(true);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return b;
    }

    public Bitmap getViewBitmap(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
        v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }

    private Bitmap getViewBitmapWithoutBottom(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
        v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        Bitmap bp = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight() - v.getPaddingBottom());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return bp;
    }

    /**
     * 拼接图片 * @param newImageH * @param newImageW * @param background * @param backX * @param backY * @param foreground * @param foreX * @param foreY * @return
     */
    private Bitmap mergeBitmap(int newImageH, int newImageW, Bitmap background, float backX, float backY, Bitmap foreground, float foreX, float foreY) {
        if (null == background || null == foreground) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(newImageW, newImageH, Bitmap.Config.RGB_565);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(background, backX, backY, null);
        cv.drawBitmap(foreground, foreX, foreY, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return bitmap;
    }

    /**
     * get the width of screen
     */
    public int getScreenWidth(Context ctx) {
        int w = 0;
        Point p = new Point();
        ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
        w = p.x;
        return w;
    }

    private String regImgName() {
        return sp_reg.getString("p_image", "");
    }

    private void showPicView() {
//        imgFile = new File(App.filesDir + regImgName());
        imgFile = new File(mImgPath);
        LogUtil.i(TAG, "showPicView:" + imgFile.getAbsolutePath());

        if (imgFile.exists()) {
            LogUtil.i(TAG, "imgFile.exists()");

            //.override(IMG_WIDTH, IMG_HEIGHT)

            Bitmap bitmap = BitmapFactory.decodeFile(mImgPath);
            int imgWidth = bitmap.getWidth();
            int imgHeight = bitmap.getHeight();

            LogUtil.i(TAG, "imgWidth = " + imgWidth + ", imgHeight= " + imgHeight);


            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).override(imgWidth,imgHeight);// 保存在本地的图片，由于文件名都是reg.png，为了防止重置后图片仍显示之前的缓存图片，因此设置不缓存
            Glide.with(mContext).load(imgFile).apply(options).into(mImageView);
            imgUrl = imgFile.getAbsolutePath();
        } else {
            LogUtil.e(TAG, "!imgFile.exists()");
        }
    }

    public final ObservableField<String> tvHeader = new ObservableField<>();
    public final ObservableField<String> tvFooter = new ObservableField<>();

    public void reset() {
        imgFile = new File(mImgPath);
        if (imgFile.exists()) {
            boolean deleteImg = imgFile.delete();
            LogUtil.e(TAG, "reset: reg img is exists, so delete it." + deleteImg + ", path=" + mImgPath);
        }
        AppUtil.putLogout();
        isLoginOrReged.set(false);
        App.mSP_Login.edit().putBoolean("IsPreUser", false).apply();

        AppUtil.trackViewLog(420, "PR", "", "");
        AppUtil.setStatEvent(mContext, "PreregReset", "PR");
        showWebView();
    }

    private OnIntentListener mIntentListener;

    public void onImageClick() {
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

    }

}
