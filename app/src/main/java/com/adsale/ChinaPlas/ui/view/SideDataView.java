package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.databinding.ViewSideBinding;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/28.
 * 列表侧边bar、Dialog Text、No Data
 */

public class SideDataView extends RelativeLayout implements SideLetter.OnLetterClickListener{
    public final ObservableField<String> dialogLetter = new ObservableField<>();
    public final ObservableBoolean isNoData = new ObservableBoolean(true);
    private SideLetter mSideLetter;

    public SideDataView(Context context) {
        super(context);
        init(context);
    }

    public SideDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SideDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewSideBinding binding = ViewSideBinding.inflate(LayoutInflater.from(context));
        binding.setView(this);
        binding.executePendingBindings();
        mSideLetter = binding.sideLetter;
    }

    public void setupSideLitter(ArrayList<String> letters){
        mSideLetter.setList(letters);
        mSideLetter.setOnLetterClickListener(this);
    }


    @Override
    public void onClick(String letter) {
        dialogLetter.set(letter);
    }
}
