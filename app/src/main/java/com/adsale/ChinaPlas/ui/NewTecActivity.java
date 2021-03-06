package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.NewTecListAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.databinding.ActivityNewTecBinding;
import com.adsale.ChinaPlas.helper.CSVHelper;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.ui.view.SideDataView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReleaseHelper;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.App.mLogHelper;

public class NewTecActivity extends BaseActivity implements OnIntentListener {
    private ActivityNewTecBinding binding;
    private SideDataView mSideDataView;
    private ArrayList<NewProductInfo> products;  /*  没有广告的、从数据库取出的 产品列表, 用于 搜索 时判断 */
    private ArrayList<NewProductInfo> productCaches = new ArrayList<>(); /*  没有广告的、从数据库取出的 产品列表缓存，当返回筛选结果时，在这个列表里选择符合条件的数据 */
    private ArrayList<NewProductInfo> listCaches = new ArrayList<>();  /* 包含产品和广告的全部列表缓存，用于清空搜索时还原列表 */
    private ArrayList<NewProductInfo> list = new ArrayList<>(); /*  交给adapter的列表（可能有产品和广告） */
    public final ObservableBoolean isEmpty = new ObservableBoolean();

    private RecyclerView recyclerView;
    private NewTecListAdapter adapter;
    private NewProductInfo entity;
    private ArrayList<NewTec.ADProduct> adProducts = new ArrayList<>();
    private int adSize;
    private NewTec.ADProduct adProduct;
    private NewTecRepository mRepository;

    @Override
    protected void initView() {
        binding = ActivityNewTecBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();
        mSideDataView = binding.viewSideData;
        recyclerView = binding.newTecRecyclerView;
    }

    @Override
    protected void initData() {
        if (HelpView.isFirstShow(HelpView.HELP_PAGE_NEW_TEC_LIST)) {
            onHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_NEW_TEC_LIST, HelpView.HELP_PAGE_NEW_TEC_LIST).apply();
        }
        mSideDataView.initRecyclerView(recyclerView);

        if(ReleaseHelper.IsNewTecCSVTest){
            processCSVTest();
        }

        getList();
        generateAdList();
        insertAdList();
        listCaches.addAll(list);
        adapter = new NewTecListAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.requestFocus();
        search();
    }

    private void processCSVTest() {
        if (mRepository == null) {
            mRepository = NewTecRepository.newInstance();
            mRepository.initDao();
        }
        CSVHelper csvHelper = new CSVHelper();
        csvHelper.initNewTec(mRepository);
        csvHelper.readNewTecCSV2();
    }

    private void getList() {
        mRepository = NewTecRepository.newInstance();
        mRepository.initDao();
        products = mRepository.getAllProductInfoList();
        list.addAll(products);
        productCaches.addAll(products);
        LogUtil.i(TAG, "logo = " + productCaches.get(0).imageThumb);
    }

    /**
     * 每打开一次，头到尾，下一个顶上。如：1,2,3,4,5. -》 23451 -》 34512
     */
    private void generateAdList() {
        NewTec newTec = Parser.parseJsonFilesDirFile(NewTec.class, Constant.TXT_NEW_TEC);
        adProducts = newTec.adProduct;
        adSize = adProducts.size();
        if (adSize == 0) {
            return;
        }
        LogUtil.i(TAG, "adProducts=" + adProducts.toString());

        /*  更换广告顺序 */
        SharedPreferences spConfig = App.mSP_Config;
        int adIndex = spConfig.getInt("NEW_TEC_AD_INDEX", 1);
        LogUtil.i(TAG, "adIndex=" + adIndex);
        if (adIndex > 1) {
            for (int i = 1; i < adIndex; i++) {
                mLogHelper.logD8(adProducts.get(i).CompanyID,true);

                adProducts.add(adProducts.get(0));
                adProducts.remove(0);
            }
        }
        spConfig.edit().putInt("NEW_TEC_AD_INDEX", adIndex == adSize ? 1 : ++adIndex).apply();
    }

    /**
     * 将广告列表插入进去
     */
    private void insertAdList() {
        int insertIndex;
        for (int i = 0; i < list.size(); i++) {
            insertIndex = i / 3;
            if (insertIndex < adSize) {
                if (i == 2 || i % 3 == 2) {
                    adProduct = adProducts.get(insertIndex);
                    entity = new NewProductInfo(null, adProduct.CompanyID, adProduct.BoothNo, adProduct.CompanyName_EN, adProduct.CompanyName_SC, adProduct.CompanyName_TC,
                            adProduct.ProductName_SC, adProduct.Description_SC, adProduct.ProductName_TC, adProduct.Description_TC, adProduct.ProductName_EN,
                            adProduct.Description_EN);
                    entity.adItem = true;
                    entity.image = adProduct.FirstPageImage;
                    entity.imageLinks = adProduct.ImageLinks;
                    entity.videoLink = adProduct.vedioLink;
                    entity.LogoImageLink = adProduct.LogoImageLink;
                    LogUtil.i(TAG, "adProduct.LogoImageLink=" + adProduct.LogoImageLink);
                    LogUtil.i(TAG, "entity.LogoImageLink=" + entity.LogoImageLink);
                    list.add(i, entity);
                }
            } else {
                break;
            }
        }
    }

    /**
     * 可按 公司名、产品名、展台号 搜索
     */
    private void search() {
        binding.editFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    adapter.setList(listCaches);
                } else {
                    list.clear();
                    /* 从 products 产品列表中选出符合的list，再用 generateAdList() 向 list 中插入广告列表 */
                    for (int i = 0; i < products.size(); i++) {
                        entity = products.get(i);
                        if (entity.getBoothNo().toLowerCase().contains(editable.toString().toLowerCase())
                                || entity.isContainsCompany(editable.toString())
                                || entity.isContainsProductName(editable.toString())) {
                            list.add(entity);
                        }
                    }
                    insertAdList();
                    adapter.setList(list);
                }
            }
        });
    }

    public void onFilter() {
        Intent intent = new Intent(this, NewTecFilterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
        overridePendingTransPad();
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = new Intent(this, NewTecDtlActivity.class);
        intent.putExtra("obj", (NewProductInfo) entity);
        intent.putExtra("title", barTitle.get());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransPad();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TAG = "NewTecActivity";
        if (data == null) {
            return;
        }
        ArrayList<ExhibitorFilter> filters = data.getParcelableArrayListExtra("data");
        if (filters.size() == 0) {
            return;
        }
        LogUtil.i(TAG, "onActivityResult::filters=" + filters.size() + "," + filters.toString());
        list.clear();
        listCaches.clear();
        products.clear();
        list = mRepository.getFilterList(productCaches, filters);
        products.addAll(list);
        insertAdList();
        adapter.setList(list);
        listCaches.addAll(list);
        isEmpty.set(list.isEmpty());
        LogUtil.i(TAG, "list= " + list.size() + "," + list.toString());
    }

    public void onHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        HelpView helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_NEW_TEC_LIST);
        helpDialog.show(ft, "Dialog");
    }
}
