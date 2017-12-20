package com.adsale.ChinaPlas.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.model.MainPic;
import com.adsale.ChinaPlas.databinding.ListItemActionParentBinding;
import com.adsale.ChinaPlas.ui.MainActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.utils.Constant.BDTJ_MY_ACCOUNT;

public class DrawerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "DrawerAdapter";

    /**
     * 总集合：包含父与子
     */
    private ArrayList<MainIcon> lists;
    private ArrayList<MainIcon> mainIcons;

    private static final int TYPE_PARENT = 0;
    private static final int TYPE_CHILD = 1;

    private ChildViewHolder childHolder;
    private ParentViewHolder parentHolder;

    private Context mContext;

    boolean isMatch = false;

    private MainIcon entity;

//    private int uc_count;
    private int mClickPos;

    private boolean isLogin = false;

    private ArrayList<MainIcon> mParents;
    private boolean isPadDevice;
    private NavViewModel mNavViewModel;
    private String mBaseIconUrl;
    private MainIcon mainIcon;
    private ListItemActionParentBinding parentBinding;

    public DrawerAdapter(ArrayList<MainIcon> icons, ArrayList<MainIcon> parentList, NavViewModel baseViewModel) {
        super();
        this.mainIcons = icons;
        this.mParents = parentList;
        this.mNavViewModel = baseViewModel;

        isLogin = mNavViewModel.isLoginSuccess.get();

        LogUtil.e(TAG, "_________LeftMenuAdapter_____________parentList= " + parentList.size() + ",icons=" + icons.size());

        lists = new ArrayList<>();
        lists = mParents;

        MainPic mainPic = Parser.parseJsonFilesDirFile(MainPic.class, Constant.TXT_MAIN_PIC_INFO);
        mBaseIconUrl = mainPic.IconPath;
    }

    public void setLoginChanged() {
        int size = lists.size();
        for (int i = 0; i < size; i++) {
            if (lists.get(i).getBaiDu_TJ().equals(BDTJ_MY_ACCOUNT)) {
                LogUtil.i(TAG, "找到了：" + lists.get(i).getBaiDu_TJ());
                isLogin = AppUtil.isLogin();
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void updateCenterCount(int count) {
//        uc_count = count;
        int size = lists.size();
        LogUtil.i(TAG, "updateCenterCount_uc_count=" + count + ",size=" + size);
        for (int i = 0; i < size; i++) {
            entity = lists.get(i);
            if (entity.getBaiDu_TJ().trim().equals("ContentUpdate")) {
                entity.updateCount.set(count);
                lists.set(i,entity);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mainIcon = lists.get(position);
        parentBinding.setObj(mainIcon);
        parentBinding.executePendingBindings();

        if (holder.getItemViewType() == TYPE_PARENT) {
            parentHolder = (ParentViewHolder) holder;
            parentHolder.tv_parent_name.setText(mainIcon.getTitle(mNavViewModel.mCurrLang.get()));
            if (mainIcon.getDrawerIcon() != null) {
                Glide.with(mContext).load(mBaseIconUrl.concat(mainIcon.getDrawerIcon())).into(parentHolder.ivIcon);
            }

            if (mainIcon.hasChild) {
                if (mainIcon.getBaiDu_TJ().trim().equals("Visitor") && !isLogin) {
                    parentHolder.ib_arrow.setVisibility(View.GONE);
                    if (mainIcon.isExpanded) {
                        LogUtil.e(TAG, "当前预登记正在展开啊，赶紧关闭了");
                        parentHolder.collapseChildItemsNotRefresh(position);
                    }
                } else {
                    parentHolder.ib_arrow.setVisibility(View.VISIBLE);
                    if (mainIcon.isExpanded) {
                        parentHolder.ib_arrow.setBackgroundResource(R.drawable.ic_down_arrow2);
                    } else {
                        parentHolder.ib_arrow.setBackgroundResource(R.drawable.ic_right_arrow2);
                    }
                }
            } else {
                parentHolder.ib_arrow.setVisibility(View.GONE);
            }
        } else if (holder.getItemViewType() == TYPE_CHILD) {
            childHolder = (ChildViewHolder) holder;
            childHolder.tv_child_name.setText(lists.get(position).getTitle(mNavViewModel.mCurrLang.get()));
            childHolder.tv_child_name.setTextColor(mContext.getResources().getColor(R.color.gray_qrcode_tips));
            childHolder.item_child_rl.setBackgroundColor(mContext.getResources().getColor(R.color.white_smoke));
            if (mainIcon.getDrawerIcon() != null) {
                Glide.with(mContext).load(mBaseIconUrl.concat(mainIcon.getDrawerIcon())).into(childHolder.ivItemIcon);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        mContext = group.getContext();
        isLogin = AppUtil.isLogin();
        isPadDevice = AppUtil.isPadDevice(mContext);

        // 分别为 父 与 子 设置布局
        if (viewType == TYPE_PARENT) {
//            View parentView = LayoutInflater.from(group.getContext()).inflate(R.layout.list_item_action_parent, group, false);
            parentBinding = ListItemActionParentBinding.inflate( LayoutInflater.from(group.getContext()),group,false);
            return new ParentViewHolder(parentBinding.getRoot());
        } else {
            View childView = LayoutInflater.from(group.getContext()).inflate(R.layout.list_item_action_child, group, false);
            return new ChildViewHolder(childView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (lists.get(position).getGoogle_TJ().contains("_")) {
            return TYPE_CHILD;
        } else {
            return TYPE_PARENT;
        }
    }

    /**
     * 判断该header是否有子区域,所有元素集合mainIcons与列出来的元素集合lists比较。
     * <p>如果googleTJ包含"_"，说明是子项，如果它的左端和parentGoogleTJ，说明是parentGoogleTJ位置的子项，【插入到这个位置下面 or 从这个位置开始折叠】</>
     *
     * @param googleTJ
     * @param parentGoogleTJ
     * @return
     */
    private boolean isChildSection(String googleTJ, String parentGoogleTJ) {
        return googleTJ.contains("_") && googleTJ.split("_")[0].equals(parentGoogleTJ);
    }

    public class ParentViewHolder extends ViewHolder {
        public ImageButton ib_arrow;
        public TextView tv_parent_name, tv_uc_count;
        private int childSize;
        private Animation animation;
        private ImageView ivIcon;

        public ParentViewHolder(View itemView) {
            super(itemView);

            ib_arrow = (ImageButton) itemView.findViewById(R.id.parent_menu_left_item_arrow);
            tv_parent_name = (TextView) itemView.findViewById(R.id.parent_menu_left_item_tv);
            tv_uc_count = (TextView) itemView.findViewById(R.id.parent_menu_left_item_count);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_drawer_icon);

            ib_arrow.setFocusable(true);
            // 箭头的点击事件
            ib_arrow.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    arrowRotate(ib_arrow, collapseOrExpand(getAdapterPosition()));
                }
            });

            tv_parent_name.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    LogUtil.i(TAG, "点击了XXX");

                    if (lists.get(getAdapterPosition()).hasChild) {
                        if (lists.get(getAdapterPosition()).getBaiDu_TJ().trim().equals("Visitor") && !isLogin) {
                            lists.get(getAdapterPosition()).isClicked = true;
                            closeDrawerAndIntent(lists.get(getAdapterPosition()), getAdapterPosition(), true);
                        } else
                            arrowRotate(ib_arrow, collapseOrExpand(getAdapterPosition()));
                    } else {
                        menuIntent(getAdapterPosition());
                    }
                }
            });
        }

        /**
         * 箭头动画，如果箭头需要往下，则执行向下转的动画；否则，执行右转动画
         *
         * @param arrow
         * @param down
         */
        public void arrowRotate(ImageButton arrow, boolean down) {
            arrow.setBackgroundResource(R.drawable.ic_right_arrow2);
            if (down) {
                animation = AnimationUtils.loadAnimation(mContext, R.anim.arrow_rotate_down);
                LogUtil.e(TAG, "箭头向下↓");
            } else {
                animation = AnimationUtils.loadAnimation(mContext, R.anim.arrow_rotate_right);
                LogUtil.e(TAG, "箭头向→");
            }
            animation.setInterpolator(new LinearInterpolator());
            animation.setFillAfter(true);
            arrow.startAnimation(animation);
        }

        private boolean collapseOrExpand(int position) {
            if (lists.get(position).isExpanded) {
                // 如果当前position的状态为展开的话，点击就折叠
                collapseChildItems(position);
                return false;
            } else {
                // 如果当前position的状态为折叠的话，点击就展开
                expandChildItems(position);
                return true;
            }
        }

        /**
         * 折叠子区域
         */
        public void collapseChildItems(int position) {
            int size2 = mainIcons.size();
            int childSize = 0;
            for (int j = 0; j < size2; j++) {//找子元素
                if (isChildSection(mainIcons.get(j).getGoogle_TJ(), lists.get(position).getGoogle_TJ())) {
                    childSize++;
                }
            }

            int childSize2 = childSize;
            changeExpandState(false, position);
            int size = mainIcons.size();
            for (int i = size; i > 0; i--) {
                if (isChildSection(mainIcons.get(i - 1).getGoogle_TJ(), lists.get(position).getGoogle_TJ())) {
                    lists.remove(position + childSize);// 将要折叠的数据从list中移除掉
                    childSize--;
                }
            }
            notifyItemRangeRemoved(position + 1, childSize2);// 刷新界面
        }

        /**
         * 折叠子区域，为防止出现侧边栏刷新与折叠刷新交叠，一边正在计算一边正在刷新的情况，这儿只将数据移除，不执行刷新操作
         */
        public void collapseChildItemsNotRefresh(int position) {
            LogUtil.i(TAG, "collapseChildItemsNotRefresh_Before:lists=" + lists.size());
            int size2 = mainIcons.size();
            int childSize = 0;
            for (int j = 0; j < size2; j++) {//找子元素
                if (isChildSection(mainIcons.get(j).getGoogle_TJ(), lists.get(position).getGoogle_TJ())) {
                    childSize++;
                }
            }

            int childSize2 = childSize;
            LogUtil.i(TAG, "collapseChildItemsNotRefresh_childSize2=" + childSize2);

            changeExpandState(false, position);
            int size = mainIcons.size();
            for (int i = size; i > 0; i--) {
                if (isChildSection(mainIcons.get(i - 1).getGoogle_TJ(), lists.get(position).getGoogle_TJ())) {
                    lists.remove(position + childSize);// 将要折叠的数据从list中移除掉
                    childSize--;
                    LogUtil.i(TAG, "collapseChildItemsNotRefresh_childSize=" + childSize);
                }
            }
            LogUtil.i(TAG, "collapseChildItemsNotRefresh_After:lists=" + lists.size());
        }

        /**
         * 展开
         */
        private void expandChildItems(int position) {
            changeExpandState(true, position);
            childSize = 0;
            int size = mainIcons.size();
            for (int i = 0; i < size; i++) {
                entity = mainIcons.get(i);
                if (isChildSection(entity.getGoogle_TJ(), lists.get(position).getGoogle_TJ())) {
                    lists.add(position + childSize + 1, entity);// 将要显示的数据添加进来
                    childSize++;
                    LogUtil.i(TAG, "childSize=" + childSize + ",position+childSize+1=" + (position + childSize + 1));
                }
            }
            Log.i("expandChildItems", "lists=" + lists.size());
            notifyItemRangeInserted(position + 1, childSize);//position + 1
        }

        /**
         * 改变状态：当前为展开状态，设为true ； 当前为折叠状态，设为false
         * <p/>
         * 更改lists集合中对应position位置的isExpanded值
         */
        private void changeExpandState(boolean isExpanded, int position) {
            entity = lists.get(position);
            entity.isExpanded = isExpanded;
            lists.set(position, entity);
            LogUtil.i(TAG, "changeExpandState,position=" + position);
        }

    }


    private void menuIntent(final int position) {
        String mBaiduTJ = lists.get(position).getBaiDu_TJ();
        LogUtil.i(TAG, "mBaiduTJ=" + mBaiduTJ);

        int iconSize = mainIcons.size();
        for (int j = 0; j < iconSize; j++) {
            mainIcon = mainIcons.get(j);
            if (mBaiduTJ.equals(mainIcon.getBaiDu_TJ())) {
                isMatch = true;
                mainIcon.isClicked = true;
                mClickPos = j;
                break;
            }
        }

        if (isMatch) {
            closeDrawerAndIntent(mainIcon, mClickPos, false);
        }
    }

    private void closeDrawerAndIntent(final MainIcon mainIcon, final int pos, final boolean isVistor) {
        LogUtil.i(TAG, "mainIcon=" + mainIcon.getBaiDu_TJ() + ",pos=" + pos+","+mainIcon.getTitleCN()+","+mainIcon.getTitleEN()+","+mainIcon.getTitleTW());

        if (isPadDevice) {

        } else {

        }
        mCloseListener.close();
        mNavViewModel.intent((Activity) mContext, mainIcon);
        if (!(mContext instanceof MainActivity)) {
            ((Activity) mContext).finish();
        }


    }

    public class ChildViewHolder extends ViewHolder {
        public TextView tv_child_name;
        private RelativeLayout item_child_rl;
        private ImageView ivItemIcon;

        public ChildViewHolder(View itemView) {
            super(itemView);

            tv_child_name = (TextView) itemView.findViewById(R.id.child_menu_left_item_tv);
            item_child_rl = (RelativeLayout) itemView.findViewById(R.id.item_child_rl);
            ivItemIcon = (ImageView) itemView.findViewById(R.id.iv_drawer_item_icon);

            tv_child_name.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    menuIntent(getAdapterPosition());
                }
            });
        }
    }

    //在 NavViewModel 类中实现这个接口
    public interface OnCloseDrawerListener {
        void close();
    }

    public void setOnCloseDrawerListener(OnCloseDrawerListener listener) {
        AppUtil.checkNotNull(listener);
        mCloseListener = listener;
    }

    private OnCloseDrawerListener mCloseListener;


}
