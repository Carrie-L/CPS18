package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.FileUtils;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

    //LoginActivity
    public final ObservableField<String> loginName = new ObservableField<>();
    public final ObservableField<String> loginPwd = new ObservableField<>();

    private Disposable mDisposable;

    private Context mContext;
    private String vmid;
    private boolean isSaveSuccess;
    private LoginClient client;
    private Disposable mImgDisposable;

    public LoginViewModel(Context context) {
        mContext = context.getApplicationContext();
    }

    public void loginAction() {
        if (TextUtils.isEmpty(loginName.get())) {
            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_1), Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(loginPwd.get())) {
            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_2), Toast.LENGTH_SHORT).show();
            return;
        }
        isDialogShow.set(true);

        loginRetrofit();
    }

    private String getLangStr() {
        return AppUtil.getName(AppUtil.getCurLanguage(), "lang-trad", "lang-eng", "lang-simp");
    }

    private RequestBody getLoginRequestBody() {
        return new FormBody.Builder().add("globalSaveLogin", "1").add("hd_LoginType", "1")
                .add("globalLogin", loginName.get())
                .add("globalPassword", loginPwd.get()).build();
    }

    private RequestBody getRegRequestBody() {
        return new FormBody.Builder().add("showid", Constant.SHOW_ID).add("email", loginName.get()).build();
    }

    private void loginRetrofit() {
        client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);
        client.loginRx(getLangStr(), getLoginRequestBody())
                .map(new Function<Response<ResponseBody>, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody responseBody = responseBodyResponse.body();
                        if (responseBody != null) {
                            vmid = getVmid(responseBody.string());
                            LogUtil.i(TAG, "vm id String ----" + vmid);
                            responseBody.close();
                        }
                        responseBody = null;
                        return !TextUtils.isEmpty(vmid);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "onSubscribe");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean login) {
                        LogUtil.i(TAG, "onNext:  " + login);
//                        App.mSP_Login.edit().putBoolean("LoginRegPicDownSuccess", bool).apply(); // 預登記圖片下載成功
                        if (login) {
                            saveLoginData();
                            if (mLoginListener != null) {
                                mLoginListener.login(true);
                            }
                            downRegImg();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onError: vmid= " + vmid + ",,," + e.getMessage());
                        isDialogShow.set(false);
                        mDisposable.dispose();

                        if (vmid == null) {
                            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_3), Toast.LENGTH_LONG).show();
                            return;
                        }

                        /*  有幾率是登錄成功，但圖片沒下載成功，這樣的情況依然按照登錄成功來處理，圖片在預登記里下載 */
                        if (mLoginListener != null && !TextUtils.isEmpty(vmid)) {
                            App.mSP_Login.edit().putBoolean("LoginRegPicDownSuccess", false).apply();
                            mLoginListener.login(true);
                        }

                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        isDialogShow.set(false);
                        mDisposable.dispose();
                    }
                });
    }

    private void saveLoginData() {
        App.mSP_Login.edit().putString(Constant.USER_EMAIL, loginName.get().trim()).putString(Constant.USER_PWD, loginPwd.get().trim()).putString(Constant.VMID, vmid.trim()).putBoolean(Constant.IS_LOGIN, true).apply();
        LogUtil.i(TAG, "NAME=" + loginName.get() + ",PWD=" + loginPwd.get());
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
        mLoginListener = listener;
    }

    private OnLoginFinishListener mLoginListener;

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
