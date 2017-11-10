package com.adsale.ChinaPlas.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.dao.WebContentDao;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.databinding.ActivitySubscribeBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SubscribeActivity extends BaseActivity {
    public static String TAG = "SubscribeActivity";
    public final ObservableField<String> company = new ObservableField<>("");
    public final ObservableField<String> name = new ObservableField<>("");
    public final ObservableField<String> email = new ObservableField<>("");
    private ActivitySubscribeBinding binding;
    private Disposable disposable;
    private int language;

    @Override
    protected void initView() {
        binding = ActivitySubscribeBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
    }

    @Override
    protected void initData() {
        language = App.mLanguage.get();
        test();
    }

    private void test() {
        binding.etCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().toLowerCase().equals("adsale test")) {
                    company.set("adsale test");
                    name.set("adsale test");
                    email.set("dotcom1101@gmail.com");
                }
            }
        });

    }

    public void onSend() {
        name.set(name.get().trim());
        email.set(email.get().trim());
        company.set(company.get().trim());
        track();
        if (!checkEditText()) {
            return;
        }

        DownloadClient client = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.Subscribe_BASE_URL);
        client.onSubscribe(getLangType(), getFormBody())
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        LogUtil.i(TAG, "apply");
                        ResponseBody body = response.body();
                        if (body != null) {
                            try {
                                String res = body.string();
                                if (!res.isEmpty() && res.contains("callSuccess()")) {
                                    body.close();
                                    return true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    private ProgressDialog mProgressDialog;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                        mProgressDialog = AppUtil.createProgressDialog(SubscribeActivity.this, R.string.Registering_MSg);
                        mProgressDialog.show();
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext: aBoolean=" + aBoolean);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                        if (aBoolean) {
                            reset();
                            AppUtil.showConfirmAlertDialog(SubscribeActivity.this, getString(R.string.thx_dy));
                        } else {
                            AppUtil.showConfirmAlertDialog(SubscribeActivity.this, getString(R.string.Registering_MSg));
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                    }
                });
    }


    public void onReset() {
        reset();
    }

    private void reset() {
        company.set("");
        name.set("");
        email.set("");
        binding.etCompany.requestFocus();
    }

    public void onPrivacy() {
        Intent intent = new Intent(this, WebContentActivity.class);
        WebContentDao webContentDao = App.mDBHelper.mWebContentDao;
        WebContent webContent = webContentDao.load("99");
        LogUtil.i(TAG, "webContent=" + webContent.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.WEB_URL, webContent.getPageId());
        intent.putExtra("title", webContent.getTitle(language));
        startActivity(intent);
        overridePendingTransPad();
    }

    private FormBody getFormBody() {
        return new FormBody.Builder()
                .add("CompName", company.get())
                .add("EName", name.get())
                .add("Email", email.get())
                .add("rad", getLangCode())
                .build();
    }

    private String getLangCode() {
        return language == 0 ? "950" : language == 1 ? "1252" : "936";
    }

    private String getLangType() {
        return language == 0 ? "trad" : language == 1 ? "eng" : "simp";
    }


    private void track() {
//		SystemMethod.trackViewLog(mContext, 422, "SE", "", "");
//		SystemMethod.setStatEvent(mContext, "SubscribeE", "SE", mLanguage);
    }


    private boolean checkEditText() {
        if (company.get().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.register_msg013, Toast.LENGTH_LONG).show();
            binding.etCompany.requestFocus();
            return false;
        }

        if (name.get().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.register_msg001, Toast.LENGTH_LONG).show();
            binding.etName.requestFocus();
            return false;
        }

        String check = "[a-zA-Z.\u4e00-\u9fa5\\s]+";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(name.get());
        if (!matcher.matches()) {
            AppUtil.showConfirmAlertDialog(SubscribeActivity.this, getString(R.string.register_msg002));
            binding.etName.requestFocus();
            return false;
        }

        if (email.get().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.register_msg003), Toast.LENGTH_LONG).show();
            binding.etEmail.requestFocus();
            return false;
        }

        check = "[\\w\\-.]+@([\\w-]+\\.)+[a-z]{2,3}";
        regex = Pattern.compile(check);
        matcher = regex.matcher(email.get());
        LogUtil.i(TAG, "email=" + email.get());
        if (!matcher.matches()) {
            AppUtil.showConfirmAlertDialog(SubscribeActivity.this, getString(R.string.register_msg004));
            binding.etEmail.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
