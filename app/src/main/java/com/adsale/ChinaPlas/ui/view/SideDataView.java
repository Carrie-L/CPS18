package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.databinding.ViewSideBinding;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/28.
 * 列表侧边bar、Dialog Text、No Data
 */

public class SideDataView extends RelativeLayout implements SideLetter.OnLetterClickListener {
    private final String TAG = "SideDataView";
    public final ObservableField<String> dialogLetter = new ObservableField<>();
    public final ObservableBoolean isNoData = new ObservableBoolean(true);
    private Context mContext;
    private SideLetter mSideLetter;
    private RecyclerViewScrollTo mRecyclerScroll;
    private ArrayList<Exhibitor> mExhibitors;
    private ArrayList<NewProductInfo> mNewTecProducts;

    public SideDataView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SideDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SideDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        ViewSideBinding binding = ViewSideBinding.inflate(LayoutInflater.from(mContext), this, true);
        binding.executePendingBindings();
        mSideLetter = binding.sideLetter;
    }

    public void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.VERTICAL));
        mRecyclerScroll = new RecyclerViewScrollTo(layoutManager, recyclerView);
    }

    /**
     * 如果是展商列表，就调用这个方法
     */
    public void initSideLetterExhibitor(ArrayList<Exhibitor> list,ArrayList<String> letters){
        mSideLetter.setList(letters);
        mSideLetter.setOnLetterClickListener(this);
        mExhibitors = list;
        isNoData.set(mExhibitors.isEmpty());
    }

    public void refreshExhibitors(ArrayList<Exhibitor> list,ArrayList<String> letters){
        mExhibitors = list;
        mSideLetter.setList(letters);
        mSideLetter.refresh();
        isNoData.set(mExhibitors.isEmpty());
    }

    /**
     * 如果是新技术产品列表，就调用这个方法
     */
    public void initNewTecProducts(ArrayList<NewProductInfo> list,ArrayList<String> letters){
        mSideLetter.setList(letters);
        mSideLetter.setOnLetterClickListener(this);
        mNewTecProducts = list;
        isNoData.set(mNewTecProducts.isEmpty());
    }

    public void refreshNewTecProducts(ArrayList<NewProductInfo> list,ArrayList<String> letters){
        mNewTecProducts = list;
        mSideLetter.setList(letters);
        mSideLetter.refresh();
        isNoData.set(mExhibitors.isEmpty());
    }

    @Override
    public void onClick(String letter) {
        dialogLetter.set(letter);
        scrollToExhibitor();
    }

    private void scrollToExhibitor() {
        if (mExhibitors == null || mExhibitors.isEmpty()) {
            return;
        }
        int size = mExhibitors.size();
        for (int j = 0; j < size; j++) {
            if (mExhibitors.get(j).getSort().equals(dialogLetter.get())) {
                mRecyclerScroll.scroll(j);
                break;
            }
        }
    }

    private void scrollToNewTec() {
        if (mNewTecProducts == null || mNewTecProducts.isEmpty()) {
            return;
        }
        int size = mNewTecProducts.size();
        for (int j = 0; j < size; j++) {
//            if (mExhibitors.get(j).getSort().equals(dialogLetter.get())) {
//                mRecyclerScroll.scroll(j);
//                break;
//            }
        }
    }


}
