package com.adsale.ChinaPlas.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.ActivityFloorDetailBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
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

/**
 * todo   mTypePrefix = "FPage_" + gIntent.getStringExtra("FloorID");
 * <p>
 * intent:【BOOTH & HALL】 / 【HALL】
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
    private int index;
    private adAdvertisementObj adObj;
    private View navView;
    private ImageView ivShade;


    @Override
    protected void initView() {
        mHall = getIntent().getStringExtra("HALL");
        barTitle.set(mHall);
        binding = ActivityFloorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);

        getImgNameFromSD();
        if (TextUtils.isEmpty(imgName)) { // 如果找不到这张图片，则返回
            return;
        }

        FloorRepository repository = FloorRepository.getInstance();
        mFloorModel = new FloorDtlViewModel(getApplicationContext(), repository, getFragmentManager(), this, binding.ivMap, mHall);
        binding.setModel(mFloorModel);
        binding.executePendingBindings();

        initNavView();
        showM4();
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
        Bitmap bitmap = BitmapFactory.decodeStream(AppUtil.getInputStream("FloorPlan/" + imgName));
        binding.ivMap.setImageBitmap(bitmap);
        mFloorModel.startMap(bitmap);

        int width = (2000 * AppUtil.getScreenHeight()) / 1575;
        LogUtil.i(TAG, "width=" + width);
        binding.ivMap.setZoomable(true);
        binding.ivMap.setMaximumScale(4 * BASE_SCALE);
        binding.ivMap.setMediumScale(3 * BASE_SCALE);
        binding.ivMap.setMinimumScale(BASE_SCALE);
        binding.ivMap.setScale(BASE_SCALE, AppUtil.getScreenWidth() / 2, AppUtil.getScreenHeight() / 2, true);

        mFloorModel.setOnDrawerListener(this);

        if (index != -1) {
            showM4Flag();
        }
        mFloorModel.showBitmap();

        Intent intent = getIntent();
        String booth = intent.getStringExtra("BOOTH");
        if (!TextUtils.isEmpty(booth)) {
            mFloorModel.drawSingleFlagOnMap(booth);
        }

    }


    private void showM4() {
        ADHelper adHelper = new ADHelper(getApplicationContext());
        index = adHelper.showM4(mHall);
        if (index == -1) {
            return;
        }

         /* M4 LEFT */
        ImageView ivLeft = binding.ivM4Left;
        ivLeft.setVisibility(View.VISIBLE);
        int adWidth = AppUtil.getScreenWidth() / 2;
        int adHeight = (adWidth * Constant.M4_HEIGHT) / Constant.M4_WIDTH;
        LogUtil.i(TAG, "adWidth=" + adWidth + ",adHeight=" + adHeight);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(adWidth, adHeight);
        ivLeft.setLayoutParams(params);

        adObj = adHelper.getAdObj();
        StringBuilder m4Url = new StringBuilder();
        m4Url.append(adObj.Common.baseUrl).append(adObj.M4_left.filepath).append(adObj.M4_left.floor[index]).append("/")
                .append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(AppUtil.getName("tc", "en", "sc"))
                .append("_left_").append(adObj.M4_left.version[index]).append(adObj.M4_left.format);
        LogUtil.i(TAG, "M4_leftUrl=" + m4Url.toString());
        Glide.with(getApplicationContext()).load(m4Url.toString()).into(ivLeft);


        /* M4 RIGHT */
        ImageView ivRight = binding.ivM4Right;
        if (Integer.valueOf(adObj.M4_right.version[index]) == 0) {
            ivRight.setClickable(false);
            return;
        }
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setLayoutParams(params);

        m4Url = AppUtil.clearSB(m4Url);
        m4Url.append(adObj.Common.baseUrl).append(adObj.M4_right.filepath).append(adObj.M4_right.floor[index]).append("/")
                .append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(AppUtil.getName("tc", "en", "sc"))
                .append("_right_").append(adObj.M4_right.version[index]).append(adObj.M4_right.format);
        LogUtil.i(TAG, "M4_rightUrl=" + m4Url.toString());
        ivRight.setLayoutParams(params);
        Glide.with(getApplicationContext()).load(m4Url.toString()).into(ivRight);
    }

    private void showM4Flag() {
        String m4RightCompanyId = adObj.M4_right.action_companyID[index];
        String m4LeftCompanyId = adObj.M4_left.action_companyID[index];
        if (TextUtils.isEmpty(m4RightCompanyId.trim())) {
            mFloorModel.drawAdFlagOnMap(m4LeftCompanyId);
        } else {
            Random random = new Random();
            int number = random.nextInt(1); // 随机数0或1
            LogUtil.i(TAG, "random number=" + number);
            if (number == 0) {
                mFloorModel.drawAdFlagOnMap(m4LeftCompanyId);
            } else {
                mFloorModel.drawAdFlagOnMap(m4RightCompanyId);
            }
        }
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls == null) {
            String value = (String) entity;
            if (value.equals("1")) {
                LogUtil.i(TAG, "点击了左边广告");
                m4Intent(adObj.M4_left);
            } else if (value.equals("2")) {
                LogUtil.i(TAG, "点击了you边广告");
                m4Intent(adObj.M4_right);
            } else { // 侧边点击了展商item，显示在地图上
                closeAnimation();
                mFloorModel.drawSingleFlagOnMap(value); // booth
            }
        } else {
            Intent intent = new Intent(this, ExhibitorDetailActivity.class);
            intent.putExtra(Constant.COMPANY_ID, ((Exhibitor) entity).getCompanyID());
            startActivity(intent);
            overridePendingTransPad();
        }

    }

    private void m4Intent(adAdvertisementObj.AdM4 adM4) {
        int function = adM4.function[index];
        if (function == 1) { //展商详情
            if (TextUtils.isEmpty(adM4.action_companyID[index])) {
                return;
            }
            Intent intent = new Intent(this, ExhibitorDetailActivity.class);
            intent.putExtra(Constant.COMPANY_ID, adM4.action_companyID[index]);
            startActivity(intent);
            overridePendingTransPad();
        } else if (function == 2) { // 同期活动
            if (TextUtils.isEmpty(adM4.action_eventID[index])) {
                return;
            }
            Intent intent = new Intent(this, WebContentActivity.class);
            intent.putExtra(Constant.WEB_URL, adM4.action_eventID[index]);
            startActivity(intent);
            overridePendingTransPad();
        }
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
    }
}
