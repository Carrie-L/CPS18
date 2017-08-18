package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableInt;

import com.adsale.ChinaPlas.utils.AppUtil;

/**
 * BaseActivity的viewModel
 * Created by Carrie on 2017/8/13.
 */

public class BaseViewModel extends BaseObservable{

    public final ObservableInt language=new ObservableInt(0);

    private Context mContext;

    public BaseViewModel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void changeLanguage(int lang){
        language.set(lang);

        AppUtil.switchLanguage(mContext,language.get());




    }

}
