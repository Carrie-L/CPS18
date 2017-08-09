package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.GitHubClient;
import com.adsale.ChinaPlas.data.GitHubRepo;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.FileUtils;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.adsale.ChinaPlas.utils.AppUtil.checkNotNull;

/**
 * Created by Ponyo on 2017/8/6.
 */

public class LoginViewModel {
    private static final String TAG = "LoginViewModel";
    //侧边栏
    public final ObservableField<String> drawerLoginTitle = new ObservableField<>();
    public final ObservableField<String> drawerLogin = new ObservableField<>();
    public final ObservableField<String> drawerLogout = new ObservableField<>();

    public final ObservableBoolean isLoginSuccess = new ObservableBoolean(false);

    //LoginActivity
    public final ObservableField<String> loginName = new ObservableField<>();
    public final ObservableField<String> loginPwd = new ObservableField<>();

    private Context mContext;

    public LoginViewModel(Context context) {
        mContext = context.getApplicationContext();
    }

    public void loginAction() {
        Toast.makeText(mContext, loginName.get() + "," + loginPwd.get(), Toast.LENGTH_LONG).show();
        isLoginSuccess.set(true);
//        checkNotNull(mListener);
//        mListener.loginSuccess("123");


        testRetrofit();
    }

    private OnLoginSuccessListener mListener;

    public void setOnLoginSuccessListener(OnLoginSuccessListener listener) {
        mListener = listener;
    }

    public interface OnLoginSuccessListener {
        void loginSuccess(String vmid);
    }

    private String getLangStr() {
        int language = AppUtil.getCurLanguage(mContext);
        return AppUtil.getName(language, "lang-trad", "lang-eng", "lang-simp");
    }

    public void testRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(NetWorkHelper.BASE_URL_CPS)
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(httpClient.build()).build();

        // Create a very simple REST adapter which points the GitHub API endpoint.
        GitHubClient client = retrofit.create(GitHubClient.class);

        RequestBody requestBody = new FormBody.Builder().add("hd_LoginType", "2").build();

        String name, pwd;
        if (loginName.get() != null && loginName.get().contains("8947")) {
            App.mSP_Config.edit().putString("LoginName", loginName.get().trim()).putString("LoginPwd", loginPwd.get()).apply();
        }
        name = App.mSP_Config.getString("LoginName", "");
        pwd = App.mSP_Config.getString("LoginPwd", "");

        if (TextUtils.isEmpty(name)) {
            name = loginName.get();
            pwd = loginPwd.get();
        }

        LogUtil.i(TAG,"NAME="+name+",PWD="+pwd);

        FormBody formBody = new FormBody.Builder().add("globalSaveLogin", "1").add("hd_LoginType", "1")
                .add("globalLogin", name)
                .add("globalPassword", pwd).build();
        Call<String> call = client.preLogin(getLangStr(), formBody);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                LogUtil.i("CPS: testRetrofit", "onResponse:" + response.toString());
                String res = response.body().toString();
                LogUtil.i("CPS: testRetrofit", "onResponse: res: " + res);

                if (!TextUtils.isEmpty(res)) {

                    String vmidString = findLoginLink(res);
                    LogUtil.i(TAG, "vmidString ----" + vmidString);

                }else{
                    LogUtil.i(TAG, "res isEmpty");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LogUtil.i("CPS: testRetrofit", "onFailure:" + t.getMessage());
            }
        });

    }

    public String findLoginLink(String result) {
        String link = "0";

        LogUtil.i(TAG,"result="+result);
       if(TextUtils.isEmpty(result)){
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
