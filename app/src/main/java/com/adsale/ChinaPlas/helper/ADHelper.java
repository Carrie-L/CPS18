package com.adsale.ChinaPlas.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.adsale.ChinaPlas.App.mSP_Config;

/**
 * Created by Carrie on 2017/9/13.
 */

public class ADHelper {
    public static final String TAG = "ADHelper";
    private Context mContext;
    private static ADHelper INSTANCE;
    private final String AD_TXT = "advertisement_test.txt";

    public static ADHelper getInstance(Context context) {
        if (INSTANCE == null) {
            return new ADHelper(context);
        }
        return INSTANCE;
    }

    public ADHelper(Context context) {
        mContext = context;
    }

    private adAdvertisementObj adObj;

    public void setAdObj(adAdvertisementObj obj) {
        this.adObj = obj;
    }

    public adAdvertisementObj getAdObj() {
        if (adObj != null) {
            return adObj;
        }
        return Parser.parseJsonFilesDirFile(adAdvertisementObj.class, AD_TXT);
    }

    public boolean isM1Open() {
        int size = adObj.M1.version.length;
        for (int i = 0; i < size; i++) {
            if (Integer.valueOf(adObj.M1.version[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    public List<View> generateM1View(LinearLayout viewIndicator) {
        String[] adPics;

        List<View> pagers = new ArrayList<>();
        int mLanguage = AppUtil.getCurLanguage();

        if (adObj == null || adObj.M1 == null) {
            return pagers;
        }
        String[] adCompanyIDs = new String[adObj.M1.action_companyID.size()];
        int change = adObj.M1.isChange;
        if (change != 0) {
            adCompanyIDs = adObj.M1.action_companyID.get(mLanguage);
            LogUtil.i(TAG, "adCompanyID=" + adCompanyIDs.toString());
            String languageType = AppUtil.getLanguageType();

            adPics = new String[change];
            StringBuffer fileLink = new StringBuffer();

            for (int i = 0; i < change; i++) {
                fileLink.delete(0,fileLink.length());
                LogUtil.i(TAG,i+",,fileLink="+fileLink.toString());
                fileLink.append(adObj.Common.baseUrl).append(adObj.M1.filepath).append(adCompanyIDs[i])
                        .append("/").append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(languageType).append("_")
                        .append(adObj.M1.version[i]).append(adObj.M1.format);
                adPics[i] = fileLink.toString();
//                SystemMethod.trackViewLog(context, 201, "Ad", "M1", adCompanyIDs[i]);
//                SystemMethod.setStatEvent(context, "ViewM1", "Ad_M1_" + adCompanyIDs[i], mLanguage);
            }
            LogUtil.i(TAG, "adPics.length=" + adPics.length);
            LogUtil.e(TAG, "fileLink:" + fileLink.toString());// fileLink:http://www.chinaplasonline.com/apps/2016/advertisement/M1/219369/phone_sc_1.jpg
            fileLink = null;

            LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView adDraweeView;
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            String companyId;
            for (int i = 0; i < adPics.length; ++i) {
                img_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                view = inflater.inflate(R.layout.imageview, null);
                adDraweeView = (ImageView) view.findViewById(R.id.image_view);
//                adDraweeView.setImageURI(Uri.parse(adPics[i]));
                Glide.with(mContext).load(adPics[i]).into(adDraweeView);
                adDraweeView.setLayoutParams(img_params);
                pagers.add(adDraweeView);

                LogUtil.e(TAG, "~~~~~~~~~~~~~~~~~~~M1~~~~~~~~~~~~~~~~~~~~~");

                //2017.3.8 增加点击M1跳转到展商详情，companyId为空则不跳转
                if (adObj.M1.function != 0) {
                    onM1Click(adDraweeView, adCompanyIDs[i]);
                }
            }

            // 几个圆点
            if (adPics.length > 1) {
                int width = DisplayUtil.dip2px(mContext, 8);
                LinearLayout.LayoutParams ind_params = new LinearLayout.LayoutParams(width, width);
                ind_params.setMargins(width, width, 0, width * 2);
                ImageView iv;
                for (int i = 0; i < adPics.length; ++i) {
                    iv = new ImageView(mContext);
                    if (i == 0)
                        iv.setBackgroundResource(R.drawable.dot_focused);
                    else
                        iv.setBackgroundResource(R.drawable.dot_normal);
                    iv.setLayoutParams(ind_params);
                    viewIndicator.addView(iv);
                }
            }
        }

        return pagers;
    }

    private void onM1Click(ImageView iv, final String companyId) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(companyId);
            }
        });
    }

    public interface OnM1ClickListener {
        void onClick(String companyId);
    }

    private OnM1ClickListener mListener;

    public void setOnM1ClickListener(OnM1ClickListener listener) {
        mListener = listener;
    }


}
