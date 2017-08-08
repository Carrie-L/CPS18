package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.adapter.DrawerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.adsale.ChinaPlas.R.id.language;

/**
 * Created by Carrie on 2017/8/8.
 */

public class NavViewModel {
    public final ObservableField<String> drawerLoginTitle = new ObservableField<>();
    public final ObservableField<String> drawerLogin = new ObservableField<>();
    public final ObservableField<String> drawerLogout = new ObservableField<>();

    private static final String TAG="NavViewModel";
    private Context mContext;
    private MainIconRepository mainIconRepository;
    private ArrayList<MainIcon> mLeftMenus;
    private ArrayList<MainIcon> mParents;
    private ArrayList<ArrayList<MainIcon>> children;
    private int mLanguage;


    public  NavViewModel(Context context){
        mContext= context.getApplicationContext();

        LogUtil.i(TAG,"NavViewModel");

    }

    public void onStart(RecyclerView recyclerView){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mainIconRepository = MainIconRepository.getInstance();
        mLanguage= AppUtil.getCurLanguage(mContext);

        initPadList();

        processDraerList();

        setAdapter(recyclerView);
    }

    public void processDraerList() {
        ArrayList<MainIcon> child = new ArrayList<>();
        mLeftMenus = mainIconRepository.getLeftMenus(mLeftMenus, mParents, child);

        int parentSize = mParents.size();
        int childSize = child.size();

        //将parent按照大小排序
        Collections.sort(mParents, new Comparator<MainIcon>() {
            @Override
            public int compare(MainIcon lhs, MainIcon rhs) {
                return Integer.valueOf(lhs.getGoogle_TJ()).compareTo(Integer.valueOf(rhs.getGoogle_TJ()));
            }
        });

        MainIcon parent;
        for (int i = 0; i < parentSize; i++) {
            parent = mParents.get(i);
            for (int j = 0; j < childSize; j++) {
                if (child.get(j).getGoogle_TJ().split("_")[0].equals(parent.getGoogle_TJ())) {
                    parent.hasChild = true;
                    mParents.set(i, parent);
                    children.add(child);
                }
            }
        }
    }

    public void setAdapter(RecyclerView recyclerView){
        DrawerAdapter drawerAdapter = new DrawerAdapter(mLeftMenus, mParents, children, mLanguage, true);
        recyclerView.setAdapter(drawerAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
    }

    private void initPadList(){
        mLeftMenus = new ArrayList<>();
        mParents = new ArrayList<>();
        children = new ArrayList<>();
    }


}
