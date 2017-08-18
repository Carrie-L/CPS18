package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.NameCardDao;
import com.adsale.ChinaPlas.utils.AESCrypt;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adsale.ChinaPlas.R.id.et_company;


/**
 * Created by Carrie on 2017/8/17.
 */

public class NCardViewModel {
    private static final String TAG="NCardViewModel";

    public final ObservableField<String> company = new ObservableField<>();
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> phone1 = new ObservableField<>();
    public final ObservableField<String> phone2 = new ObservableField<>();
    public final ObservableField<String> email = new ObservableField<>();
    public final ObservableField<String> qq = new ObservableField<>();
    public final ObservableField<String> weChat = new ObservableField<>();

    public final ObservableBoolean isCreate = new ObservableBoolean();

    private NameCardDao nameCardDao;
    private Context mContext;
    private final SharedPreferences spNameCard;

    public NCardViewModel(Context context){
        mContext=context.getApplicationContext();

        nameCardDao= App.mDBHelper.mNameCardDao;
        spNameCard = context.getSharedPreferences("MyNameCard", Context.MODE_PRIVATE);
    }

    public void onStart(){
        LogUtil.i(TAG,"onStart: isCreate0="+isCreate.get());
        isCreate.set(spNameCard.getBoolean("isCreate",true));
        LogUtil.i(TAG,"onStart: isCreate1="+isCreate.get());

        if (!isCreate.get()){
            showData();
            decrypt();
//            if(mListener!=null){
//                mListener.setSelection();
//            }
        }
    }

    private void showData(){
        company.set(spNameCard.getString("company",""));
        name.set(spNameCard.getString("name",""));
        title.set(spNameCard.getString("title",""));
        phone1.set(spNameCard.getString("phone1",""));
        phone2.set(spNameCard.getString("phone2",""));
        email.set(spNameCard.getString("email",""));
        qq.set(spNameCard.getString("qq",""));
        weChat.set(spNameCard.getString("weChat",""));
    }


    public void save(){
        boolean isNotNull=false;
        if(mListener!=null){
            isNotNull=  mListener.checkNotNull();
        }
        if(isNotNull){
            isCreate.set(false);
            encrypt();
            saveToSP();
            if(mListener!=null){
                mListener.saved();
            }
        }
    }

    private void saveToSP(){
        LogUtil.i(TAG,"saveToSP: isCreate2="+isCreate.get());
        spNameCard.edit().putBoolean("isCreate",isCreate.get()).putString("company",company.get()).putString("name",name.get()).putString("title",title.get()).putString("phone1",phone1.get()).putString("phone2",phone2.get()).
                putString("email",email.get()).putString("qq",qq.get()).putString("weChat",weChat.get()).apply();
    }



    private void encrypt(){
        try {
            company.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,company.get()));
            name.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,name.get()));
            title.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,title.get()));
            phone1.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,phone1.get()));
            phone2.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,phone2.get()));
            email.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,email.get()));
            if(qq.get()!=null&&!TextUtils.isEmpty(qq.get().trim())){
                qq.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,qq.get()));
            }
            if(weChat.get()!=null&&!TextUtils.isEmpty(weChat.get().trim())){
                weChat.set(AESCrypt.encrypt(AESCrypt.AESPASSWORD,weChat.get()));
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private void decrypt(){
        try {
            company.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,company.get()));
            name.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,name.get()));
            title.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,title.get()));
            phone1.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,phone1.get()));
            phone2.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,phone2.get()));
            email.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,email.get()));
            if(qq.get()!=null&&!TextUtils.isEmpty(qq.get().trim())){
                qq.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,qq.get()));
            }
            if(weChat.get()!=null&&!TextUtils.isEmpty(qq.get().trim())){
                weChat.set(AESCrypt.decrypt(AESCrypt.AESPASSWORD,weChat.get()));
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private void insert(){




    }

    private void update(){
//        nameCardDao.update();
    }

    public interface OnNCSavedListener{
        void setSelection();
        boolean checkNotNull();
        void saved();

    }

    public void setOnNCSavedListener(OnNCSavedListener listener){
        mListener=listener;
    }

    private OnNCSavedListener mListener;
}
