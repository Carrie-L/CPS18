package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.adsale.ChinaPlas.databinding.ActivityLoginBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private WebView webView;
    private String mWebUrl;
    private ProgressBar progressBar;
    private WebSettings settings;

    private ObservableBoolean isCapturing = new ObservableBoolean(false);
    private final String mImgPath = App.filesDir + "confirm.jpg";

    @Override
    protected void initView() {
        setBarTitle(AppUtil.isLogin() ? R.string.title_my_chinaplas : R.string.title_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        webView = binding.webView;
        progressBar = binding.progressBar1;

    }

    @Override
    protected void initData() {
        mWebUrl = getIntent().getStringExtra(Constant.WEB_URL);
        LogUtil.i(TAG, "mWebUrl=" + mWebUrl);
        initWebView();
        getJSValue();
        webView.loadUrl(mWebUrl);
    }

    private void getAPIData(){
        LoadingClient mClient = ReRxUtils.setupRxtrofit(LoadingClient.class, NetWorkHelper.BASE_URL_CPS);
        mClient.getData(NetWorkHelper.API_TOKEN)
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {

                        return null;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });


    }

    private void getJSValue() {
        webView.setWebViewClient(new WebViewClient() {


            private String guid;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                LogUtil.i(TAG, "shouldOverrideUrlLoading: URL = " + url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                LogUtil.i(TAG, "onPageStarted: URL = " + url);

                if (!TextUtils.isEmpty(mAppToken) && url.contains("mychinaplas")) { // 此时说明 再次执行了 https://www.chinaplasonline.com/CPS19/mychinaplas/simp?device=APP ，说明点击了退出按钮
                    LogUtil.i(TAG, "你是想退出吧 ？？？ ");
                    AppUtil.setLogin(false);
                    canGetToken = false;
                    LogUtil.i(TAG, "onPageStarted: 未登录");
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.i(TAG, "onPageFinished : " + url);

                if (url.contains("PreregSuccess") && url.contains("device") && url.contains("guid")) {
                    // visa确认信链接示例：https://www.chinaplasonline.com/CPS19/PreregSuccess/simp/?device=mobileapp&guid=68E19706D7DE4F90A925E48DF2C9ACF0&Ref=CPS19_PR_Mob_E24271408_C3553
                    // visa支付，且显示确认信图片，则截图

                    guid = AppUtil.subStringLast(url, "=");
                    LogUtil.i(TAG, "onPageFinished guid = " + guid);
                    if (!TextUtils.isEmpty(guid)) {
                        setGuid(guid);
                        LogUtil.i(TAG, "-------MyCHinaplas截图");
                        startCapture();
                    }
                }
                if (url.contains("GetGuid")) {  //  https://www.chinaplasonline.com/CPS19/CPSAPI/GetGuid
                    canGetToken = true;
                }
                view.evaluateJavascript("var element;\n" +
                        "\telement = document.getElementById(\"AppToken\");\n" +
                        "\tif(element == null){}else{document.getElementById(\"AppToken\").value;}", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        LogUtil.e(TAG, "onPageFinished_onReceiveValue:AppToken=" + value);
                        if (!TextUtils.isEmpty(value) && !value.equals("null")) {
                            AppUtil.setLogin(true);
                            LogUtil.i(TAG, "登录成功");
                            mAppToken = value;
                            AppUtil.setToken(value);

                            AppUtil.trackViewLog(429, "Login", "OK", "");
                            AppUtil.setStatEvent(getApplicationContext(), "Login", "Login_OK");

                            // 如果是从主界面MyChinaplas点击跳转而来，则关闭这个页面，显示用户小工具；
                            if (getIntent().getBooleanExtra("MyTool", false)) {
                                Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransPad();
                            } else if (getIntent().getBooleanExtra("ExhibitorInfo", false)) {
                                finish();
                                overridePendingTransPad();
                            }
                        } else {
                            AppUtil.setLogin(false);
                            canGetToken = false;
                            LogUtil.i(TAG, "未登录");
//                                AppUtil.trackViewLog(429, "Login", "OK", "");
//                                AppUtil.setStatEvent(getApplicationContext(), "Login", "Login_CANCEL");
                        }
                    }
                });
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String myChinaplasUrl() {
        return String.format("https://www.chinaplasonline.com/CPS19/mychinaplas/%s?device=APP", AppUtil.getLanguageUrlType());
    }

    private boolean canGetToken = false;  // 当点击登录后会跳转  https://www.chinaplasonline.com/CPS19/CPSAPI/GetGuid 链接，在这个链接后 会跳转 https://www.chinaplasonline.com/CPS19/mychinaplas/simp?device=APP
    // 链接，此时在加载完成后，能拿到返回的AppToken，代表已经登录成功。
    private String mAppToken = "";

    private void setGuid(String guid) {
        App.mSP_Login.edit().putString("LoginGuid", guid).apply();
    }

    private String getGuid() {
        return App.mSP_Login.getString("LoginGuid", "");
    }

    private void initWebView() {
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); //如果访问的页面中有Javascript，则WebView必须设置支持Javascript
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (progressBar.getVisibility() == View.GONE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

    }

    private void logoutBack() {
        // 从 MyChinaplas列表跳转过来，且点击了 Logout ，则返回主界面
        if (getIntent().getBooleanExtra("MyCPS", false)) {
            Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransPad();
        }
    }

    @Override
    public void back() {
        logoutBack();
        super.back();
    }

    @Override
    public void onBackPressed() {
        logoutBack();
        super.onBackPressed();
    }

    private void startCapture() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(1) // 等待1s 让网页加载完成再截图,否则会报错
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long v) throws Exception {
                        return 1 - v;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        isCapturing.set(true);
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        LogUtil.i(TAG, "倒计时  " + aLong);
                        isCapturing.set(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (!TextUtils.isEmpty(getGuid())) {
                            LogUtil.i(TAG, "-------kaishi截图啦------");
                            isCapturing.set(true);
                            captureScreen();
                        }
                    }
                });
    }

    private void captureScreen() {
        isCapturing.set(true);
        Bitmap bitmap = getWebViewBitmap(getApplicationContext(), webView);

        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        App.mSP_Config.edit().putInt("imgWidth", imgWidth).putInt("imgHeight", imgHeight).apply();

        File imgFile = new File(mImgPath);

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
        webView.clearHistory();
        isCapturing.set(false);
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

    private Bitmap getViewBitmap(View v) {
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
}
