package com.adsale.ChinaPlas.viewmodel;

import android.databinding.ObservableArrayList;

import com.adsale.ChinaPlas.data.model.ConcurrentEvent;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/9/19.
 */

public class EventModel {
    private static final String TAG="EventModel";
    public final ObservableArrayList<ConcurrentEvent> events = new ObservableArrayList<>();


    public void chooseDate(int dateIndex){
        LogUtil.i(TAG,"dateIndex="+dateIndex);



    }

    public void parseEvents(){
        ArrayList<ConcurrentEvent> eventList=new ArrayList<>();


    }


}
