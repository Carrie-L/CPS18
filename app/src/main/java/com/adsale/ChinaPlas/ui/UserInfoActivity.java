package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityUserInfoBinding;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;
import com.bumptech.glide.Glide;


/**
 * 用戶資料 / My Tools  改名为 MyCHINAPLAS
 */
public class UserInfoActivity extends BaseActivity {
    public ObservableBoolean isLogin = new ObservableBoolean();
    private ImageView ivD7;
    private EPOHelper mEPOHelper;

    @Override
    protected void initView() {
        setBarTitle(R.string.title_my_chinaplas);
        ActivityUserInfoBinding binding = ActivityUserInfoBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        binding.executePendingBindings();
        isLogin.set(AppUtil.isLogin());

        ivD7 = binding.adD7;
    }

    @Override
    protected void initData() {
        syncToast();
        showD7();
    }

    /**
     * 登录成功后，同步提示，只在登录页面返回时才有
     */
    private void syncToast() {
        if (getIntent().getBooleanExtra("LoginSync", false)) {
            AppUtil.showAlertDialog(this, R.string.syncMessage, R.string.ok, R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sync();
                }
            }, null);
        }
    }

    public void onMyChinaplas() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra(Constant.WEB_URL, String.format(NetWorkHelper.MY_CHINAPLAS_URL, AppUtil.getLanguageUrlType()));
        intent.putExtra(Constant.TITLE, AppUtil.isLogin() ? getString(R.string.title_my_chinaplas) : getString(R.string.title_login));
        intent.putExtra("MyCPS", true);
        startActivity(intent);
        finish();
        overridePendingTransPad();
    }

    public void onMyExhibitor() {
        intent(MyExhibitorActivity.class, getString(R.string.title_my_exhibitor));
    }

    public void onMySchedule() {
        intent(ScheduleActivity.class, getString(R.string.title_schedule));
    }

    public void onMyNameCard() {
        SharedPreferences spNameCard = getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
        boolean isCreate = spNameCard.getBoolean("isCreate", true);
        if (isCreate) {
            intent(NCardCreateEditActivity.class, getString(R.string.create_card));
        } else {
            intent(NCardActivity.class, getString(R.string.title_my_name_card));
        }
    }

    public void onMyHistoryExhibitor() {
        intent(ExhibitorHistoryActivity.class, "");
    }

    public void onSync() {
        Toast.makeText(getApplicationContext(), getString(R.string.syncStart), Toast.LENGTH_SHORT).show();
        SyncViewModel syncViewModel = new SyncViewModel(getApplicationContext());
        syncViewModel.syncMyExhibitor();
    }

    public void onLogout() {
        AppUtil.showAlertDialog(this, getString(R.string.logout_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                processLogout();
                isLogin.set(false);
                // 清除cookie即可彻底清除缓存
                CookieSyncManager.createInstance(getApplicationContext());
                CookieSyncManager.getInstance().sync();

                android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.removeSessionCookie();
                cookieManager.removeAllCookie();

                finish();
                overridePendingTransPad();
            }
        });

    }

    private static final Integer D7_INDEX = 0;

    public void showD7() {
        mEPOHelper = EPOHelper.getInstance();
        if (mEPOHelper.isD7Open(D7_INDEX)) {
            Glide.with(getApplicationContext()).load(mEPOHelper.getD7Image(D7_INDEX)).into(ivD7);
            mEPOHelper.setD7ViewLog(D7_INDEX,getApplicationContext());
        }
    }

    public void onAdClick() {
        com.adsale.ChinaPlas.data.model.EPO.D7 D7 = mEPOHelper.getItemD7(D7_INDEX);
        Intent intent = mEPOHelper.intentAd(D7.function, D7.companyID, D7.PageID);
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            mEPOHelper.setD7ClickLog(D7_INDEX,getApplicationContext());
            startActivity(intent);
            overridePendingTransPad();
        }

    }


}
