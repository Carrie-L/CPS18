package com.adsale.ChinaPlas.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ExhibitorPhotoAdapter;
import com.adsale.ChinaPlas.adapter.NewTecListAdapter;
import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.adapter.TextAdapter2;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.data.model.EPO;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.databinding.LayoutExhibitorAddScheduleBinding;
import com.adsale.ChinaPlas.databinding.ViewNoteBinding;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.ui.ScheduleEditActivity;
import com.adsale.ChinaPlas.ui.TakePhotoActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.ui.view.AboutView;
import com.adsale.ChinaPlas.ui.view.ExhiDtlInfoView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.PermissionUtil;
import com.adsale.ChinaPlas.utils.ShareSDKDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_WRITE_SD;
import static com.adsale.ChinaPlas.viewmodel.ExhibitorListViewModel.TYPE_APP_INDUSTRY;
import static com.adsale.ChinaPlas.viewmodel.ExhibitorListViewModel.TYPE_INDUSTRY;


/**
 * Created by Carrie on 2017/11/15.
 * 展商详情
 */

public class ExhibitorDtlViewModel {
    private final String TAG = "ExhibitorDtlViewModel";
    public final ObservableField<String> companyName = new ObservableField<>("");
    public final ObservableBoolean isCollected = new ObservableBoolean(); // 是否收藏
    public final ObservableBoolean isInfoView = new ObservableBoolean(true); // 显示 InfoView, 控制底部button背景的
    public final ObservableBoolean isNoteView = new ObservableBoolean(); // 显示 NoteView
    public final ObservableBoolean isScheduleView = new ObservableBoolean(); // 显示 ScheduleView
    public final ObservableBoolean isShareView = new ObservableBoolean(); // 显示 ShareView
    public final ObservableBoolean isIndustryEmpty = new ObservableBoolean(); // 产品列表是否为空，空则不显示“产品”Tab
    public final ObservableBoolean isAppIndEmpty = new ObservableBoolean(); // 应用行业列表是否为空，空则不显示“产品”Tab
    public final ObservableBoolean isNewTecEmpty = new ObservableBoolean(); // 新技术产品列表是否为空，空则不显示“产品”Tab

    private Context mContext;
    private OnIntentListener mListener;
    private FrameLayout mInfoFrameLayout; // company info
    private FrameLayout mContentFrameLayout; // other button view
    private LayoutInflater inflater;
    private ExhiDtlInfoView mInfoView;
    public Exhibitor exhibitor;
    private ExhibitorRepository mRepository;
    private ArrayList<Text2> industries;
    private ArrayList<Text2> appIndustries = new ArrayList<>();
    private ArrayList<NewProductInfo> newProductInfos = new ArrayList<>();
    private FilterRepository mFilterRepository;
    private RecyclerView mCatalogRV; // 产品
    private RecyclerView mAppIndustryRV; // 应用行业
    private RecyclerView mNewTecRV; // 新技术产品
    private View mScheduleView;
    private ArrayList<ScheduleInfo> mScheduleInfos = new ArrayList<>();
    private ScheduleAdapter mScheduleAdapter;
    private ViewStub mViewStub;
    private View mNoteView;
//    private String M5Description;
    //    private adAdvertisementObj adObj;
    private EPO.D4 D4;
    private AboutView mAboutView;
    private ArrayList<String> mPhotos = new ArrayList<>();
    private String mPhotoDir = "";
    private ExhibitorPhotoAdapter mPhotoAdapter;
    private boolean hasSDPermission;
    private boolean hasCameraPermission;

    public ExhibitorDtlViewModel(Context mContext, FrameLayout frameLayout) {
        this.mContext = mContext;
        this.mInfoFrameLayout = frameLayout;
        inflater = LayoutInflater.from(mContext);
    }

    public void setOnCallPhoneListener(ExhiDtlInfoView.OnCallPhoneListener listener) {

    }

