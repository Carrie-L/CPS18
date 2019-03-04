package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ExhibitorListAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorAllListBinding;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.helper.IntentHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.helper.OnHelpCallback;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.ui.view.SideDataView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.ExhibitorListViewModel;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.App.mLogHelper;

/**
 * 全部参展商列表
 * <p>
 * D3: list - insert banner 、 filter - banner
 * D4: list - logo 、 info - product + about + video
 */
public class ExhibitorAllListActivity extends BaseActivity implements OnItemClickCallback, OnIntentListener, OnHelpCallback {
    private final String TAG = "ExhibitorAllListActivity";
    private ExhibitorListViewModel mExhibitorModel;
    private ActivityExhibitorAllListBinding binding;
    private ExhibitorRepository mRepository;
    private String date;
    //    private SideLetter sideLetter;
    private ExhibitorListAdapter adapter;
    private final int REQUEST_FILTER = 100;
    private final int REQUEST_COLLECT = 101;
    private int position;
    private Exhibitor mEntity;
    private SideDataView mSideDataView;
    private EditText etSearch;
    private HelpView helpDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
    }

    @Override
    protected void initView() {
        setBarTitle(R.string.title_exhibitor);
        binding = ActivityExhibitorAllListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
    }

    @Override
    protected void initData() {
        mRepository = ExhibitorRepository.getInstance();
        mExhibitorModel = new ExhibitorListViewModel(mRepository, this);
        binding.setExhibitorModel(mExhibitorModel);
        mExhibitorModel.setOnHelpCallback(this);
        if (HelpView.isFirstShow(HelpView.HELP_PAGE_EXHIBITOR_LIST)) {
            show();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_EXHIBITOR_LIST, HelpView.HELP_PAGE_EXHIBITOR_LIST).apply();
        }

        /*  来自日程 */
        date = getIntent().getStringExtra("date");

        /* 来自展示详情 */
        String id = getIntent().getStringExtra("id");
        LogUtil.i(TAG, "id=" + id);
        if (!TextUtils.isEmpty(id)) {
            mExhibitorModel.setPartList(getIntent().getIntExtra("type", -1), id);

            LogUtil.i(TAG, "type=" + getIntent().getIntExtra("type", -1));

        }

        setExhibitorList();
        setupSideLetter();

        etSearch = binding.editFilter;
        etSearch.addTextChangedListener(new TextWatcher() {

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
        RecyclerView rvExhibitors = binding.rvKs;
        mSideDataView = binding.viewSideData;
        mSideDataView.initRecyclerView(rvExhibitors);
        mExhibitorModel.setLayoutManager(mSideDataView);

        mExhibitorModel.getAllExhibitorsAZ();

        LogUtil.i(TAG, "mExhibitors=" + mExhibitorModel.mExhibitors.size());

        adapter = new ExhibitorListAdapter(this, mExhibitorModel.mExhibitors, mRepository, this);
        rvExhibitors.setAdapter(adapter);
        mExhibitorModel.setAdapter(adapter);
    }

    public void setupSideLetter() {
        mSideDataView.initSideLetterExhibitor(mExhibitorModel.mExhibitors, mExhibitorModel.mLetters);
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        mExhibitorModel.isSearching = false;
        etSearch.setText("");
        mExhibitorModel.isFiltering = true;
        Intent intent = new Intent(this, toCls);
        startActivityForResult(intent, REQUEST_FILTER);
        overridePendingTransPad();
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
    public <T> void onItemClick(T entity, int pos) {
        LogUtil.i(TAG, "onItemClick: pos=" + pos);
        Intent intent;
        if (!TextUtils.isEmpty(date)) {
            intent = new Intent(ExhibitorAllListActivity.this, ScheduleEditActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("title", getString(R.string.title_add_schedule));
            intent.putExtra(Constant.INTENT_EXHIBITOR, (Exhibitor) entity);
            startActivity(intent);
            finish();
            overridePendingTransPad();
        } else {
            position = pos;
            mEntity = (Exhibitor) entity;

            if (mEntity.isD3Banner.get()) {
                // 广告banner跳转
                LogUtil.i(TAG, "d3 banner intent: " + mEntity.function + ", pageID= " + mEntity.pageID);
                intent = IntentHelper.intentAd(mEntity.function, mEntity.getCompanyID(), mEntity.pageID);
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                mLogHelper.logD3(mEntity.function, mEntity.pageID, false);
                mLogHelper.setBaiDuLog(getApplicationContext(), LogHelper.EVENT_ID_AD_Click);
            } else {
                intent = new Intent(ExhibitorAllListActivity.this, ExhibitorDetailActivity.class);
                intent.putExtra(Constant.COMPANY_ID, mEntity.getCompanyID());
                intent.putExtra("from", "list");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_COLLECT);
            }
            overridePendingTransPad();

        }
    }

    @Override
    public void show() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_EXHIBITOR_LIST);
        helpDialog.show(ft, "Dialog");
    }
}
