package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by Carrie on 2017/12/3.
 * 照片暂时不保存在数据库，而是在本地。
 * companyId/companyId_0.jpg
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraView";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Uri outputMediaFileUri;
    private boolean safeToTakePicture = false;
    private List<Camera.Size> mPreviewSizes;
    private Camera.Parameters parameters;
    private boolean success;

    private OnTakePhotoCallback mCallback;
    private int previewWidth;
    private int previewHeight;
    private String mAbsPath;
    private final SharedPreferences mSP_config;

    public CameraPreview(Context context, OnTakePhotoCallback callback) {
        super(context);
        this.mCallback = callback;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSP_config = App.mSP_Config;
    }

    public Camera init() {
        LogUtil.i(TAG, "--init--");
        mCamera = Camera.open();
        mCamera.stopPreview();
        setCameraParameters();
        mCamera.startPreview();
        safeToTakePicture = true;
        return mCamera;
    }

    public void setAbsPath(String path) {
        this.mAbsPath = path;
    }

    public void takePicture() {//final ImageView view
        success = false;
        if (safeToTakePicture && mCamera != null) {
            LogUtil.i(TAG, "拍照");
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    success = saveToMemory(data);
                    LogUtil.i(TAG, "takePicture:success=" + success);
                    if (success) {
                        mCamera.stopPreview();
                        safeToTakePicture = false;
                        if (mCallback != null) {
                            mCallback.success(mAbsPath, previewHeight, previewWidth);//横宽倒置，因为e.g.:preHeight=960,previewWidth=1280
                        }
                    }
                }
            });
            safeToTakePicture = false;
        }
    }

    private boolean saveToMemory(byte[] data) {
        /* 以 companyId 作为文件夹名称, companyId_1.jpg 作为文件名 */
//        String dir = App.rootDir.concat(name.split("_")[0]).concat("/");
//        sdDir = FileUtil.getSDRootPath().concat("CPS18/").concat(name.split("_")[0]).concat("/");
//        File file = new File( FileUtil.getSDRootPath().concat("CPS18/"));
//        if(!file.exists()){
//            file.mkdir();
//        }
        LogUtil.i(TAG, "saveToMemory: mAbsPath=" + mAbsPath);
        return FileUtil.saveToMemory(mAbsPath, data);
    }

    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
            safeToTakePicture = true;
            LogUtil.i(TAG, "startPreview");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
                safeToTakePicture = true;
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            safeToTakePicture = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        LogUtil.i(TAG, "surfaceChanged");
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
            safeToTakePicture = false;
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        int rotation = getDisplayOrientation();
        mCamera.setDisplayOrientation(rotation);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(rotation);
        mCamera.setParameters(parameters);
        adjustDisplayRatio(rotation);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            safeToTakePicture = true;
        } catch (Exception e) {
            Log.d("MotionDetector", "Error starting camera preview: " + e.getMessage());
            safeToTakePicture = false;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mHolder.removeCallback(this);
        onStop();
    }

    private void setCameraParameters() {
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setPictureFormat(ImageFormat.JPEG);
            previewWidth = mSP_config.getInt("PreviewWidth", 0);
            previewHeight = mSP_config.getInt("PreviewHeight", 0);
            int pictureWidth = mSP_config.getInt("PictureWidth", 0);
            int pictureHeight = mSP_config.getInt("PictureHeight", 0);
            if (previewWidth == 0 && previewHeight == 0) {
                setPreviewSize();
            } else {
                parameters.setPreviewSize(previewWidth, previewHeight);
                parameters.setPictureSize(pictureWidth, pictureHeight);
            }
            LogUtil.i(TAG, "previewWidth=" + previewWidth + ",previewHeight=" + previewHeight);
            LogUtil.i(TAG, "pictureWidth=" + pictureWidth + ",pictureHeight=" + pictureHeight);

            parameters.setJpegQuality(100);//设置照片品质，100最高
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//设置对焦模式
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(parameters);
        }
    }

    private void setPreviewSize() {
        //设置被支持的预览界面与图片的size
        mPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = getOptimalSize(mPreviewSizes);
        LogUtil.i(TAG, "getOptimalSize: " + previewSize.width + "x" + previewSize.height);
//            parameters.setPreviewSize(1280,960);//设置预览分辨率
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        //设置被支持的预览界面与图片的size
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size pictureSize = getOptimalSize(pictureSizes);
        LogUtil.i(TAG, "pictureSize: " + pictureSize.width + "x" + pictureSize.height);
//            parameters.setPictureSize(3264,2448);//设置照片分辨率
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        App.mSP_Config.edit().putInt("PreviewWidth", previewSize.width).putInt("PreviewHeight", previewSize.height).putInt("PictureWidth", pictureSize.width).putInt("PictureHeight", pictureSize.height).apply();
    }

    private void adjustDisplayRatio(int rotation) {
        ViewGroup parent = ((ViewGroup) getParent());
        Rect rect = new Rect();
        parent.getLocalVisibleRect(rect);
        int width = rect.width();
        int height = rect.height();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        int previewWidth;
        int previewHeight;
        if (rotation == 90 || rotation == 270) {
            previewWidth = previewSize.height;
            previewHeight = previewSize.width;
        } else {
            previewWidth = previewSize.width;
            previewHeight = previewSize.height;
        }

        if (width * previewHeight > height * previewWidth) {
            final int scaledChildWidth = previewWidth * height / previewHeight;

            layout((width - scaledChildWidth) / 2, 0,
                    (width + scaledChildWidth) / 2, height);
        } else {
            final int scaledChildHeight = previewHeight * width / previewWidth;
            layout(0, (height - scaledChildHeight) / 2,
                    width, (height + scaledChildHeight) / 2);
        }
    }

    public int getDisplayOrientation() {
        Camera.CameraInfo camInfo =
                new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return (camInfo.orientation - degrees + 360) % 360;
    }

    private Camera.Size getOptimalSize(List<Camera.Size> sizes) {
//        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) 4 / 3;
        LogUtil.i(TAG, "targetRatio=" + targetRatio);
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = 960;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        LogUtil.i(TAG, "optimalSize 0 =" + optimalSize);
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            LogUtil.i(TAG, "optimalSize == null");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        LogUtil.i(TAG, "optimalSize 1 =" + optimalSize);
        return optimalSize;
    }

    public void onStop() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            safeToTakePicture = false;
        }
    }

    public void onPause() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            safeToTakePicture = false;
        }
    }

    /**
     * Created by new on 2017/5/9.
     * 拍照完成时的监听
     */
    public interface OnTakePhotoCallback {
        /**
         * 拍照、照片保存成功
         *
         * @param path   照片保存路径，绝对路径
         * @param width  宽：camera的预览尺寸(previewHeight)
         * @param height 高：camera的预览尺寸(previewWidth)
         */
        void success(String path, int width, int height);
    }
}
