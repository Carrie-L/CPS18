package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.data.DownloadClient;
import com.adsale.ChinaPlas.data.RxInterface;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.databinding.ItemDocomentsChildBinding;
import com.adsale.ChinaPlas.databinding.ItemDocumentsListBinding;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.ReRxUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
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
import static com.adsale.ChinaPlas.utils.FileUtil.createFile;

/**
 * Created by Carrie on 2017/10/31.
 */

public class DocumentsItemTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<DocumentsCenter.Child> list = new ArrayList<>();//列表
    private ArrayList<DocumentsCenter> documentsCenterArrayList;
    private final Integer TYPE_HEADER = 0;
    private final Integer TYPE_ITEM = 1;
    private Context mContext;
    private final LayoutInflater inflater;
    private HeaderViewHolder headerViewHolder;
    private ItemViewHolder itemViewHolder;
    private DownloadClient mClient;
    private final String mDir;
    private Disposable disposable;

    public DocumentsItemTestAdapter(ArrayList<DocumentsCenter> documentsCenterArrayList, Context mContext) {
        this.documentsCenterArrayList = documentsCenterArrayList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        mDir = App.rootDir.concat("DocumentsPDF/");
        createFile(mDir);

        generate();
    }

    /**
     * 把父与子数据都放到一个列表里
     */
    private void generate() {
        DocumentsCenter header;
        DocumentsCenter.Child child;
        int size = documentsCenterArrayList.size();
        for (int i = 0; i < size; i++) {
            header = documentsCenterArrayList.get(i);
            child = new DocumentsCenter.Child();
            child.isHeader = true;
            child.CategoryName = header.getCategoryName();
            list.add(child);

            for (int j = 0; j < header.Items.size(); j++) {
                child = header.Items.get(j);
                child.isHeader = false;
                child.CategoryName = header.getCategoryName();
                list.add(child);
            }
        }
//        LogUtil.i(TAG, "list=" + list.size() + "," + list.toString());
    }

    /**
     * 伸展与收缩
     */
    public void onParentItemClick(DocumentsCenter.Child entity, int pos) {
        if (entity.isExpanded) {
            entity.isExpanded = false;
            list.set(pos, entity);
            for (int i = list.size() - 1; i >= 0; i--) {
                if (!list.get(i).isHeader && list.get(i).CategoryName.equals(entity.CategoryName)) {
                    list.remove(i);
                }
            }
        } else {
            entity.isExpanded = true;
            list.set(pos, entity);
            for (int j = 0; j < documentsCenterArrayList.size(); j++) {
                if (documentsCenterArrayList.get(j).getCategoryName().equals(entity.CategoryName)) {
                    list.addAll(pos + 1, documentsCenterArrayList.get(j).Items);
                    break;
                }
            }
        }
        notifyDataSetChanged(); /* 不能用 itemRangeXX 方法，因为 pos 变了，所以要全部刷新 */
    }


    public void onDownload(DocumentsCenter.Child entity) {
        entity.downloadStatus.set(1);

        download(entity);

    }

    public void onDelete(int pos) {

    }

    public void onRestart(int pos) {

    }

    private void download(DocumentsCenter.Child entity) {
        if (mClient == null) {
            mClient = ReRxUtils.setupRxtrofit(DownloadClient.class, NetWorkHelper.UC_BASE_URL);
        }
        final String url = entity.getFileLink();
        mClient.downUrls(url)
                .map(new Function<Response<ResponseBody>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Response<ResponseBody> response) throws Exception {
                        ResponseBody body = response.body();
                        if (body != null) {
                            LogUtil.i(TAG, "发射文件");
                            FileOutputStream fos = new FileOutputStream(new File(mDir.concat(AppUtil.subStringLast(url, '/'))));
                            fos.write(body.bytes());
                            fos.close();
                            body.close();
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtil.i(TAG, "----------------------------------------》onSubscribe:开始下载");
                        disposable = d;


                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        LogUtil.i(TAG, "onNext:aBoolean=" + aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtil.i(TAG, "onNext:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i(TAG, "onComplete");
                        disposable.dispose();
                    }
                });


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            ItemDocumentsListBinding listBinding = ItemDocumentsListBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(listBinding);
        } else {
            ItemDocomentsChildBinding listBinding = ItemDocomentsChildBinding.inflate(inflater, parent, false);
            return new ItemViewHolder(listBinding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerBinding.setPos(position);
            headerViewHolder.headerBinding.setAdapter(this);
            headerViewHolder.headerBinding.setObj(list.get(position));
            headerViewHolder.headerBinding.executePendingBindings();
        } else {
            itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.childBinding.setPos(position);
            itemViewHolder.childBinding.setAdapter(this);
            itemViewHolder.childBinding.setObj(list.get(position));
            itemViewHolder.childBinding.executePendingBindings();
            Glide.with(mContext).load(itemViewHolder.childBinding.getObj().Cover_SC).into(itemViewHolder.childBinding.ivDcCover);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (list.get(position).CategoryName.equals(list.get(position - 1).CategoryName)) {
            return TYPE_ITEM;
        } else {
            return TYPE_HEADER;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ItemDocumentsListBinding headerBinding;

        HeaderViewHolder(ItemDocumentsListBinding binding) {
            super(binding.getRoot());
            headerBinding = binding;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemDocomentsChildBinding childBinding;

        ItemViewHolder(ItemDocomentsChildBinding binding) {
            super(binding.getRoot());
            childBinding = binding;
        }
    }

}
