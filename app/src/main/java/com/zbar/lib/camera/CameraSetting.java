package com.zbar.lib.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.IOException;

import static android.content.ContentValues.TAG;


/**
 * Created by Carrie on 2017/8/30.
 */

public class CameraSetting {
    public static CameraSetting INSTANCE;
    private Camera camera;
    private SurfaceHolder mHolder;
    private PreviewCallback mPreviewCallback;
    private AutoFocusCallback mFocusCallback;
    private Context mContext;
    private boolean previewing ;
    private boolean initialized;
    private final CameraConfigurationManager mConfigurationManager;
    private Camera.Parameters parameter;

    public static CameraSetting getInstance(Context context, @NonNull AutoFocusCallback focusCallback, @NonNull PreviewCallback callback) {
        if (INSTANCE == null) {
            return new CameraSetting(context, focusCallback, callback);
        }
        return INSTANCE;
    }

    public CameraSetting(Context context, @NonNull AutoFocusCallback focusCallback, @NonNull PreviewCallback callback) {
        mFocusCallback=focusCallback;
        mPreviewCallback = callback;
        mContext = context;
        mConfigurationManager = new CameraConfigurationManager(mContext);
    }

    public void openCamera(SurfaceHolder holder) {
        mHolder=holder;
        if (camera == null) {
            LogUtil.i(TAG,"openCamera: camera == null");
            camera = Camera.open();
            initCameraParameters();
            FlashlightManager.enableFlashlight();
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCameraParameters(){
        if (!initialized) {
            initialized = true;
            mConfigurationManager.initFromCameraParameters(camera);
        }
        mConfigurationManager.setDesiredCameraParameters(camera);
    }

    public void startPreview() {
        LogUtil.i("CameraSetting","startPreview0: previewing="+previewing);
        if(camera!=null){
            LogUtil.i("CameraSetting","camera!=null");
        }

        if (camera != null && !previewing) {
            LogUtil.i("CameraSetting","startPreview1");
            camera.startPreview();
            previewing = true;
        }
    }

    public void requestPreviewFrame() {
        if (camera != null && previewing) {
            camera.setOneShotPreviewCallback(mPreviewCallback);
        }
    }

    public void requestAutoFocus() {
        if (camera != null && previewing) {
            camera.autoFocus(mFocusCallback);
        }
    }

    public Point getCameraResolution() {
        return mConfigurationManager.getCameraResolution();
    }

    public void openLight() {
        if (camera != null) {
            parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
        }
    }

    public void offLight() {
        if (parameter != null && camera != null) {
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
        }
    }

    public void stopPreview() {
        if (camera != null && previewing) {
            camera.stopPreview();
            previewing = false;
        }
    }

    public void closeCamera() {
        mHolder = null;
        mPreviewCallback = null;

        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.setOneShotPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


}
