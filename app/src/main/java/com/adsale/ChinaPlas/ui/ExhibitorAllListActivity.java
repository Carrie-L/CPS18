package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.adapter.ExhibitorAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ActivityExhibitorAllListBinding;
import com.adsale.ChinaPlas.ui.view.SideLetter;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;
import com.adsale.ChinaPlas.viewmodel.ExhibitorListViewModel;

import java.util.ArrayList;


/**
 * 全部参展商列表
 * todo  ①、sideBar.  ②、search cache  ③、
 */
public class ExhibitorAllListActivity extends BaseActivity implements OnItemClickCallback, OnIntentListener, SideLetter.OnLetterClickListener {
    private final String TAG = "ExhibitorAllListActivity";
    private ExhibitorListViewModel mExhibitorModel;
    private ActivityExhibitorAllListBinding binding;
    private ExhibitorRepository mRepository;
    private RecyclerView rvExhibitors;
    private String date;
    private RecyclerViewScrollTo mRVScrollTo;
    private SideLetter sideLetter;
    private ExhibitorAdapter adapter;

    @Override
    protected void initView() {
        binding = ActivityExhibitorAllListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
    }

    @Override
    protected void initData() {
        mRepository = ExhibitorRepository.getInstance();
        mExhibitorModel = new ExhibitorListViewModel(mRepository, this);
        binding.setExhibitorModel(mExhibitorModel);

        date = getIntent().getStringExtra("date");

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
        sideLetter = binding.sideLetter;
        rvExhibitors.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvExhibitors.setLayoutManager(layoutManager);
        rvExhibitors.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        mRVScrollTo = new RecyclerViewScrollTo(layoutManager, rvExhibitors);
        mExhibitorModel.setLayoutManager(mRVScrollTo, sideLetter);
        mExhibitorModel.getAllExhibitorsAZ();
        adapter = new ExhibitorAdapter(this, mExhibitorModel.mExhibitors, mRepository, this);
        rvExhibitors.setAdapter(adapter);
        mExhibitorModel.setAdapter(adapter);

        onItemClick();
    }

    public void setupSideLetter() {
        sideLetter.setList(mExhibitorModel.mLetters);
        sideLetter.setOnLetterClickListener(this);
    }

    private void onItemClick() {
    }

    @Override
    public <T> void onItemClick(T entity) {
        Intent intent;
        if (!TextUtils.isEmpty(date)) {
            intent = new Intent(ExhibitorAllListActivity.this, ScheduleEditActivity.class);
            intent.putExtra("date", date);
            intent.putExtra(Constant.INTENT_EXHIBITOR, (Exhibitor) entity);
            startActivity(intent);
            finish();
            overridePendingTransPad();
        } else {
            intent = new Intent(ExhibitorAllListActivity.this, ExhibitorDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constant.COMPANY_ID, ((Exhibitor) entity).getCompanyID());
            startActivity(intent);
            overridePendingTransPad();
        }

    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = new Intent(this, toCls);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        ArrayList<ExhibitorFilter> filters = data.getParcelableArrayListExtra("data");
        LogUtil.i(TAG, "onActivityResult::filters=" + filters.size() + "," + filters.toString());
        mExhibitorModel.clearCache();
        mExhibitorModel.isFiltering = true;
        mExhibitorModel.setFilters(filters);
        mExhibitorModel.getFilterExhibitorsAZ();
        LogUtil.i(TAG, "mExhibitorModel.letters= " + mExhibitorModel.mLetters.size() + "," + mExhibitorModel.mLetters.toString());
    }

    @Override
    public void onClick(String letter) {
        mExhibitorModel.dialogLetter.set(letter);
        mExhibitorModel.scrollTo(letter);
    }

    @Override
    public void onBackPressed() {
        LogUtil.e(TAG, "onBackPressed 0");
        super.onBackPressed();
        LogUtil.e(TAG, "onBackPressed 1");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
    }
}
