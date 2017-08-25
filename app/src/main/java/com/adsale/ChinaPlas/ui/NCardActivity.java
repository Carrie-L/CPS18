package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityNcardBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NCardViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * 我的名片
 */
public class NCardActivity extends BaseActivity {

    private SharedPreferences spNameCard;
    private NCardViewModel viewModel;

    public final ObservableField<Bitmap> bitmap=new ObservableField<>();

    @Override
    protected void initView() {
        ActivityNcardBinding binding = ActivityNcardBinding.inflate(getLayoutInflater(),mBaseFrameLayout,true);
        binding.setView(this);
        viewModel = new NCardViewModel(getApplicationContext());
    }

    @Override
    protected void initData() {
        spNameCard = getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
    }

    public void onEdit(){
        Intent intent = new Intent(this,NCardCreateEditActivity.class);
        intent.putExtra("edit",true);
        startActivity(intent);
    }

    public void onScanner(){

    }

    public void onAllList(){

    }

    @Override
    protected void onResume() {
        super.onResume();

        String qrCode= spNameCard.getString("my_qrcode","");
        LogUtil.i(TAG,"qrCode="+qrCode);

        try {
            bitmap.set(viewModel.createQrCode("UserNC:" + qrCode, BarcodeFormat.QR_CODE));
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }




}
