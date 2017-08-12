package com.adsale.ChinaPlas.data;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ScheduleInfo;

import java.util.ArrayList;

/**
 *
 * Created by Carrie on 2017/8/11.
 */

public class ListBindings {

    @BindingAdapter("app:items")
    public static <T> void setItems(RecyclerView recyclerView, ArrayList<T> list){
        CpsBaseAdapter<T> adapter= (CpsBaseAdapter<T>) recyclerView.getAdapter();
        if(adapter!=null){
            adapter.setList(list);
        }
    }

//    @BindingAdapter("app:scheduleItems")
//    public static void setScheduleItems(RecyclerView recyclerView, ArrayList<ScheduleInfo> list){
//        ScheduleAdapter adapter = (ScheduleAdapter) recyclerView.getAdapter();
//        if(adapter!=null){
//            adapter.setList(list);
//        }
//    }

}
