package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.NameCard;
import com.adsale.ChinaPlas.dao.NameCardDao;
import com.adsale.ChinaPlas.data.NameCardRepository;
import com.adsale.ChinaPlas.utils.AESCrypt;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adsale.ChinaPlas.R.id.et_company;
import static com.adsale.ChinaPlas.R.string.tel;


/**
 * Created by Carrie on 2017/8/17.
 */

public class NCardViewModel {
    private static final String TAG="NCardViewModel";

    public final ObservableField<String> deviceId = new ObservableField<>();
    public final ObservableField<String> company = new ObservableField<>();
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> phone1 = new ObservableField<>();
    public final ObservableField<String> phone2 = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();
    public final ObservableField<String> email = new ObservableField<>();
    public final ObservableField<String> qq = new ObservableField<>();
    public final ObservableField<String> weChat = new ObservableField<>();

    public final ObservableBoolean isCreate = new ObservableBoolean();

    private StringBuffer mQRContents;

    /*全部名片列表*/
    public final ObservableArrayList<NameCard> nameCards=new ObservableArrayList<>();
    public final ObservableBoolean noData = new ObservableBoolean(true);
    private ArrayList<NameCard> mCardsCaches = new ArrayList<>();

    private NameCardRepository mRepository;
    private Context mContext;
    private final SharedPreferences spNameCard;
    private NameCard nameCard;


    public NCardViewModel(Context context){
        mContext=context.getApplicationContext();

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
        mQRContents = new StringBuffer();
        mQRContents.append(AppUtil.getUUID()).append("###").append(company.get()).append("###").append(name.get()).append("###").
                append(title.get()).append("###").append("+").append(phone1.get()).append(phone2.get()).append("###").append(email.get()).append("###").append(qq.get()).append("###").append(weChat.get());

        spNameCard.edit().putBoolean("isCreate",isCreate.get()).putString("company",company.get()).putString("name",name.get()).putString("title",title.get()).putString("phone1",phone1.get()).putString("phone2",phone2.get()).
                putString("email",email.get()).putString("qq",qq.get()).putString("weChat",weChat.get()).putString("my_qrcode",mQRContents.toString()).apply();
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

    public Bitmap createQrCode(String string, BarcodeFormat format) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        Hashtable<EncodeHintType, String> hst = new Hashtable<>();
        hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = writer.encode(string, format, 700, 700, hst);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        LogUtil.i(TAG,"qr_code_width="+width+",height="+height);

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
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


    /*-----------NCardList--------------------*/
    public void onListStart(){
        mRepository= new NameCardRepository();
        nameCards.addAll(mRepository.getData());
        noData.set(nameCards.isEmpty());
    }

    public void exportBsCard(){

    }

    public void resetList(){
        nameCards.clear();
        nameCards.addAll(mCardsCaches);
    }

    public void search(String text){
        nameCards.clear();
        nameCards.addAll(mRepository.getSearchData(text));
        noData.set(nameCards.isEmpty());
    }

    /*---------------------ScanDtl--------------*/
      public void getNCInfo(String nameCardInfo){
        deviceId.set(nameCardInfo.split("###")[0]);
        company.set(nameCardInfo.split("###")[1]);
        name.set(nameCardInfo.split("###")[2]);
        title.set(nameCardInfo.split("###")[3]);
        phone.set(nameCardInfo.split("###")[4]);
        email.set(nameCardInfo.split("###")[5]);
        qq.set(nameCardInfo.split("###")[6]);
        weChat.set(nameCardInfo.split("###")[7]);
    }

    private boolean isNameCardExisits(){
       return mRepository.isNameCardExisits(deviceId.get());
    }

    private void addToList(){
        nameCard = new NameCard(deviceId.get(),company.get(),name.get(),title.get(),phone.get(),email.get(),qq.get(),weChat.get(), AppUtil.getCurrentTime());
        if(isNameCardExisits()){
            updateNameCard();
        }else{
            insertNameCard();
        }
    }

    private void updateNameCard(){
        mRepository.updateItemData(nameCard);
    }

    private void insertNameCard(){
        //String DeviceId, String Company, String Name, String Title, String Phone, String Email, String QQ,String WeChat, String UpdateDateTime
        mRepository.insertItemData(nameCard);
    }

    public void delete(){
        mRepository.deleteItemData();
    }


}
