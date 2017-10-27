package com.adsale.ChinaPlas.ui;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityMainBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;

import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void preView() {
        super.preView();
        mToolbarBackgroundRes=R.drawable.main_header;
        isShowTitleBar.set(false);
    }

    @Override
    protected void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);

        MainFragment mainFragment = MainFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.contentFrame, mainFragment).commit();
    }

    @Override
    protected void initData() {
        permissionSD();
        if(mNavViewModel!=null){
            mNavViewModel.setMainActivity(this);
        }




    }

    private void permissionSD() {
        boolean sdPermission = PermissionUtil.checkPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        LogUtil.i(TAG, "sdPermission=" + sdPermission);

        if (!sdPermission) {
            LogUtil.i(TAG, "请求权限");
            PermissionUtil.requestPermission(this, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, PMS_CODE_WRITE_SD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
