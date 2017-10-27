package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableField;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.LoadingClient;
import com.adsale.ChinaPlas.data.model.UpdateCenterUrl;
import com.adsale.ChinaPlas.databinding.ItemUpdateCenterBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.ReRxUtils;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.adsale.ChinaPlas.App.rootDir;
import static com.adsale.ChinaPlas.R.string.create;
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

public class UpdateCenterView extends RelativeLayout {
    public final ObservableField<UpdateCenter> updateCenter = new ObservableField<>();
    //	public final ObservableField<String> itemName=new ObservableField<>();
    public final ObservableField<String> lastUpdateTime = new ObservableField<>();
//	public final ObservableInt index=new ObservableInt();
    /**
     * 是否有更新，是 true，否 false
     */
//	public final ObservableBoolean hasUpdate = new ObservableBoolean();
    /**
     * Update按钮是否可点击
     */
    public boolean isClickable;
    private Context mContext;
    private ItemUpdateCenterBinding binding;
    private UpdateCenterUrl urlEntity;
    private ArrayList<String> linkUrls = new ArrayList<>();
    private DownloadClient mClient;

    public UpdateCenterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public UpdateCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UpdateCenterView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        binding = ItemUpdateCenterBinding.inflate(LayoutInflater.from(context), this, true);
        binding.setView(this);
    }

    public void setData(UpdateCenter entity) {
        updateCenter.set(entity);
        binding.setObj(updateCenter.get());
        binding.executePendingBindings();
        lastUpdateTime.set(String.format(mContext.getString(R.string.uc_last_update_time), AppUtil.GMT2UTC(updateCenter.get().getLUT())));

    }

    public void onUpdate(long index) {
        LogUtil.i(TAG, "onUpdate:id=" + updateCenter.get().getId() + ",index=" + index);
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.UC_BASE_URL);
        }
        if (index == 1) {
            getUpdateLink0();
            downExhibitor();
        }
    }

    /**
     * 参展商链接
     *
     * @return
     */
    private ArrayList<String> getUpdateLink0() {
        urlEntity = Parser.parseJsonFilesDirFile(UpdateCenterUrl.class, Constant.UC_TXT_EXHIBITOR);
        linkUrls.add(urlEntity.link);
        return linkUrls;
    }

    private void downExhibitor() {
        final String exhibitorDir = rootDir.concat("ExhibitorInfo/");
        createFile(exhibitorDir);
        mClient.downExhibitorCSVs(linkUrls.get(0))
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            FileUtil.unpackZip("20170614.zip", body.byteStream(), exhibitorDir);
                            body.close();
                        }
                        return true; //
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                    }
                });
    }

}
