package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.LoginClient;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.FileUtils;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 * 同步公共類
 *
 * @author Carrie
 * @version 创建时间：2016年8月19日 下午4:57:05
 * @Description: TODO
 */
public class SyncViewModel {

    private static final String TAG = "SyncViewModel";
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
    private Disposable mDisposable;

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
        LogUtil.i(TAG, "同步我的参展商syncMyExhibitor: sync_date=" + sync_date);

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

    public void syncMyExhibitor() {
        final LoginClient client = ReRxUtils.setupRxtrofit(LoginClient.class, NetWorkHelper.BASE_URL_CPS);
        client.sync(getRequestBody())
                .map(new Function<retrofit2.Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull retrofit2.Response<ResponseBody> responseBodyResponse) throws Exception {
                        ResponseBody body = responseBodyResponse.body();
                        if (body != null) {
                            LogUtil.i(TAG,"body != null");
                            return processSyncData(body.string());
                        }
                        LogUtil.i(TAG,"body == null");
                        return false;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        LogUtil.i(TAG, "sync: aBoolean=" + aBoolean);
                        if (aBoolean) {
                            Toast.makeText(mContext, mContext.getString(R.string.syncSuccess), Toast.LENGTH_SHORT).show();

                            AppUtil.trackViewLog(419, "Sync", "", "");// SME_en_Android
                            AppUtil.setStatEvent(mContext, "Sync", "Sync_OK");

                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.syncFailure), Toast.LENGTH_SHORT).show();

                            AppUtil.trackViewLog(419, "Sync", "", "");// SME_en_Android
                            AppUtil.setStatEvent(mContext, "Sync", "Sync_Fail");
                        }
                        if (mSyncCallback != null) {
                            mSyncCallback.sync(aBoolean);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "SYNC ON ERROR: " + e.getMessage());
                        mDisposable.dispose();
                        Toast.makeText(mContext, mContext.getString(R.string.syncFailure), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "SYNC onComplete");
                        mDisposable.dispose();
                    }
                });
    }

    private boolean processSyncData(String data) {
        writeDataTo(data);
        /*  获取中间的值 <label id="KVal">   </label>   */
        Matcher matcher = Pattern.compile("KVal\"(.*?)/label").matcher(data);
        String responseSyncData = "";
        while (matcher.find()) {
            responseSyncData = matcher.group(1).replaceAll("&gt;","").replace("&lt;","").replace("<","").replace(">","");
            LogUtil.e(TAG, "matcher : " + responseSyncData);
        }
        if (TextUtils.isEmpty(responseSyncData)) {
            return false;
        }

        saveDeleteData();
        saveUpdateData(responseSyncData);
        responseSyncData = "|" + responseSyncData;
        Matcher matcher1 = Pattern.compile("\\|(.*?),").matcher(responseSyncData);
        LogUtil.e(TAG, "匹配参展商");
        boolean hasMatcher = false;
        ExhibitorRepository repo = ExhibitorRepository.getInstance();
        while (matcher1.find()) {
            LogUtil.i(TAG, "find=" + matcher1.group(1));
            repo.updateFavourite(matcher1.group(1));
            hasMatcher = true;
        }
        return hasMatcher;
    }

    private void writeDataTo(String data) {
        FileUtils.writeFileTo(data, mContext.getFilesDir().getAbsolutePath().concat("/sync.html"));
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

    public interface SyncCallback {
        void sync(boolean success);
    }

    private SyncCallback mSyncCallback;

    public void setSyncCallback(SyncCallback callback) {
        mSyncCallback = callback;
    }


}
