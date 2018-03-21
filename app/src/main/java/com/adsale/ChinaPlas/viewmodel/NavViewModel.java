package com.adsale.ChinaPlas.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.DrawerListAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.MainIconRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.CommonListActivity;
import com.adsale.ChinaPlas.ui.ConcurrentEventActivity;
import com.adsale.ChinaPlas.ui.DocumentsDownCenterActivity;
import com.adsale.ChinaPlas.ui.ExhibitorAllListActivity;
import com.adsale.ChinaPlas.ui.FloorDetailActivity;
import com.adsale.ChinaPlas.ui.FloorDtlActivity;
import com.adsale.ChinaPlas.ui.MyExhibitorActivity;
import com.adsale.ChinaPlas.ui.NCardActivity;
import com.adsale.ChinaPlas.ui.NCardCreateEditActivity;
import com.adsale.ChinaPlas.ui.NewTecActivity;
import com.adsale.ChinaPlas.ui.NewsActivity;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.ui.ScannerActivity;
import com.adsale.ChinaPlas.ui.ScheduleActivity;
import com.adsale.ChinaPlas.ui.SettingActivity;
import com.adsale.ChinaPlas.ui.SubscribeActivity;
import com.adsale.ChinaPlas.ui.TechnicalListActivity;
import com.adsale.ChinaPlas.ui.TravelInfoActivity;
import com.adsale.ChinaPlas.ui.UpdateCenterActivity;
import com.adsale.ChinaPlas.ui.UserInfoActivity;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.adsale.ChinaPlas.utils.Constant.INTENT_COMMON_TYPE;
import static com.adsale.ChinaPlas.utils.Constant.WEB_URL;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PERMISSION_CAMERA;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_CAMERA;

/**
 * Created by Carrie on 2017/8/8.
 * 侧边栏
 */

public class NavViewModel implements OnIntentListener {
    //drawer
    public final ObservableField<String> drawerLoginTitle = new ObservableField<>();
    public final ObservableField<String> drawerLoginOrSync = new ObservableField<>();
    public final ObservableField<String> drawerLogout = new ObservableField<>();
    public final ObservableBoolean isLoginSuccess = new ObservableBoolean();
    public final ObservableBoolean isLoginStatusChanged = new ObservableBoolean();

    private static final String TAG = "NavViewModel";
    private Context mContext;
    private MainIconRepository mainIconRepository;
    private ArrayList<MainIcon> mLeftMenus;
    private ArrayList<MainIcon> mParents;
    private ArrayList<ArrayList<MainIcon>> children;
    private DrawerLayout mDrawerLayout;
    private Intent intent;

    private static NavViewModel INSTANCE;

    public final ObservableInt language = new ObservableInt(0);
//    private DrawerAdapter drawerAdapter;

    public final ObservableInt mCurrLang = new ObservableInt(AppUtil.getCurLanguage());
    private DrawerListAdapter drawerListAdapter;

    public NavViewModel(Context context) {
        LogUtil.i(TAG, "-- NavViewModel Construct--");
        mContext = context.getApplicationContext();
        isLoginSuccess.set(AppUtil.isLogin());
    }

