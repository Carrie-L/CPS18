package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.view.View;
import android.widget.ImageView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.ad.AD;
import com.adsale.ChinaPlas.data.ad.D6;
import com.adsale.ChinaPlas.databinding.ActivityUserInfoBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.bumptech.glide.Glide;

/**
 * 用戶資料 / My Tools
 */
public class UserInfoActivity extends BaseActivity {
    public ObservableBoolean isLogin = new ObservableBoolean();
    private ImageView ivD6;

    @Override
    protected void initView() {
        setBarTitle(R.string.title_my_account);
        ActivityUserInfoBinding binding = ActivityUserInfoBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        binding.executePendingBindings();
        isLogin.set(AppUtil.isLogin());

        ivD6 = binding.adD6;

    }

    @Override
    protected void initData() {
        syncToast();

        showD6();
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

    public void onLogout() {
        AppUtil.showAlertDialog(this, getString(R.string.logout_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isLogin.set(false);
                processLogout();
//                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//                overridePendingTransPad();
            }
        });

    }


    public void parseAD() {


    }


    public void showD6() {
       final AD ad = Parser.parseJsonFilesDirFile(AD.class, Constant.TXT_AD_TEST);
        LogUtil.i(TAG, "AD = " + ad.toString());

        if (ad.D6.isOpen(App.mLanguage.get())[0] == 0) {
            return;
        }

        Glide.with(this).load(ad.base.baseUrl + ad.D6.SC.images[0]).into(ivD6);
        LogUtil.i(TAG, "D6 URL=" + ad.base.baseUrl + ad.D6.SC.images[0]);

        ivD6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ad.D6.SC.function[0]==1){

                }
            }
        });


    }


}
