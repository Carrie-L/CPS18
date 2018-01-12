package com.adsale.ChinaPlas.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.M6B;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.ui.NewsDtlActivity;
import com.adsale.ChinaPlas.ui.TechSeminarDtlActivity;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2017/9/13.
 */

public class ADHelper {
    public final String TAG = "ADHelper";
    private Context mContext;
    private static ADHelper INSTANCE;
    private final String AD_TXT = "advertisement.txt";
    private Intent intent;
    private int mLanguage;
    private String mRightUrl;
    private String mCenterUrl;
    private String mLeftUrl;
    private M6B.Topic M6Topic;


    public ADHelper(Context context) {
        mContext = context;
        mLanguage = AppUtil.getCurLanguage();
    }

    private adAdvertisementObj adObj;

    public adAdvertisementObj getAdObj() {
        if (adObj != null) {
            return adObj;
        }
        adObj = Parser.parseJsonFilesDirFile(adAdvertisementObj.class, AD_TXT);
        LogUtil.i(TAG, "adObj=" + adObj.toString());
        return adObj;
    }

    /**
     * todo 每次进入app时都判断 isAdOpen ,然后将结果存到App里。其他广告就直接访问值就可以了。
     *
     * @return
     */
    public boolean setIsAdOpen() {
        getAdObj();
        String todayDate = AppUtil.getCurrentDate();
        String adStartTime = adObj.Common.time.split("-")[0];
        String adEndTime = adObj.Common.time.split("-")[1];
        LogUtil.i(TAG, "todayDate=" + todayDate + ".adStartTime=" + adStartTime + ".adEndTime=" + adEndTime);
        int c1 = todayDate.compareTo(adStartTime);
        int c2 = todayDate.compareTo(adEndTime);
        LogUtil.i(TAG, "c1=" + c1 + ".c2=" + c2);
        if (todayDate.compareTo(adStartTime) > 0 && todayDate.compareTo(adEndTime) < 0) {
            App.mSP_Config.edit().putBoolean(Constant.IS_AD_OPEN, true).apply();
            LogUtil.e(TAG, "~~~ad opening~~");
            return true;
        }
        App.mSP_Config.edit().putBoolean(Constant.IS_AD_OPEN, false).apply();
        App.mSP_Config.edit().putBoolean("M1ShowFinish", true).apply();
        LogUtil.e(TAG, "~~~ad closed~~");
        return false;
    }

    public boolean isAdOpen() {
        return App.mSP_Config.getBoolean(Constant.IS_AD_OPEN, false);
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
                fileLink.delete(0, fileLink.length());
                LogUtil.i(TAG, i + ",,fileLink=" + fileLink.toString());
                fileLink.append(adObj.Common.baseUrl).append(adObj.M1.filepath).append(adCompanyIDs[i])
                        .append("/").append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(languageType).append("_")
                        .append(adObj.M1.version[i]).append(adObj.M1.format);
                adPics[i] = fileLink.toString();
                AppUtil.trackViewLog(201, "Ad", "M1", adCompanyIDs[i]);
                AppUtil.setStatEvent(mContext, "ViewM1", "Ad_M1_" + adCompanyIDs[i]);
            }
            LogUtil.i(TAG, "adPics.length=" + adPics.length);
            LogUtil.e(TAG, "fileLink:" + fileLink.toString());// fileLink:http://www.chinaplasonline.com/apps/2016/advertisement/M1/219369/phone_sc_1.jpg
            fileLink = null;

            LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView adDraweeView;
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
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

