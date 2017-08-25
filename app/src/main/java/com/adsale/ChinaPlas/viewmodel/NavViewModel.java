package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Canvas;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.DrawerAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.ui.ExhibitorActivity;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.ui.MainActivity;
import com.adsale.ChinaPlas.ui.MyAccountActivity;
import com.adsale.ChinaPlas.ui.NCardActivity;
import com.adsale.ChinaPlas.ui.NCardCreateEditActivity;
import com.adsale.ChinaPlas.ui.NewsActivity;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.ui.ScheduleActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.adsale.ChinaPlas.R.id.language;

/**
 * Created by Carrie on 2017/8/8.
 */

public class NavViewModel implements DrawerAdapter.OnCloseDrawerListener {
    //drawer
    public final ObservableField<String> drawerLoginTitle = new ObservableField<>();
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

    public final ObservableInt language = new ObservableInt(0);
    private DrawerAdapter drawerAdapter;


    public NavViewModel(Context context) {
        mContext = context.getApplicationContext();

        LogUtil.i(TAG, "NavViewModel");

    }

    public void onStart(RecyclerView recyclerView, DrawerLayout drawerLayout) {
        long startTime = System.currentTimeMillis();

        mDrawerLayout = drawerLayout;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mainIconRepository = MainIconRepository.getInstance();
        mLanguage = AppUtil.getCurLanguage();

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

        drawerAdapter = new DrawerAdapter(mLeftMenus, mParents, children, this);
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
        isLoginSuccess.set(AppUtil.isLogin());
        if (isLoginSuccess.get()) {
            drawerLoginTitle.set(AppUtil.getUserEmail());
        }
    }

    public void updateRegister() {

    }

    public void sync(View view) {
        Toast.makeText(mContext, mContext.getString(R.string.syncStart), Toast.LENGTH_SHORT).show();
        if (mDrawerListener != null) {
            mDrawerListener.sync();
        }
    }

    public void login(View view) {
        if (mDrawerListener != null) {
            mDrawerListener.login();
        }
    }

    public void clickTW() {
        changeLanguage(0);
    }

    public void clickEN() {
        changeLanguage(1);
    }

    public void clickCN() {
        changeLanguage(2);
    }

    private void changeLanguage(int lang) {
//        language.set(lang);
        AppUtil.switchLanguage(mContext, lang);

        String className = mContext.getClass().getSimpleName();
        LogUtil.i(TAG, "className=" + className);


        if (mainActivity != null) {
            LogUtil.i(TAG, "in MainActivity");
            drawerAdapter.notifyDataSetChanged();

        } else {
            LogUtil.i(TAG, "changeLanguage: other aty");

        }
        setLanguageListener(lang, mainActivity != null);

    }

    private void setLanguageListener(int lang, boolean isInMain) {
        if (mDrawerListener != null) {
            mDrawerListener.languageChanged(lang, isInMain);
        }
    }

    private MainActivity mainActivity;

    public void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void close() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private <T> void toIntent(Activity activity, Class<T> cls) {
        intent = new Intent(activity, cls);

    }

    public void intent(Activity activity, MainIcon mainIcon) {

        LogUtil.i(TAG, "mContext: " + mContext.getClass().getSimpleName());
        LogUtil.i(TAG, "activity: " + activity.getClass().getSimpleName());

        if (mContext.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
            return;
        }
        newIntent(activity, mainIcon);
        if (intent != null) {
            intent.putExtra(Constant.TITLE, mainIcon.getTitle());
            activity.startActivity(intent);
        }


    }

    private void newIntent(Activity activity, MainIcon mainIcon) {
        switch (mainIcon.getBaiDu_TJ()) {
            case Constant.BDTJ_VISITOR_REG://预登记
            case Constant.BDTJ_VISITOR_REG_TEXT://预登记
                intent = new Intent(activity, RegisterActivity.class);
                break;
            case Constant.BDTJ_MY_ACCOUNT://用户资料
                if (AppUtil.isLogin()) {
                    intent = new Intent(activity, MyAccountActivity.class);
                } else {
                    intent = new Intent(activity, LoginActivity.class);
                }
                break;
            case Constant.BDTJ_EXHIBITOR_LIST://展商名单
                LogUtil.i(TAG, "BDTJ_EXHIBITOR_LIST");
                intent = new Intent(activity, ExhibitorActivity.class);
                break;
            case Constant.BDTJ_SCHEDULE://我的日程
                intent = new Intent(activity, ScheduleActivity.class);
                break;
            case Constant.BDTJ_CONTENT_UPDATE:
//                intent = new Intent(context, UpdateCenterActivity.class);
                break;
            case Constant.BDTJ_MY_NAME_CARD://名片
                SharedPreferences spNameCard=activity.getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
                boolean isCreate=spNameCard.getBoolean("isCreate",true);
                if(isCreate){
                    intent = new Intent(activity, NCardCreateEditActivity.class);
                }else{
                    intent = new Intent(activity, NCardActivity.class);
                }
                break;
            case Constant.BDTJ_HALL_MAP_TEXT:
            case Constant.BDTJ_HALL_MAP:
//                intent = new Intent(activity,. class);
                break;
            case Constant.BDTJ_INTERESTED_EXHIBITOR:
//                 intent = new Intent(context, FavouriteProductActivity.class);
                break;
            case Constant.BDTJ_NEWS:
                intent = new Intent(activity, NewsActivity.class);
                break;
            case Constant.BDTJ_EVENTS:
//                intent = new Intent(context, EventActivity.class);
                break;
            case Constant.BDTJ_TRAVEL_INFO:
//                intent = new Intent(context, HotelInfoActivity.class);
                break;
            case Constant.BDTJ_SETTING:
//                intent = new Intent(context, SettingActivity.class);
//                intent.putExtra("title", context.getString(R.string.title_setting));// oclsMainIcon.getTitle(SystemMethod.getCurLanguage(context)
                break;
            case Constant.BDTJ_MY_EXHIBITOR:
//                intent = new Intent(context, MyExhibitorListActivity.class);
                break;
            case Constant.BDTJ_SUBSRIBEE_NEWSLETTER:
//                intent = new Intent(activity,SubscribeActivity.class);
                break;
            case Constant.BDTJ_QR_SCANNER:
//                intent = new Intent(context, CaptureActivity.class);
                break;
            case Constant.BDTJ_NOTIFICATION_CENTER:
//                intent = new Intent(context, CommonListActivity.class);
//                intent.putExtra("TYPE", Constant.COM_MSG_CENTER);
                break;
            default:
                intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(Constant.MAIN_ICON, mainIcon);
                break;
        }
    }

    public interface OnDrawerClickListener {
        void login();

        void logout();

        void sync();

        void itemClicked(String bdTJ);

        void languageChanged(int language, boolean inMainAty);
    }

    public void setOnDrawerClickListener(OnDrawerClickListener listener) {
        mDrawerListener = listener;
    }

    private OnDrawerClickListener mDrawerListener;
}
