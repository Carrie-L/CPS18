package com.adsale.ChinaPlas.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityRegisterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CalendarUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.viewmodel.RegisterViewModel;
import com.pingplusplus.android.Pingpp;

import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

public class RegisterActivity extends BaseActivity implements RegisterViewModel.OnWebViewLoadCallback, OnIntentListener {
    private RegisterViewModel mRegModel;
    private ActivityRegisterBinding binding;
    private WebView webView;
    private int failCount;

    @Override
    protected void initView() {
        if (AppUtil.isLogin()) {
            setBarTitle(R.string.title_register_success);
        } else {
            setBarTitle(R.string.title_register);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.webkit.WebView.enableSlowWholeDocumentDraw();
        }

        permissionSD();

        binding = ActivityRegisterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mRegModel = new RegisterViewModel(this, new CalendarUtil(this), this);
        binding.setRegModel(mRegModel);
        binding.setAty(this);
        binding.executePendingBindings();
    }

    private boolean permissionSD() {
        boolean sdPermission = PermissionUtil.checkPermission(getApplicationContext(), PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        LogUtil.i(TAG, "sdPermission=" + sdPermission);

        if (!sdPermission) {
            PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, PMS_CODE_WRITE_SD);
        }
        return sdPermission;
    }


    @Override
    protected void initData() {
        webView = binding.regWeb;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        mRegModel.setOnWebViewLoadCallback(this);
        mRegModel.start(webView, binding.ivRegisted, binding.progressBar);

        mRegModel.show();

        App.mSP_Config.edit().putBoolean("RegDestroy", false).apply();
    }

    public void onReset() {
        barTitle.set(getString(R.string.title_register));
        mRegModel.reset();
    }

    public void onGetInvoice() {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRegModel.getInvoiceUrl()));
//        startActivity(intent);
//        overridePendingTransPad();

        // 点击跳转到MyChinaplas
        Intent   intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constant.WEB_URL, String.format(NetWorkHelper.MY_CHINAPLAS_URL,AppUtil.getLanguageUrlType()));
        intent.putExtra(Constant.TITLE,getString(R.string.title_my_chinaplas));
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
                    mRegModel.isShowProgressBar.set(true);
                    mRegModel.setPingPay(true);
                    //请求自家服务器接口
                    mRegModel.getPostStatus();
                } else if (result.contains("fail")) { /* 支付失败，重新调起支付 */
                    if (failCount < 5) {
//                        mRegModel.createPayment();
                        createPay();
                        failCount++;
                    } else {
                        failCount = 0;
                    }
                } else if (result.equals("cancel")) {
//                    Toast.makeText(getApplicationContext(),getString(R.string.cancel))
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.getGrantResults(grantResults) && requestCode == PermissionUtil.PMS_CODE_READ_CALENDAR) {
            mRegModel.addToCalendar();
        }
    }

    @Override
    public void load(String url) {

    }

    @Override
    public void createPay() {
        Pingpp.createPayment(this, mRegModel.getCharge());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mRegModel.isCapturing.get()) {
                if (webView != null) {
                    if (webView.canGoBack()) {
                        LogUtil.i(TAG, "webView.canGoBack()");
                        if (webView.getUrl().contains("PreregSuccess") && webView.getUrl().contains("device=mobileapp&guid=")) {
                            finish();
                            LogUtil.i(TAG, "在确认信页面，直接结束");
                        } else {
                            webView.goBack();
                            LogUtil.i(TAG, "goBack（）");
                        }
                    } else {
                        finish();
                    }
                }
            } else {
                LogUtil.i(TAG, "截图中，不许返回");
            }


        }

//        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null) {
//            LogUtil.i(TAG, "按了返回键");
//
//            if (webView.canGoBack()) {
//                LogUtil.i(TAG, "返回上一页《《《");
//                webView.goBack();
//
//            } else {
//                finish();
//            }
//        }
        return false;
    }

    @Override
    public void back() {
        if (mRegModel.isCapturing.get()) {
            LogUtil.i(TAG, "正在截图，不能返回");
        } else {
            super.back();
            LogUtil.i(TAG, "调用了 back()");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { //do something.

            if (mRegModel.isCapturing.get()) {
                LogUtil.i(TAG, "正在截图，不能返回");
                return true;
            } else {
                LogUtil.i(TAG, "dispatchKeyEvent KEYCODE_BACK  0");
                return super.dispatchKeyEvent(event);

            }
        } else {
            LogUtil.i(TAG, "dispatchKeyEvent KEYCODE_BACK  1");
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        mRegModel.dispose();
        App.mSP_Config.edit().putBoolean("RegDestroy", true).apply();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
//        Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
//        intent.putExtra("url", (String) entity);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        overridePendingTransPad();
    }





}
