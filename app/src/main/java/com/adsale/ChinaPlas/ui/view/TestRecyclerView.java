package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/12/3.
 */

public class TestRecyclerView extends RecyclerView {
    private final String TAG = "TestRecyclerView";
    private int displayHeight;
    private RecyclerView recyclerView;
    private TestAdapter adapter;

    public TestRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public TestRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
//        RecyclerView recyclerView = new RecyclerView(context);
//        View view
//        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view,this);
//        recyclerView =view.findViewById(R.id.test_rv);
        recyclerView = this;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        displayHeight = App.mSP_Config.getInt(Constant.DISPLAY_HEIGHT, 0);
        int screenHeight = App.mSP_Config.getInt(Constant.SCREEN_HEIGHT, 0);
        LogUtil.i(TAG, "displayHeight=" + displayHeight);
        LogUtil.i(TAG, "screenHeight=" + screenHeight);
        LayoutParams layoutParams = new LayoutParams(DisplayUtil.dip2px(context, 24), displayHeight);
        recyclerView.setLayoutParams(layoutParams);
    }

    public void setAdapter(ArrayList<String> list) {
        adapter = new TestAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    public void setList(ArrayList<String> list) {
        adapter.setList(list);
    }

    protected class TestAdapter extends CpsBaseAdapter<String> {
        private ArrayList<String> letters;

        public TestAdapter(ArrayList<String> letters) {
            this.letters = letters;
            int totalHeight = displayHeight;
            int size = letters.size();
            int itemHeight = totalHeight / size;
        }

        @Override
        public void setList(ArrayList<String> list) {
            super.setList(list);
        }

        @Override
        protected Object getObjForPosition(int position) {
            return letters.get(position);
        }

        @Override
        protected int getLayoutIdForPosition(int position) {
            return R.layout.item_test_side;
        }

        @Override
        public int getItemCount() {
            return letters.size();
        }
    }


}
