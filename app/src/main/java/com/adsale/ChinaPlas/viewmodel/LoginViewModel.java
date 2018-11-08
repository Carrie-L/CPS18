package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 登录
 * TODO ① 整个登录的过程中所花费的时间太长了，要10s左右。② 取消订阅
 * Created by Ponyo on 2017/8/6.
 */

public class LoginViewModel {
    private static final String TAG = "LoginViewModel";

    public final ObservableBoolean isDialogShow = new ObservableBoolean(false);
    public final ObservableBoolean isSmsShow = new ObservableBoolean(false);

    //LoginActivity
    public final ObservableField<String> loginEmail = new ObservableField<>();
    public final ObservableField<String> loginPwd = new ObservableField<>();

    //SMS
    public final ObservableField<String> loginPhone = new ObservableField<>();
    public final ObservableField<String> loginCode = new ObservableField<>();
    public final ObservableField<String> loginLang = new ObservableField<>();
    public final ObservableBoolean isBtnCanGetCode = new ObservableBoolean(true);   //  发送验证码的按钮能否按下


    private Disposable mDisposable;

    private Context mContext;
    private String vmid;
    private boolean isSaveSuccess;
    private LoginClient client;
    private Disposable mImgDisposable;

    public LoginViewModel(Context context) {
        mContext = context.getApplicationContext();
    }

    public void switchToSMSLogin() {
        isSmsShow.set(true);


    }


    private String guid = "";

    /**
     * 发送验证码
     */
    public void sendCode() {
//        if (TextUtils.isEmpty(loginLang.get())) {
//            loginLang.set(App.mLanguage.get() == 1 ? "eng" : App.mLanguage.get() == 2 ? "simp" : "trad");
//        }
//
//        if (client == null) {
//            client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);
//        }
//
//        LogUtil.i(TAG, loginPhone.get() + ",sendCode: " + loginLang.get() + ",loginEmail= " + loginEmail.get());
//
//        client.getCode(String.format(NetWorkHelper.SMS_GET_CODE_URL0, loginPhone.get(), loginLang.get()))
//                .map(new Function<Response<ResponseBody>, String>() {
//
//                    @Override
//                    public String apply(Response<ResponseBody> responseBodyResponse) throws Exception {
//
//                        ResponseBody body = responseBodyResponse.body();
//
//                        if (body != null) {
//                            String value = body.string();
//                            LogUtil.i(TAG, "getCode: VALUE= " + value);
//                            body.close();
//                            return value;
//                        } else {
//                            LogUtil.e(TAG, "get code null");
//                            return "get Code null";
//                        }
//
//
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Toast.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
//                        isBtnCanGetCode.set(false);
//                    }
//
//                    @Override
//                    public void onNext(String value) {
//                        LogUtil.i(TAG, "onNext: value=" + value);
//
//                        guid = value;
//
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.e(TAG, "onError:" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtil.i(TAG, "onComplete");
//                        isBtnCanGetCode.set(true);
//                    }
//                });


    }

    /**
     * 验证码登录
     */
    public void onSmsLogin() {
//        if (TextUtils.isEmpty(loginPhone.get())) {
//            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_1), Toast.LENGTH_SHORT).show();
//            return;
//        } else if (TextUtils.isEmpty(loginCode.get())) {
//            Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_SHORT).show();
//            return;
//        }
////        isDialogShow.set(true);
//
//        mOnLoginListener.verificationCode();
//
//
//        if (client == null) {
//            client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);
//        }
//
////        client.smsLogin(String.format(NetWorkHelper.SMS_LOGIN_URL, loginEmail.get(),loginPhone.get(),loginCode.get(),guid))
//        client.smsLogin(String.format(NetWorkHelper.SMS_LOGIN_URL, guid, loginCode.get()))
//                .map(new Function<Response<ResponseBody>, Boolean>() {
//                    @Override
//                    public Boolean apply(Response<ResponseBody> responseBodyResponse) throws Exception {
//
//                        ResponseBody body = responseBodyResponse.body();
//
//                        if (body != null) {
//                            String value = body.string();
//                            body.close();
//                            LogUtil.i(TAG, "getCode: VALUE= " + value);
//
////                            if(value.equals("1")){
////                                return true;
////                            }
//
//                            if (value.equals("True")) {
//                                return true;
//                            }
//
//                        } else {
//                            LogUtil.e(TAG, "get code null");
//
//                        }
//
//
//                        return false;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Boolean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Toast.makeText(mContext, "登录中...", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onNext(Boolean value) {
//                        LogUtil.i(TAG, "sms login onnext:" + value);
//                        isSmsLoginSuccess = value;
//                        if (value) {
//                            Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(mContext, "登录失败！", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                        if (isSmsLoginSuccess) {
//                            Intent intent = new Intent(mContext, RegisterActivity.class);
//                            intent.putExtra("guid", guid);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            mContext.startActivity(intent);
//                        }
//
//
//                    }
//                });


    }

//    private RequestBody getCodeRequestBody() {
//        return new FormBody.Builder().add("globalSaveLogin", "1").add("hd_LoginType", "1")
//                .add("globalLogin", loginEmail.get())
//                .add("globalPassword", loginPwd.get()).build();
//    }


    public void loginAction() {
        if (TextUtils.isEmpty(loginEmail.get())) {
            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_1), Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(loginPwd.get())) {
            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_2), Toast.LENGTH_SHORT).show();
            return;
        }
        isDialogShow.set(true);

