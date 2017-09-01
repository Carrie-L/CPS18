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
    private boolean previewing;
    private boolean initialized;
    private final CameraConfigurationManager mConfigurationManager;
    private Camera.Parameters parameter;

    public static CameraSetting getInstance(Context context,@NonNull PreviewCallback callback,@NonNull AutoFocusCallback focusCallback) {
        if (INSTANCE == null) {
            return new CameraSetting(context, callback,focusCallback);
        }
        return INSTANCE;
    }

    public CameraSetting(Context context,  @NonNull PreviewCallback callback,AutoFocusCallback focusCallback) {
        mPreviewCallback = callback;
        mFocusCallback=focusCallback;
        mContext = context;
        mConfigurationManager = new CameraConfigurationManager(mContext);
    }

    public void openCamera(SurfaceHolder holder) {
        mHolder = holder;
        if (camera == null) {
            previewing=false;
            LogUtil.i(TAG, "openCamera: camera == null");
            camera = Camera.open();
            setPreviewDisplay();
            initCameraParameters();
        } else {
            setPreviewDisplay();
        }
    }

    private void setPreviewDisplay() {
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCameraParameters() {
        if (!initialized) {
            initialized = true;
            mConfigurationManager.initFromCameraParameters(camera);
        }
        mConfigurationManager.setDesiredCameraParameters(camera);
    }

    public void startPreview() {
        LogUtil.i("CameraSetting", "startPreview0: previewing=" + previewing);
        if (camera != null) {
            LogUtil.i("CameraSetting", "camera!=null");
        }

        if (camera != null && !previewing) {
            LogUtil.i("CameraSetting", "startPreview1");
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

    public void closeCamera() {
        mHolder = null;
        mPreviewCallback = null;

        if (camera != null) {
            camera.setOneShotPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


}
