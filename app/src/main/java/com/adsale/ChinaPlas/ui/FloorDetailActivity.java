package com.adsale.ChinaPlas.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.FloorNavAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.ActivityFloorDetailBinding;
import com.adsale.ChinaPlas.databinding.LayoutD6OneBannerBinding;
import com.adsale.ChinaPlas.databinding.LayoutD6TwoHalfBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.IntentHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.FloorDtlViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.reactivex.observers.DisposableObserver;

import static com.adsale.ChinaPlas.App.mLogHelper;

/**
 * todo   mTypePrefix = "FPage_" + gIntent.getStringExtra("FloorID");
 * <p>
 * intent:【BOOTH & HALL】 [FromCls(ExhibitorDelAty must)] / 【HALL】
 */
public class FloorDetailActivity extends BaseActivity implements OnIntentListener, FloorDtlViewModel.OnDrawerListener {
    private final String TAG = "FloorDetailActivity";
    private final static Integer NAV_WIDTH = AppUtil.isTablet() ? 320 : 250;
    public final static Integer BASE_SCALE = AppUtil.isTablet() ? 1 : 2;
    private FloorDtlViewModel mFloorModel;
    private ActivityFloorDetailBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Exhibitor> exhibitors = new ArrayList<>();
    private ArrayList<Exhibitor> exhibitorsCache = new ArrayList<>();
    private FloorNavAdapter navAdapter;
    //    private NavigationView navView;
    private FrameLayout navFrame;
    private EditText editText;
    private String mHall;
    private String imgName;
    private View navView;
    private ImageView ivShade;
    /**
     * 从哪个类跳转而来，1：展商详情
     */
    private int fromCls;
    private HelpView helpDialog;
    private EPOHelper epoHelper;
    private FrameLayout mFrameD6;
    private LayoutD6TwoHalfBinding d6TwoHalfBinding;

    @Override
    protected void initView() {
        mHall = getIntent().getStringExtra("HALL");
        fromCls = getIntent().getIntExtra("FromCls", 0);
        barTitle.set(mHall);
        binding = ActivityFloorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);

        getImgNameFromSD();
        if (TextUtils.isEmpty(imgName)) { // 如果找不到这张图片，则返回
            return;
        }

        FloorRepository repository = FloorRepository.getInstance();
        mFloorModel = new FloorDtlViewModel(getApplicationContext(), repository, getFragmentManager(), this, binding.ivMap, mHall, fromCls);
        binding.setModel(mFloorModel);
        binding.executePendingBindings();

        initNavView();
        showD6();
    }

    private void getImgNameFromSD() {
        String sdPath = App.rootDir + "FloorPlan/";
        File file = new File(sdPath);
        if (file.exists()) {  // 1> 如果SD卡中有这个文件夹，则从SD卡中寻找图片
            getImgName(file.list());
            if (TextUtils.isEmpty(imgName)) { // 2> 如果在SD卡中没有找到图片，则从Asset中寻找
                getImgNameFromAsset();
            }
        } else { // 1> 否则从Asset中寻找
            getImgNameFromAsset();
        }
    }

    private void getImgNameFromAsset() {
        LogUtil.i(TAG, "getImgNameFromAsset");
        AssetManager am = getAssets();
        try {
            getImgName(am.list("FloorPlan"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getImgName(String[] names) {
        for (String name : names) {
            if (name.contains(mHall)) {
                imgName = name;
                break;
            }
        }
    }

    private void initNavView() {
        navFrame = binding.floorNav;
        ivShade = binding.ivShade;
        navView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.nav_view_floor_search, navFrame, true);
        editText = navView.findViewById(R.id.et);
        recyclerView = navView.findViewById(R.id.rv_floor_nav);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        recyclerView.setHasFixedSize(true);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (navAdapter == null) {
                    return;
                }
                if (TextUtils.isEmpty(s.toString())) {
                    navAdapter.setList(exhibitorsCache);
                } else {
                    exhibitors.clear();
                    exhibitors = mFloorModel.getEditExhibitors(s.toString());
                    navAdapter.setList(exhibitors);
                }
            }
        });

        ivShade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAnimation();
            }
        });

    }


    @Override
    protected void initData() {
        if (TextUtils.isEmpty(imgName)) { // 如果找不到这张图片，则返回
            binding.tvNodata.setVisibility(View.VISIBLE);
            return;
        }
        mFloorModel.setOnDrawerListener(this);

        Bitmap bitmap = BitmapFactory.decodeStream(AppUtil.getInputStreamFromSdOrAsset("FloorPlan/" + imgName));
//        binding.ivMap.setImageBitmap(bitmap);
        binding.ivMap.setZoomable(true);
        mFloorModel.startMap(bitmap);

//        if (index != -1) {
//            showM4Flag();
//        } else {
//            mFloorModel.showBitmap();
//        }
        mFloorModel.showBitmap();

        Intent intent = getIntent();
        String booth = intent.getStringExtra("BOOTH");
        if (!TextUtils.isEmpty(booth)) {
            mFloorModel.drawSingleFlagOnMap(booth);
        }

        if (HelpView.isFirstShow(HelpView.HELP_PAGE_FLOOR_DTL)) {
            showHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_FLOOR_DTL, HelpView.HELP_PAGE_FLOOR_DTL).apply();
        }

        binding.ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpPage();
            }
        });

        mLogHelper.logFloorInfo(mHall);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_Info);
    }

    private void showHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_FLOOR_DTL);
        helpDialog.show(ft, "Dialog");
    }

