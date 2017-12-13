package com.adsale.ChinaPlas.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.LayoutCameraBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

/**
 * Created by Carrie on 2017/12/3.
 */

public class TakePhotoView extends Dialog implements CameraPreview.OnTakePhotoCallback{
    private final String TAG = "TakePhotoView";
    private View view;
    private CameraPreview cameraPreview;
    private String mAbsPath;
    private Bitmap bitmap;
    private Context mContext;
    private CameraPreview.OnTakePhotoCallback mCallback;

    public TakePhotoView(@NonNull Context context, CameraPreview.OnTakePhotoCallback callback) {
        super(context, R.style.transparentDialog);
        this.mCallback=callback;
        init(context);
    }

    private void init(Context context) {
        LayoutCameraBinding binding = LayoutCameraBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        binding.setView(this);
        binding.executePendingBindings();
        FrameLayout frameLayout = view.findViewById(R.id.camera_frame);
        cameraPreview = new CameraPreview(context,this);
        frameLayout.addView(cameraPreview);
        cameraPreview.init();
    }

    /**
     * 绝对路径。包含文件名
     * @param path
     */
    public void setPhotoAbsPath(String path) {
        mAbsPath = path;
        LogUtil.i(TAG, "mAbsPath=" + mAbsPath);
    }

    public void onTakePhoto() {
        cameraPreview.setAbsPath(mAbsPath);
        if (bitmap != null) {
            onReTakePhoto();
        } else {
             cameraPreview.takePicture();
        }
    }

    public void onReTakePhoto() {

    }

    public void onPhotoOK() {

    }

    public void onPhotoCancel() {
        cancel();
    }

    @Override
    public void onBackPressed() {
        cancel();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraPreview.onStop();
    }

    @Override
    public void cancel() {
        cameraPreview.onStop();
        super.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.height = App.mSP_Config.getInt(Constant.DISPLAY_HEIGHT, 0);
        wl.width = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        wl.gravity = Gravity.TOP;
        window.setAttributes(wl);
    }

    @Override
    public void success(String path, int width, int height) {
        mCallback.success(path,width,height);
        LogUtil.i(TAG, "take photo success > " + path);
        dismiss();
    }
}
