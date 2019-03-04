package com.adsale.ChinaPlas.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableThumbnailImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ThumbnailImageViewTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

/**
 * Created by Carrie on 2018/11/14.
 */

public class URLImageParser implements Html.ImageGetter {
    ArrayList<Target> targets;
    final TextView mTextView;
    Context mContext;

    public URLImageParser(Context ctx, TextView tv) {
        this.mTextView = tv;
        this.mContext = ctx;
        this.targets = new ArrayList<Target>();
    }

    @Override
    public Drawable getDrawable(String url) {
//        final UrlDrawable urlDrawable = new UrlDrawable();
//        final Target target = new BitmapTarget(urlDrawable);
//        targets.add(target);
//
//        int width = urlDrawable.getIntrinsicWidth();
//        int height = urlDrawable.getIntrinsicHeight();
//        LogUtil.i("URLImageParser", "width=" + width + ",height=" + height);
//        RequestOptions options = new RequestOptions().override(width / 5, height / 5).placeholder(R.drawable.place_holder_grey);
//        Glide.with(mContext).asBitmap().load(url).apply(options).into(target);
//        return urlDrawable;

        LogUtil.i("","url = "+url);

        final UrlDrawable urlDrawable = new UrlDrawable();
        final RequestBuilder load = Glide.with(mContext).asBitmap().thumbnail(0.5f).load(url);
        final Target target = new BitmapTarget(urlDrawable);
        targets.add(target);
        load.into(target);
        return urlDrawable;
    }

    private class BitmapTarget extends SimpleTarget<Bitmap> {

        Drawable drawable;
        private final UrlDrawable urlDrawable;

        public BitmapTarget(UrlDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;
        }

        @Override
        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation) {

            drawable = new BitmapDrawable(mContext.getResources(), resource);

            int width = urlDrawable.getIntrinsicWidth();
            int height = urlDrawable.getIntrinsicHeight();
            LogUtil.i("URLImageParser", " 2 width=" + width + ",height=" + height);

            mTextView.post(new Runnable() {
                @Override
                public void run() {
                    int w = mTextView.getWidth();
                    int hh = drawable.getIntrinsicHeight();
                    int ww = drawable.getIntrinsicWidth();
                    int newHeight = hh * (w) / ww;
                    Rect rect = new Rect(0, 0, w, newHeight);
                    drawable.setBounds(rect);
                    urlDrawable.setBounds(rect);
                    urlDrawable.setDrawable(drawable);
                    mTextView.setText(mTextView.getText());
                    mTextView.invalidate();
                }
            });

        }
    }

    class UrlDrawable extends BitmapDrawable {
        private Drawable drawable;

        @SuppressWarnings("deprecation")
        public UrlDrawable() {
        }

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null)
                drawable.draw(canvas);
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }

}
