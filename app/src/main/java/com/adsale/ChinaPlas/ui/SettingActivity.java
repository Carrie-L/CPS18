package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.NameCardRepository;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.databinding.ActivitySettingBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.CalendarUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import static com.adsale.ChinaPlas.ui.view.HelpView.HELP_PAGE;
import static com.adsale.ChinaPlas.ui.view.HelpView.HELP_PAGE_MAIN;
import static com.adsale.ChinaPlas.utils.Constant.DIR_WEB_CONTENT;

public class SettingActivity extends BaseActivity {
    public final ObservableField<String> version = new ObservableField<>("");
    private Intent intent;

    @Override
    protected void initView() {
        ActivitySettingBinding binding = ActivitySettingBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            version.set(info.versionName.concat("v"));// 测试版本将“V”换成“T”
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onShare(){

    }

    public void onLinkWebsite() {
        final String[] items = getResources().getStringArray(R.array.url_ver);
        final String[] urls = new String[]{String.format(NetWorkHelper.FULL_WEBSITE, AppUtil.getUrlLangType(App.mLanguage.get())),
                String.format(NetWorkHelper.MOBILE_WEBSITE, AppUtil.getUrlLangType(App.mLanguage.get()))};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_url);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(urls[item]);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    public void onAddToCalendar() {
        CalendarUtil calendarUtil = new CalendarUtil(this);
        calendarUtil.addToCalendar();
    }

    public void onResetAll() {
        AppUtil.showAlertDialog(this, R.string.ask_clear, R.string.yes, R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 清空我的日程表
                ScheduleRepository scheduleRepository = ScheduleRepository.getInstance();
                scheduleRepository.clearAll();

                // 清空我的参展商（本地）| 清空兴趣展商 : 查找Exhibitor表中isFavourite=1的数据，删除
                ExhibitorRepository exhibitorRepository = ExhibitorRepository.getInstance();
                exhibitorRepository.cancelMyExhibitor();
                exhibitorRepository.clearInterestedExhibitor();

                // 退出登录
                logout();

                // 重置我的名片
                SharedPreferences spNameCard = getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
                spNameCard.edit().putBoolean("isCreate", false).apply();

                // 名片列表
                NameCardRepository cardRepository = new NameCardRepository();
                cardRepository.clearNameCard();

                // 预登记资料
                App.mSP_Login.edit().putBoolean("IsPreUser", false).apply();

            }
        }, null);
    }

    public void onPrivacy() {
        intent = new Intent(getApplicationContext(), WebContentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", getString(R.string.setting_privacy_policy));
        intent.putExtra(Constant.WEB_URL, DIR_WEB_CONTENT.concat("99"));
        startActivity(intent);
        overridePendingTransPad();
    }

    public void onUseItems() {
        intent = new Intent(getApplicationContext(), WebContentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", getString(R.string.setting_use_terms));
        intent.putExtra(Constant.WEB_URL, DIR_WEB_CONTENT.concat("100"));
        startActivity(intent);
        overridePendingTransPad();
    }

    public void onHelpPage() {
        App.mSP_Config.edit().putInt(HELP_PAGE + HELP_PAGE_MAIN, -1).apply();
        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransPad();
    }


}
