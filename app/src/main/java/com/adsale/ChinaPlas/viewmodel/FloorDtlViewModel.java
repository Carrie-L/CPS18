package com.adsale.ChinaPlas.viewmodel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.PhotoView.OnPhotoTapListener;
import com.adsale.ChinaPlas.PhotoView.PhotoView;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.FloorPlanCoordinate;
import com.adsale.ChinaPlas.dao.FloorPlanCoordinateDao;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.ui.view.FloorDialogFragment;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.adsale.ChinaPlas.App.mLogHelper;
import static com.adsale.ChinaPlas.ui.FloorDetailActivity.BASE_SCALE;


/**
 * Created by Carrie on 2018/1/30.
 * 展馆平面图
 */

public class FloorDtlViewModel implements FloorDialogFragment.OnDialogCancelListener {
    private final String TAG = "FloorDtlViewModel";
    private Context mContext;
    private FloorRepository mRepository;
    private ArrayList<FloorPlanCoordinate> mFloorCoors = new ArrayList<>();
    private FloorPlanCoordinate mFloorCoor;
    private FragmentManager mFragmentManager;
    private OnIntentListener mIntentListener;
    private int size;
    /*    约定： 原始图片用 O(Original) 表示，缩放后适应屏幕的图片用 P(Process) 表示       */

    /**
     * 地图在屏幕上显示的宽高
     */
    private float displayWidth;
    private float displayHeight;
    private int screenWidth;
    /**
     * 原始图片——>宽度为屏幕宽度的图片，缩放了多少, 比例
     */
    private float bmRatio;
    private FloorDialogFragment mFragment;
    /**
     * dialog 是否被cancel了。是：重新实例化FloorDialogFragment，否，直接传值。
     */
    private boolean isDialogCanceled = false;
    /**
     * 点击地图，是否找到booth参展商，是，弹出对话框，否（或者在Booth外点击），则dismiss
     */
    private boolean isFind = false;
    private ArrayList<Exhibitor> exhibitors;
    private PhotoView pvMap;
    private Canvas canvas;
    private Bitmap bitmap1;

    public final ObservableBoolean isNavOpened = new ObservableBoolean(false);
    private String mHall;
    private int fromCls;
    private Bitmap blueBitmap; // 普通 flag
    private Bitmap adBitmap; // 广告 flag
    private Bitmap favoBitmap; // 我的参展商 flag
    /* 该展馆内是否有M4广告，有则显示ad flag，无则不显示 */
    private Bitmap newBitmap;
    /* item booth coordinate, values from table FLOOR_PLAN_COORDINATE  */
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int screenHeight;
    private int flagX;
    private int flagY;

    /**
     * 在缩放后图片P上的触摸点坐标
     */
    private float touchPY;
    private float touchPX;
    private float scale;

    public FloorDtlViewModel(Context mContext, FloorRepository repository, FragmentManager fm, OnIntentListener listener, PhotoView pv, String hall, int fromCls) {
        this.mContext = mContext;
        this.mRepository = repository;
        this.mFragmentManager = fm;
        this.mIntentListener = listener;
        this.pvMap = pv;
        this.mHall = hall;
        this.fromCls = fromCls;

    }

    private adAdvertisementObj adObj;
    private int adIndex;

    public void setM4Data(adAdvertisementObj adObj, int index) {
        this.adObj = adObj;
        this.adIndex = index;
    }