    private ExhiDtlInfoView.OnCallPhoneListener mCallListener;

    public void start(String companyID, OnIntentListener listener, ViewStub viewStub, boolean isAder, ExhiDtlInfoView.OnCallPhoneListener callListener) {
        mListener = listener;
        mCallListener = callListener;
        mViewStub = viewStub;
        mRepository = ExhibitorRepository.getInstance();
        LogUtil.i(TAG, "start:companyID=" + companyID);
        exhibitor = mRepository.getExhibitor(companyID);
        companyName.set(exhibitor.getCompanyName());
        isCollected.set(exhibitor.getIsFavourite() == 1);
        if (isAder) {
            showAbout();
        } else {
            showInfo();
        }
        mFilterRepository = FilterRepository.getInstance();
        getProductList();
        getAppIndList();
        getNewTecList();
    }

    public void setD4Data(EPO.D4 D4) {
        this.D4 = D4;
//        M5Description = D4.getAbout();
    }

    public void addToHistory() {
        HistoryExhibitor historyExhibitor = new HistoryExhibitor(null, exhibitor.getCompanyID(), exhibitor.getCompanyNameEN(), exhibitor.getCompanyNameCN(), exhibitor.getCompanyNameTW(), exhibitor.getBoothNo(), AppUtil.getCurrentTime());
        mRepository.initHistoryDao();
        mRepository.insertHistoryExhiItem(historyExhibitor);
    }

    private void showInfo() {
        mViewStub.setVisibility(View.GONE);
        mInfoFrameLayout.removeAllViews();
        if (mInfoView == null) {
            LogUtil.e(TAG, "mInfoView==null, so init it.");
            mInfoView = new ExhiDtlInfoView(mContext);
            mInfoView.setOnCallPhoneListener(mCallListener);
            mInfoView.setData(exhibitor);
        }
        mInfoFrameLayout.addView(mInfoView);
    }

    private void showAbout() {
        mViewStub.setVisibility(View.GONE);
        mInfoFrameLayout.removeAllViews();
        if (mAboutView == null) {
            mAboutView = new AboutView(mContext);
//            mAboutView.initData(M5Description, D4.website, D4.videoCover, D4.videoLink);
            mAboutView.initData(D4);
        }
        mInfoFrameLayout.addView(mAboutView);
    }

    /**
     * 关于 ： 广告商才有
     */
    public void onAbout() {
        setIsView(0);
        showAbout();
    }

    /**
     * 公司资料
     */
    public void onInfo() {
        setIsView(0);
        showInfo();
    }

    /**
     * 产品
     */
    public void onCatalog() {
        if (mCatalogRV == null) {
            mCatalogRV = new RecyclerView(mContext);
            setRecyclerView(mCatalogRV);
            TextAdapter2 mIndustryAdapter = new TextAdapter2(industries, mContext, TYPE_INDUSTRY);
            mCatalogRV.setAdapter(mIndustryAdapter);
        }
        mInfoFrameLayout.removeAllViews();
        mInfoFrameLayout.addView(mCatalogRV);
    }

    /**
     * 应用行业
     */
    public void onAppIndustry() {
        if (mAppIndustryRV == null) {
            mAppIndustryRV = new RecyclerView(mContext);
            setRecyclerView(mAppIndustryRV);
            TextAdapter2 mAppIndAdapter = new TextAdapter2(appIndustries, mContext, TYPE_APP_INDUSTRY);
            mAppIndustryRV.setAdapter(mAppIndAdapter);
        }
        mInfoFrameLayout.removeAllViews();
        mInfoFrameLayout.addView(mAppIndustryRV);
    }

    /**
     * 新技术产品
     */
    public void onNewTec() {
        if (mNewTecRV == null) {
            mNewTecRV = new RecyclerView(mContext);
            setRecyclerView(mNewTecRV);
            NewTecListAdapter mTecAdapter = new NewTecListAdapter(newProductInfos, mListener);
            mNewTecRV.setAdapter(mTecAdapter);
        }
        mInfoFrameLayout.removeAllViews();
        mInfoFrameLayout.addView(mNewTecRV);
    }

