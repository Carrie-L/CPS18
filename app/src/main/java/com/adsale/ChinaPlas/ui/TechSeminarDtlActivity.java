package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarSpeaker;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.databinding.ActivityTechSeminarDtlBinding;
import com.adsale.ChinaPlas.databinding.ActivityTechnicalListBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

// TODO: 2017/10/27  M6 
public class TechSeminarDtlActivity extends BaseActivity {

    private ActivityTechSeminarDtlBinding binding;
    private SeminarInfo seminarInfo;

    @Override
    protected void initView() {
        binding = ActivityTechSeminarDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
    }

    @Override
    protected void initData() {
        OtherRepository repository = OtherRepository.getInstance();
        repository.initSeminarSpeakDao();

        seminarInfo = getIntent().getParcelableExtra("Info");
        binding.setInfo(seminarInfo);
        binding.setView(this);
        LogUtil.i(TAG, "seminarInfo=" + seminarInfo.toString());

        ArrayList<SeminarSpeaker> speaker = repository.getSeminarSpeakerItem(seminarInfo.getCompanyID(), getCurrLangId());
        if (!speaker.isEmpty()) {
            SeminarSpeaker seminarSpeaker = speaker.get(0);
            binding.setSpeak(seminarSpeaker);
            LogUtil.i(TAG, "seminarSpeaker=" + seminarSpeaker.toString());
        }
        binding.executePendingBindings();
    }

    // TODO: 2017/10/27 tel call email
    public void onCall(String tel) {

    }

    public void onSendEmail(String email) {

    }

    private int getCurrLangId() {
        int currLang = AppUtil.getCurLanguage();
        return currLang == 0 ? 950 : currLang == 1 ? 1252 : 936;
    }
}