    public void startMqp2(final Bitmap bitmap, DisposableObserver<Bitmap> observer) {
        Observable<Bitmap> observable = new io.reactivex.Observable<Bitmap>() {
            @Override
            protected void subscribeActual(Observer<? super Bitmap> observer) {
                startMap(bitmap);
                if (adIndex != -1) {
//                    showM4Flag();
                }
                observer.onNext(bitmap1);
                observer.onComplete();
            }
        };
        observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void startMap(Bitmap bitmap) {
        pvMap.setMaximumScale(4 * BASE_SCALE);
        pvMap.setMediumScale(3 * BASE_SCALE);
        pvMap.setMinimumScale(1);
        pvMap.setScale(BASE_SCALE, AppUtil.getScreenWidth() / 2, AppUtil.getScreenHeight() / 2, true);

        getFloorCoors(mHall);

        int bmWidth = bitmap.getWidth(); // 图片原始尺寸
        int bmHeight = bitmap.getHeight();
        screenWidth = AppUtil.getScreenWidth();
        screenHeight = AppUtil.getScreenHeight();
        bmRatio = (float) bmWidth / (float) screenWidth;
        displayHeight = bmHeight / bmRatio;
        displayWidth = screenWidth;
        LogUtil.i(TAG, "bmWidth=" + bmWidth + ",bmHeight=" + bmHeight + ",bmRatio=" + bmRatio);
        LogUtil.i(TAG, "displayWidth=" + displayWidth + ",displayHeight=" + displayHeight);

        float mapCenterX = displayWidth / 2;
        float mapCenterY = displayHeight / 2;
        LogUtil.i(TAG, "mapCenterX=" + mapCenterX + ",mapCenterY=" + mapCenterY);

        /* 修改图片，在原图上添加flag。 1.先创建一个原图大小的bitmap1 2.将原图绘制到新的bitmap1上 3.以副本形式修改图片 */
        bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap1);
        canvas.drawBitmap(bitmap, 0, 0, null);

        drawFavoFlagsOnMap();


        newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);


        pvMap.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                /* 因为源代码里返回的x,y是除过 displayRect.width() 和 displayRect.height()的，而我们需要的是触摸点坐标，因此这里乘回来。   */

                touchPX = x;
                touchPY = y;

                LogUtil.i(TAG, "displayWidth=" + displayWidth + ",displayHeight=" + displayHeight);

                // 将x,y 转换成在原始图片O 上的坐标点
                x = x * bmRatio;
                y = y * bmRatio;