//    DisposableObserver observer = new DisposableObserver() {
//        @Override
//        public void onNext(Object value) {
//            LogUtil.i(TAG, "DisposableObserver:onNext");
//            binding.ivMap.setImageBitmap((Bitmap) value);
////            binding.ivMap.setScale(4);
//        }
//
//        @Override
//        public void onError(Throwable e) {
//
//        }
//
//        @Override
//        public void onComplete() {
//            LogUtil.i(TAG, "DisposableObserver:onComplete");
////            mFloorModel.showBitmap();
//
//            if (mFloorModel.isNeedScale) {
//                binding.ivMap.setScale(4, mFloorModel.flagX, mFloorModel.flagY, false);
//                mFloorModel.translate();
//            }
//
//        }
//    };

    private void showD6OneBanner() {
        LayoutD6OneBannerBinding d6OneBannerBinding = LayoutD6OneBannerBinding.inflate(LayoutInflater.from(getApplicationContext()), null);
        d6OneBannerBinding.setModel(mFloorModel);
        d6OneBannerBinding.executePendingBindings();
        mFrameD6.addView(d6OneBannerBinding.getRoot());
        setD6OneBannerSize(false, d6OneBannerBinding.ivD6Banner);
        Glide.with(getApplicationContext()).load(epoHelper.getD6BannerImage(0)).into(d6OneBannerBinding.ivD6Banner);
        // hall_function_pageId
        mLogHelper.logD6(mHall + "_one_" + epoHelper.D6.function[0] + "_" + epoHelper.D6.pageID[0], true);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);
    }

    private void setD6OneBannerSize(boolean isHalf, ImageView iv) {
        int adWidth = isHalf ? AppUtil.getScreenWidth() / 2 : AppUtil.getScreenWidth();
        int adHeight = (adWidth * Constant.M4_HEIGHT) / Constant.M4_WIDTH;
        LogUtil.i(TAG, "adWidth=" + adWidth + ",adHeight=" + adHeight);
        if (isHalf) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(adWidth, adHeight);
            iv.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(adWidth, adHeight);
            iv.setLayoutParams(params);
        }
    }

    private void showD6LeftBanner() {
        initD6HalfView();
        ImageView ivLeft = d6TwoHalfBinding.ivD6Left;
        setD6OneBannerSize(true, ivLeft);
        Glide.with(getApplicationContext()).load(epoHelper.getD6BannerImage(0));

        mLogHelper.logD6(mHall + "_left_" + epoHelper.D6.function[0] + "_" + epoHelper.D6.pageID[0], true);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);

