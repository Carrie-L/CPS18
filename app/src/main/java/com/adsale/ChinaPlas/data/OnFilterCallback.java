package com.adsale.ChinaPlas.data;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/17.
 */

public interface OnFilterCallback {
    <T> void onSelected(ArrayList<T> list,int itemIndex);
}
