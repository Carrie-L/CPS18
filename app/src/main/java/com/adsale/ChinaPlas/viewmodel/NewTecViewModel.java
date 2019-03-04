package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.helper.ViewPagerHelper;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.ui.FloorDetailActivity;
import com.adsale.ChinaPlas.ui.ImageActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.adsale.ChinaPlas.App.mLogHelper;

/**
 * Created by Carrie on 2017/11/28.
 */

public class NewTecViewModel {
    private Context mContext;
    private NewProductInfo entity;
    private FrameLayout mTopFrame;
    private RequestOptions requestOptions;
    private LinearLayout mLlPoint;
    private VideoView videoView;
    private Bitmap bitmap;
    private OnIntentListener mListener;

    private static NewTecViewModel INSTANCE;

    public static NewTecViewModel getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NewTecViewModel(context);
        }
        return INSTANCE;
    }

    private NewTecViewModel(Context mContext) {
        this.mContext = mContext;
    }

    public void setView(FrameLayout frameLayout, NewProductInfo entity, OnIntentListener listener) {
        this.mTopFrame = frameLayout;
        this.entity = entity;
        this.mListener = listener;
    }

    public void setupTop() {
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.place_holder_grey).error(R.drawable.place_holder_grey).fitCenter();
        if (entity.adItem) {
            if (TextUtils.isEmpty(entity.videoLink)) {
                showProductViewPager();
            } else {
                showProductVideo();
            }
        } else {
            showProductImage();
        }
    }

    /**
     * product image, !ad
     */
    public void showProductImage() {
        ImageView imageView = new ImageView(mContext);
        Glide.with(mContext).load(Uri.parse(entity.image)).apply(requestOptions).into(imageView);
        mTopFrame.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onIntent(entity.image, null);
            }
        });
    }

    /**
     * view pager image，only ad product
     */
    private void showProductViewPager() {
        View topView = LayoutInflater.from(mContext).inflate(R.layout.view_pager, null);
        ViewPager viewPager = topView.findViewById(R.id.view_pager);
        mLlPoint = topView.findViewById(R.id.vpindicator);
        mTopFrame.addView(topView);

        final List<View> images = new ArrayList<>();
        final int size = entity.imageLinks.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            Glide.with(mContext).load(entity.imageLinks.get(i)).apply(requestOptions).into(imageView);
            images.add(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i("showProductViewPager", "V=" + v.toString() + ",v.id=" + v.getId());
                    if (v == images.get(0)) {
                        LogUtil.i("", "v==images.get(1)");
                        mLogHelper.logD8(entity.getCompanyID() + "_image1", false);
                        mListener.onIntent(entity.imageLinks.get(0), null);
                    } else if (size > 1 && v == images.get(1)) {
                        LogUtil.i("", "v==images.get(1)");
                        mLogHelper.logD8(entity.getCompanyID() + "_image2", false);
                        mListener.onIntent(entity.imageLinks.get(1), null);
                    } else if (size > 2 && v == images.get(2)) {
                        LogUtil.i("", "v==images.get(1)");
                        mLogHelper.logD8(entity.getCompanyID() + "_image3", false);
                        mListener.onIntent(entity.imageLinks.get(2), null);
                    }
                }
            });
        }

        ViewPagerHelper viewPagerHelper = new ViewPagerHelper(mContext, mLlPoint, viewPager, images);
        viewPagerHelper.generateView(false);

    }

    /**
     * product video，only ad product
     */
    private void showProductVideo() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video, null);
        mTopFrame.addView(view);
//        videoView = view.findViewById(R.id.video_view);
        final ImageView ivPlayer = view.findViewById(R.id.player);
        final ImageView ivFirstFrame = view.findViewById(R.id.iv_first_frame);
        /* 显示第一帧图像 */
        LogUtil.i("showProductVideo", "entity.image=" + entity.image);
        Glide.with(mContext).load(entity.image).into(ivFirstFrame);
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        try {
//            mmr.setDataSource(entity.videoLink, new HashMap<String, String>());
//            bitmap = mmr.getFrameAtTime();
//            ivFirstFrame.setImageBitmap(bitmap);
//        } catch (IllegalArgumentException exception) {
//
//        } finally {
//            mmr.release();
//        }

        if (entity.videoLink == null) {
            return;
        }
        videoView.setVideoURI(Uri.parse(entity.videoLink));
        ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ivPlayer.setVisibility(View.GONE);
//                ivFirstFrame.setVisibility(View.GONE);
                bitmapRecycle();

                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(Constant.WEB_URL, entity.videoLink);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

                mLogHelper.logD11(entity.getCompanyID(), false);


//                videoView.start();
//                videoView.requestFocus();
//                videoView.setMediaController(new MediaController(mTopFrame.getContext()));
            }
        });

    }

    public void onBoothClick(String booth) {
        mListener.onIntent(booth, FloorDetailActivity.class);
    }

    public void onCompanyClick(String companyId) {
        mListener.onIntent(companyId, ExhibitorDetailActivity.class);
    }

    private Bitmap getBm(String url) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(url);
        bitmap = retriever.getFrameAtTime();
        retriever.release();
        return bitmap;
    }

    private void bitmapRecycle() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public void onDestroy() {
        bitmapRecycle();
        if (mTopFrame != null) {
            mTopFrame.removeAllViews();
        }

    }

    public void onImClick(int pos) {
        LogUtil.i("NewTecModel", "onImClick: " + pos);
    }


}
