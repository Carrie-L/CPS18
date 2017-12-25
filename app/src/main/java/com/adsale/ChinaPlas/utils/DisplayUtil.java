package com.adsale.ChinaPlas.utils;

import android.content.Context;

/**
 * dp、sp转换为px的工具类
 * Created by Carrie on 2017/7/28.
 */

public class DisplayUtil {

    public static float getScale(Context context){
       return context.getResources().getDisplayMetrics().density;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context,float pxValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue/fontScale+0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int sp2px(Context context,float pxValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue*fontScale+0.5f);
    }

}
