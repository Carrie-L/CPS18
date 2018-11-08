package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarSpeaker;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.ActivityTechSeminarDtlBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 2017/10/27  M6

/**
 * 從List過來, 接收 Info
 * 从其他地方过来，接收 INTENT_SEMINAR_DTL_ID (EventID)
 */
public class TechSeminarDtlActivity extends BaseActivity {

    private ActivityTechSeminarDtlBinding binding;
    private SeminarInfo seminarInfo;
    private OtherRepository repository;
    private adAdvertisementObj.M6V2.Topic m6Topic;
    private String mBannerUrl;
    private View m6View;
    private String mEventID;
    private String seminarsummary;

    @Override
    protected void initView() {
        setBarTitle(R.string.title_technical_seminar);
        binding = ActivityTechSeminarDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
    }

    @Override
    protected void initData() {
        repository = OtherRepository.getInstance();
        repository.initSeminarSpeakDao();

        getSeminarInfo();

        binding.setInfo(seminarInfo);
        binding.setView(this);
        LogUtil.i(TAG, "seminarInfo=" + seminarInfo.toString());

        ArrayList<SeminarSpeaker> speaker = repository.getSeminarSpeakerItem(seminarInfo.getEventID(), getCurrLangId());
        if (!speaker.isEmpty()) {
            SeminarSpeaker seminarSpeaker = speaker.get(0);
            binding.setSpeak(seminarSpeaker);
            LogUtil.i(TAG, "seminarSpeaker=" + seminarSpeaker.toString());
            seminarsummary = seminarSpeaker.getSeminarsummary();
        }
        binding.executePendingBindings();

        showM6();

        setSummaryUrlLink();
    }

    private void getSeminarInfo() {
        seminarInfo = getIntent().getParcelableExtra("Info");
        if (seminarInfo == null) {
            /*  seminar_info.eventId。ad.txt: M6.Topics.EVENTID  */
            mEventID = getIntent().getStringExtra(Constant.INTENT_SEMINAR_DTL_ID);

            seminarInfo = repository.getItemSeminarInfo(mEventID);
            if (seminarInfo == null) {
                Toast.makeText(this, getString(R.string.nodata), Toast.LENGTH_LONG).show();
            }
        } else {
            mEventID = seminarInfo.getEventID() + "";
        }
        LogUtil.i(TAG, "mEventID=" + mEventID);
    }

    /**
     * https://o97tbiy1f.qnssl.com/advertisement/M6/banner/phone_17016en_20170413.jpg
     * 712 * 218
     * 从 M6.Topics 中取得id 与 SeminarInfo.id 对比，相同则显示M6广告。
     */
    private boolean isShowM6() {
        ADHelper adHelper = new ADHelper(this);
        if (!adHelper.isAdOpen()) {
            return false;
        }
        adAdvertisementObj adObj = adHelper.getAdObj();
        adAdvertisementObj.M6V2 M6 = adObj.M6_V2;
        StringBuilder sb = new StringBuilder();
        sb.append(adObj.Common.baseUrl).append(M6.filepath).append(M6.banner).append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone);//topics image

        ArrayList<adAdvertisementObj.M6V2.Topic> topics = adObj.M6_V2.topics;
        int size = topics.size();
        for (int i = 0; i < size; i++) {
            if (topics.get(i).eventID.equals(mEventID)) {
                m6Topic = topics.get(i);
                sb.append(m6Topic.getImage()).append(M6.format);
                mBannerUrl = sb.toString();
                LogUtil.i(TAG,"mBannerUrl="+mBannerUrl);
                return true;
            }
        }
        sb.delete(0, sb.length() - 1);
        return false;
    }

    private void showM6() {
        if (!isShowM6()) {
            return;
        }
        ImageView ivBanner = binding.ivSeminarBanner;
        ivBanner.setVisibility(View.VISIBLE);
        int height = AppUtil.getCalculatedHeight(Constant.M6_BANNER_WIDTH, Constant.M6_BANNER_HEIGHT);
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(AppUtil.getScreenWidth(), height);
        Glide.with(getApplicationContext()).load(Uri.parse(mBannerUrl)).apply(options).into(ivBanner);

        LogUtil.i(TAG,"Uri.parse(mBannerUrl)="+Uri.parse(mBannerUrl));

        if (!TextUtils.isEmpty(m6Topic.description)) {
            if (m6View == null) {
                m6View = binding.vsM6.getViewStub().inflate();
                TextView tvDes = m6View.findViewById(R.id.tv_seminar_ad_description);
                tvDes.setText(m6Topic.description.replaceAll("  ", "\n\n"));
                tvDes.setVisibility(View.VISIBLE);
            } else {
                m6View.setVisibility(View.VISIBLE);
            }
        }
        AppUtil.trackViewLog(303, "Technical", "", m6Topic.eventID);
    }

    public void onCall(String tel) {
        AppUtil.callPhoneIntent(this, tel);
    }

    public void onSendEmail(String email) {
        AppUtil.sendEmailIntent(this, email);
    }

    private int getCurrLangId() {
        int currLang = AppUtil.getCurLanguage();
        return currLang == 0 ? 950 : currLang == 1 ? 1252 : 936;
    }

    public void onCompanyClick(String companyId) {
        Intent intent = new Intent(this, ExhibitorDetailActivity.class);
        intent.putExtra(Constant.COMPANY_ID, companyId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }

    /**
     * summary中给url设置超链接
     */
    private void setSummaryUrlLink() {
        if (seminarsummary != null && seminarsummary.toLowerCase().contains("http")) {
            Pattern p = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(seminarsummary);
            while (matcher.find()) {
                String url = matcher.group();
                LogUtil.i("SeminarSpeaker", "url=" + url);

                SpannableStringBuilder builder = new SpannableStringBuilder(seminarsummary);
                builder.setSpan(new URLSpan(url), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                binding.tvSummary.setText(builder);
                // 在单击链接时凡是有要执行的动作，都必须设置MovementMethod对象
                binding.tvSummary.setMovementMethod(LinkMovementMethod.getInstance());
            }
        } else {
            binding.tvSummary.setText(seminarsummary);
        }
    }
}
