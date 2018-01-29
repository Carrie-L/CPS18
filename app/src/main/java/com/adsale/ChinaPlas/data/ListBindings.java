package com.adsale.ChinaPlas.data;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/11.
 * 加上前缀（ @BindingAdapter("app:items")  ）就会出现：
 * Error:(23, 28) 警告: Application namespace for attribute app:items will be ignored.
 * 因此，去掉前缀，改为： @BindingAdapter({"items"})
 * <p>
 * 所有列表
 */

public class ListBindings {

//    @BindingAdapter("app:items")

    /**
     * 由于死循环，取消此方法
     *
     * @param recyclerView
     * @param list
     * @param <T>
     */
    @BindingAdapter({"items"})
    public static <T> void setItems(RecyclerView recyclerView, ArrayList<T> list) {
        CpsBaseAdapter<T> adapter = (CpsBaseAdapter<T>) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setList(list);
            if (adapter.getItemCount() == list.size()) {
                LogUtil.i("ListBindings_setItems", "adapter.getItemCount()==list.size()");
                return;
            }
            LogUtil.i("ListBindings_setItems", "setList");
        }
    }

    @BindingAdapter({"imageUrl", "requestOptions"})
    public static void loadListImage(ImageView imageView, String url, RequestOptions requestOptions) {
        Glide.with(imageView.getContext())
                .load(url)
                .apply(requestOptions)
                .thumbnail(0.1f)
                .into(imageView);
    }

    @BindingAdapter({"thumbnailImage"})
    public static void setThumbnailImage(ImageView imageView, String url) {
        if (url != null && !url.trim().isEmpty()) {
            LogUtil.i("setThumbnailImage","thumbnailImage="+url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .thumbnail(0.1f)
                    .into(imageView);
        }

    }

    @BindingAdapter({"imgUrl"})
    public static void setImgUrl(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith("http")) {
            Glide.with(imageView.getContext()).load(Uri.parse(url)).into(imageView);
        } else {
            // 侧边栏按钮：当无网时显示asset下的Icon
            Glide.with(imageView.getContext()).load("file:///android_asset/MainIcon/".concat(url)).into(imageView);
            LogUtil.i("setImgUrl", "menu: asset: " + url);
        }
    }

    @BindingAdapter({"imagePath", "requestOptions"})
    public static void setImagePath(ImageView imageView, String absolutePath, RequestOptions options) {
        LogUtil.i("setImagePath", "absolutePath=" + absolutePath);
        if (TextUtils.isEmpty(absolutePath)) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(new File(absolutePath))
                .apply(options)
                .into(imageView);
    }


}
