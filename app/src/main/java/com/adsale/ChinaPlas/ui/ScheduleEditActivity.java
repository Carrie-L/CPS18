package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.databinding.ActivityScheduleItemBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ScheduleViewModel;

import static android.R.attr.name;
import static java.lang.Long.getLong;

public class ScheduleEditActivity extends BaseActivity {

    private ScheduleViewModel mScheduleModel;
    private long id;

    @Override
    protected void initView() {
        ActivityScheduleItemBinding binding = ActivityScheduleItemBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        Bundle bundle = getIntent().getExtras();
        long id = bundle.getLong("id");
        int dateIndex = bundle.getInt("dateIndex");
        LogUtil.i(TAG, "id=" + id + ",dateIndex=" + dateIndex);
        if (id == 0) {
            mScheduleModel = new ScheduleViewModel(getApplicationContext(), dateIndex);
        } else {
            mScheduleModel = new ScheduleViewModel(getApplicationContext(), id);
        }
        binding.setScheduleModel(mScheduleModel);
    }

    @Override
    protected void initData() {

    }
}
