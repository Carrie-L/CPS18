package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ShareSDKDialog;

import static com.adsale.ChinaPlas.utils.Constant.DIR_WEB_CONTENT;

public class SettingActivity extends BaseActivity {
    public final ObservableField<String> version = new ObservableField<>("");
    private Intent intent;

    @Override
    protected void initView() {
        setBarTitle(R.string.title_setting);
        ActivitySettingBinding binding = ActivitySettingBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        binding.executePendingBindings();
    }

    @Override
    protected void initData() {
        version.set("v".concat(AppUtil.getAppVersion()));
    }

    public void onLangClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] languages = new String[]{"English", "繁體中文", "简体中文"};
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onChooseLanguage(i == 0 ? 1 : i == 1 ? 0 : 2);
            }
        }).create().show();
    }

    private boolean isChangeLanguage = false;

    public void onChooseLanguage(int language) {
        AppUtil.switchLanguage(this, language);
        App.mLanguage.set(language);
        recreate();
        App.mSP_Config.edit().putBoolean("isChangeLanguage", true).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("Setting", "onResume:isChangeLanguage=" + isChangeLanguage);
    }

    public void onShare() {
        ShareSDKDialog share = new ShareSDKDialog();
        LogUtil.i(TAG, "Constant.SHARE_IMAGE_PATH=" + Constant.SHARE_IMAGE_PATH);
        share.showDialog(this, getString(R.string.share_setting_text), Constant.SHARE_IMAGE_URL, getString(R.string.share_setting_url),
                Constant.SHARE_IMAGE_PATH);
        AppUtil.trackViewLog( 423, "SA", "", "");
        AppUtil.setStatEvent(getApplicationContext(), "ShareApp", "SA");
    }

    public void onLinkWebsite() {
        final String[] items = getResources().getStringArray(R.array.url_ver);
        final String[] urls = new String[]{String.format(NetWorkHelper.FULL_WEBSITE, AppUtil.getUrlLangType(AppUtil.getCurLanguage())),
                String.format(NetWorkHelper.MOBILE_WEBSITE, AppUtil.getUrlLangType(AppUtil.getCurLanguage()))};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_url);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(urls[item]);
                intent.setData(content_url);
                startActivity(intent);
                overridePendingTransPad();
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
        App.mSP_HP.edit().putBoolean("HELP_PAGE_OPEN_MAIN", true).apply();
        intent = new Intent(getApplicationContext(), isTablet?PadMainActivity.class:MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransPad();
    }

    private void backToMain() {
        isChangeLanguage = App.mSP_Config.getBoolean("isChangeLanguage", false);
        LogUtil.i(TAG, "因爲更換了語言，所以返回主界面:isChangeLanguage=" + isChangeLanguage);
        if (isChangeLanguage) {
            Intent intent = new Intent(this, isTablet?PadMainActivity.class:MainActivity.class);
            startActivity(intent);
            App.mSP_Config.edit().putBoolean("isChangeLanguage", false).apply();
        }
    }

    @Override
    public void back() {
        backToMain();
        super.back();
    }

    @Override
    public void onBackPressed() {
        backToMain();
        super.onBackPressed();
    }
}