    /**
     * 收藏/取消 到我的参展商
     */
    public void onCollect() {
        if (AppUtil.isLogin()) {
            isCollected.set(!isCollected.get());
            exhibitor.setIsFavourite(isCollected.get() ? 1 : 0);
            mRepository.updateIsFavourite(mContext, exhibitor.getCompanyID(), exhibitor.getIsFavourite());
            if (isCollected.get()) { // 只有收藏展商时记录，取消展商不记录
                AppUtil.trackViewLog(414, "AddExhibitor", "", exhibitor.getCompanyID());
                AppUtil.setStatEvent(mContext, "AddExhibitor", "AddExhibitor_EInfo_" + exhibitor.getCompanyID());
            }
        } else {
            toastLogin(mContext.getString(R.string.login_first_add_exhibitor));
        }
    }

    private void toastLogin(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.WEB_URL, String.format(NetWorkHelper.MY_CHINAPLAS_URL, AppUtil.getLanguageUrlType()));
                        intent.putExtra(Constant.TITLE, mContext.getString(R.string.title_login));
                        intent.putExtra("ExhibitorInfo", true); // 登录成功后返回到用户小工具列表
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /*   ↓↓↓ ------------------------ Note --------------------------- ↓↓↓           */
    public final ObservableInt mRate = new ObservableInt(1);
    public final ObservableField<String> mNote = new ObservableField<>("");

    /**
     * 笔记、评分、拍照
     */
    public void onNote() {
        setIsView(2);
        initContentFrame();
        mContentFrameLayout.removeAllViews();
        if (mNoteView == null) {
            ViewNoteBinding noteBinding = ViewNoteBinding.inflate(inflater);
            mNoteView = noteBinding.getRoot();
            noteBinding.setModel(this);
            noteBinding.executePendingBindings();
            showNoteAndRate();
            RecyclerView mNoteRV = noteBinding.photoRecyclerView;
            setHorizontalRecyclerView(mNoteRV);
            getPhotos();
            mPhotoAdapter = new ExhibitorPhotoAdapter(mPhotos, mContext, mListener);
            mNoteRV.setAdapter(mPhotoAdapter);
        }
        mContentFrameLayout.addView(mNoteView);
    }

    private void showNoteAndRate() {
        if (exhibitor.getRate() != null) {
            mRate.set(exhibitor.getRate());
        }
        if (!TextUtils.isEmpty(exhibitor.getNote())) {
            mNote.set(exhibitor.getNote());
        }
    }

    public void onRate(int rate) {
        mRate.set(rate);
    }

    public void saveNoteAndRate() {
        LogUtil.i(TAG, "= saveNoteAndRate =");
        if (exhibitor.getNote() != null && !TextUtils.isEmpty(exhibitor.getNote()) && !exhibitor.getNote().equals(mNote.get())) {
            LogUtil.i(TAG, "保存笔记，添加日志");
            AppUtil.trackViewLog(417, "UN", "", exhibitor.getCompanyID());
            AppUtil.setStatEvent(mContext, "UpdateNote", "UN_" + exhibitor.getCompanyID());
        }
        exhibitor.setNote(mNote.get());
        exhibitor.setRate(mRate.get());
        mRepository.updateItemData(exhibitor);
    }

    private void getPhotos() {
        mPhotoDir = FileUtil.getSDRootPath().concat("CPS18/").concat(exhibitor.getCompanyName()).concat("/");
        File file0 = new File(FileUtil.getSDRootPath().concat("CPS18/"));
        if (!file0.exists()) {
            file0.mkdir();
        }
        File file = new File(mPhotoDir);
        if (!file.exists()) {
            file.mkdir();
        }
        checkSDPermission();
        if (!hasSDPermission) {
            return;
        }
        String[] files = file.list();
        for (String fileName : files) {
            LogUtil.i(TAG, "fileName =" + fileName);
            mPhotos.add(mPhotoDir.concat(fileName));
        }
        LogUtil.i(TAG, "mPhotos=" + mPhotos.size() + "," + mPhotos.toString());
    }

