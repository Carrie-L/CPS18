package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityUpdateCenterBinding;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.UpdateCenterViewModel;

/**
 * 内容更新中心
 * 表 UPDATE_CENTER
 * 如果 STATUS == 1，说明没有更新；否则，有更新。
 * 全部没有更新时，btnUpdateAll,btnUpdate 按钮为灰色，不可点击。
 * // TODO: 2017/11/10 下载csv后，解析，保存到数据库
 */
public class UpdateCenterActivity extends BaseActivity {
    private ActivityUpdateCenterBinding binding;
    private UpdateCenterViewModel model;

    @Override
    protected void initView() {
        setBarTitle(R.string.title_update_center);
        binding = ActivityUpdateCenterBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        model = new UpdateCenterViewModel();
        binding.setModel(model);
    }

    @Override
    protected void initData() {
        model.init(binding.downloadProgress);
        if (mNavViewModel != null) {
            LogUtil.i(TAG, "mNavViewModel!=NULL");
            model.setNavModel(mNavViewModel);
        }
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
        if(getIntent().getBooleanExtra("FromMain",false)){ // 从Main跳转过来，则设置 isM2Popup 为false，这样跳转回去时可以在Main的onResume()弹出M2广告
            App.mSP_Config.edit().putBoolean("isM2Popup",false).apply();
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
        if(getIntent().getBooleanExtra("FromMain",false)){ // 从Main跳转过来，则设置 isM2Popup 为false，这样跳转回去时可以在Main的onResume()弹出M2广告
            App.mSP_Config.edit().putBoolean("isM2Popup",false).apply();
        }
        LogUtil.i(TAG, "onBackPressed:可以返回:" + model.canBack);
        super.onBackPressed();
    }
}
