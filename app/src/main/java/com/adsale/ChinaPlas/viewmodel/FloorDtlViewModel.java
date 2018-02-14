package com.adsale.ChinaPlas.viewmodel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.FloorPlanCoordinate;
import com.adsale.ChinaPlas.data.FloorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.ui.view.FloorDialogFragment;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;


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
    /**
     * 图片原始尺寸
     */
    private int bmWidth;
    private int bmHeight;
    /**
     * 地图在屏幕上显示的宽高
     */
    public float displayWidth;
    public float displayHeight;
    private int screenWidth;
    /**
     * 原始图片——>宽度为屏幕宽度的图片，缩放了多少, 比例
     */
    public float bmRatio;
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
    private Bitmap blueBitmap;
    private Bitmap bitmap;
    private Bitmap bitmap1;

    private float left;
    private float top;
    private float mapCenterX;
    private float mapCenterY;

    public final ObservableBoolean isNavOpened = new ObservableBoolean(false);
    private String mHall;
    private int navWidth;


    public FloorDtlViewModel(Context mContext, FloorRepository repository, FragmentManager fm, OnIntentListener listener, PhotoView pv, String hall) {
        this.mContext = mContext;
        this.mRepository = repository;
        this.mFragmentManager = fm;
        this.mIntentListener = listener;
        this.pvMap = pv;
        this.mHall = hall;

    }

    public void startMap(Bitmap bitmap) {
        getFloorCoors(mHall);
        this.bitmap = bitmap;
//        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.RGB_565, true); // 否则会报错：java.lang.IllegalStateException: Immutable bitmap passed to Canvas constructor
        bmWidth = bitmap.getWidth();
        bmHeight = bitmap.getHeight();
        screenWidth = AppUtil.getScreenWidth();
        bmRatio = (float) bmWidth / (float) screenWidth;
        displayHeight = bmHeight / bmRatio;
        displayWidth = screenWidth;
        LogUtil.i(TAG, "bmWidth=" + bmWidth + ",bmHeight=" + bmHeight + ",bmRatio=" + bmRatio);
        LogUtil.i(TAG, "displayWidth=" + displayWidth + ",displayHeight=" + displayHeight);

        mapCenterX = displayWidth / 2;
        mapCenterY = displayHeight / 2;
        LogUtil.i(TAG, "mapCenterX=" + mapCenterX + ",mapCenterY=" + mapCenterY);

        /* 修改图片，在原图上添加flag。 1.先创建一个原图大小的bitmap1 2.将原图绘制到新的bitmap1上 3.以副本形式修改图片 */
        bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);

        pvMap.setOnPhotoTapListener(new OnPhotoTapListener() {


            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                /* 因为源代码里返回的x,y是除过 displayRect.width() 和 displayRect.height()的，而我们需要的是触摸点坐标，因此这里乘回来。   */
                RectF rectf = pvMap.getDisplayRect();
                LogUtil.i(TAG, "rectf.top=" + rectf.top + ",left=" + rectf.left);
                LogUtil.i(TAG, "rectf.width=" + rectf.width() + ",height=" + rectf.height());

                top = rectf.top;
                left = rectf.left;

                x = x * displayWidth;
                y = y * displayHeight;

                onMapClick(Math.abs(x), Math.abs(y));
            }
        });

    }

    public void onClickDrawer() {
        isNavOpened.set(!isNavOpened.get());
        mDrawerListener.onDrawerClick(isNavOpened.get());
    }

    public void openAnimation(FrameLayout navigationView) {
        isNavOpened.set(true);
        navWidth = navigationView.getWidth();
        ValueAnimator animator = createLeftAnimator(navigationView, 0, DisplayUtil.dip2px(mContext, 250));
        animator.setDuration(300);
        animator.start();
        navigationView.requestLayout();
    }

    private ValueAnimator createLeftAnimator(final FrameLayout navView, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) navView.getLayoutParams();
                layoutParams.width = value;
                navView.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public void closeAnimation(final FrameLayout navigationView) {
        isNavOpened.set(false);
        ValueAnimator animator = createLeftAnimator(navigationView, DisplayUtil.dip2px(mContext, 250), 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                navigationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * @param etContent companyName or boothNo
     */
    public ArrayList<Exhibitor> getEditExhibitors(String etContent) {
        return mRepository.getEditExhibitors(etContent, mHall);
    }

    public void onMapClick(float touchX, float touchY) {

        // touchx,y and bluex,y 是指在这个display地图上的触摸点坐标，相对pvMap来的，不受scale大小的影响。但是rectf受scale影响。
        LogUtil.i(TAG, "onTouch: (" + touchX + "," + touchY + ")");


        float scale = pvMap.getScale();
        LogUtil.i(TAG, "scale=" + scale);

        float mapX = (touchX) * bmRatio; // 在屏幕图片上的触摸点坐标 转换为 原始图片上的坐标 （屏幕图片未放大的情况下）
        float mapY = (touchY) * bmRatio;

        LogUtil.i(TAG, "after: * bmRatio = (" + mapX + "," + mapY + ")");

        isFind = false;
        for (int i = 0; i < size; i++) {
            mFloorCoor = mFloorCoors.get(i);


//            LogUtil.i(TAG,  x + "," + y + ")");
//            LogUtil.i(TAG, "X1= " + mFloorCoor.getX1() + ",X2=" + mFloorCoor.getX2());
//            LogUtil.i(TAG, "Y1= " + mFloorCoor.getY1() + ",Y2=" + mFloorCoor.getY2() + ",sharp=" + mFloorCoor.getSharp());

            if (mapX > mFloorCoor.getX1() && mapX < mFloorCoor.getX2()
                    && mapY > mFloorCoor.getY1() && mapY < mFloorCoor.getY2()) {

                int x1 = mFloorCoor.getX1();
                int x2 = mFloorCoor.getX2();
                int y1 = mFloorCoor.getY1();
                int y2 = mFloorCoor.getY2();

                if (mFloorCoor.getSharp().equals("rect")) {

                     /*  显示flag */

                    // 为了让flag只显示一个
                    canvas = new Canvas(bitmap1);
                    canvas.drawBitmap(bitmap, 0, 0, null);

                    if (blueBitmap == null) {
                        blueBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.flag_blue).copy(Bitmap.Config.ARGB_4444, true);
                    }
                    int flagWidth = blueBitmap.getWidth();
                    int flagHeight = blueBitmap.getHeight();

//                    int centerX = (mFloorCoor.getX1() + (mFloorCoor.getX2() - mFloorCoor.getX1()) / 2) * (bmWidth < 2000 ? 1 : 2);// 中心点x
//                    int centerY = (mFloorCoor.getY1() + (mFloorCoor.getY2() - mFloorCoor.getY1()) / 2) * (bmWidth < 2000 ? 1 : 2);// 中心点y

                    int centerX = (x2 - x1) / 2 + x1;
                    int centerY = (y2 - y1) / 2 + y1;
                    LogUtil.i(TAG, "centerX,centerY=" + centerX + "," + centerY);

                    // 蓝色标记
                    int blueX = centerX - (flagWidth / 2);// 中心点坐标减去图片宽度的一半，得到marginLeftWidth
                    int blueY = centerY - (flagHeight * 2 / 3); // 中心点坐标减去图片的高度，得到marginTopHeight
                    LogUtil.i(TAG, "blueX,blueY=" + blueX + "," + blueY);
                    canvas.drawBitmap(blueBitmap, blueX, blueY, null);


                    // 没有这句flag就显示不出来
                    pvMap.setImageBitmap(bitmap1);
//                    pvMap.setZoomable(true);

                    float blueX1 = blueX / bmRatio;
                    float blueY1 = blueY / bmRatio;
                    LogUtil.i(TAG, "blueX,blueY转化为屏幕坐标点：blueX1=" + blueX1 + ",blueY1=" + blueY1);

                    float swCenterX = displayWidth / 2;
                    float swCenterY = displayHeight / 2;
                    LogUtil.i(TAG, "swCenterX=" + swCenterX + ",swCenterY=" + swCenterY);

                    float mcx = Math.abs(left / scale) + swCenterX / scale;
                    float mcy = Math.abs(top / scale) + swCenterY / scale;
                    LogUtil.i(TAG, ">findin: rectf.top=" + top + ",left=" + left);
                    LogUtil.i(TAG, "要移到的点坐标C': mcx=" + mcx + ",mcy=" + mcy);

                    float dx = mcx - blueX1;
                    float dy = mcy - blueY1;
                    LogUtil.i(TAG, "dx=" + dx + ",dy=" + dy);

                    pvMap.setScale(scale, blueX1, blueY1, false);
//                    pvMap.setScale(scale);

                    Matrix matrix = pvMap.getImageMatrix();
                    matrix.postTranslate(dx * scale, dy * scale);


//                    pvMap.setImageDrawable(new BitmapDrawable(mContext.getResources(), bitmap1));


                    /*  弹出对话框  */
                    LogUtil.i(TAG, "找到啦：booth=" + mFloorCoor.getBoothNum());
                    exhibitors = mRepository.getBoothExhibitors(mFloorCoor.getBoothNum());
                    if (exhibitors.isEmpty()) {
                        break;
                    }
                    if (mFragment == null || isDialogCanceled) {
                        initFloorDialogFragment();
                    } else {
//                        LogUtil.i(TAG, "mFragment setData");
                        mFragment.setData(exhibitors);
                    }
                    isFind = true;

                    break;
                }
            }
        }

        if (!isFind && mFragment != null) {
            mFragment.dismiss();
            isDialogCanceled = true;
        }


    }


//    private void initMeshPostprocessor() {
//        meshPostprocessor = new MeshPostprocessor(mContext, eachHallFloorData, resultList, points, clsType);
//        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(getImgUri()).setPostprocessor(meshPostprocessor)
//                .build();
//        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
//        controller.setImageRequest(imageRequest);
//        controller.setOldController(pvMap.getController());
//        // You need setControllerListener
//        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
//            @Override
//            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
//                super.onFinalImageSet(id, imageInfo, animatable);
//                if (imageInfo == null || pvMap == null) {
//                    return;
//                }
//                pvMap.update(imageInfo.getWidth(), imageInfo.getHeight());
//            }
//        });
//        pvMap.setController(controller.build());
//    }

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
        int x1 = mFloorCoor.getX1();
        int x2 = mFloorCoor.getX2();
        int y1 = mFloorCoor.getY1();
        int y2 = mFloorCoor.getY2();

        if (mFloorCoor.getSharp().equals("rect")) {
                     /*  显示flag */
            // 为了让flag只显示一个
            canvas = new Canvas(bitmap1);
            canvas.drawBitmap(bitmap, 0, 0, null);

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
            int blueY = centerY - (flagHeight * 2 / 3); // 中心点坐标减去图片的高度，得到marginTopHeight
            LogUtil.i(TAG, "blueX,blueY=" + blueX + "," + blueY);
            canvas.drawBitmap(blueBitmap, blueX, blueY, null);

            // 全部换算成在地图上的坐标位置
            float blueX1 = blueX / bmRatio;
            float blueY1 = blueY / bmRatio;
            LogUtil.i(TAG, "blueX,blueY转化为屏幕坐标点：blueX1=" + blueX1 + ",blueY1=" + blueY1);

            // 没有这句flag就显示不出来
            pvMap.setImageBitmap(bitmap1);
            pvMap.setScale(6, blueX1, blueY1, false);

                    /*  弹出对话框  */
            LogUtil.i(TAG, "找到啦：booth=" + mFloorCoor.getBoothNum());
            exhibitors = mRepository.getBoothExhibitors(mFloorCoor.getBoothNum());
            if (mFragment == null || isDialogCanceled) {
                initFloorDialogFragment();
            } else {
                mFragment.setData(exhibitors);
            }
            isFind = true;


            // 平移
            RectF rectf = pvMap.getDisplayRect();
            top = rectf.top;
            left = rectf.left;
            LogUtil.i(TAG, "drawSingleFlagOnMap.top=" + rectf.top + ",left=" + rectf.left);
            LogUtil.i(TAG, "drawSingleFlagOnMap.width=" + rectf.width() + ",height=" + rectf.height());

            float swCenterX = displayWidth / 2;
            float swCenterY = displayHeight / 2;
            float scale = pvMap.getScale();
            LogUtil.i(TAG, "swCenterX=" + swCenterX + ",swCenterY=" + swCenterY + ",scale=" + scale);

            float mcx = Math.abs(left) + swCenterX / scale;
            float mcy = Math.abs(top) + swCenterY / scale;
            LogUtil.i(TAG, ">findin: rectf.top=" + top + ",left=" + left);
            LogUtil.i(TAG, "要移到的点坐标C': mcx=" + mcx + ",mcy=" + mcy);

            float dx = mcx - blueX1;
            float dy = mcy - blueY1;
            LogUtil.i(TAG, "dx=" + dx + ",dy=" + dy);

            pvMap.setScale(scale, blueX1, blueY1, false);
//                    pvMap.setScale(scale);

            Matrix matrix = pvMap.getImageMatrix();
            matrix.postTranslate(dx * scale, dy * scale);


        }

    }

    private void drawSomeFlagsOnMap() {

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

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        if (blueBitmap != null && !blueBitmap.isRecycled()) {
            blueBitmap.recycle();
            blueBitmap = null;
        }
    }

}
