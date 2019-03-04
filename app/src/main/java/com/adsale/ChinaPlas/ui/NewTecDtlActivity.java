package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.AdViewPagerAdapter;
import com.adsale.ChinaPlas.adapter.NewTecPagerAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityNewTecDtlBinding;
import com.adsale.ChinaPlas.databinding.ViewPagerNewTechDtlBinding;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.helper.MyViewPager;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewTecViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.adsale.ChinaPlas.App.mLogHelper;

public class NewTecDtlActivity extends BaseActivity implements OnIntentListener {

    private NewProductInfo entity;
    private NewTecViewModel viewModel;
    private List<View> pages = new ArrayList<>();
    private MyViewPager viewPager;
    private NewTecRepository repository;
    private List<NewProductInfo> pagesInfo;
    private AdViewPagerAdapter viewPagerAdapter;


//    ActivityNewTecDtlBinding binding = ActivityNewTecDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    protected void initView() {
        entity = getIntent().getParcelableExtra("obj");
//        barTitle.set(entity.getProductName());
        barTitle.set(getIntent().getStringExtra("title"));

        viewModel = NewTecViewModel.getINSTANCE(getApplicationContext());
        repository = NewTecRepository.newInstance();

        if (entity.adItem) {
            adView();
        } else {
            setViewPager();
        }
    }

    private void backgroundProcess() {
        Observable.fromIterable(pagesInfo)
                .map(new Function<NewProductInfo, Integer>() {
                    @Override
                    public Integer apply(NewProductInfo newProductInfo) throws Exception {

                        initPageView(newProductInfo);

                        return 0;
                    }
                }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.i(TAG, "integer=" + firstShowIndex);
                    }
                });
    }

    private void setViewPager() {
        ViewPagerNewTechDtlBinding pagerBinding = ViewPagerNewTechDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        pagerBinding.executePendingBindings();
        viewPager = pagerBinding.newTechViewPager;
        pages = new ArrayList<>();

//        pagesInfo = repository.getNewProductInfoScroll(entity.getRID());
        pagesInfo = repository.getAllProductInfoList();

        for (int i = 0; i < pagesInfo.size(); i++) {
            initPageView(i);
        }

//        backgroundProcess();    //IllegalStateException: DataBinding must be created in view's UI Thread

        viewPagerAdapter = new AdViewPagerAdapter(pages);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        viewPager.setCurrentItem(firstShowIndex);
        viewPagerAdapter.notifyDataSetChanged();

    }


    @Override
    protected void initData() {
        mLogHelper.logNewTechInfo(entity.getRID());
        mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_Info);
    }

    private int firstShowIndex = 0;
    private int i;

    private void initPageView(NewProductInfo info) {
//        NewProductInfo info = pagesInfo.get(i);
        ActivityNewTecDtlBinding binding = ActivityNewTecDtlBinding.inflate(LayoutInflater.from(getApplicationContext()), null);
        LinearLayout llLayout = binding.llNewTec;
        binding.setObj(info);
        binding.executePendingBindings();
        viewModel.setView(binding.newTecFrame, info, this);
        binding.setModel(viewModel);
        LogUtil.i(TAG, "entity.LogoImageLink=" + info.LogoImageLink);
        if (!TextUtils.isEmpty(info.LogoImageLink)) {
            Glide.with(getApplicationContext()).load(info.LogoImageLink).into(binding.ivLogo);
        } else {
            binding.ivLogo.setVisibility(View.GONE);
        }
        binding.tvDes.setText(info.getDescription().replace("\\n", "\n"));
        viewModel.setupTop();

        pages.add(llLayout);

        i++;

        if (info.getRID().equals(entity.getRID())) {
            LogUtil.i(TAG, "设置当前显示的页面为：" + entity.getCompanyName() + ", ❤ i=" + i + ",RID=" + info.getRID() + "," + entity.getRID());
//            viewPager.setCurrentItem(i);
            firstShowIndex = i;
        }


    }

    private void adView() {
        mLogHelper.logD8(entity.getCompanyID(), false);

        ActivityNewTecDtlBinding binding = ActivityNewTecDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setObj(entity);
        binding.executePendingBindings();
        viewModel.setView(binding.newTecFrame, entity, this);
        binding.setModel(viewModel);
        LogUtil.i(TAG, "entity.LogoImageLink=" + entity.LogoImageLink);
        if (!TextUtils.isEmpty(entity.LogoImageLink)) {
            Glide.with(getApplicationContext()).load(entity.LogoImageLink).into(binding.ivLogo);
        } else {
            binding.ivLogo.setVisibility(View.GONE);
        }
        binding.tvDes.setText(entity.getDescription().replace("\\n", "\n"));
        viewModel.setupTop();
    }


    private void initPageView(int i) {
        NewProductInfo info = pagesInfo.get(i);
        ActivityNewTecDtlBinding binding = ActivityNewTecDtlBinding.inflate(LayoutInflater.from(getApplicationContext()), null);
        LinearLayout llLayout = binding.llNewTec;
        binding.setObj(info);
        binding.executePendingBindings();
        viewModel.setView(binding.newTecFrame, info, this);
        binding.setModel(viewModel);
        LogUtil.i(TAG, "entity.LogoImageLink=" + info.LogoImageLink);
        if (!TextUtils.isEmpty(info.LogoImageLink)) {
            Glide.with(getApplicationContext()).load(info.LogoImageLink).into(binding.ivLogo);
        } else {
            binding.ivLogo.setVisibility(View.GONE);
        }
        binding.tvDes.setText(info.getDescription().replace("\\n", "\n"));
        viewModel.setupTop();

        pages.add(llLayout);

        if (info.getRID().equals(entity.getRID())) {
            LogUtil.i(TAG, "设置当前显示的页面为：" + entity.getCompanyName() + ", ❤ i=" + i + ",RID=" + info.getRID() + "," + entity.getRID());
//            viewPager.setCurrentItem(i);
            firstShowIndex = i;
        }


    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            LogUtil.i(TAG, "onPageScrolled: ");

        }

        @Override
        public void onPageSelected(int position) {
            LogUtil.i(TAG, "onPageSelected: " + position);
//            pagesInfo = repository.getNewProductInfoScroll(pagesInfo.get(position).getRID());
//            LogUtil.i(TAG, "onPageSelected pagesInfo = " + pagesInfo.size() +", "+pagesInfo.toString());
//
//            pages.clear();
//
//            for (int i = 0; i < pagesInfo.size(); i++) {
//                initPageView(i);
//            }
//            viewPagerAdapter.setList(pages, true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            LogUtil.i(TAG, "onPageScrollStateChanged: ");
        }
    };

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        if (toCls == null) {
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("url", (String) entity);
            startActivity(intent);
            overridePendingTransPad();
        } else if (toCls.getSimpleName().equals("FloorDetailActivity")) {
            String booth = (String) entity;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < booth.length(); i++) {
                char c = booth.charAt(i);
                if (Character.isLetter(c)) {
                    LogUtil.i(TAG, c + " is a letter : ");
                    break;
                }
                sb.append(c);
            }
            sb.append("H");
            LogUtil.i(TAG, "sb=" + sb.toString());
            Intent intent = new Intent(getApplicationContext(), FloorDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("BOOTH", booth);
            intent.putExtra("HALL", sb.toString());
            startActivity(intent);
            overridePendingTransPad();
        } else if (toCls.getSimpleName().equals("ExhibitorDetailActivity")) {
            Intent intent = new Intent(getApplicationContext(), ExhibitorDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constant.COMPANY_ID, (String) entity);
            intent.putExtra("from", "NewTech");
            startActivity(intent);
            overridePendingTransPad();
        }

    }
}
