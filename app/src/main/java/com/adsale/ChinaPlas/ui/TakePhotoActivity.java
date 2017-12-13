package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityTakePhotoBinding;
import com.adsale.ChinaPlas.ui.view.CameraPreview;

public class TakePhotoActivity extends BaseActivity implements CameraPreview.OnTakePhotoCallback {
    private final String TAG = "TakePhotoActivity";
    private CameraPreview cameraPreview;
    private String mAbsPath;

    @Override
    protected void initView() {
        ActivityTakePhotoBinding binding = ActivityTakePhotoBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();
        FrameLayout frameLayout = binding.cameraFrame;
        cameraPreview = new CameraPreview(this, this);
        frameLayout.addView(cameraPreview);
        cameraPreview.init();
    }

    @Override
    protected void initData() {
        mAbsPath = getIntent().getStringExtra("absPath");
    }

    public void onTakePhoto() {
        cameraPreview.setAbsPath(mAbsPath);
        cameraPreview.takePicture();
    }

    @Override
    public void success(String path, int width, int height) {
//        mCallback.success(path, width, height);
        Intent intent =  new Intent();
        intent.putExtra("path",path);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void onPhotoCancel() {
        stop();
        finish();
    }

    @Override
    public void onBackPressed() {
        stop();
        super.onBackPressed();
    }

    private void stop() {
        if (cameraPreview != null) {
            cameraPreview.onStop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

}
