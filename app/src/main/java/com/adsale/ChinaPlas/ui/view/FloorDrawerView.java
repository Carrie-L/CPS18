package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.adsale.ChinaPlas.R;

/**
 * Created by Carrie on 2018/2/6.
 * 展馆平面图的侧边
 */

public class FloorDrawerView extends View {

    public FloorDrawerView(Context context) {
        super(context);
    }

    public FloorDrawerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloorDrawerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_map, null, false);
//        PhotoView pvMap = view.findViewById(R.id.pv_map);
    }

    public void onClick() {

    }


}