    private void createPhotoDir() {

    }

    public void photoSuccess(String path) {
        LogUtil.i(TAG, "take photo success:" + path);
        mPhotos.add(path);
        mPhotoAdapter.setList(mPhotos);

        LogUtil.i(TAG, "exhibitor.isCollected 0 = " + exhibitor.isCollected.get());
        // 拍了照片的自动加入到我的参展商
        if (!exhibitor.isCollected.get()) {
            isCollected.set(true);
            exhibitor.setIsFavourite(1);
            mRepository.updateItemData(exhibitor);
            LogUtil.i(TAG, "exhibitor.isCollected 1 = " + exhibitor.isCollected.get());
        }
        LogUtil.i(TAG, "exhibitor.isCollected 2 = " + exhibitor.isCollected.get());
    }

    public void deletePhoto(String path) {
        LogUtil.i(TAG, "deletePhoto:" + path);
        int size = mPhotos.size();
        for (int i = 0; i < size; i++) {
            if (mPhotos.get(i).equals(path)) {
                mPhotos.remove(i);
                break;
            }
        }
        mPhotoAdapter.setList(mPhotos);

        File file = new File(path);
        boolean isDel = file.delete();
        Toast.makeText(mContext, "delete: " + isDel, Toast.LENGTH_SHORT).show();
    }

    /**
     * only when app has camera & sd permissions, can intent to TakePhotoActivity.class.
     * Otherwise, request permissions.
     */
    public void onTakePhoto() {
        checkCameraPermission();
        if (hasCameraPermission && hasSDPermission) {
            showCamera();
        } else if (hasCameraPermission) {
            PermissionUtil.requestPermission(activity, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE, PMS_CODE_WRITE_SD);
        } else if (hasSDPermission) {
            PermissionUtil.requestPermission(activity, PermissionUtil.PERMISSION_CAMERA, PermissionUtil.PMS_CODE_CAMERA);
        } else {
            PermissionUtil.requestPermissions(activity, new String[]{PermissionUtil.PERMISSION_CAMERA, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE});
        }
    }

    public boolean checkSDPermission() {
        hasSDPermission = PermissionUtil.checkPermission(activity, PermissionUtil.PERMISSION_WRITE_EXTERNAL_STORAGE);
        return hasSDPermission;
    }

    public boolean checkCameraPermission() {
        hasCameraPermission = PermissionUtil.checkPermission(activity, PermissionUtil.PERMISSION_CAMERA);
        return hasCameraPermission;
    }

    public void showCamera() {
        mListener.onIntent(mPhotoDir.concat(System.currentTimeMillis() + "").concat(".jpg"), TakePhotoActivity.class);
    }

    private ExhibitorDetailActivity activity;

    public void setActivity(ExhibitorDetailActivity activity) {
        this.activity = activity;
    }

    /*   ↓↓↓ ------------------------ Schedule --------------------------- ↓↓↓           */
    public final ObservableBoolean isScheduleNoData = new ObservableBoolean();

    public void onSchedule() {
        if (AppUtil.isLogin()) {
            setIsView(3);
            initContentFrame();
            mContentFrameLayout.removeAllViews();
            if (mScheduleView == null) {
                LayoutExhibitorAddScheduleBinding scheduleBinding = LayoutExhibitorAddScheduleBinding.inflate(inflater);
                mScheduleView = scheduleBinding.getRoot();
                scheduleBinding.setModel(this);
                scheduleBinding.executePendingBindings();
                RecyclerView mScheduleRV = scheduleBinding.rvSchedule;
                setRecyclerView(mScheduleRV);
                getExhibitorSchedules();
                mScheduleAdapter = new ScheduleAdapter(mScheduleInfos, mContext);
                mScheduleRV.setAdapter(mScheduleAdapter);
            }
            mContentFrameLayout.addView(mScheduleView);
        } else {
            toastLogin(mContext.getString(R.string.login_first_add_schedule));
        }
    }

