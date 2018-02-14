package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.FloorNavAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityFloorDetailBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.FloorDtlViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

/**
 * todo   mTypePrefix = "FPage_" + gIntent.getStringExtra("FloorID");
 * <p>
 * intent:【BOOTH & HALL】 / 【HALL】
 */
public class FloorDetailActivity extends BaseActivity implements OnIntentListener, FloorDtlViewModel.OnDrawerListener {
    private final String TAG = "FloorDetailActivity";
    private FloorDtlViewModel mFloorModel;
    private ActivityFloorDetailBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Exhibitor> exhibitors = new ArrayList<>();
    private ArrayList<Exhibitor> exhibitorsCache = new ArrayList<>();
    private FloorNavAdapter navAdapter;
//    private NavigationView navView;
    private FrameLayout navView;
    private EditText editText;

    @Override
    protected void initView() {
        setBarTitle(R.string.uc_floorplan);
        binding = ActivityFloorDetailBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);




        FloorRepository repository = FloorRepository.getInstance();
        mFloorModel = new FloorDtlViewModel(getApplicationContext(), repository, getFragmentManager(), this, binding.ivMap, "2H");
        binding.setModel(mFloorModel);
        binding.executePendingBindings();

        initNavView();

    }

    private void initNavView() {
        navView = binding.floorNav;
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.nav_view_floor_search, navView, true);
        editText = view.findViewById(R.id.et);
        recyclerView = view.findViewById(R.id.rv_floor_nav);

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

    }

    @Override
    protected void initData() {
        Bitmap bitmap = BitmapFactory.decodeStream(AppUtil.getInputStream("FloorPlan/2H_20180123.jpg"));
        binding.ivMap.setImageBitmap(bitmap);
        mFloorModel.startMap(bitmap);

        int width = (2000 * AppUtil.getScreenHeight()) / 1575;
        LogUtil.i(TAG, "width=" + width);
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//        options.override(width, AppUtil.getScreenHeight());
//        Glide.with(this).load("file:///android_asset/FloorPlan/2H_20180123.jpg").apply(options).into(binding.ivMap);
        binding.ivMap.setZoomable(true);
        binding.ivMap.setMaximumScale(6);
        binding.ivMap.setMediumScale(4);
        binding.ivMap.setMinimumScale(2);
        binding.ivMap.setScale(2, AppUtil.getScreenWidth() / 2, AppUtil.getScreenHeight() / 2, true);

        mFloorModel.setOnDrawerListener(this);

        Intent intent = getIntent();
        String booth = intent.getStringExtra("BOOTH");
        if (!TextUtils.isEmpty(booth)) {
            mFloorModel.drawSingleFlagOnMap(booth);
        }

    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls == null) {
            String booth = (String) entity;
            mFloorModel.closeAnimation(navView);
            mFloorModel.drawSingleFlagOnMap(booth);
        } else {
            Intent intent = new Intent(this, ExhibitorDetailActivity.class);
            intent.putExtra(Constant.COMPANY_ID, ((Exhibitor) entity).getCompanyID());
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

            LogUtil.i(TAG, "openAnimation");
            mFloorModel.openAnimation(navView);
            if (exhibitorsCache.isEmpty()) {
                exhibitors = mFloorModel.getEditExhibitors("");
                exhibitorsCache.addAll(exhibitors);
            }
            navAdapter = new FloorNavAdapter(exhibitorsCache, this);
            recyclerView.setAdapter(navAdapter);
            LogUtil.i(TAG, "exhibitorsCache=" + exhibitorsCache.size());
        } else {
            binding.ivMap.setClickable(true);
            // close
            LogUtil.i(TAG, "closeAnimation");
            mFloorModel.closeAnimation(navView);
            exhibitors.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFloorModel.onDestroy();
        mFloorModel = null;
    }
}