                onMapClick(Math.abs(x), Math.abs(y));
            }
        });

    }

    public void showBitmap() {
        pvMap.setImageBitmap(bitmap1);
        canvas.save();
    }

    public void onClickDrawer() {
        isNavOpened.set(!isNavOpened.get());
        mDrawerListener.onDrawerClick(isNavOpened.get());
    }

    public void onMapMinimize(){
        pvMap.setScale(1);
    }

    /**
     * @param etContent companyName or boothNo
     */
    public ArrayList<Exhibitor> getEditExhibitors(String etContent) {
        return mRepository.getEditExhibitors(etContent, mHall);
    }

    /**
     * @param mapX 在原始图片O上的坐标点X
     * @param mapY OY
     */
    public void onMapClick(float mapX, float mapY) {
        scale = pvMap.getScale();
        LogUtil.i(TAG, "scale=" + scale);
        mapX = mapX / scale;
        mapY = mapY / scale;
        LogUtil.i(TAG, "after: * bmRatio = (" + mapX + "," + mapY + ")");

        isFind = false;
        for (int i = 0; i < size; i++) {
            mFloorCoor = mFloorCoors.get(i);

            if (mapX > mFloorCoor.getX1() && mapX < mFloorCoor.getX2()
                    && mapY > mFloorCoor.getY1() && mapY < mFloorCoor.getY2()) {
                x1 = mFloorCoor.getX1();
                x2 = mFloorCoor.getX2();
                y1 = mFloorCoor.getY1();
                y2 = mFloorCoor.getY2();

                     /*  显示flag */
                // 为了让flag只显示一个
                Canvas canvas = new Canvas(newBitmap);
                canvas.drawBitmap(bitmap1, 0, 0, null);

                if (blueBitmap == null) {
                    blueBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.flag_blue).copy(Bitmap.Config.ARGB_4444, true);
                }
                int flagWidth = blueBitmap.getWidth();
                int flagHeight = blueBitmap.getHeight();
                int centerX = (x2 - x1) / 2 + x1;
                int centerY = (y2 - y1) / 2 + y1;
                LogUtil.i(TAG, "centerX,centerY=" + centerX + "," + centerY);

                // 蓝色标记
                int blueX = centerX - (flagWidth / 2);// 中心点坐标减去图片宽度的一半，得到marginLeftWidth
                int blueY = centerY - flagHeight + 3; // 中心点坐标减去图片的高度，得到marginTopHeight
                LogUtil.i(TAG, "blueX,blueY=" + blueX + "," + blueY);
                canvas.drawBitmap(blueBitmap, blueX, blueY, null);
                pvMap.setImageBitmap(newBitmap);

                if (scale <= pvMap.getMinimumScale()) {
                    LogUtil.i(TAG, "》》》放大到" + pvMap.getMediumScale());
                    scale = pvMap.getMediumScale();
                }
                pvMap.setScale(scale, blueX, blueY, false);
                touchPX = blueX / bmRatio * scale;
                touchPY = blueY / bmRatio * scale;
                translate();

                    /*  弹出对话框  */
                LogUtil.i(TAG, "找到啦：booth=" + mFloorCoor.getBoothNum());
                exhibitors = mRepository.getBoothExhibitors(mFloorCoor.getBoothNum());
                if (exhibitors.isEmpty()) {
                    break;
                }
                if (mFragment == null || isDialogCanceled) {
                    initFloorDialogFragment();
                } else {
                    mFragment.setData(exhibitors);
                }
                isFind = true;

                break;
            }
        }

        if (!isFind && mFragment != null) {
            mFragment.dismiss();
            isDialogCanceled = true;
        }


    }

    public void translate() {
        RectF rectF = pvMap.getDisplayRect();
        float left = Math.abs(rectF.left);
        float top = Math.abs(rectF.top);
        float dx;
        float dy;
        LogUtil.i(TAG, "after scale: left=" + left + ",top=" + top);
        LogUtil.i(TAG, "translate: touchPX=" + touchPX + ",touchPY=" + touchPY);

        if (Math.abs(touchPX) >= left) {
            // 在屏幕范围内的点击
            float w1 = touchPX - left;
            float h1 = touchPY - top;
            LogUtil.i(TAG, "after scale ① :  x/bmRatio=" + touchPX + ", y/bmRatio=" + touchPY + ",w1=" + w1 + ",h1=" + h1);

            dx = screenWidth / 2 - Math.abs(w1);
            dy = screenHeight / 2 - Math.abs(h1);
        } else {
            // 在 的点击
            float w2 = touchPX - left;
            float h2 = touchPY - top;
            LogUtil.i(TAG, "after scale ② :  x/bmRatio=" + touchPX + ", y/bmRatio=" + touchPY + ",w2=" + w2 + ",h2=" + h2);

            dx = screenWidth / 2 + Math.abs(w2);
            dy = displayHeight / 2 - Math.abs(h2);
        }
        LogUtil.i(TAG, "after scale: dx=" + dx + ", dy=" + dy);
        pvMap.onDrag(dx, dy);
    }

    public void dismissDialogFragment() {
        if (!isDialogCanceled && mFragment != null) {
            mFragment.dismiss();
            isDialogCanceled = true;
        }
    }

    private void initFloorDialogFragment() {
        LogUtil.i(TAG, "initFloorDialogFragment() : " + exhibitors.size() + "," + exhibitors.get(0).getCompanyName());
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);
        mFragment = FloorDialogFragment.newInstance(exhibitors);
        mFragment.show(ft, "Dialog");
        mFragment.setOnDialogCancelListener(this, mIntentListener);
        isDialogCanceled = false;
    }

    private void getFloorCoors(String hall) {
        mFloorCoors = mRepository.getFloorCoors(hall);
        size = mFloorCoors.size();
    }

    public void drawSingleFlagOnMap(String booth) {
        mFloorCoor = mRepository.getItemFloorCoor(booth);
        if (mFloorCoor == null) {
            return;
        }
        LogUtil.i(TAG, "booth=" + booth + "，mFloorCoor=" + mFloorCoor.getBoothNum());
        x1 = mFloorCoor.getX1();
        x2 = mFloorCoor.getX2();
        y1 = mFloorCoor.getY1();
        y2 = mFloorCoor.getY2();

                     /*  显示flag */
        // 为了让blue flag只显示一个
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap1, 0, 0, null);

        if (blueBitmap == null) {
            blueBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.flag_blue).copy(Bitmap.Config.ARGB_4444, true);
        }

        int flagWidth = blueBitmap.getWidth();
        int flagHeight = blueBitmap.getHeight();

        int centerX = (x2 - x1) / 2 + x1;
        int centerY = (y2 - y1) / 2 + y1;
        LogUtil.i(TAG, "centerX,centerY=" + centerX + "," + centerY);

        // 蓝色标记
        final int blueX = centerX - (flagWidth / 2);// 中心点坐标减去图片宽度的一半，得到marginLeftWidth
        final int blueY = centerY - flagHeight + 3; // 中心点坐标减去图片的高度，得到marginTopHeight
        LogUtil.i(TAG, "blueX,blueY=" + blueX + "," + blueY);
        canvas.drawBitmap(blueBitmap, blueX, blueY, null);
        pvMap.setImageBitmap(newBitmap);

        if (fromCls == 1) {
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    scale = pvMap.getMediumScale();
                    pvMap.setScale(scale, blueX, blueY, false);

                    // 平移
                    touchPX = blueX / bmRatio * scale;
                    touchPY = blueY / bmRatio * scale;
                    LogUtil.i(TAG, "pingyi啦");


                    translate();
                }
            }, 200);
        } else {
            scale = pvMap.getMediumScale();
            pvMap.setScale(scale, blueX, blueY, false);

            // 平移
            touchPX = blueX / bmRatio * scale;
            touchPY = blueY / bmRatio * scale;
            LogUtil.i(TAG, "pingyi啦");
            translate();
        }
        //-------- pad 可用 -----
