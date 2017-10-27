package com.adsale.ChinaPlas.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.databinding.ActivityUpdateCenterBinding;
import com.adsale.ChinaPlas.ui.view.UpdateCenterView;

import java.util.ArrayList;

/**
 * 内容更新中心
 * 表 UPDATE_CENTER
 * 如果 STATUS == 1，说明没有更新；否则，有更新。
 * 全部没有更新时，btnUpdateAll,btnUpdate 按钮为灰色，不可点击。
 */
public class UpdateCenterActivity extends BaseActivity {
    private OtherRepository mRepository;
    private ArrayList<UpdateCenter> list;
    private ArrayList<UpdateCenterView> views;
    private int j;
    private TextView btnUpdateAll;

    @Override
    protected void initView() {
        ActivityUpdateCenterBinding binding = ActivityUpdateCenterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
//        View view=getLayoutInflater().inflate(R.layout.activity_update_center,mBaseFrameLayout,true);

        views = new ArrayList<>();
//        views.add((UpdateCenterView) view.findViewById(R.id.uc1));
//        views.add((UpdateCenterView) view.findViewById(R.id.uc2));
//        views.add((UpdateCenterView) view.findViewById(R.id.uc3));
//        views.add((UpdateCenterView) view.findViewById(R.id.uc4));
//        views.add((UpdateCenterView) view.findViewById(R.id.uc5));
        views.add(binding.uc1);
        views.add(binding.uc2);
        views.add(binding.uc3);
        views.add(binding.uc4);
        views.add(binding.uc5);
        btnUpdateAll = binding.btnUpdateAll;
    }

    @Override
    protected void initData() {
        mRepository = OtherRepository.getInstance();
        mRepository.initUpdateCenterDao();
        list = mRepository.getUpdateCenters();
        setData();
    }

    private void setData() {
        int size = list.size();
        UpdateCenter entity;
        j = 0;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            entity.setStatus(0);/* JFT */
            views.get(i).setData(entity);
            if (entity.getStatus() == 1) {
                j++;
            }
        }

    }

    public void onUpdateAll() {
        if (j < 5) {
            btnUpdateAll.setClickable(true);
            btnUpdateAll.setTextColor(Color.WHITE);
            btnUpdateAll.setBackgroundResource(R.drawable.btn_update_all_red);
        }
    }


}
