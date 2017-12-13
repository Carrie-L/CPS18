package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
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
import android.widget.VideoView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.helper.ViewPagerHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public NewTecViewModel(Context mContext, FrameLayout frameLayout, NewProductInfo entity) {
        this.mContext = mContext;
        this.entity = entity;
        this.mTopFrame = frameLayout;
    }

    public void setupTop() {
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).fitCenter();
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
    }

    /**
     * view pager image，only ad product
     */
    private void showProductViewPager() {
        View topView = LayoutInflater.from(mContext).inflate(R.layout.view_pager, null);
        ViewPager viewPager = topView.findViewById(R.id.view_pager);
        mLlPoint = topView.findViewById(R.id.vpindicator);
        mTopFrame.addView(topView);

        List<View> images = new ArrayList<>();
        int size = entity.imageLinks.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            Glide.with(mContext).load(entity.imageLinks.get(i)).apply(requestOptions).into(imageView);
            images.add(imageView);
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
        videoView = view.findViewById(R.id.video_view);
        final ImageView ivPlayer = view.findViewById(R.id.player);
        final ImageView ivFirstFrame = view.findViewById(R.id.iv_first_frame);
        /* 显示第一帧图像 */
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(entity.videoLink, new HashMap<String, String>());
            bitmap = mmr.getFrameAtTime();
            ivFirstFrame.setImageBitmap(bitmap);
        } catch (IllegalArgumentException exception) {

        } finally {
            mmr.release();
        }
        videoView.setVideoURI(Uri.parse(entity.videoLink));
        ivPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlayer.setVisibility(View.GONE);
                ivFirstFrame.setVisibility(View.GONE);
                bitmapRecycle();
                videoView.start();
                videoView.requestFocus();
                videoView.setMediaController(new MediaController(mTopFrame.getContext()));
            }
        });

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
        mTopFrame.removeAllViews();
    }


}
