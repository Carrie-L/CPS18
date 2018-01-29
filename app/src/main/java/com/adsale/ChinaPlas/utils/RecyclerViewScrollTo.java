package com.adsale.ChinaPlas.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


/**
 * Created by Carrie on 2017/10/12.
 * recycler view 滚动到指定位置
 */

public class RecyclerViewScrollTo {
    private boolean move = false;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    /**
     * 是否平滑滚动
     */
    private boolean isSmoothScroller = false;

    public RecyclerViewScrollTo(LinearLayoutManager layoutManager, RecyclerView recyclerView) {
        this.mLayoutManager = layoutManager;
        this.recyclerView = recyclerView;
    }

    public void isSmoothScroller(boolean isSmoothScroller) {
        this.isSmoothScroller = isSmoothScroller;
    }

    /**
     * 定位到当前位置，并置顶
     * 平滑滚动
     * @param position 如点击了排序列表的C，position为C在展商列表中的位置
     * @version 创建时间：2016年7月22日 上午11:24:51
     */
    public void scroll(int position) {
        LogUtil.e("RecyclerViewScrollTo", "smoothScroller:position="+position);
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
//		LogUtil.i(TAG, "position=" + position + ",firstItem=" + firstItem + ",lastItem=" + lastItem);

        if (position <= firstItem) {
            // 当要定位的项在当前可见第一项的前面(比如当前第一位在C，要去A)，则可直接移动到最前并置顶
            scrollWay(position);
        } else if (position <= lastItem) {
            int top = recyclerView.getChildAt(position - firstItem).getTop();
            recyclerView.smoothScrollBy(0, top);
        } else {
            // 如果要显示的项在当前最后一项的后面，（如当前最后一项为D，要去E），则移动，并置顶显示
            scrollWay(position);
            move = true;
        }
        scrollListener(position);
    }

    /**
     * 滚动方式：平滑滚动 | 滚动。
     * 想要知道两者的区别，把 isSmoothScroller = true，看滚动效果。
     */
    private void scrollWay(int position) {
        if (isSmoothScroller) {
            recyclerView.smoothScrollToPosition(position);
        } else {
            recyclerView.scrollToPosition(position);
        }
    }

    /**
     * 滚动监听
     */
    private void scrollListener(final int position) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    move = false;
                    int i = position - mLayoutManager.findFirstVisibleItemPosition();
                    if (i >= 0 && i < recyclerView.getChildCount()) {
                        int top = recyclerView.getChildAt(i).getTop();
                        recyclerView.smoothScrollBy(0, top);
                    }
                }
            }
        });
    }
}
