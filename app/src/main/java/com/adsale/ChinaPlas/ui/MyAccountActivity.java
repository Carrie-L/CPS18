package com.adsale.ChinaPlas.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityMyAccountBinding;
import com.adsale.ChinaPlas.ui.view.IconView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;

public class MyAccountActivity extends BaseActivity {
    private ActivityMyAccountBinding binding;

    @Override
    protected void initView() {
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
    }

    @Override
    protected void initData() {
        IconView ivExhibitor = binding.myExhibitor;
        IconView ivHistory = binding.myHistory;
        IconView ivLogout = binding.myLogout;
        IconView ivNameCard = binding.myNameCard;
        IconView ivSchedule = binding.mySchedule;

        ivExhibitor.setIconText(R.drawable.ic_my_exhibitor, R.string.title_my_exhibitor);
        ivSchedule.setIconText(R.drawable.ic_schedule, R.string.title_schedule);
        ivNameCard.setIconText(R.drawable.ic_name_card_user, R.string.title_my_name_card);
        ivHistory.setIconText(R.drawable.ic_my_history, R.string.title_history_exhibitor);
        ivLogout.setIconText(R.drawable.ic_logout, R.string.logout);

        whetherSync();

        ivExhibitor.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ivExhibitor", Toast.LENGTH_SHORT).show();
            }
        });
        ivHistory.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ivHistory", Toast.LENGTH_SHORT).show();
            }
        });
        ivSchedule.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent(ScheduleActivity.class);
            }
        });
        ivNameCard.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ivNameCard", Toast.LENGTH_SHORT).show();
            }
        });
        ivLogout.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.showAlertDialog(MyAccountActivity.this,getString(R.string.logout_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtil.putLogout();
                        dialog.dismiss();
                        Intent intent = new Intent(MyAccountActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });




    }

    private void whetherSync(){
        if(getIntent().getBooleanExtra("LoginSync",false)){
            AppUtil.showAlertDialog(this, getString(R.string.syncMessage), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SyncViewModel syncViewModel = new SyncViewModel(getApplicationContext());
                    syncViewModel.syncMyExhibitor();
                }
            });
        }
    }


}
