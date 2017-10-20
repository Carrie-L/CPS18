package com.adsale.ChinaPlas.data;

/**
 * Created by Carrie on 2017/10/15.
 */

public interface OnIntentListener {
    <T> void onIntent(T entity, Class toCls);
}
