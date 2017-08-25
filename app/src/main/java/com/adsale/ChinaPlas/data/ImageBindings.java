package com.adsale.ChinaPlas.data;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Carrie on 2017/8/25.
 */

public class ImageBindings {

    @BindingAdapter("android:src")
    public static void setSrc(ImageView imageView, Bitmap bm){
        imageView.setImageBitmap(bm);
    }
}
