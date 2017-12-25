package com.adsale.ChinaPlas.ui;

import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.MenuAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.databinding.ActivityMainPadBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.MainViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/12/21.
 * 平板主界面
 */

public class PadMainActivity extends BaseActivity implements OnIntentListener {

    private MainViewModel mainViewModel;
    private ActivityMainPadBinding padBinding;
    private MainPic mainPic;
    private int scale;

    @Override
    protected void preView() {
        super.preView();
        isShowTitleBar.set(false);
        mToolbarBackgroundRes = R.drawable.main_header;
        TAG = "PadMainActivity";
    }

    @Override
    protected void initView() {
        padBinding = ActivityMainPadBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        mainViewModel = new MainViewModel(getApplicationContext(), this);
        padBinding.setModel(mainViewModel);
        int language = AppUtil.getCurLanguage();
        AppUtil.switchLanguage(getApplicationContext(), language);
    }

    @Override
    protected void initData() {
        mainViewModel.init(padBinding.mainTopViewPager, padBinding.vpindicator, padBinding.ivAd, mNavViewModel);
        mainPic = mainViewModel.parseMainInfo();
        setRightBanner();
        initRecyclerView();
        mainViewModel.setPadTopBanner(padBinding.rlTopBanner);
        mainViewModel.setTopPics();
        mainViewModel.setM2AD();
    }

    private void initRecyclerView() {
        LogUtil.i(TAG, "scale=" + scale);

        RecyclerView recyclerView = padBinding.menuRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 8 * scale;
                outRect.right = 16;
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8 * scale;
        recyclerView.setLayoutParams(params);

        ArrayList<MainIcon> largeIcons = new ArrayList<>();
        ArrayList<MainIcon> littleIcons = new ArrayList<>();
        mainViewModel.getMainIcons(largeIcons, littleIcons);
        MenuAdapter adapter = new MenuAdapter(getApplicationContext(), largeIcons, littleIcons, this, mNavViewModel);
        recyclerView.setAdapter(adapter);
    }

    private void setRightBanner() {
        scale = (int) DisplayUtil.getScale(getApplicationContext());
        scale = scale > 1 ? 2 : 1;
        LogUtil.i(TAG, "scale=" + scale);
        int margin = (scale * 8 * 3);
        LogUtil.i(TAG, "margin=" + margin);

        int bannerHeight = (int) ((632 * AppUtil.getPadHeightRate() - margin) / 2);
        int bannerWidth = (567 * bannerHeight) / 292;
        LogUtil.i(TAG, "bannerWidth=" + bannerWidth + ",bannerHeight=" + bannerHeight);
        App.mSP_Config.edit().putInt("itemBannerHeight", bannerHeight).putInt("itemBannerWidth", bannerWidth).apply();

        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(bannerWidth, (int) (632 * AppUtil.getPadHeightRate())-(8 * scale));
        llParams.bottomMargin = 8 * scale;
        padBinding.llBanner.setLayoutParams(llParams);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(bannerWidth, bannerHeight);
        params1.topMargin = 8 * scale;
        padBinding.ivUpPic.setLayoutParams(params1);
        padBinding.ivDownPic.setLayoutParams(params1);
        setBottomImage();
    }

    private void setBottomImage() {
        LogUtil.i(TAG, "mainPic.LeftBottomBanner.TC.BannerImage_Pad=" + mainPic.LeftBottomBanner.TC.BannerImage_Pad);
        Glide.with(getApplicationContext()).load(Uri.parse(mNavViewModel.mCurrLang.get() == 0 ? mainPic.LeftBottomBanner.TC.BannerImage_Pad : mNavViewModel.mCurrLang.get() == 1 ? mainPic.LeftBottomBanner.EN.BannerImage_Pad : mainPic.LeftBottomBanner.SC.BannerImage_Pad)).into(padBinding.ivUpPic);
        Glide.with(getApplicationContext()).load(Uri.parse(mNavViewModel.mCurrLang.get() == 0 ? mainPic.RightBottomBanner.TC.BannerImage_Pad : mNavViewModel.mCurrLang.get() == 1 ? mainPic.RightBottomBanner.EN.BannerImage_Pad : mainPic.RightBottomBanner.SC.BannerImage_Pad)).into(padBinding.ivDownPic);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {

    }
}
