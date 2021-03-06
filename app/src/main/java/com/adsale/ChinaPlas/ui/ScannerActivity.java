package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityScannerBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ScannerViewModel;

public class ScannerActivity extends BaseActivity implements SurfaceHolder.Callback, ScannerViewModel.OnScannedListener {

    private ActivityScannerBinding binding;
    private ScannerViewModel viewModel;
    private SurfaceHolder mHolder;
    private boolean hasSurface;
    private boolean hasCameraPerm;

    @Override
    protected void initView() {
        setBarTitle(R.string.qrcode_scanner);
        binding = ActivityScannerBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {
        viewModel = new ScannerViewModel(getApplicationContext());
        viewModel.setOnScannedListener(this);
    }

    private void setupCamera() {
        mHolder = binding.capturePreview.getHolder();
        if (hasSurface) {
            LogUtil.i(TAG, "hasSurface");
            initCamera(mHolder);
        } else {
            LogUtil.i(TAG, "! hasSurface");
            mHolder.addCallback(this);
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCamera();
    }

    private void initCamera(SurfaceHolder holder) {
        viewModel.onStart(holder, binding.captureCropLayout, binding.rlScanner, binding.captureScanLine);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated");
        initCamera(holder);
        hasSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

    @Override
    public void intentTo(Bundle data) {
        Intent intent = null;
        int intentType = data.getInt("intentType");
        if (intentType == viewModel.SCAN_RESULT) {
            intent = new Intent(this, ScanDtlActivity.class);
            intent.putExtra("result", data.getString("result"));
        }
        startActivity(intent);
        overridePendingTransPad();
//        finish();
    }

    public void onHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        HelpView helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_SCANNER);
        helpDialog.show(ft, "Dialog");
    }

    @Override
    protected void onPause() {
        super.onPause();
        release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        hasSurface = false;
        if (mHolder != null) {
            mHolder.removeCallback(this);
        }
        viewModel.unSubscribe();
        viewModel.destroy();
    }
}
