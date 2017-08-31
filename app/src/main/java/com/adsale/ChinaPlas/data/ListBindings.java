package com.adsale.ChinaPlas.data;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adsale.ChinaPlas.adapter.ExhibitorAdapter;
import com.adsale.ChinaPlas.adapter.ExhibitorAdapter3;
import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ScheduleInfo;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/11.
 */

public class ListBindings {

    @BindingAdapter("app:items")
    public static <T> void setItems(RecyclerView recyclerView, ArrayList<T> list) {
        CpsBaseAdapter<T> adapter = (CpsBaseAdapter<T>) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setList(list);
        }
    }

    @BindingAdapter("app:exhibitors")
    public static void setExhibitors(RecyclerView recyclerView, ArrayList<Exhibitor> list) {
        ExhibitorAdapter adapter = (ExhibitorAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setList(list);
        }
    }



}