//        ivLeft.setVisibility(View.VISIBLE);
//        int adWidth = AppUtil.getScreenWidth() / 2;
//        int adHeight = (adWidth * Constant.M4_HEIGHT) / Constant.M4_WIDTH;
//        LogUtil.i(TAG, "adWidth=" + adWidth + ",adHeight=" + adHeight);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(adWidth, adHeight);
//        ivLeft.setLayoutParams(params);

//        adObj = adHelper.getAdObj();
//        StringBuilder m4Url = new StringBuilder();
//        m4Url.append(adObj.Common.baseUrl).append(adObj.M4_left.filepath).append(adObj.M4_left.floor[index]).append("/")
//                .append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(AppUtil.getName("tc", "en", "sc"))
//                .append("_left_").append(adObj.M4_left.version[index]).append(adObj.M4_left.format);
//        LogUtil.i(TAG, "M4_leftUrl=" + m4Url.toString());
    }

    private void initD6HalfView() {
        if (d6TwoHalfBinding == null) {
            d6TwoHalfBinding = LayoutD6TwoHalfBinding.inflate(LayoutInflater.from(getApplicationContext()), null);
            d6TwoHalfBinding.setModel(mFloorModel);
            d6TwoHalfBinding.executePendingBindings();
            mFrameD6.addView(d6TwoHalfBinding.getRoot());
        }
    }

    private void showD6RightBanner() {
        initD6HalfView();
        ImageView ivD6Right = d6TwoHalfBinding.ivD6Right;
        setD6OneBannerSize(true, ivD6Right);
        Glide.with(getApplicationContext()).load(epoHelper.getD6BannerImage(1));

        mLogHelper.logD6(mHall + "_right_" + epoHelper.D6.function[1] + "_" + epoHelper.D6.pageID[1], true);
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_VIEW);
    }

    private void showD6() {
        epoHelper = EPOHelper.getInstance();
        EPO.D6 D6 = epoHelper.getItemD6(mHall);
        if (D6 == null) {
            return;
        }
        mFrameD6 = binding.frameD6;
        if (epoHelper.isD6Open0()) {  // 一整个 banner
            showD6OneBanner();
        } else if (epoHelper.isD6Open1()) {
            showD6LeftBanner();
        } else if (epoHelper.isD6Open2()) {
            showD6RightBanner();
        }
    }

    private void showM4Flag() {
//        String m4RightCompanyId = adObj.M4_right.action_companyID[index];
//        String m4LeftCompanyId = adObj.M4_left.action_companyID[index];
//        if (TextUtils.isEmpty(m4LeftCompanyId.trim()) && TextUtils.isEmpty(m4RightCompanyId.trim())) { // 左右两个广告都没有companyId
//            mFloorModel.showBitmap();
//        } else if (TextUtils.isEmpty(m4RightCompanyId.trim())) {
//            mFloorModel.drawAdFlagOnMap(m4LeftCompanyId);
//        } else {
//            // 左右两边轮流显示大头针
//            if (App.mSP_Config.getInt("M4_RANDOM", 0) == 1) {
//                mFloorModel.drawAdFlagOnMap(m4LeftCompanyId);
//                App.mSP_Config.edit().putInt("M4_RANDOM", 2).apply();
//            } else {
//                mFloorModel.drawAdFlagOnMap(m4RightCompanyId);
//                App.mSP_Config.edit().putInt("M4_RANDOM", 1).apply();
//            }
//        }
    }

    private void onAdIntent(int index) {
        com.adsale.ChinaPlas.data.model.EPO.D6 D6 = epoHelper.getItemD6(mHall);
        if (index == 0) {
            mLogHelper.logD6(mHall + "_one_" + epoHelper.D6.function[0] + "_" + epoHelper.D6.pageID[0], false);
        } else if (index == 1) {
            index = index - 1;
            mLogHelper.logD6(mHall + "_left_" + epoHelper.D6.function[0] + "_" + epoHelper.D6.pageID[0], false);
        } else {
            index = index - 1;
            mLogHelper.logD6(mHall + "_right_" + epoHelper.D6.function[1] + "_" + epoHelper.D6.pageID[1], false);
        }
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_Click);

        Intent intent = IntentHelper.intentAd(D6.function[index], D6.pageID[index], D6.pageID[index]);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls == null) {
            String value = (String) entity;
            if (value.equals("0")) {
                LogUtil.i(TAG, "点击了整个广告");
                onAdIntent(Integer.valueOf(value));
            } else if (value.equals("1")) {
                LogUtil.i(TAG, "点击了左边广告");
                onAdIntent(Integer.valueOf(value));
            } else if (value.equals("2")) {
                LogUtil.i(TAG, "点击了you边广告");
                onAdIntent(Integer.valueOf(value));
            } else {
                // 侧边点击了展商item，显示在地图上
                closeAnimation();
                mFloorModel.drawSingleFlagOnMap(value); // booth
            }
        } else {
            Intent intent = new Intent(this, ExhibitorDetailActivity.class);
            intent.putExtra(Constant.COMPANY_ID, ((Exhibitor) entity).getCompanyID());
            intent.putExtra("from", "floor");
            startActivity(intent);
            overridePendingTransPad();
        }

    }

    private void m4Intent(adAdvertisementObj.AdM4 adM4) {
//        int function = adM4.function[index];
//        if (function == 1) { //展商详情
//            if (TextUtils.isEmpty(adM4.action_companyID[index])) {
//                return;
//            }
//
//            AppUtil.trackViewLog(415, "CA", "M4", adObj.M4.action_companyID[index]);
//            AppUtil.setStatEvent(getApplicationContext(), "ClickM4", "CA_M4_" + adObj.M4.action_companyID[index]);
//
//            Intent intent = new Intent(this, ExhibitorDetailActivity.class);
//            intent.putExtra(Constant.COMPANY_ID, adM4.action_companyID[index]);
//            startActivity(intent);
//            overridePendingTransPad();
//        } else if (function == 2) { // 同期活动
//            if (TextUtils.isEmpty(adM4.action_eventID[index])) {
//                return;
//            }
//
//            AppUtil.trackViewLog(415, "CA", "M4", adObj.M4.action_eventID[index]);
//            AppUtil.setStatEvent(getApplicationContext(), "ClickM4", "CA_M4_" + adObj.M4.action_eventID[index]);
//
//            Intent intent = new Intent(this, WebContentActivity.class);
//            intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/" + adM4.action_eventID[index]);
//            startActivity(intent);
//            overridePendingTransPad();
//        }
    }


    @Override
    public void onDrawerClick(boolean open) {
        if (open) {
            editText.setText("");
            navView.setVisibility(View.VISIBLE);
            binding.ivMap.setClickable(false);
            mFloorModel.dismissDialogFragment();
            openAnimation();
            if (exhibitorsCache.isEmpty()) {
                exhibitors = mFloorModel.getEditExhibitors("");
                exhibitorsCache.addAll(exhibitors);
            }
            navAdapter = new FloorNavAdapter(exhibitorsCache, this);
            recyclerView.setAdapter(navAdapter);
        } else { // close
            binding.ivMap.setClickable(true);
            closeAnimation();
            exhibitors.clear();
        }
    }

    public void openAnimation() {
        mFloorModel.isNavOpened.set(true);
        ValueAnimator animator = createLeftAnimator(0, DisplayUtil.dip2px(getApplicationContext(), NAV_WIDTH));
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivShade.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
        navView.requestLayout();
    }

    public void closeAnimation() {
        mFloorModel.isNavOpened.set(false);
        ValueAnimator animator = createLeftAnimator(DisplayUtil.dip2px(getApplicationContext(), NAV_WIDTH), 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                navFrame.setVisibility(View.GONE);
                ivShade.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
    }

    private ValueAnimator createLeftAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) navView.getLayoutParams();
                layoutParams.width = value;
                navView.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFloorModel != null) {
            mFloorModel.onDestroy();
            mFloorModel = null;
        }

//        if (observer != null && !observer.isDisposed()) {
//            observer.dispose();
//        }
    }
}