//        loginRetrofit();

//        onPwdLogin();
    }

    private String getLangStr() {
        return AppUtil.getName(AppUtil.getCurLanguage(), "lang-trad", "lang-eng", "lang-simp");
    }

    private RequestBody getLoginRequestBody() {
        return new FormBody.Builder().add("globalSaveLogin", "1").add("hd_LoginType", "1")
                .add("globalLogin", loginEmail.get())
                .add("globalPassword", loginPwd.get()).build();
    }

    private RequestBody getRegRequestBody() {
        return new FormBody.Builder().add("showid", Constant.SHOW_ID).add("email", loginEmail.get()).build();
    }

    public void onPwdLogin() {
//        if (TextUtils.isEmpty(loginPhone.get())) {
//            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_1), Toast.LENGTH_SHORT).show();
//            return;
//        } else if (TextUtils.isEmpty(loginPwd.get())) {
//            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_2), Toast.LENGTH_SHORT).show();
//            return;
//        } else if (TextUtils.isEmpty(loginEmail.get())) {
//            Toast.makeText(mContext, "请输入邮箱", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        isDialogShow.set(true);
//
//        if (client == null) {
//            client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);
//        }
//
//        client.loginRxTest(String.format(NetWorkHelper.PWD_LOGIN_URL, loginEmail.get(), loginPhone.get(), loginPwd.get(), loginLang.get()))
//                .map(new Function<Response<ResponseBody>, Boolean>() {
//
//                    @Override
//                    public Boolean apply(Response<ResponseBody> responseBodyResponse) throws Exception {
//
//                        ResponseBody body = responseBodyResponse.body();
//
//                        if (body != null) {
//                            String value = body.string();
//                            body.close();
//                            LogUtil.i(TAG, "loginRxTest: VALUE= " + value);
//
//                            if (value.contains("1")) {
//                                return true;
//                            }
//                        } else {
//                            LogUtil.e(TAG, "get code null");
//
//                        }
//
//
//                        return false;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Boolean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Toast.makeText(mContext, "登录中...", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onNext(Boolean value) {
//                        LogUtil.i(TAG, "sms login onnext:" + value);
//
////                        isSmsLoginSuccess=value;
//
//                        if (value) {
//                            Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(mContext, "登录失败！", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//
//                    }
//                });
    }

    private boolean isSmsLoginSuccess = false;

    private void loginRetrofit() {
//        client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);
////        client.loginRx(getLangStr(), getLoginRequestBody())
//        client.loginRxTest(String.format(NetWorkHelper.PWD_LOGIN_URL, loginEmail.get(), loginPhone.get(), loginPwd.get(), loginLang.get()))
//                .map(new Function<Response<ResponseBody>, Boolean>() {
//
//                    @Override
//                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
//                        ResponseBody responseBody = responseBodyResponse.body();
//                        if (responseBody != null) {
//
//                            String value = responseBody.string();
//                            LogUtil.i(TAG, "loginRetrofit value ----" + value);
//                            vmid = getVmid(responseBody.string());
//                            LogUtil.i(TAG, "vm id String ----" + vmid);
//                            responseBody.close();
//                        }
//                        responseBody = null;
//                        return !TextUtils.isEmpty(vmid);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Boolean>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        LogUtil.i(TAG, "onSubscribe");
//                        mDisposable = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Boolean login) {
//                        LogUtil.i(TAG, "onNext:  " + login);
////                        App.mSP_Login.edit().putBoolean("LoginRegPicDownSuccess", bool).apply(); // 預登記圖片下載成功
//                        if (login) {
//                            saveLoginData();
//                            if (mOnLoginFinishListener != null) {
//                                mOnLoginFinishListener.login(true);
//                            }
//                            downRegImg();
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        LogUtil.i(TAG, "onError: vmid= " + vmid + ",,," + e.getMessage());
//                        isDialogShow.set(false);
//                        mDisposable.dispose();
//
//                        if (vmid == null) {
//                            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_3), Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        /*  有幾率是登錄成功，但圖片沒下載成功，這樣的情況依然按照登錄成功來處理，圖片在預登記里下載 */
//                        if (mOnLoginFinishListener != null && !TextUtils.isEmpty(vmid)) {
//                            App.mSP_Login.edit().putBoolean("LoginRegPicDownSuccess", false).apply();
//                            mOnLoginFinishListener.login(true);
//                        }
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtil.i(TAG, "onComplete");
//                        isDialogShow.set(false);
//                        mDisposable.dispose();
//                    }
//                });
    }

    private void saveLoginData() {
        App.mSP_Login.edit().putString(Constant.USER_EMAIL, loginEmail.get().trim()).putString(Constant.USER_PWD, loginPwd.get().trim()).putString(Constant.VMID, vmid.trim()).putBoolean(Constant.IS_LOGIN, true).apply();
        LogUtil.i(TAG, "NAME=" + loginEmail.get() + ",PWD=" + loginPwd.get());
        AppUtil.trackViewLog(429, "UserLogin", "", "");
        AppUtil.setStatEvent(mContext, "UserLogin", "UL");
    }

    private void downRegImg() {
        client.regGetData(NetWorkHelper.getRegRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EmailVisitorData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mImgDisposable = d;
                    }

                    @Override
                    public void onNext(EmailVisitorData value) {
                        String imgUrl = value.VisitorData.RegImageName;
                        LogUtil.i(TAG, "EmailVisitorData: " + imgUrl);
                        AppUtil.setRegImgUrl(imgUrl);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "onError:" + e.getMessage());
                        mImgDisposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        mImgDisposable.dispose();
                    }
                });
    }

    public interface OnLoginFinishListener {
        void login(boolean bool);
    }

    public void setOnLoginFinishListener(OnLoginFinishListener listener) {
        mOnLoginFinishListener = listener;
    }

    public interface OnLoginListener {
        void verificationCode();
    }

    public void setOnLoginListener(OnLoginListener listener) {
        mOnLoginListener = listener;
    }


    private OnLoginFinishListener mOnLoginFinishListener;
    private OnLoginListener mOnLoginListener;

    private String getVmid(String result) {
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        int vmidIndex = result.indexOf("vmid=");
        LogUtil.e(TAG, "getVmid::vmidIndex=" + vmidIndex);
        if (vmidIndex == -1) { // cannot find vmid
            return null;
        }
        return result.substring(vmidIndex + 6, vmidIndex + 12);
    }


}
