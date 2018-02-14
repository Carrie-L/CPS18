package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.AESCrypt;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.zbar.lib.ZbarManager;
import com.zbar.lib.camera.CameraSetting;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Carrie on 2017/8/30.
 */

public class ScannerViewModel implements Camera.PreviewCallback, Camera.AutoFocusCallback {
    private static final String TAG = "ScannerViewModel";
    public final Integer SCAN_RESULT = 1;
    public final Integer SCAN_FLOOR = 2;

    private Context mContext;
    private final CameraSetting mCameraSetting;

    private int frameX;
    private int frameY;
    private int cropWidth;
    private int cropHeight;
    private int width;
    private int height;
    private ZbarManager manager;
    private CompositeDisposable mDisposables;
    private String mScanData;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;

    public ScannerViewModel(Context context) {
        mContext = context;
        mCameraSetting = CameraSetting.getInstance(context, this,this);
        mDisposables = new CompositeDisposable();
        manager = new ZbarManager();
    }

    public void onStart(final SurfaceHolder holder, final RelativeLayout mCropLayout, final RelativeLayout mContainer, final ImageView ivLine) {
        Observable.create(new ObservableOnSubscribe<TranslateAnimation>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TranslateAnimation> e) throws Exception {
                mCameraSetting.openCamera(holder);
                setCameraFrame(mCropLayout, mContainer);
                mCameraSetting.startPreview();

                TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                        TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
                mAnimation.setDuration(1000);
                mAnimation.setRepeatCount(-1);
                mAnimation.setRepeatMode(Animation.REVERSE);
                mAnimation.setInterpolator(new LinearInterpolator());

                initAudio();

                e.onNext(mAnimation);
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TranslateAnimation>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull TranslateAnimation animation) {
                        ivLine.startAnimation(animation);
                        mCameraSetting.requestAutoFocus();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setCameraFrame(RelativeLayout mCropLayout, RelativeLayout mContainer) {
        LogUtil.i(TAG, "setCameraFrame");
        // 修改zbar有效扫描区域的代码
        Point point = mCameraSetting.getCameraResolution();
        int width = point.y;
        int height = point.x;

        frameX = mCropLayout.getLeft() * width / mContainer.getWidth();
        frameY = mCropLayout.getTop() * height / mContainer.getHeight();

        cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
        cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();


    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                decode(data);
                if (mScanData != null && !mScanData.isEmpty()) {
                    e.onNext(mScanData);
                } else {
                    mCameraSetting.requestPreviewFrame();
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        processScanData();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void decode(byte[] data) {
        Point cameraResolution = mCameraSetting.getCameraResolution();
        width = cameraResolution.x;
        height = cameraResolution.y;

        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }

        int tmp = width;// Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        mScanData = manager.decode(rotatedData, width, height, true, frameX, frameY, cropWidth, cropHeight);
    }

    private void processScanData() {
        playBeepSoundAndVibrate();
        //// TODO: 2017/8/31 其他扫描结果处理
        if (mScanData.startsWith("UserNC")) {
            userNC();
        }
    }

    private void userNC() {
        String nameCardInfo = mScanData.split(":")[1];
        try {
            nameCardInfo = AESCrypt.decrypt(AESCrypt.AESPASSWORD, nameCardInfo);
            LogUtil.i(TAG, "--nameCardInfo=" + nameCardInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String name="";
        String email="";
        if (nameCardInfo.contains("###")) {
            name=nameCardInfo.split("###")[2];
            email=nameCardInfo.split("###")[5];
            LogUtil.i(TAG, "nameCardInfo.decrypt=" + name);
            // 设备ID###公司###姓名###职位###电话###邮箱###QQ
        }
        AppUtil.trackLog(mContext,400,429,"ScanQrcode","VisitorNamecard",email,"SQ_VisitorNameCard_");

        if (mListener != null) {
            Bundle bundle = new Bundle();
            bundle.putString("result", mScanData);
            bundle.putInt("intentType", SCAN_RESULT);
            mListener.intentTo(bundle);
        }
    }




    /**
     * 原理：
     * 初始化相机，设置一些相机参数；
     * 绑定SurfaceView，在SurfaceView上显示预览图像；
     * 获取相机的一帧图像；
     * 对图像进行一定的预处理，只保留亮度信息，成为灰度图像；
     * 对灰度图像进行二维码解析，解析成功进入下一步，不成功回到第③步；
     * 返回解析结果并退出。
     */



    private void initAudio() {
        playBeep = true;
        AudioManager audioService = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        LogUtil.i(TAG, "playBeep=" + playBeep);
        initBeepSound();
        vibrate = true;
    }

    /*声音和震动*/
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            LogUtil.i(TAG, "initBeepSound");
//            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = mContext.getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private OnScannedListener mListener;

    public void setOnScannedListener(OnScannedListener listener) {
        mListener = listener;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        LogUtil.i(TAG, "onAutoFocus: " + success);
        if (success) {
            mCameraSetting.requestPreviewFrame();
        } else {
            mCameraSetting.requestAutoFocus();
        }
    }

    public interface OnScannedListener {
        void intentTo(Bundle data);
    }

    public void unSubscribe() {
        if(mDisposables!=null){
            mDisposables.clear();
        }
    }

    public void destroy() {
        LogUtil.i("ScanViewModel","destroy()");
        App.mSP_Config.edit().putBoolean("isCameraClose",true).apply();
        mCameraSetting.closeCamera();
    }








}
