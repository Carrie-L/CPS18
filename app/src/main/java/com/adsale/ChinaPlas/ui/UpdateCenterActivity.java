package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityUpdateCenterBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.UpdateCenterViewModel;

/**
 * 内容更新中心
 * 表 UPDATE_CENTER
 * 如果 STATUS == 1，说明没有更新；否则，有更新。
 * 全部没有更新时，btnUpdateAll,btnUpdate 按钮为灰色，不可点击。
 */
public class UpdateCenterActivity extends BaseActivity {
    private ActivityUpdateCenterBinding binding;
    private UpdateCenterViewModel model;

    @Override
    protected void initView() {
        binding = ActivityUpdateCenterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        model = new UpdateCenterViewModel();
        binding.setModel(model);
    }

    @Override
    protected void initData() {
        model.init(binding.downloadProgress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
        model.onDestroy();
        model = null;
    }

    @Override
    public void back() {
        if (!model.canBack) {
            LogUtil.e(TAG, "back:不可以返回:" + model.canBack);
            return;
        }
        LogUtil.i(TAG, "back:可以返回:" + model.canBack);
        super.back();
    }

    @Override
    public void onBackPressed() {
        if (!model.canBack) {
            LogUtil.e(TAG, "onBackPressed:不可以返回:" + model.canBack);
            return;
        }
        LogUtil.i(TAG, "onBackPressed:可以返回:" + model.canBack);
        super.onBackPressed();
    }
}
