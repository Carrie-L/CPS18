package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityUserInfoBinding;
import com.adsale.ChinaPlas.utils.AppUtil;


public class UserInfoActivity extends BaseActivity {
    public ObservableBoolean isLogin = new ObservableBoolean();

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.title_my_account));
        ActivityUserInfoBinding binding = ActivityUserInfoBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        binding.executePendingBindings();
        isLogin.set(AppUtil.isLogin());
    }

    @Override
    protected void initData() {
        syncToast();
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
        intent(MyExhibitorActivity.class,getString(R.string.title_my_exhibitor));
    }

    public void onMySchedule() {
        intent(ScheduleActivity.class,getString(R.string.title_schedule));
    }

    public void onMyNameCard() {
        SharedPreferences spNameCard = getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
        boolean isCreate = spNameCard.getBoolean("isCreate", true);
        if (isCreate) {
            intent(NCardCreateEditActivity.class,getString(R.string.create_card));
        } else {
            intent(NCardActivity.class,getString(R.string.title_my_name_card));
        }
    }

    public void onMyHistoryExhibitor() {
        intent(ExhibitorHistoryActivity.class,"");
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


}
