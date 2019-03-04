package com.adsale.ChinaPlas.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnItemClickCallback;
import com.adsale.ChinaPlas.helper.EPOHelper;
import com.adsale.ChinaPlas.ui.LoginActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.NetWorkHelper;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.App.mLogHelper;

/**
 * Created by Carrie on 2018/1/29.
 * <p>
 * D3: list - insert banner 、 filter - banner
 * D4: list - logo 、 info - product + about + video
 */

public class ExhibitorListAdapter extends CpsBaseAdapter<Exhibitor> {
    private static final String TAG = "ExhibitorListAdapter";
    private Context mContext;
    private ArrayList<Exhibitor> exhibitors;
    private LayoutInflater inflater;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private Exhibitor exhibitor;

    private ExhibitorRepository mRepository;
    private OnItemClickCallback mCallback;

    public final ObservableBoolean isD4Show = new ObservableBoolean(false);

    private ViewDataBinding mBinding;

    public ExhibitorListAdapter(Context context, ArrayList<Exhibitor> lists, ExhibitorRepository repository, OnItemClickCallback callback) {
        this.mContext = context;
        this.exhibitors = lists;
        mRepository = repository;
        this.mCallback = callback;

        inflater = LayoutInflater.from(mContext);

        EPOHelper epoHelper = EPOHelper.getInstance();
        isD4Show.set(epoHelper.isD4Open());
    }

    public void setList(ArrayList<Exhibitor> lists) {
        this.exhibitors = lists;
        LogUtil.e(TAG, "setList_" + exhibitors.size());
        notifyDataSetChanged();
    }

    public void setList2(ArrayList<Exhibitor> lists) {
        this.exhibitors = lists;
    }

    private Exhibitor entity;

    @Override
    protected Object getObjForPosition(int position) {
        mBinding.setVariable(BR.pos, position);
//        entity = exhibitors.get(position);
//        if (entity.isD3Banner.get()) {
//            LogUtil.i(TAG, "getObjForPosition: isD3Banner=" + entity.function + "," + entity.pageID);
//            mLogHelper.logD3(entity.function, entity.pageID, true);
//            mLogHelper.setBaiDuLog(mContext, mLogHelper.getTrackingName());
//        }
        return exhibitors.get(position);
    }

    public void onCollect(Exhibitor exhibitor, int pos) {
        if (!AppUtil.isLogin()) {
            toastLogin(mContext.getString(R.string.login_first_add_exhibitor));
        } else {
            exhibitor.setIsFavourite(exhibitor.getIsFavourite() == 0 ? 1 : 0);
            exhibitor.isCollected.set(exhibitor.getIsFavourite() == 1);
            exhibitors.set(pos, exhibitor);
            mRepository.updateIsFavourite(mContext, exhibitor.getCompanyID(), exhibitor.getIsFavourite());
            LogUtil.i(TAG, "pos=" + pos + "//isCollected=" + exhibitor.isCollected.get() + "//IsFavourite=" + exhibitor.getIsFavourite()
                    + "//IsFavourite2=" + exhibitors.get(pos).getIsFavourite() + ",logo=" + exhibitor.getPhotoFileName());
        }
    }

    private void toastLogin(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Constant.WEB_URL, String.format(NetWorkHelper.MY_CHINAPLAS_URL, AppUtil.getLanguageUrlType()));
                        intent.putExtra(Constant.TITLE, mContext.getString(R.string.title_login));
                        intent.putExtra("ExhibitorInfo", true);
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void onItemClick(Exhibitor exhibitor, int pos) {
        if (mCallback != null) {
            mCallback.onItemClick(exhibitor, pos);
        }
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if (exhibitors.get(position).isD3Banner.get()) {
            return R.layout.item_d3_banner;
        }
        if (position == 0) {
            return R.layout.item_exhi_detail_header;
        } else if (exhibitors.get(position).getSort().equals(exhibitors.get(position - 1).getSort())) {
            return R.layout.item_exhi_detail_child;
        } else {
            return R.layout.item_exhi_detail_header;
        }
    }

    @Override
    protected void bindVariable(ViewDataBinding binding) {
        mBinding = binding;
        binding.setVariable(BR.adapter, this);
        super.bindVariable(binding);
    }

    @Override
    public int getItemCount() {
        return exhibitors.size();
    }

    public void onD3Click(Exhibitor entity, int pos) {
        if (mCallback != null) {
            mCallback.onItemClick(exhibitor, pos);
        }
    }
}
