//package com.adsale.ChinaPlas.adapter;
//
//import android.content.Context;
//import android.databinding.ObservableBoolean;
//import android.databinding.ObservableInt;
//import android.databinding.ViewDataBinding;
//
//import com.adsale.ChinaPlas.R;
//import com.adsale.ChinaPlas.base.CpsBaseAdapter;
//import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
//import com.adsale.ChinaPlas.data.model.DocumentsCenter;
//import com.adsale.ChinaPlas.databinding.ItemDocomentsChildBinding;
//import com.adsale.ChinaPlas.databinding.ItemExhiDetailChildBinding;
//import com.bumptech.glide.Glide;
//
//import java.util.ArrayList;
//
///**
// * Created by Carrie on 2017/10/31.
// */
//
//public class DocumentsItemAdapter extends CpsBaseAdapter<DocumentsCenter.Child> {
//    private ArrayList<DocumentsCenter.Child> list;
//    private Context mContext;
//    private ItemDocomentsChildBinding childBinding;
//    /**  -1:未开始下载；0：点击下载，暂停中；1：正在下载; 2:下载完成 */
//    public final ObservableInt downloadStatus=new ObservableInt(-1);
//
//
//    public DocumentsItemAdapter(ArrayList<DocumentsCenter.Child> list, Context mContext) {
//        this.list = list;
//        this.mContext = mContext;
//    }
//
//    public void setList(ArrayList<DocumentsCenter.Child> list){
//        this.list = list;
//        super.setList(list);
//    }
//
//    public void onDownload(DocumentsCenter.Child entity){
//        downloadStatus.set(1);
//    }
//
//    public void onDelete(int pos){
//
//    }
//
//    public void onRestart(int pos){
//
//    }
//
//    @Override
//    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//        Glide.with(mContext).load(childBinding.getObj().Cover_SC).into(childBinding.ivDcCover);
//    }
//
//    @Override
//    protected void bindVariable(ViewDataBinding binding) {
//        childBinding = (ItemDocomentsChildBinding) binding;
//        childBinding.setAdapter(this);
//        super.bindVariable(binding);
//    }
//
//    @Override
//    protected Object getObjForPosition(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    protected int getLayoutIdForPosition(int position) {
//        return R.layout.item_docoments_child;
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//}