    public void showM3(final ImageView imageView) {
        getAdObj();
        if (!isAdOpen()) {
            imageView.setVisibility(View.INVISIBLE);
            return;
        }
        if (Integer.valueOf(adObj.M3.version) == 0) {
            imageView.setVisibility(View.INVISIBLE);
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        boolean isTablet = AppUtil.isTablet();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        int screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        params.width = screenWidth;
        params.height = isTablet ? (screenWidth * 64) / 880 : (screenWidth * 100) / 640;
        imageView.setLayoutParams(params);
//        imageView.setAspectRatio(isPadDevice ? 13.75f : 6.4f);

        StringBuffer m3Url = new StringBuffer();
        m3Url.append(adObj.Common.baseUrl).append(adObj.M3.filepath)
                .append(isTablet ? adObj.Common.tablet : adObj.Common.phone)
                .append(AppUtil.getName("tc", "en", "sc")).append("_").append(adObj.M3.version).append(adObj.M3.format);
        LogUtil.i(TAG, "SHOWM3:URL=" + m3Url.toString());
        Glide.with(imageView.getContext()).load(Uri.parse(m3Url.toString())).apply(requestOptions).into(imageView);

        AppUtil.trackViewLog(203, "Ad", "M3", adObj.M3.getCompanyID(AppUtil.getCurLanguage()));
        AppUtil.setStatEvent(mContext, "ViewM3", "Ad_M3_" + adObj.M3.getCompanyID(AppUtil.getCurLanguage()));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m3Intent();
            }
        });
    }

    public int isM5Show(String companyId) {
        getAdObj();
        int index = -1;
        int length = adObj.M5.companyID.length;
        LogUtil.i(TAG, "companyId=" + companyId);
        if (length == 0) {
            return index;
        }
        for (int i = 0; i < length; i++) {
            if (!adObj.M5.version[i].equals("0") && companyId.equals(adObj.M5.companyID[i])) {
                LogUtil.i(TAG, "adCompanyID=" + adObj.M5.companyID[i]);
                index = i;
                break;
            } else {
                index = -1;
            }
        }
        LogUtil.e(TAG, ",index=" + index);
        return index;
    }

    public void showM5(int index, ImageView leftImg, ImageView centerImg, ImageView rightImg, ImageView logo) {
        getAdObj();
        StringBuilder rootUrl = new StringBuilder();
        rootUrl.append(adObj.Common.baseUrl).append(adObj.M5.filepath).append(adObj.M5.companyID[index]).append("/").append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone);
        LogUtil.i(TAG, "000_rootUrl=" + rootUrl.toString());

        String logoUrl = rootUrl.toString().concat(adObj.M5.logo).concat("_").concat(adObj.M5.version[index]).concat(adObj.M5.format);
        LogUtil.i(TAG, "logoUrl=" + logoUrl);

        mLeftUrl = rootUrl.toString().concat(adObj.M5.product[0]).concat("_").concat(adObj.M5.version[index]).concat(adObj.M5.format);
        LogUtil.i(TAG, "leftUrl=" + mLeftUrl);

        mCenterUrl = rootUrl.toString().concat(adObj.M5.product[1]).concat("_").concat(adObj.M5.version[index]).concat(adObj.M5.format);
        LogUtil.i(TAG, "centerUrl=" + mCenterUrl);

        mRightUrl = rootUrl.toString().concat(adObj.M5.product[2]).concat("_").concat(adObj.M5.version[index]).concat(adObj.M5.format);
        LogUtil.i(TAG, "rightUrl=" + mRightUrl);

        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.scan_mask);
        Glide.with(mContext).load(Uri.parse(logoUrl)).apply(options.override(45, 45)).into(logo);
        Glide.with(mContext).load(Uri.parse(mLeftUrl)).apply(options).into(leftImg);
        Glide.with(mContext).load(Uri.parse(mCenterUrl)).apply(options).into(centerImg);
        Glide.with(mContext).load(Uri.parse(mRightUrl)).apply(options).into(rightImg);

        rootUrl.delete(0, rootUrl.length());
        rootUrl = null;

        AppUtil.trackViewLog(205, "Ad", "M5", adObj.M5.companyID[index]);
        AppUtil.setStatEvent(mContext, "ViewM5", "Ad_M5_".concat(adObj.M5.companyID[index]));

    }

    public String getM5ProductUrl(int pos) {
        return pos == 1 ? mLeftUrl : pos == 2 ? mCenterUrl : mRightUrl;
    }