    public void onStart(RecyclerView recyclerView, DrawerLayout drawerLayout) {
        long startTime = System.currentTimeMillis();
        mDrawerLayout = drawerLayout;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mainIconRepository = MainIconRepository.getInstance();
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
        LogUtil.i(TAG, "processDrawerList");

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
                    parent.isDrawerHasChild.set(true);
                    mParents.set(i, parent);
                    children.add(child);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, " processDrawerList spend : " + (endTime - startTime) + "ms");
    }

    private void setAdapter(RecyclerView recyclerView) {
        drawerListAdapter = new DrawerListAdapter(mLeftMenus, mParents, this);
        recyclerView.setAdapter(drawerListAdapter);
    }

    public void refreshUpdateCount(int count) {
        if (drawerListAdapter == null) {
            return;
        }
        drawerListAdapter.updateCenterCount(count);
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
        isLoginStatusChanged.set(true);
        setHeaderText();
    }

    private void setHeaderText() {
        if (isLoginSuccess.get()) {
            drawerLoginTitle.set("");
            drawerLoginOrSync.set(mContext.getString(R.string.drawer_sync));
            drawerLogout.set(mContext.getString(R.string.left_menu_logout));
        } else {
            drawerLoginTitle.set(mContext.getString(R.string.left_menu_user_login));
            drawerLoginOrSync.set(mContext.getString(R.string.left_menu_login));
        }
    }

    public void updateLanguage() {
        setHeaderText();
    }

    public void sync(View view) {
        Toast.makeText(mContext, mContext.getString(R.string.syncStart), Toast.LENGTH_SHORT).show();
        if (mDrawerListener != null) {
            mDrawerListener.sync();
        }
    }

    public void login(View view) {
//        close();
        if (mDrawerListener != null) {
            mDrawerListener.login();
        }
    }

    public Intent intent(Activity activity, MainIcon mainIcon) {
        LogUtil.i(TAG, "mContext: " + mContext.getClass().getSimpleName());
        LogUtil.i(TAG, "activity: " + activity.getClass().getSimpleName());

        if (mContext.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
            return null;
        }
        intent = newIntent(activity, mainIcon);
        if (intent != null) {
            intent.putExtra(Constant.TITLE, mainIcon.getTitle(AppUtil.getCurLanguage()));
            intent.putExtra(Constant.BAIDU_TJ, mainIcon.getBaiDu_TJ());
            LogUtil.i(TAG, "Constant.BAIDU_TJ=" + mainIcon.getBaiDu_TJ());
            activity.startActivity(intent);
            if (AppUtil.isTablet()) {
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        return intent;
    }

    public Intent newIntent(Activity activity, MainIcon mainIcon) {
        String iconID = mainIcon.getIconID();
        if (iconID.startsWith("MI")) {
            if (iconID.length() > 2) {
                String subIconId = iconID.substring(iconID.length() - 2, iconID.length());
                AppUtil.trackViewLog(100 + Integer.valueOf(subIconId), "Page", "", mainIcon.getBaiDu_TJ());
            }
        } else
            AppUtil.trackViewLog(100 + Integer.valueOf(iconID), "Page", "", mainIcon.getBaiDu_TJ());

        switch (mainIcon.getBaiDu_TJ()) {
            case Constant.BDTJ_VISITOR_REG://预登记
            case Constant.BDTJ_VISITOR_REG_TEXT://预登记
            case Constant.BDTJ_VISITO://预登记
                intent = new Intent(activity, RegisterActivity.class);
//                intent = new Intent(activity, DocumentsDownCenterActivity.class);
                break;
            case Constant.BDTJ_MY_ACCOUNT://用户资料
                intent = new Intent(activity, UserInfoActivity.class);
//                intent = new Intent(activity, TechnicalListActivity.class);
                break;
            case Constant.BDTJ_EXHIBITOR_LIST:// 展商名单
                LogUtil.i(TAG, "BDTJ_EXHIBITOR_LIST");
                intent = new Intent(activity, ExhibitorAllListActivity.class);
                break;
            case Constant.BDTJ_SCHEDULE:// 我的日程
                intent = new Intent(activity, ScheduleActivity.class);
                break;
            case Constant.BDTJ_CONTENT_UPDATE:
                intent = new Intent(activity, UpdateCenterActivity.class);
                break;
            case Constant.BDTJ_MY_NAME_CARD://名片
                SharedPreferences spNameCard = activity.getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
                boolean isCreate = spNameCard.getBoolean("isCreate", true);
                if (isCreate) {
                    intent = new Intent(activity, NCardCreateEditActivity.class);
                } else {
                    intent = new Intent(activity, NCardActivity.class);
                }
                break;
//            case Constant.BDTJ_HALL_MAP_TEXT:
//            case Constant.BDTJ_HALL_MAP:
//                intent = new Intent(activity, WebContentActivity.class);
//                break;
            case Constant.BDTJ_INTERESTED_EXHIBITOR:
//                 intent = new Intent(context, FavouriteProductActivity.class);
                break;
            case Constant.BDTJ_NEWS:
                intent = new Intent(activity, NewsActivity.class);
                break;
            case Constant.BDTJ_EVENTS:  // 同期活动
            case Constant.BDTJ_EVENTS_TXT:
                intent = new Intent(activity, ConcurrentEventActivity.class);
                break;
            case Constant.BDTJ_TRAVEL_INFO:
                intent = new Intent(activity, TravelInfoActivity.class);
                break;
            case Constant.BDTJ_SETTING:/* 设置 */
                intent = new Intent(activity, SettingActivity.class);
//                intent.putExtra("title", activity.getString(R.string.title_setting));// oclsMainIcon.getTitle(AppUtil.getCurLanguage(context)
                break;
            case Constant.BDTJ_MY_EXHIBITOR:
                intent = new Intent(activity, MyExhibitorActivity.class);
                break;
            case Constant.BDTJ_SUBSRIBEE_NEWSLETTER:/* 订阅电子快讯 */
                LogUtil.i(TAG, "跳转。。。SubscribeActivity");
                intent = new Intent(activity, SubscribeActivity.class);
                break;
            case Constant.BDTJ_QR_SCANNER:/* 二维码扫描器 */
                boolean hasCameraPerm = PermissionUtil.checkPermission(activity, PERMISSION_CAMERA);
                LogUtil.i(TAG, "hasCameraPerm=" + hasCameraPerm);
                if (hasCameraPerm) {
                    intent = new Intent(activity, ScannerActivity.class);
                } else {
                    PermissionUtil.requestPermission(activity, PERMISSION_CAMERA, PMS_CODE_CAMERA);
                    return null;
                }
                break;
            case Constant.BDTJ_NOTIFICATION_CENTER: /* 通知中心 */
                intent = new Intent(activity, CommonListActivity.class);
                intent.putExtra(INTENT_COMMON_TYPE, CommonListActivity.TYPE_MSG_CENTER);
                break;
            case Constant.BDTJ_NEW_TEC: /* 新技术产品 */
            case Constant.BDTJ_NEW_TEC_TXT: /* formal */
                intent = new Intent(activity, NewTecActivity.class);
                break;
            default:
                intent = new Intent(activity, WebContentActivity.class);
                intent.putExtra("Url", "WebContent/".concat(mainIcon.getIconID()));
                break;
        }
        intent.putExtra("title", mainIcon.getTitle(AppUtil.getCurLanguage()));
        intent.putExtra(Constant.BAIDU_TJ, mainIcon.getBaiDu_TJ());
        return intent;
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        LogUtil.i(TAG, "-  onIntent -");
//        intent(mContext,(MainIcon) entity);
//        if(intent!= null && !(mContext instanceof MainActivity)){
//            ((Activity) mContext).finish();
//        }


    }

    public interface OnDrawerClickListener {
        void login();

        void sync();
    }

    public void setOnDrawerClickListener(OnDrawerClickListener listener) {
        mDrawerListener = listener;
    }

    public OnDrawerClickListener mDrawerListener;
}
