package com.adsale.ChinaPlas.ui.view;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.adsale.ChinaPlas.BR;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.CpsBaseAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ViewFloorDialogBinding;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2018/1/30.
 * 展馆平面图，点击booth弹出的展商内容对话框
 */

public class FloorDialogFragment extends DialogFragment {
    private final String TAG = "FloorDialogFragment";
    private Window window;
    private View mView;
    private RecyclerView recyclerView;
    private ArrayList<Exhibitor> list = new ArrayList<>();
    private FloorDialogAdapter adapter;
    private OnDialogCancelListener mCancelListener;
    private OnIntentListener mIntentListener;

    public interface OnDialogCancelListener {
        void onCancel();
    }

    public static FloorDialogFragment newInstance(ArrayList<Exhibitor> list) {//
        FloorDialogFragment fragment = new FloorDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", list);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDialogCancelListener(OnDialogCancelListener listener1, OnIntentListener listener2) {
        mCancelListener = listener1;
        mIntentListener = listener2;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /* Make us non-modal, so that others can receive touch events.
        说人话就是 取消默认的 对话框外部区域点击取消对话框事件，而是换成响应事件，不能理解则注释下面两端代码运行看效果。 */
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        ViewFloorDialogBinding binding = ViewFloorDialogBinding.inflate(LayoutInflater.from(getActivity()), container, false);
        mView = binding.getRoot();
        initView();
        return mView;
    }

    private void initView() {
        recyclerView = mView.findViewById(R.id.rv_floor_dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setWindow();
        initData();
    }

    /**
     * 关键是要设置style,继承 android:style/Theme.Dialog ，否则不会生效。
     * {@link #onCreate(Bundle)}
     */
    private void setWindow() {
        window = getDialog().getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = AppUtil.getScreenWidth(); //WindowManager.LayoutParams.MATCH_PARENT
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.BOTTOM;
        window.setAttributes(wl);
    }

    private void initData() {
        list = getArguments().getParcelableArrayList("list");
        adapter = new FloorDialogAdapter(list, mIntentListener);
        recyclerView.setAdapter(adapter);
    }

    public void setData(ArrayList<Exhibitor> list) {
        this.list = list;
        adapter.setList(list);
    }

    public class FloorDialogAdapter extends CpsBaseAdapter<Exhibitor> {
        private ArrayList<Exhibitor> list;
        private OnIntentListener mListener;

        FloorDialogAdapter(ArrayList<Exhibitor> list, OnIntentListener listener) {
            this.list = list;
            this.mListener = listener;
        }

        @Override
        public void setList(ArrayList<Exhibitor> list) {
            this.list = list;
            super.setList(list);
        }

        public void onItemClick(Exhibitor exhibitor) {
            if (mListener != null) {
                mListener.onIntent(exhibitor, ExhibitorDetailActivity.class);
            }
        }

        public void onCollect(Exhibitor exhibitor) {
            exhibitor.setIsFavourite(exhibitor.getIsFavourite() == 1 ? 0 : 1);
            ExhibitorRepository repository = ExhibitorRepository.getInstance();
            repository.updateIsFavourite(mContext,exhibitor.getCompanyID(), exhibitor.getIsFavourite());
        }

        @Override
        protected void bindVariable(ViewDataBinding binding) {
            binding.setVariable(BR.adapter, this);
            super.bindVariable(binding);
        }

        @Override
        protected Object getObjForPosition(int position) {
            return list.get(position);
        }

        @Override
        protected int getLayoutIdForPosition(int position) {
            return R.layout.item_floor_dialog;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


}