//        float dx = bmWidth / 2 - blueX;
//        float dy = bmHeight / 2 - blueY;
//        float dy = screenHeight / 2 - blueY;
        //-------- ----- --------

   /*  弹出对话框 (广告不弹出) */
        LogUtil.i(TAG, "找到啦：booth=" + mFloorCoor.getBoothNum());
        exhibitors = mRepository.getBoothExhibitors(mFloorCoor.getBoothNum());
        if (mFragment == null || isDialogCanceled) {
            initFloorDialogFragment();
        } else {
            mFragment.setData(exhibitors);
        }
        isFind = true;

    }

    /**
     * 显示[我的参展商] flag
     * <p>
     * SELECT * FROM FLOOR_PLAN_COORDINATE WHERE BOOTH_NUM IN
     * (select BOOTH_NO FROM EXHIBITOR WHERE COMPANY_ID in ( '234068','234322','234440','234398') and HALL_NO='1H')
     * <p>
     * SELECT * FROM FLOOR_PLAN_COORDINATE WHERE BOOTH_NUM IN (select BOOTH_NO FROM EXHIBITOR WHERE HALL_NO='1H' AND IS_FAVOURITE=1 )
     */
    public void drawFavoFlagsOnMap() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FLOOR_PLAN_COORDINATE WHERE BOOTH_NUM IN (select ")
                .append(ExhibitorDao.Properties.BoothNo.columnName).append(" FROM ").append(ExhibitorDao.TABLENAME)
                .append(" where ").append(ExhibitorDao.Properties.HallNo.columnName).append("='").append(mHall).append("' AND ")
                .append(ExhibitorDao.Properties.IsFavourite.columnName).append("=1)");
        Cursor cursor = App.mDBHelper.db.rawQuery(sql.toString(), null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            x1 = cursor.getInt(cursor.getColumnIndex("X1"));
            x2 = cursor.getInt(cursor.getColumnIndex("X2"));
            y1 = cursor.getInt(cursor.getColumnIndex("Y1"));
            y2 = cursor.getInt(cursor.getColumnIndex("Y2"));

            if (favoBitmap == null) {
                favoBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.flag_red).copy(Bitmap.Config.ARGB_4444, true);
            }
            drawBoothBitmap(favoBitmap, false);
        }
        cursor.close();


    }

    /**
     * @param bm
     * @param isNeedScale 如果是从展商booth过来或突出广告位置，则需要放大并将flag显示在中间   fromCls==1：展商详情booth；
     */
    private void drawBoothBitmap(Bitmap bm, boolean isNeedScale) {
        int flagWidth = bm.getWidth();
        int flagHeight = bm.getHeight();

        int centerX = (x2 - x1) / 2 + x1;
        int centerY = (y2 - y1) / 2 + y1;

        // 中心点坐标减去图片宽度的一半，得到marginLeftWidth
        flagX = centerX - (flagWidth / 2);
        // 中心点坐标减去图片的高度，得到marginTopHeight
        flagY = centerY - flagHeight + 3;
        canvas.drawBitmap(bm, flagX, flagY, null);

        LogUtil.i(TAG, "fromCls=" + fromCls);

        if (isNeedScale && fromCls != 1) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    final float scale = pvMap.getMediumScale();
                    LogUtil.i(TAG, "评议啦:scale=" + scale);
                    pvMap.setImageBitmap(bitmap1);
                    pvMap.setScale(scale, flagX, flagY, false);
                    touchPX = flagX / bmRatio * scale;
                    touchPY = flagY / bmRatio * scale;
                    translate();
                }
            }, 200);

        }


    }

    /**
     * SELECT * FROM FLOOR_PLAN_COORDINATE WHERE BOOTH_NUM IN (select BOOTH_NO FROM EXHIBITOR WHERE COMPANY_ID='237979' )
     *
     * @param companyId
     */
    public void drawAdFlagOnMap(String companyId) {
        LogUtil.i(TAG, "companyId=" + companyId);
        Cursor cursor = App.mDBHelper.db.rawQuery("SELECT * FROM FLOOR_PLAN_COORDINATE WHERE BOOTH_NUM IN (select BOOTH_NO FROM EXHIBITOR WHERE COMPANY_ID = '" + companyId + "')", null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            x1 = cursor.getInt(cursor.getColumnIndex("X1"));
            x2 = cursor.getInt(cursor.getColumnIndex("X2"));
            y1 = cursor.getInt(cursor.getColumnIndex("Y1"));
            y2 = cursor.getInt(cursor.getColumnIndex("Y2"));

            if (adBitmap == null) {
                adBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.flag_star).copy(Bitmap.Config.ARGB_4444, true);
            }
            drawBoothBitmap(adBitmap, true);

        }
        cursor.close();
    }

    @Override
    public void onCancel() {
        isDialogCanceled = true;
    }

    private OnDrawerListener mDrawerListener;

    public void setOnDrawerListener(OnDrawerListener listener) {
        mDrawerListener = listener;
    }

    public interface OnDrawerListener {
        void onDrawerClick(boolean open);
    }

    public void onDestroy() {
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            bitmap1.recycle();
            bitmap1 = null;
        }

        if (newBitmap != null && !newBitmap.isRecycled()) {
            newBitmap.recycle();
            newBitmap = null;
        }

        if (adBitmap != null && !adBitmap.isRecycled()) {
            adBitmap.recycle();
            adBitmap = null;
        }

        if (blueBitmap != null && !blueBitmap.isRecycled()) {
            blueBitmap.recycle();
            blueBitmap = null;
        }

        if (favoBitmap != null && !favoBitmap.isRecycled()) {
            favoBitmap.recycle();
            favoBitmap = null;
        }

        mRepository = null;
    }

//    public void onM4Click(int pos) {
//        mIntentListener.onIntent(pos + "", null);
//    }

    public void onAdClick(int pos){
        mIntentListener.onIntent(pos + "", null);
    }

}
