package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityUserInfoBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;

import static com.adsale.ChinaPlas.utils.Constant.COM_HISTORY_EXHI;

public class UserInfoActivity extends BaseActivity {

    @Override
    protected void initView() {
        ActivityUserInfoBinding binding = ActivityUserInfoBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {

    }

    public void onMyExhibitor() {
        intent(MyExhibitorActivity.class);
    }

    public void onMySchedule() {
        intent(ScheduleActivity.class);
    }

    public void onMyNameCard() {
        SharedPreferences spNameCard = getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
        boolean isCreate = spNameCard.getBoolean("isCreate", true);
        if (isCreate) {
            intent(NCardCreateEditActivity.class);
        } else {
            intent(NCardActivity.class);
        }
    }

    public void onMyHistoryExhibitor() {
        Intent intent = new Intent(this, CommonListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", getString(R.string.title_history_exhibitor));
        intent.putExtra(Constant.INTENT_COMMON_TYPE, COM_HISTORY_EXHI);
        startActivity(intent);
        overridePendingTransPad();
    }

    public void onLogout() {
        AppUtil.showAlertDialog(this, getString(R.string.logout_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                processLogout();
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransPad();
            }
        });

    }


}
