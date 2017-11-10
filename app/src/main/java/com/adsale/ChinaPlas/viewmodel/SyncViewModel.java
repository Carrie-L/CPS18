package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 同步公共類
 *
 * @author Carrie
 * @version 创建时间：2016年8月19日 下午4:57:05
 * @Description: TODO
 */
public class SyncViewModel {

    private static final String TAG = "SyncHelper";
    private Context mContext;

    private DBHelper dbHelper;

    private static final int SYNC_SUCCESS = 1;
    private static final int SYNC_FAILED = 0;

    private StringBuilder mAddBuilder;
    private StringBuilder mRemoveBuilder;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private String mCompanyId;
    private Matcher matcher;
//    public ProgressDialog pb;

    public SyncViewModel(Context context) {
        this.mContext = context;

        sp = mContext.getSharedPreferences(Constant.SYNC_DATA, Context.MODE_PRIVATE);
        editor = sp.edit();

        mAddBuilder = new StringBuilder();
        mRemoveBuilder = new StringBuilder();
    }

    private RequestBody getRequestBody() {
        SharedPreferences mSpLogin = App.mSP_Login;

//        pb = ProgressDialog.show(mContext, mContext.getString(R.string.loading),
//                mContext.getString(R.string.loading));

        String sync_date = AppUtil.getCurrentTime();
        LogUtil.i(TAG, "同步我的参展商syncMyExhibitor: sync_date="+sync_date);

        SharedPreferences.Editor editor = mSpLogin.edit();
        editor.putString("sync_date", sync_date).apply();

        String vmid = mSpLogin.getString(Constant.VMID, "");
        LogUtil.i(vmid, "vmid=" + vmid);

        String updateData = getUpdateData();
        String deleteData = getDeleteData();

        LogUtil.e(TAG, "updateData=" + updateData);
        LogUtil.e(TAG, "deleteData=" + deleteData);

        final RequestBody requestBody = new FormBody.Builder().add("txtMID", vmid).add("txtLogUpdate", updateData)
                .add("txtLogDelete", deleteData).add("txtLUDate", sync_date).add("hidAct", "6872").build();

        LogUtil.e(TAG, "sync_date=" + sync_date);

        return requestBody;
    }

    public void syncMyExhibitor(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(NetWorkHelper.BASE_URL_CPS)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(httpClient.build()).build();
        final LoginClient client = retrofit.create(LoginClient.class);

        client.sync(getRequestBody())
                .map(new Function<retrofit2.Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull retrofit2.Response<ResponseBody> responseBodyResponse) throws Exception {
                        if(responseBodyResponse.isSuccessful()){
                           return processSyncData(responseBodyResponse.body().string());
                        }
                        return false;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        LogUtil.i(TAG,"aBoolean="+aBoolean);
                        if(aBoolean){
                            Toast.makeText(mContext, mContext.getString(R.string.syncSuccess), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, mContext.getString(R.string.syncFailure), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private boolean processSyncData(String data){

        LogUtil.e(TAG,"processSyncData::data="+data);

//        Document oDocument = Jsoup.parse(data);
//        Element oElement = oDocument.getElementById("KVal");
//        LogUtil.i(TAG, "oElement=" + oElement);

//        if (oElement != null) {
//            String responseSyncData = oElement.text().trim();
//            Log.i(TAG, "responseSyncData=" + responseSyncData);
//            if (!responseSyncData.equals("")) {
//                saveDeleteData();
//                saveUpdateData(responseSyncData);
//                responseSyncData = "|" + responseSyncData;
//                Matcher matcher = Pattern.compile("\\|(.*?),").matcher(responseSyncData);
//                LogUtil.e(TAG, "匹配参展商");
//                while (matcher.find()) {
//                    LogUtil.i(TAG, "find=" + matcher.group(1));
//                    ExhibitorRepository repo=ExhibitorRepository.getInstance();
//                    repo.updateIsFavourite(matcher.group(1));
//                }
//            }
//            return true;
//        }
        return false;
    }

    //-----------------------------------------Newer 2017.1.6------------------------------------------------------------------
    public String getUpdateData() {
        return sp.getString(Constant.SYNC_UPDATE, "");
    }

    public String getDeleteData() {
        return sp.getString(Constant.SYNC_DELETE, "");
    }

    public void saveData(String update, String delete) {
        editor.putString(Constant.SYNC_UPDATE, update);
        editor.putString(Constant.SYNC_DELETE, delete);
        editor.apply();
    }

    public void saveUpdateData() {
        editor.putString(Constant.SYNC_UPDATE, mAddBuilder.toString()).apply();
    }

    public void saveUpdateData(String data) {
        editor.putString(Constant.SYNC_UPDATE, data).apply();
    }

    public void saveDeleteData() {
        editor.putString(Constant.SYNC_DELETE, mRemoveBuilder.toString()).apply();
    }

    public void add(String companyId) {
        mAddBuilder = new StringBuilder();
        mCompanyId = companyId;

        if (!TextUtils.isEmpty(getUpdateData())) {
            mAddBuilder.append(getUpdateData()).append("|");
        }
        mAddBuilder.append(companyId).append(",").append(AppUtil.getCurrentTime());
        saveData(mAddBuilder.toString(), removeData(getDeleteData()));
    }

    public void remove(String companyId) {
        mCompanyId = companyId;
        mRemoveBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(getDeleteData())) {
            mRemoveBuilder.append(getDeleteData()).append("|");
        }
        mRemoveBuilder.append(companyId).append(",").append(AppUtil.getCurrentTime());
        saveData(removeData(getUpdateData()), mRemoveBuilder.toString());
    }

    public String removeData(String data) {
        if (data.contains(mCompanyId)) {
            if (data.contains("|")) {
                if (!data.endsWith("|")) {
                    data = data + "|";
                }

                String find = "";
                matcher = Pattern.compile(mCompanyId + "(.*?)\\|").matcher(data);
                while (matcher.find()) {
                    find = matcher.group();
                }

                if (!TextUtils.isEmpty(find)) {
                    data = data.replace(find, "");
                }

                if (data.endsWith("|")) {
                    data = AppUtil.subStringFront(data, "|");
                }
                find = null;

            } else {
                data = "";
            }
        }
        LogUtil.e(TAG, "data=" + data);
        return data;
    }

    public void post() {

    }


}
