package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityNcardBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.NCardViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import static com.adsale.ChinaPlas.utils.PermissionUtil.PERMISSION_CAMERA;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_CAMERA;

/**
 * 我的名片
 */
public class NCardActivity extends BaseActivity {

    private SharedPreferences spNameCard;
    private NCardViewModel viewModel;

    public final ObservableField<Bitmap> bitmap = new ObservableField<>();

    @Override
    protected void initView() {
        setBarTitle(R.string.title_my_name_card);
        ActivityNcardBinding binding = ActivityNcardBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        viewModel = new NCardViewModel(getApplicationContext());
    }

    @Override
    protected void initData() {
        spNameCard = getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
        generateQRCodeBitmap();
    }

    public void onEdit() {
        Intent intent = new Intent(this, NCardCreateEditActivity.class);
        intent.putExtra("edit", true);
        startActivity(intent);
    }

    public void onScanner() {
        boolean hasCameraPerm = PermissionUtil.checkPermission(this, PERMISSION_CAMERA);
        if (hasCameraPerm) {
            toScanner();
        } else {
            PermissionUtil.requestPermission(this, PERMISSION_CAMERA, PMS_CODE_CAMERA);
        }
    }

    private void toScanner() {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivity(intent);
    }

    public void onAllList() {
        Intent intent = new Intent(this, NCardListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // update QRCode
        if (spNameCard.getBoolean("HasUpdated", false)) {
            generateQRCodeBitmap();
            spNameCard.edit().putBoolean("HasUpdated", false).apply();
        }

    }

    private void generateQRCodeBitmap() {
        String qrCode = spNameCard.getString("my_qrcode", "");
        LogUtil.i(TAG, "qrCode=" + qrCode);
        try {
            bitmap.set(viewModel.createQrCode("UserNC:" + qrCode, BarcodeFormat.QR_CODE));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PMS_CODE_CAMERA && PermissionUtil.getGrantResults(grantResults)) {
            toScanner();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_camera_permission), Toast.LENGTH_SHORT).show();
        }
    }
}
