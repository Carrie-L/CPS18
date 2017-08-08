package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.widget.Toast;

import static com.adsale.ChinaPlas.utils.AppUtil.checkNotNull;

/**
 * Created by Ponyo on 2017/8/6.
 */

public class LoginViewModel {
    //侧边栏
    public final ObservableField<String> drawerLoginTitle = new ObservableField<>();
    public final ObservableField<String> drawerLogin = new ObservableField<>();
    public final ObservableField<String> drawerLogout = new ObservableField<>();

    public final ObservableBoolean isLoginSuccess = new ObservableBoolean(false);

    //LoginActivity
    public final ObservableField<String> loginName = new ObservableField<>();
    public final ObservableField<String> loginPwd = new ObservableField<>();

    private Context mContext;

    public LoginViewModel(Context context){
        mContext=context.getApplicationContext();
    }

    public void loginAction(){
        Toast.makeText(mContext,loginName.get()+","+loginPwd.get(),Toast.LENGTH_LONG).show();
        isLoginSuccess.set(true);
//        checkNotNull(mListener);
//        mListener.loginSuccess("123");
    }

    private OnLoginSuccessListener mListener;
    public void setOnLoginSuccessListener(OnLoginSuccessListener listener){
        mListener=listener;
    }

    public interface OnLoginSuccessListener{
        void loginSuccess(String vmid);
    }


}
