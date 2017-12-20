package com.adsale.ChinaPlas.adapter;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.base.CpsBaseViewHolder;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ItemLargeMenuBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/10/23.
 * 主界面中间的2行Menus
 */

public class MenuAdapter extends CpsBaseAdapter<MainIcon> {
    private final String TAG = "MenuAdapter";
    /**
     * 所有的大按钮
     */
    private ArrayList<MainIcon> largeMenus;
    /**
     * 所有的小按钮
     */
    private ArrayList<MainIcon> littleMenus;
    /**
     * item inner two menus
     */
    private ArrayList<MainIcon> innerIcons;
    /**
     * 父与子
     */
    private ArrayList<ArrayList<MainIcon>> menus = new ArrayList<>();
    /**
     * 1. 给每个large menu传它的 position值
     * 2. 如果当前点击的位置 mClickPos 与 position 一致，则显示子menu，否则隐藏
     * 这个判断在xml里。
     */
    public final ObservableInt mClickPos = new ObservableInt(-1);
    public final ObservableField<MainIcon> innerMenu0 = new ObservableField<>();
    public final ObservableField<MainIcon> innerMenu1 = new ObservableField<>();

    private ItemLargeMenuBinding menuBinding;
    private Context mContext;
    private final String mBaseUrl;
    private final FrameLayout.LayoutParams largeParams;
    private OnIntentListener mListener;
    private MainIcon littleIcon;
    private NavViewModel navViewModel;
    private final RequestOptions requestOptions;

    public MenuAdapter(Context context, ArrayList<MainIcon> largeMenus, ArrayList<MainIcon> littleMenus, OnIntentListener listener, NavViewModel navViewModel) {
        this.mContext = context;
        this.largeMenus = largeMenus;
        this.littleMenus = littleMenus;
        this.mListener = listener;
        this.navViewModel = navViewModel;

        LogUtil.i(TAG, "largeMenus=" + largeMenus.size() + "," + largeMenus.toString());

        mBaseUrl = NetWorkHelper.DOWNLOAD_PATH.concat("WebContent/");
        int mScreenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        int height = (mScreenWidth * Constant.MAIN_MENU_HEIGHT) / Constant.MAIN_MENU_WIDTH;
        LogUtil.i(TAG, "menu: width=" + mScreenWidth / 3 + ",height=" + height / 3);
        largeParams = new FrameLayout.LayoutParams(mScreenWidth / 3, height / 3);
        generate();
        requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(Constant.MENU_ICON_SIZE * 2, Constant.MENU_ICON_SIZE * 2);
    }

    /**
     * 把每个大按钮中的两个小按钮加入到集合中。父-子
     * position: 0-5，父位置
     * menus.get(position) - 父
     * menus.get(position).get(0) - 儿子0号
     * menus.get(position).get(1) - 儿子1号
     */
    private void generate() {
        int innerSize = littleMenus.size();
        MainIcon largeIcon;
        for (int i = 0; i < largeMenus.size(); i++) {
            largeIcon = largeMenus.get(i);
            innerIcons = new ArrayList<>();/* 必须在里面 */
            for (int j = 0; j < innerSize; j++) {
                littleIcon = littleMenus.get(j);
                if (littleIcon.getMenuList().contains(largeIcon.getMenuList())) {
                    innerIcons.add(littleIcon);
                }
            }
            menus.add(i, innerIcons);
        }
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        binding.setVariable(BR.adapter, this);
        binding.setVariable(BR.navModel, navViewModel);
        super.bindVariable(binding);
        menuBinding = (ItemLargeMenuBinding) binding;
    }

    @Override
    public void onBindViewHolder(CpsBaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        LogUtil.i(TAG, "ICON=" + largeMenus.get(position).getIcon());
        Glide.with(mContext).load(Uri.parse(mBaseUrl.concat(largeMenus.get(position).getIcon())))
                .apply(requestOptions).into(menuBinding.icon);//
        resizeLargeMenu();
    }

    private void resizeLargeMenu() {
        menuBinding.rlLargeMenu.setLayoutParams(largeParams);
    }

    /**
     * 如果inner icon是图片，则使用这个方法。ImageView
     */
    private void setInnerIcon(int pos) {
        if (menus.get(pos).size() > 0) {
//            Glide.with(mContext).load(Uri.parse(mBaseUrl.concat(menus.get(pos).get(0).getIcon()))).into(menuBinding.tvMenu0);
        }
    }

    @Override
    protected Object getObjForPosition(int position) {
        menuBinding.setPos(position);
        menuBinding.executePendingBindings();
        return largeMenus.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.item_large_menu;
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void onLargeMenuClick(MainIcon entity, int pos) {
        LogUtil.i(TAG, "pos=" + pos + ",mClickPos=" + mClickPos.get() + ",bdtj=" + entity.getBaiDu_TJ());
        if (menus.get(pos).size() > 0) {
            mClickPos.set(pos);
            innerMenu0.set(menus.get(pos).get(0));
            innerMenu1.set(menus.get(pos).get(1));
        } else {
            mListener.onIntent(entity, null);
        }
    }

    public void onInnerClick(int index, MainIcon entity) {
        LogUtil.i(TAG, "---onInnerClick: " + index + ", entity=" + entity.getTitle(App.mLanguage.get()) + "," + entity.getBaiDu_TJ());
        mListener.onIntent(entity, null);
    }

}
