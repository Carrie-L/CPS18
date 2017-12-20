package com.adsale.ChinaPlas.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityRegisterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.RegisterViewModel;
import com.pingplusplus.android.Pingpp;

public class RegisterActivity extends BaseActivity {
    private RegisterViewModel mRegModel;
    private ActivityRegisterBinding binding;
    private WebView webView;

    @Override
    protected void initView() {
        if(AppUtil.isLogin()){
            setBarTitle(R.string.title_register_success);
        }else{
            setBarTitle(R.string.title_register);
        }
        binding = ActivityRegisterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mRegModel = new RegisterViewModel(this);
        binding.setRegModel(mRegModel);
        binding.setAty(this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {
        webView = binding.regWeb;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        mRegModel.start(webView, binding.ivRegisted, binding.progressBar);
    }

    public void onReset(){
        barTitle.set(getString(R.string.title_register));
        mRegModel.reset();
    }

    public void onGetInvoice(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRegModel.getInvoiceUrl()));
        startActivity(intent);
        overridePendingTransPad();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");

        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                LogUtil.i(TAG, "result=" + result + ",errorMsg=" + errorMsg + ",extraMsg=" + extraMsg);

                if (result.contains("success")) { /* 刷新确认信 */
                    mRegModel.paySuccess();

                    //请求自家服务器接口



                } else if (result.contains("fail")) { /* 支付失败，重新调起支付 */
                    mRegModel.createPayment();
                }else if(result.equals("cancel")){

                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(PermissionUtil.getGrantResults(grantResults)&&requestCode==PermissionUtil.PMS_CODE_READ_CALENDAR){
            mRegModel.addCalendar();
        }
    }
}
