package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.NewTecListAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.databinding.ActivityNewTecBinding;
import com.adsale.ChinaPlas.ui.view.SideDataView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;

import java.util.ArrayList;

public class NewTecActivity extends BaseActivity implements OnIntentListener {
    //    public final ObservableField<String> etFilter = new ObservableField<>();
    private ActivityNewTecBinding binding;
    private SideDataView mSideDataView;
    private ArrayList<NewProductInfo> products;  /*  没有广告的、从数据库取出的 产品列表 */
    private ArrayList<NewProductInfo> productCaches = new ArrayList<>();  /* 包含产品和广告的全部列表 */
    private ArrayList<NewProductInfo> list = new ArrayList<>(); /*  交给adapter的列表（可能有产品和广告） */
    private RecyclerView recyclerView;
    private NewTecListAdapter adapter;
    private NewProductInfo entity;
    private ArrayList<NewTec.ADProduct> adProducts = new ArrayList<>();
    private int adSize;
    private NewTec.ADProduct adProduct;

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
        mSideDataView.initRecyclerView(recyclerView);
        ArrayList<String> letters = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            letters.add(i + "a");
        }
        mSideDataView.setupSideLitter(letters);
        getList();
        generateAdList();
        insertAdList();
        productCaches.addAll(list);
        adapter = new NewTecListAdapter(getApplicationContext(), list, this);
        recyclerView.setAdapter(adapter);

        search();
    }

    private void getList() {
        NewTecRepository mRepository = NewTecRepository.newInstance();
        mRepository.initDao();
        products = mRepository.getAllProductInfoList();
        list.addAll(products);
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

        /*  更换广告顺序 */
        SharedPreferences spConfig = App.mSP_Config;
        int adIndex = spConfig.getInt("NEW_TEC_AD_INDEX", 1);
        LogUtil.i(TAG, "adIndex=" + adIndex);
        if (adIndex > 1) {
            for (int i = 1; i < adIndex; i++) {
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
                    list.add(i, entity);
                }
            } else {
                break;
            }
        }
    }

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
                    adapter.setList(productCaches);
                } else {
                    list.clear();
                    /* 从 products 产品列表中选出符合的list，再用 generateAdList() 向 list 中插入广告列表 */
                    for (int i = 0; i < products.size(); i++) {
                        entity = products.get(i);
                        if (entity.getBoothNo().toLowerCase().contains(editable.toString().toLowerCase())
                                || entity.isContainsCompany(editable.toString())) {
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
        intent(NewTecFilterActivity.class,getString(R.string.title_filter));
    }


    @Override
    public <T> void onIntent(T entity, Class toCls) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        ArrayList<ExhibitorFilter> filters = data.getParcelableArrayListExtra("data");
        LogUtil.i(TAG, "onActivityResult::filters=" + filters.size() + "," + filters.toString());
        list.clear();


        LogUtil.i(TAG, "mExhibitorModel.letters= " + mExhibitorModel.mLetters.size() + "," + mExhibitorModel.mLetters.toString());
    }
}
