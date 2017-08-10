package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Canvas;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.DrawerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.ui.MyAccountActivity;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.adsale.ChinaPlas.R.id.language;

/**
 * Created by Carrie on 2017/8/8.
 */

public class NavViewModel implements DrawerAdapter.OnCloseDrawerListener {
    public final ObservableField<String> drawerLoginTitle = new ObservableField<>();
    public final ObservableField<String> drawerLogin = new ObservableField<>();
    public final ObservableField<String> drawerLogout = new ObservableField<>();
    public final ObservableBoolean isLoginSuccess = new ObservableBoolean(false);

    private static final String TAG = "NavViewModel";
    private Context mContext;
    private MainIconRepository mainIconRepository;
    private ArrayList<MainIcon> mLeftMenus;
    private ArrayList<MainIcon> mParents;
    private ArrayList<ArrayList<MainIcon>> children;
    private int mLanguage;
    private DrawerLayout mDrawerLayout;
    private Intent intent;


    public NavViewModel(Context context) {
        mContext = context.getApplicationContext();

        LogUtil.i(TAG, "NavViewModel");

    }

    public void onStart(RecyclerView recyclerView, DrawerLayout drawerLayout) {
        long startTime = System.currentTimeMillis();

        mDrawerLayout=drawerLayout;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mainIconRepository = MainIconRepository.getInstance();
        mLanguage = AppUtil.getCurLanguage(mContext);

        initPadList();

        processDrawerList();

        setAdapter(recyclerView);

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "------------- onStart spend : " + (endTime - startTime) + "ms");
    }

    public void openDrawer() {
        setUpHeader();
    }

    private void processDrawerList() {
        long startTime = System.currentTimeMillis();

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

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, " processDrawerList spend : " + (endTime - startTime) + "ms");
    }

    private void setAdapter(RecyclerView recyclerView) {
        long startTime = System.currentTimeMillis();

        DrawerAdapter drawerAdapter = new DrawerAdapter(mLeftMenus, mParents, children,this);
        recyclerView.setAdapter(drawerAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });

        drawerAdapter.setOnCloseDrawerListener(this);

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, " setAdapter spend : " + (endTime - startTime) + "ms");
    }

    private void initPadList() {
        long startTime = System.currentTimeMillis();

        mLeftMenus = new ArrayList<>();
        mParents = new ArrayList<>();
        children = new ArrayList<>();

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, " initPadList spend : " + (endTime - startTime) + "ms");
    }

    public void setUpHeader() {
        if (AppUtil.isLogin()) {
            isLoginSuccess.set(true);
            drawerLoginTitle.set(AppUtil.getUserEmail());
        } else {
            isLoginSuccess.set(false);
        }
    }

    public void updateRegister() {

    }

    public void sync(View view) {
        Toast.makeText(mContext, "sync", Toast.LENGTH_SHORT).show();
    }

    public void login(View view) {
        Toast.makeText(mContext, "login", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, LoginActivity.class);

    }

    @Override
    public void close() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void intent(Activity activity, MainIcon mainIcon) {

        LogUtil.i(TAG,"mContext: "+mContext.getClass().getSimpleName());
        LogUtil.i(TAG,"activity: "+activity.getClass().getSimpleName());

        if(mContext.getClass().getSimpleName().equals(activity.getClass().getSimpleName())){
            return;
        }


        switch (mainIcon.getBaiDu_TJ()) {
            case "VisitorPreRegistration":
            case "VisitorPreRegistrationText":
                intent = new Intent(activity, RegisterActivity.class);
                activity.startActivity(intent);
                break;
            case "MyAccount":
                if (AppUtil.isLogin()) {
                    intent = new Intent(activity, MyAccountActivity.class);
                } else {
                    intent = new Intent(activity, LoginActivity.class);
                }
                activity.startActivity(intent);
                break;
            case "ContentUpdate":
//                intent = new Intent(context, UpdateCenterActivity.class);
                break;
            case "MyNameCard":

                break;
            case "HallMap":
            case "HallMapText":
//                intent = new Intent(activity,. class);
                break;
            case "MyInterestedExhibitor":
//                 intent = new Intent(context, FavouriteProductActivity.class);
                break;
            case "News":
//                intent = new Intent(context, NewsActivity.class);
                break;
            case "CurrentEvents":
//                intent = new Intent(context, EventActivity.class);
                break;
            case "TravelInfo":
//                intent = new Intent(context, HotelInfoActivity.class);
                break;
            case "Settings":
//                intent = new Intent(context, SettingActivity.class);
//                intent.putExtra("title", context.getString(R.string.title_setting));// oclsMainIcon.getTitle(SystemMethod.getCurLanguage(context)
                break;
            case "Schedule":
//                intent = new Intent(context, ScheduleActivity.class);
                break;
            case "MyExhibitor":
//                intent = new Intent(context, MyExhibitorListActivity.class);
                break;
            case "SubscribeeNewsletter":
//                intent = new Intent(activity,SubscribeActivity.class);
                break;
            case "QRCodeScanner":
//                intent = new Intent(context, CaptureActivity.class);
                break;
            case "NotificationCenter":
//                intent = new Intent(context, CommonListActivity.class);
//                intent.putExtra("TYPE", Constant.COM_MSG_CENTER);
                break;
            default:
                intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("BD_TJ",mainIcon);
                activity.startActivity(intent);
                break;

        }
    }
}
