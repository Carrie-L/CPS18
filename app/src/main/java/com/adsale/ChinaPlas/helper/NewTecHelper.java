package com.adsale.ChinaPlas.helper;

import android.content.Context;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.adsale.ChinaPlas.App.rootDir;
import static com.adsale.ChinaPlas.utils.AppUtil.getInputStream;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;
import static com.adsale.ChinaPlas.utils.FileUtil.unpackZip;

/**
 * Created by Carrie on 2017/11/16.
 * 处理NewTec的相关事情，例如 下载zip包并解压，保存csv数据到db
 */

public class NewTecHelper {
    private NewTec mNewTecEntity;
    private NewTecRepository mRepository;
    private NewTec newTecEntity;
    private Context mContext;


    public void init() {
        mRepository = NewTecRepository.newInstance();
        mRepository.initDao();
    }

    public NewTec parseNewTecJson() {
        return Parser.parseJsonFilesDirFile(NewTec.class, Constant.TXT_NEW_TEC);
    }

    public void downNewTecZip(LoadingClient client) {
        if (newTecEntity == null) {
            newTecEntity = parseNewTecJson();
        }
        final String name = AppUtil.subStringLast(newTecEntity.NewTechInfoLink, '/');
        if (!name.equals(App.mSP_Config.getString("NewTecZip", ""))) { /*  zip包名字不相同，说明有更新 */
            client.download(newTecEntity.NewTechInfoLink)
                    .map(new Function<ResponseBody, Boolean>() {
                        @Override
                        public Boolean apply(ResponseBody response) throws Exception {
                            String dir = rootDir.concat(Constant.DIR_NEW_TEC);
                            dir = createFile(dir);
                            boolean isUnpackSuccess = FileUtil.unpackZip(name, response.byteStream(), dir);
                            if (isUnpackSuccess) {
                                 /*  解析csv，写入到数据库 (UpdateCenter也需要) */
                                if(mRepository==null){
                                    throw new NullPointerException("mRepository cannot be null,please #init()");
                                }
                                CSVHelper csvHelper = new CSVHelper();
                                csvHelper.initNewTec(mRepository);
                                return csvHelper.readNewProductInfoCSV(getInputStream(Constant.CSV_NEWTEC_PRODUCT_INFO));
                            }
                            return false;
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean bool) throws Exception {
                            LogUtil.i("NewTecHelper", "downNewTecZip: accept=" + bool);
                            if (bool) {
                                App.mSP_Config.edit().putString("NewTecZip", name).apply();
                            }
                        }
                    });
        }

    }

//   private void getAdList(){
//       NewTec newTecEntity = Parser.parseJsonFilesDirFile(NewTec.class, Constant.TXT_NEW_TEC);
//       int size = newTecEntity.adProduct.size();
//       for (int i = 0; i < size; i++) {
//           if (newTecEntity.adProduct.get(i).CompanyID.trim().equals(exhibitor.getCompanyID())) {
//               adProducts.add(newTecEntity.adProduct.get(i));
//           }
//       }
//       LogUtil.i(TAG, "adProducts=" + adProducts.toString());
//       isNewTecEmpty.set(adProducts.isEmpty());
//   }

}