    public void updateSchedule() {
        setIsView(3);
        getExhibitorSchedules();
        mScheduleAdapter.setList(mScheduleInfos);
    }

    private void getExhibitorSchedules() {
        mScheduleInfos.clear();
        ScheduleRepository scheduleRepository = ScheduleRepository.getInstance();
        mScheduleInfos = scheduleRepository.getExhibitorSchedules(exhibitor.getCompanyID());
        isScheduleNoData.set(mScheduleInfos.isEmpty());
    }

    public void onAddSchedule() {
        mListener.onIntent(exhibitor, ScheduleEditActivity.class);
    }
/*   ↑↑↑↑ ------------------------ Schedule End  --------------------------- ↑↑↑↑           */

    public void onShare() {
        String url = "";
        if (App.mLanguage.get() == 1) {
            url = String.format(NetWorkHelper.SHARE_EXHIBITOR_EN_URL, exhibitor.getCompanyID());
        } else {
            url = String.format(NetWorkHelper.SHARE_EXHIBITOR_URL, AppUtil.getName("trad", "eng", "simp"), exhibitor.getCompanyID());
        }
        LogUtil.i(TAG, "分享展商地址为：" + url);

        ShareSDKDialog shareSDKDialog = new ShareSDKDialog();
        shareSDKDialog.showDialog(activity, companyName.get().concat("\n").concat(mContext.getString(R.string.share_exhibitor_booth)).concat(exhibitor.getBoothNo()),
                Constant.SHARE_IMAGE_URL, url, Constant.SHARE_IMAGE_PATH);

        AppUtil.trackViewLog(425, "SE", "", exhibitor.getCompanyID());
        AppUtil.setStatEvent(mContext, "Share", "Share_Exhibitor_".concat(exhibitor.getCompanyID()));

    }

    public void onBackToInfo() {
        setIsView(0);
        showInfo();
    }

    private void getProductList() {
        industries = new ArrayList<>();
        mFilterRepository.initIndustryDao();
        industries = mFilterRepository.getIndustries(exhibitor.getCompanyID());
        isIndustryEmpty.set(industries.isEmpty());
    }

    private void getAppIndList() {
        mFilterRepository.initAppIndustryDao();
        appIndustries = mFilterRepository.queryAppIndustryLists(exhibitor.getCompanyID());
        isAppIndEmpty.set(appIndustries.isEmpty());
    }

    private void getNewTecList() {
        NewTecRepository repository = new NewTecRepository();
        repository.initDao();
        newProductInfos.clear();
        newProductInfos = repository.getProductInfoList(exhibitor.getCompanyID());
        isNewTecEmpty.set(newProductInfos.isEmpty());
    }

    private void initContentFrame() {
        if (mContentFrameLayout == null) {
            mContentFrameLayout = (FrameLayout) mViewStub.inflate();
        }
        mViewStub.setVisibility(View.VISIBLE);
    }

    /**
     * 当前显示的是哪个View，对应的按钮背景改变
     * collect 没有 View，因此不算在内
     *
     * @param currIndex 底部bar 5个按钮，（0..4,1） 0-4,除了1
     */
    private void setIsView(int currIndex) {
        isInfoView.set(currIndex == 0);
        isNoteView.set(currIndex == 2);
        isScheduleView.set(currIndex == 3);
        isShareView.set(currIndex == 4);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayout.VERTICAL));
    }

    private void setHorizontalRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llManager);
    }


}
