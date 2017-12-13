package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.adapter.FilterAdapter;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.databinding.ViewExhibitorFilterBinding;
import com.adsale.ChinaPlas.ui.FilterApplicationListActivity;
import com.adsale.ChinaPlas.ui.FilterBoothListActivity;
import com.adsale.ChinaPlas.ui.FilterCountryListActivity;
import com.adsale.ChinaPlas.ui.FilterHallListActivity;
import com.adsale.ChinaPlas.ui.FilterIndustryListActivity;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 */

public class FilterView extends LinearLayout {
    private final String TAG = "FilterView";
    public final ObservableField<Drawable> iconDrawable = new ObservableField<>();
    public final ObservableArrayList<ExhibitorFilter> filters = new ObservableArrayList<>();
    public final ObservableField<String> itemName = new ObservableField<>();
//    public final ObservableInt itemIndex = new ObservableInt();

    private ViewExhibitorFilterBinding binding;
    private Context mContext;
    private int index;
    private OnIntentListener mListener;
    private FilterAdapter adapter;

    public FilterView(Context context) {
        super(context);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewExhibitorFilterBinding.inflate(inflater, this, true);
        binding.setView(this);

        RecyclerView recyclerView = binding.filterRecyclerView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right=8;
                outRect.left=8;
                outRect.bottom=8;
            }
        });

        adapter = new FilterAdapter(new ArrayList<ExhibitorFilter>(0));
        recyclerView.setAdapter(adapter);
    }

    public void initData(int index, String name, Drawable drawable,OnIntentListener listener) {
        if (TextUtils.isEmpty(name)) {
            throw new NullPointerException("filer name cannot be null, please #initData()");
        }
        this.index = index;
        mListener=listener;
        itemName.set(name);
        iconDrawable.set(drawable);
    }

    public void setList(ArrayList<ExhibitorFilter> list){
        filters.clear();
        filters.addAll(list);
        adapter.setList(filters);
    }

    public void onItemClick() {
        LogUtil.i(TAG, "onItemClick:" + index);
        if(mListener==null){
            throw new NullPointerException("mListener cannot be null,please add #initData()");
        }
        if (index == 0) {//产品类别
            mListener.onIntent(index,FilterIndustryListActivity.class);
        } else if (index == 1) {//应用分类
//            intent = new Intent(mContext, FilterApplicationListActivity.class);
            mListener.onIntent(index,FilterApplicationListActivity.class);
        } else if (index == 2) {//国家
//            intent = new Intent(mContext, FilterCountryListActivity.class);
            mListener.onIntent(index,FilterCountryListActivity.class);
        } else if (index == 3) {//展馆
//            intent = new Intent(mContext, FilterHallListActivity.class);
            mListener.onIntent(index,FilterHallListActivity.class);
        } else if (index == 4) {//展区
//            intent = new Intent(mContext, FilterBoothListActivity.class);
            mListener.onIntent(index,FilterBoothListActivity.class);
        }
    }

}