//    public void showM6(int currIndex) {
//        if (!isAdOpen()) {
//            return;
//        }
//        getAdObj();
//        String url = adObj.Common.baseUrl.concat(adObj.M6.filepath).concat(adObj.M6.companyID[currIndex]).concat("/").concat(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).concat(adObj.M6.logo).concat("_").concat(adObj.M6.version[currIndex])
//                .concat(adObj.M6.format);
//        LogUtil.i(TAG, "M6_url=" + url);
//    }

    public boolean isM6Open(int index) {
        getAdObj();
        return Integer.valueOf(adObj.M6.version[index]) > 0;
    }

    public boolean isShowM6Banner(String id) {
        getAdObj();
        if (!isAdOpen()) {
            return false;
        }
        ArrayList<M6B.Topic> topics = adObj.M6B.topics;
        int size = topics.size();
        for (int i = 0; i < size; i++) {
            if (topics.get(i).id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param index start from 0
     */
    public String getM6LogoUrl(int index) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(adObj.Common.baseUrl).append(adObj.M6B.filepath).append(adObj.M6B.companyID[index]).append("/")
                .append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(adObj.M6B.logo).append("_").append(adObj.M6B.version[index])
                .append(adObj.M6B.format);
        LogUtil.i(TAG, "getM6LogoUrl= " + sbUrl.toString());
        return sbUrl.toString();
    }

    public String getM6HeaderUrl(int index) {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(adObj.Common.baseUrl).append(adObj.M6B.filepath).append(adObj.M6B.companyID[index]).append("/")
                .append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(adObj.M6B.header).append("_").append(adObj.M6B.version[index])
                .append(adObj.M6B.format);
        LogUtil.e(TAG, "getM6HeaderUrl=" + sbUrl.toString());
        return sbUrl.toString();
    }

    public void intentAd(int function, String companyId, String eventId, String seminarId, String newsId) {
        if (function == 1) {
            intentToCompany(companyId);
        } else if (function == 2) {
            intentToCurEvent(eventId);
        } else if (function == 3) {
            intentToSeminar(seminarId, companyId);
        } else if (function == 4) {
            intentToNews(newsId);
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if (AppUtil.isTablet()) {
                ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    public Intent intentToCompany(String adCompanyId) {
        ExhibitorRepository repository = ExhibitorRepository.getInstance();
        if (!repository.isExhibitorIDExists(adCompanyId)) {
            Toast.makeText(mContext, mContext.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
        } else {
            LogUtil.i(TAG, ",companyID=" + adCompanyId);
            intent = new Intent(mContext, ExhibitorDetailActivity.class);
            intent.putExtra("CompanyID", adCompanyId);
            intent.putExtra("title", mContext.getString(R.string.title_exhibitor_deti));
            return intent;
        }
        return null;
    }

    private void intentToCurEvent(String eventId) {
        intent = new Intent(mContext, WebContentActivity.class);
        intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(eventId));
        intent.putExtra("title", mContext.getString(R.string.title_event));
    }

    private void intentToSeminar(String seminarId, String adCompanyId) {
        intent = new Intent(mContext, TechSeminarDtlActivity.class);
        intent.putExtra("ID", seminarId);
        LogUtil.i(TAG, "M:seminarID=" + seminarId);
        intent.putExtra("title", mContext.getString(R.string.title_technical_seminar));
        intent.putExtra("adCompanyID", adCompanyId);
    }

    private void intentToNews(String newsId) {
        intent = new Intent(mContext, NewsDtlActivity.class);
        intent.putExtra("ID", newsId);
        intent.putExtra("title", mContext.getString(R.string.title_news));
    }

    public void m3Intent() {
        if (adObj == null) {
            adObj = getAdObj();
        }
        intentAd(Integer.valueOf(adObj.M3.function), adObj.M3.getCompanyID(mLanguage), adObj.M3.action_eventID, adObj.M3.action_seminarID, adObj.M3.action_newsID);
        AppUtil.trackViewLog( 415, "CA", "M3", adObj.M3.getCompanyID(mLanguage));
        AppUtil.setStatEvent(mContext, "ClickM3", "CA_M3_" + adObj.M3.getCompanyID(mLanguage));
    }

    public void m4Intent(int index) {
        if (adObj == null) {
            adObj = getAdObj();
        }
        intentAd(Integer.valueOf(adObj.M4.function), checkEmpty(adObj.M4.action_companyID, index), checkEmpty(adObj.M4.action_eventID, index), checkEmpty(adObj.M4.action_seminarID, index), checkEmpty(adObj.M4.action_newsID, index));

        AppUtil.trackViewLog(415, "CA", "M4", adObj.M4.action_companyID[index]);
        AppUtil.setStatEvent(mContext, "ClickM4", "CA_M4_" + adObj.M4.action_companyID[index]);
    }

    private String checkEmpty(String[] strings, int index) {
        if (strings != null && strings.length > 0) {
            return strings[index];
        } else {
            return "";
        }
    }

    /**
     * @param mContext
     * @param position 0:LEFT | 1:CENTER | 2:RIGHT
     */
    public void m5Intent(Context mContext, String mCompanyID, int position) {
//        Intent intent = new Intent(mContext, CompanyProductImgActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("title", mContext.getString(R.string.title_exhibitor_deti));
//        intent.putExtra("imgUrl", position == 0 ? mLeftUrl : position == 1 ? mCenterUrl : mRightUrl);
//        mContext.startActivity(intent);
//        if (AppUtil.isTablet()) {
//            ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
        AppUtil.trackViewLog(415, "CA", "M5", mCompanyID);
        AppUtil.setStatEvent(mContext, "ClickM5", "CA_M5_" + mCompanyID);
    }


}
