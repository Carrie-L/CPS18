package com.adsale.ChinaPlas.adapter;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.ui.MainActivity;
import com.adsale.ChinaPlas.ui.PadMainActivity;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/12/20.
 */

public class DrawerListAdapter extends CpsBaseAdapter<MainIcon> {
    private ArrayList<MainIcon> list;
    private ArrayList<MainIcon> leftIcons;
    ArrayList<MainIcon> children = new ArrayList<>();
    private NavViewModel navViewModel;
    private final String TAG = "DrawerListAdapter";
    /**
     * 主要是用来判断这种情况：当 当前已有一个(A)展开，点击另一个有子项的父item(B)时，mClickPos 的值记录上一个父A的位置，因此可以根据mClickPos移除上一个children.
     * 然后在展开B后，将 mClickPos 赋值为B 的position.
     */
    private final ObservableInt mClickPos = new ObservableInt(-1);
    /**
     * 用于在 item_drawer.xml 中判断 arrow 图片。如果 entity.DrawerList = drawerListStr, 展开向下；否则收起向右。
     */
    public final ObservableField<String> drawerListStr = new ObservableField<>("");
    private int mChildFirstPos;

    public DrawerListAdapter(ArrayList<MainIcon> icons, ArrayList<MainIcon> parentList, NavViewModel navViewModel) {
        this.list = parentList;
        this.leftIcons = icons;
        this.navViewModel = navViewModel;
    }

    public void updateCenterCount(int count) {
        int size = list.size();
        LogUtil.i(TAG, "updateCenterCount_uc_count=" + count + ",size=" + size);
        MainIcon entity;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            if (entity.getBaiDu_TJ().trim().equals("ContentUpdate")) {
                entity.updateCount.set(count);
                list.set(i,entity);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void onItemClick(MainIcon entity) {
        LogUtil.i(TAG, "entity =" + entity.getBaiDu_TJ() + ",context = " + mContext.getClass().getSimpleName());
        if (entity.isDrawerHasChild.get()) {
            showChild(entity);
        } else {
            Intent intent = navViewModel.intent((Activity) mContext, entity);
            if (intent != null && (!(mContext instanceof MainActivity) || !(mContext instanceof PadMainActivity) )) {
                ((Activity) mContext).finish();
            }
        }
    }

    private void showChild(MainIcon entity) {
        mChildFirstPos = Integer.valueOf(entity.getDrawerList().split("_")[1]); // 因为 drawerList 的值从1开始，而list position从0开始，因此刚好第一个子项等于父drawerList而无需加1
        if (mClickPos.get() == -1) { // 当前没有展开的，直接添加
            insertChild(entity);
        } else if (mClickPos.get() == mChildFirstPos - 1) { // 点击的和当前展开的一样，就把它收起来
            removeChild();
        } else { // 先关闭上一个展开的，再展开下一个
            removeChild();
            insertChild(entity);
        }
    }

    private void removeChild() {
        list.removeAll(children);
        notifyItemRangeRemoved(mClickPos.get() + 1, children.size());
        mClickPos.set(-1);
        drawerListStr.set("");
    }

    private void insertChild(MainIcon entity) {
        int size = leftIcons.size();
        MainIcon icon;
        children.clear();
        for (int i = 0; i < size; i++) {
            icon = leftIcons.get(i);
            if (icon.getDrawerList().contains(entity.getDrawerList()) && !icon.isDrawerHasChild.get()) { // S_3_1. S_3_1包含S_3，但本身不属于isDrawerHasChild
                icon.isDrawerChild.set(true);
                children.add(icon);
            }
        }
        mClickPos.set(mChildFirstPos - 1);
        list.addAll(mChildFirstPos, children);
        notifyItemRangeInserted(mChildFirstPos, children.size());//position + 1
        drawerListStr.set(entity.getDrawerList());
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.navModel, navViewModel);
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return list.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        LogUtil.i(TAG, "getLayoutIdForPosition");
        if (list.get(position).isDrawerChild.get()) {
            return R.layout.item_drawer_child;
        }
        return R.layout.item_drawer;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


//    class DrawerViewHolder extends RecyclerView.ViewHolder{
//
//        public DrawerViewHolder(View itemView) {
//            super(itemView);
//        }
//    }
}
