package com.adsale.ChinaPlas.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.data.model.EmailVisitorData;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


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

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private Context mContext;
    private String vmid;
    private boolean isSaveSuccess;

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

        testRetrofit();

        // TODO: 2017/8/10 同步
    }

    private String getLangStr() {
        int language = App.mLanguage.get();
        return AppUtil.getName(language, "lang-trad", "lang-eng", "lang-simp");
    }

    public void testRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(NetWorkHelper.BASE_URL_CPS)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(httpClient.build()).build();
        final LoginClient client = retrofit.create(LoginClient.class);

        final RequestBody requestBody = new FormBody.Builder().add("globalSaveLogin", "1").add("hd_LoginType", "1")
                .add("globalLogin", loginName.get())
                .add("globalPassword", loginPwd.get()).build();
        final RequestBody regRequestBody = new FormBody.Builder().add("showid", "453").add("email", loginName.get()).build();

        client.loginRx(getLangStr(), requestBody)
                .map(new Function<Response<ResponseBody>, String>() {

                    @Override
                    public String apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        String res = responseBodyResponse.body().string();
                        vmid = findLoginLink(res);
                        LogUtil.i(TAG, "vmidString ----" + vmid);
                        return vmid;
                    }
                })
                .flatMap(new Function<String, Observable<EmailVisitorData>>() {//根据email得到 EmailVisitorData
                    @Override
                    public Observable<EmailVisitorData> apply(@NonNull String s) throws Exception {
                        App.mSP_Login.edit().putString(Constant.USER_EMAIL, loginName.get().trim()).putString(Constant.USER_PWD, loginPwd.get().trim()).putBoolean(Constant.IS_LOGIN, true).apply();
                        LogUtil.i(TAG, "NAME=" + loginName.get() + ",PWD=" + loginPwd.get());
                        return client.regGetData(regRequestBody).subscribeOn(Schedulers.computation());
                    }
                })
                .flatMap(new Function<EmailVisitorData, Observable<Response<ResponseBody>>>() {//从EmailVisitorData 中得到 RegImageName ，根据名称下载图片
                    @Override
                    public Observable<Response<ResponseBody>> apply(@NonNull EmailVisitorData emailVisitorData) throws Exception {
                        String picName = emailVisitorData.VisitorData.RegImageName;
                        LogUtil.i(TAG, "picName=" + picName);
                        return client.downImg(picName).subscribeOn(Schedulers.io());
                    }
                })
                .flatMap(new Function<Response<ResponseBody>, Observable<Boolean>>() {

                    @Override
                    public Observable<Boolean> apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        //保存图片
                        ResponseBody body=responseBodyResponse.body();
                        if(body!=null){
                            byte[] bytes = body.bytes();
                            isSaveSuccess = AppUtil.saveFileOutput(mContext, Constant.REG_PNG, bytes);
                            body.close();
                        }
                        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {
                                subscriber.onNext(isSaveSuccess);
                                subscriber.onComplete();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Boolean bool) {
                        LogUtil.i(TAG, "onNext:  " + bool);

                        if (bool) {
                            Toast.makeText(mContext, "登录成功:" + vmid, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onError: vmid= " + vmid + ",,," + e.getMessage());
                        isDialogShow.set(false);
                        if (vmid == null) {
                            Toast.makeText(mContext, mContext.getString(R.string.login_error_msg_3), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mLoginListener != null) {
                            mLoginListener.login(false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        isDialogShow.set(false);
                        if (mLoginListener != null) {
                            mLoginListener.login(true);
                        }

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

    private String findLoginLink(String result) {
        String link = null;
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        Document doc = Jsoup.parse(result);
        Elements element = doc.getElementsByTag("td");
        for (Element subelement : element) {
            if (subelement.attr("vmid") != null && !subelement.attr("vmid").equals("")
                    && Integer.parseInt(subelement.attr("vmid")) > 0) {
                link = subelement.attr("vmid");
                break;
            }
        }
        return link;
    }


}
