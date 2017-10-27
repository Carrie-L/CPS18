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
import com.adsale.ChinaPlas.viewmodel.ExhibitorViewModel;

import java.util.ArrayList;


/**
 * 全部参展商列表
 * todo  ①、sideBar.  ②、search cache  ③、
 */
public class ExhibitorAllListActivity extends BaseActivity implements OnItemClickCallback, OnIntentListener,SideLetter.OnLetterClickListener {
    private final String TAG = "ExhibitorAllListActivity";
    private ExhibitorViewModel mExhibitorModel;
    private ActivityExhibitorAllListBinding binding;
    private ExhibitorRepository mRepository;
    private RecyclerView rvExhibitors;
    private int dateIndex = -1;
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
        mExhibitorModel = new ExhibitorViewModel(getApplicationContext(), mRepository, this);
        binding.setExhibitorModel(mExhibitorModel);

        dateIndex = getIntent().getIntExtra("dateIndex", -1);

        setExhibitorList();
        setupSideLetter();

        EditText etFilter = binding.editFilter;
        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.i(TAG, "onTextChanged: s=" + s.toString() + ",start=" + start + ",before=" + before + ",count=" + count);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.i(TAG, "beforeTextChanged: s=" + s.toString() + ",start=" + start + ",after=" + after + ",count=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.i(TAG, "afterTextChanged: s=" + s.toString());

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

        mExhibitorModel.getAllExhibitors();
        adapter = new ExhibitorAdapter(this, mExhibitorModel.exhibitors, mRepository, this);
        rvExhibitors.setAdapter(adapter);

        mExhibitorModel.setLayoutManager(mRVScrollTo, adapter,sideLetter);

        onItemClick();

    }

    public void setupSideLetter() {
        sideLetter.setList(mExhibitorModel.letters);
        sideLetter.setOnLetterClickListener(this);
    }

    private void onItemClick() {
    }

    public void onLetterClick(String letter) {
        Toast.makeText(this, letter, Toast.LENGTH_SHORT).show();
        mExhibitorModel.scrollTo(letter);
    }

    @Override
    public <T> void onItemClick(T entity) {
        if (dateIndex != -1) {
            Intent intent = new Intent(ExhibitorAllListActivity.this, ScheduleEditActivity.class);
            intent.putExtra(Constant.EXHIBITOR, (Exhibitor) entity);
            intent.putExtra("dateIndex", dateIndex);
            startActivity(intent);
            finish();
        }else{
            Exhibitor exhibitor= (Exhibitor) entity;
            Toast.makeText(getApplicationContext(),"pos: "+exhibitor.getCompanyName(),Toast.LENGTH_SHORT).show();
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

        mExhibitorModel.exhibitors.clear();
        mExhibitorModel.letters.clear();
        mExhibitorModel.exhibitors.addAll( mRepository.queryFilterExhibitor(filters,mExhibitorModel.letters,true));
        mExhibitorModel.resetCache();
        adapter.setList(mExhibitorModel.exhibitors);

        LogUtil.i(TAG, "mExhibitorModel.letters= " + mExhibitorModel.letters.size() + "," + mExhibitorModel.letters.toString());
        mExhibitorModel.refreshSideLetter();
    }

    @Override
    public void onClick(String letter) {
        mExhibitorModel.dialogLetter.set(letter);
        mExhibitorModel.scrollTo(letter);
    }


}
