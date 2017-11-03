//package com.adsale.ChinaPlas.adapter;
//
//import android.content.Context;
//import android.databinding.ViewDataBinding;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//
//import com.adsale.ChinaPlas.R;
//import com.adsale.ChinaPlas.base.CpsBaseAdapter;
//import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
//import com.adsale.ChinaPlas.data.model.DocumentsCenter;
//import com.adsale.ChinaPlas.databinding.ItemDocumentsListBinding;
//import com.adsale.ChinaPlas.utils.DisplayUtil;
//import com.adsale.ChinaPlas.utils.LogUtil;
//
//import java.util.ArrayList;
//
//
///**
// * Created by Carrie on 2017/10/31.
// */
//
//public class DocumentsAdapter extends CpsBaseAdapter<DocumentsCenter> {
//    private final String TAG = "DocumentsAdapter";
//    private ArrayList<DocumentsCenter> list;
//    private Context mContext;
//    private ItemDocumentsListBinding listBinding;
//    private RecyclerView recyclerView;
//    private DocumentsItemAdapter itemAdapter;
//
//    public DocumentsAdapter(ArrayList<DocumentsCenter> list, Context mContext) {
//        this.list = list;
//        this.mContext = mContext;
//    }
//
//    public void onParentItemClick(DocumentsCenter entity,int pos) {
//        LogUtil.i(TAG, "onParentItemClick::entity=" + entity.toString());
//        entity.isItemShow.set(!entity.isItemShow.get());
//
//        if (entity.isItemShow.get()) {
//            DocumentsCenter documentsCenter=new DocumentsCenter();
//            documentsCenter.isItemShow.set(true);
//            documentsCenter.isHeaderShow.set(false);
//            list.add(pos+1,documentsCenter);
//
////            itemAdapter.setList(entity.Items);
//
//            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//            recyclerView.setHasFixedSize(true);
//            itemAdapter = new DocumentsItemAdapter(entity.Items, mContext);
//            recyclerView.setAdapter(itemAdapter);
//
//            notifyItemInserted(pos+1);
//
////            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext,200)*entity.Items.size());
////            recyclerView.setLayoutParams(params);
//
//
//
//        }else{
//            list.remove(pos+1);
//            notifyItemRemoved(pos+1);
////            itemAdapter.setList(new ArrayList<DocumentsCenter.Child>(0));
//        }
//
//
//
//    }
//
//    @Override
//    protected void bindVariable(ViewDataBinding binding) {
//        listBinding = (ItemDocumentsListBinding) binding;
//        listBinding.setAdapter(this);
//        super.bindVariable(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//    }
//
//    @Override
//    protected Object getObjForPosition(int position) {
//        recyclerView = listBinding.itemRecyclerView;
//        listBinding.setPos(position);
//        listBinding.executePendingBindings();
//
//        return list.get(position);
//    }
//
//    @Override
//    protected int getLayoutIdForPosition(int position) {
//        return R.layout.item_documents_list;
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//}
