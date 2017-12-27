package com.adsale.ChinaPlas.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Carrie on 2017/12/19.
 */

public class M2Dialog extends AlertDialog {
    private final String TAG = "M2Dialog";
    private Activity activity;
    private Disposable mM2Disposable;
    private String mUrl;
    private final Integer SECOND = 3;

    public M2Dialog(Activity activity) {
        super(activity, R.style.MyDialog);
        this.activity = activity;
    }

    public void setUrl(String url) {
        LogUtil.i(TAG, "setUrl:" + url);
        mUrl = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");

        View view = LayoutInflater.from(activity).inflate(R.layout.imageview, null);
        Glide.with(activity).load(Uri.parse(mUrl)).into((ImageView) view.findViewById(R.id.image_view));
        setContentView(view);

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        int sw = AppUtil.getScreenWidth();
        boolean isTablet = AppUtil.isTablet();
        int m2BigWidth = isTablet ? 920 : 640;
        int m2BigHeight = isTablet ? 335 : 400;
        wl.height = (sw * m2BigHeight) / m2BigWidth;
        wl.width = sw;
        wl.gravity = Gravity.BOTTOM;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(wl);
        window.setWindowAnimations(R.style.M2AnimStyle);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        closeDialogAnim();

    }

    /**
     * 停留2s，再關閉
     */
    private void closeDialogAnim() {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(SECOND) // up to SECOND items
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long v) throws Exception {
                        return SECOND - v;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mM2Disposable = d;
                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mM2Disposable != null && mM2Disposable.isDisposed()) {
                            mM2Disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismiss();
                        LogUtil.i(TAG, "倒计时结束");
                        if (mM2Disposable != null && mM2Disposable.isDisposed()) {
                            mM2Disposable.dispose();
                        }
                    }
                });
    }
}
