package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableField;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ItemInnerMenuBinding;
import com.adsale.ChinaPlas.utils.LogUtil;

/**
 * Created by Carrie on 2017/10/23.
 */

public class InnerMenuView extends RelativeLayout {
    private final String TAG = "InnerMenuView";
    public final ObservableField<String> menuText = new ObservableField<>();
    private MainIcon mainIcon;
    private OnIntentListener mListener;
    private TextView menum0;
    private TextView menum1;

    public InnerMenuView(Context context) {
        super(context);
        init(context);
    }

    public InnerMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InnerMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ItemInnerMenuBinding binding = ItemInnerMenuBinding.inflate(LayoutInflater.from(context), this, true);
        binding.setView(this);
        menum0 = binding.tvMenu0;
        menum1 = binding.tvMenu1;
    }

//    public void setData(MainIcon mainIcon, OnIntentListener listener) {
//        menuText.set(mainIcon.getTitle());
//        this.mainIcon = mainIcon;
//        mListener = listener;
//        menum0.setText(mainIcon.getTitle());
//    }
//
//    public void setData(MainIcon icon0,MainIcon icon1, OnIntentListener listener){
//        mListener = listener;
//        menum0.setText(icon0.getTitle());
//        menum1.setText(icon1.getTitle());
//    }

    public void destroyView() {

    }

    public void onInnerMenuClick(int pos) {
        if (mListener == null) {
            throw new NullPointerException("mListener cannot be null, please #setData() !");
        }
        mListener.onIntent(mainIcon, null);
        LogUtil.i(TAG, "onInnerMenuClick: " + mainIcon.getBaiDu_TJ());
    }

}
