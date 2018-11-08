package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.data.model.DocumentsCenter;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.ACTION_OPEN;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_DOWNLOADING;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_FINISHED;
import static com.adsale.ChinaPlas.data.model.DocumentsCenter.Child.STATUS_NS;


/**
 * Created by Carrie on 2018/1/11.
 * 文档下载中心
 * 更改存储地址为 SD卡。
 */

public class PDFDownloadAdapter extends CpsBaseAdapter<DocumentsCenter.Child> {
    private final String TAG = "PDFDownloadAdapter";
    private ArrayList<ArrayList<DocumentsCenter.Child>> docs = new ArrayList<>();

    /* 父与子共用一个列表 */
    private ArrayList<DocumentsCenter.Child> list = new ArrayList<>();

    public final ObservableInt mClickPos = new ObservableInt(-1);
    public final ObservableBoolean isExpand = new ObservableBoolean(false);

    private ArrayList<DocumentsCenter.Child> children = new ArrayList<>();
    private ViewDataBinding mBinding;
    private OnItemClickCallback mClickCallback;

    public PDFDownloadAdapter(ArrayList<ArrayList<DocumentsCenter.Child>> docs, ArrayList<DocumentsCenter.Child> children, Context mContext, OnItemClickCallback callback) {
        this.docs = docs;
        this.mContext = mContext;
        this.mClickCallback = callback;
        this.list = children;
    }

    public void onParentItemClick(int pos) {
        LogUtil.i(TAG, "pos = " + pos + ",mClickPos=" + mClickPos.get());
        if (mClickPos.get() == -1) {
            insertChild(pos);
        } else if (mClickPos.get() == pos) {
            closeItems();
        } else {
            closeItems();
            insertChild(pos);
        }
    }

    private void insertChild(int pos) {
        isExpand.set(true);
        mClickPos.set(pos);
        children = docs.get(pos);
        list.addAll(pos + 1, children);
        notifyItemRangeInserted(pos + 1, children.size());//position + 1
    }

    private void closeItems() {
        isExpand.set(false);
        list.removeAll(children);
        notifyItemRangeRemoved(mClickPos.get() + 1, children.size());
        mClickPos.set(-1);
    }

    public void onDownload(DocumentsCenter.Child entity) {
        if (entity.downloadStatus.get() == STATUS_FINISHED) {
            mClickCallback.onItemClick(entity, ACTION_OPEN);
        } else {
            mClickCallback.onItemClick(entity, STATUS_DOWNLOADING);
        }
    }

    public void onDelete(DocumentsCenter.Child entity) {
        entity.downloadStatus.set(STATUS_NS);
        entity.cancel();
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        mBinding = binding;
        binding.setVariable(BR.pdfAdapter, this);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        mBinding.setVariable(BR.pos, position);
        mBinding.executePendingBindings();
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if (list.get(position).isParent.get()) {
            return R.layout.item_documents_list;
        }
        return R.layout.item_docoments_child;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
