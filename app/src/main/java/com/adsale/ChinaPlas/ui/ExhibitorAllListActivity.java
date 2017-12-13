package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ExhibitorAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorAllListBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.ui.view.SideDataView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ExhibitorListViewModel;

import java.util.ArrayList;


/**
 * 全部参展商列表
 */
public class ExhibitorAllListActivity extends BaseActivity implements OnItemClickCallback, OnIntentListener {
    private final String TAG = "ExhibitorAllListActivity";
    private ExhibitorListViewModel mExhibitorModel;
    private ActivityExhibitorAllListBinding binding;
    private ExhibitorRepository mRepository;
    private RecyclerView rvExhibitors;
    private String date;
    //    private SideLetter sideLetter;
    private ExhibitorAdapter adapter;
    private final int REQUEST_FILTER = 100;
    private final int REQUEST_COLLECT = 101;
    private int position;
    private Exhibitor mEntity;
    private SideDataView mSideDataView;

    @Override
    protected void initView() {
        if(TextUtils.isEmpty(barTitle.get())){
            barTitle.set(getString(R.string.title_exhibitor));
        }
        binding = ActivityExhibitorAllListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        ADHelper adHelper = new ADHelper(this);
        adHelper.showM3(binding.ivAd);
    }

    @Override
    protected void initData() {
        mRepository = ExhibitorRepository.getInstance();
        mExhibitorModel = new ExhibitorListViewModel(mRepository, this);
        binding.setExhibitorModel(mExhibitorModel);

        /*  来自日程 */
        date = getIntent().getStringExtra("date");

        /* 来自展示详情 */
        String id = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(id)) {
            mExhibitorModel.setPartList(getIntent().getIntExtra("type", -1), id);
        }

        setExhibitorList();
        setupSideLetter();

        EditText etFilter = binding.editFilter;
        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mExhibitorModel.resetList();
                } else {
                    mExhibitorModel.search(s.toString().trim());
                }
            }
        });

    }

    private void setExhibitorList() {
        rvExhibitors = binding.rvKs;
        mSideDataView = binding.viewSideData;
        mSideDataView.initRecyclerView(rvExhibitors);
        mExhibitorModel.setLayoutManager(mSideDataView);

        mExhibitorModel.getAllExhibitorsAZ();
        adapter = new ExhibitorAdapter(this, mExhibitorModel.mExhibitors, mRepository, this);
        rvExhibitors.setAdapter(adapter);
        mExhibitorModel.setAdapter(adapter);
    }

    public void setupSideLetter() {
        mSideDataView.initSideLetterExhibitor(mExhibitorModel.mExhibitors, mExhibitorModel.mLetters);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        mExhibitorModel.isSearching = false;
        mExhibitorModel.isFiltering = true;
        Intent intent = new Intent(this, toCls);
        startActivityForResult(intent, REQUEST_FILTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "onActivityResult===/");
        mExhibitorModel.isFiltering = false;
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_FILTER) {
            ArrayList<ExhibitorFilter> filters = data.getParcelableArrayListExtra("data");
            LogUtil.i(TAG, "onActivityResult::filters=" + filters.size() + "," + filters.toString());
            if (filters.size() == 0) {
                return;
            }
            mExhibitorModel.clearCache();
            mExhibitorModel.isFiltering = true;
            mExhibitorModel.isSortAZ.set(true);
            mExhibitorModel.setFilters(filters);
            mExhibitorModel.getFilterExhibitorsAZ();
            LogUtil.i(TAG, "mExhibitorModel.letters= " + mExhibitorModel.mLetters.size() + "," + mExhibitorModel.mLetters.toString());
        } else if (requestCode == REQUEST_COLLECT) {
            boolean isCollected = data.getBooleanExtra("isCollected", false);
            LogUtil.i(TAG, "list.isCollected=" + mEntity.isCollected.get());
            LogUtil.i(TAG, "data.getBooleanExtra(\"isCollected\",false)=" + isCollected);
            if (mEntity != null && isCollected != mEntity.isCollected.get()) {
                mEntity.isCollected.set(isCollected);
                mExhibitorModel.mExhibitors.set(position, mEntity);
                adapter.setList2(mExhibitorModel.mExhibitors);
                adapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
    }

    @Override
    public <T> void onItemClick(T entity, int pos) {
        LogUtil.i(TAG, "onItemClick: pos=" + pos);
        Intent intent;
        if (!TextUtils.isEmpty(date)) {
            intent = new Intent(ExhibitorAllListActivity.this, ScheduleEditActivity.class);
            intent.putExtra("date", date);
            intent.putExtra(Constant.INTENT_EXHIBITOR, (Exhibitor) entity);
            startActivity(intent);
            finish();
            overridePendingTransPad();
        } else {
            position = pos;
            mEntity = (Exhibitor) entity;
            intent = new Intent(ExhibitorAllListActivity.this, ExhibitorDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constant.COMPANY_ID, mEntity.getCompanyID());
            startActivityForResult(intent, REQUEST_COLLECT);
            overridePendingTransPad();
        }
    }
}
