package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityScannerBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ScannerViewModel;

import java.io.IOException;

public class ScannerActivity extends BaseActivity implements SurfaceHolder.Callback,ScannerViewModel.OnScannedListener {

    private ActivityScannerBinding binding;
    private ScannerViewModel viewModel;
    private SurfaceHolder mHolder;
    private boolean hasSurface;

    @Override
    protected void initView() {
        binding = ActivityScannerBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
    }

    @Override
    protected void initData() {
        viewModel = new ScannerViewModel(getApplicationContext());
        viewModel.setOnScannedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHolder=binding.capturePreview.getHolder();
        if(hasSurface){
            LogUtil.i(TAG,"hasSurface");
            initCamera(mHolder);
        }else{
            LogUtil.i(TAG,"! hasSurface");
            mHolder.addCallback(this);
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initCamera(SurfaceHolder holder){
        viewModel.onStart(holder,binding.captureCropLayout,binding.rlScanner,binding.captureScanLine);
//        viewModel.onResume();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG,"surfaceCreated");
        initCamera(holder);
        hasSurface=true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void intentTo(Bundle data) {
        Intent intent=null;
        int intentType=data.getInt("intentType");
        if(intentType==viewModel.SCAN_RESULT){
            intent=new Intent(this,ScanDtlActivity.class);
            intent.putExtra("result",data.getString("result"));
        }
        startActivity(intent);
//        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.unSubscribe();
        viewModel.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.unSubscribe();
        viewModel.destroy();
    }
}
