package com.adsale.ChinaPlas.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 国家列表侧边的索引bar
 */
public class SideLetter extends View {
    private List<String> arrIndexList = new ArrayList<>();
    private TextPaint paint;
    private OnLetterClickListener mListener;

    public SideLetter(Context context) {
        super(context);
        init(context);
    }

    public SideLetter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SideLetter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setList(List<String> pIndexList) {
        arrIndexList = pIndexList;
    }

    public void refresh() {
        invalidate();
    }

    private void init(Context context) {
        int fontSize = DisplayUtil.dip2px(getContext(), 12);
        paint = new TextPaint();
        paint.setColor(context.getResources().getColor(R.color.home));
        paint.setTextSize(fontSize);
        paint.setStyle(Style.FILL);
//        paint.setTextAlign(Paint.Align.CENTER);
    }

    @SuppressWarnings("deprecation")
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        int intSize = arrIndexList.size();
        if (intSize == 0) {
            return false;
        }
        int idx = i / (getMeasuredHeight() / intSize);
        if (idx >= intSize) {
            idx = intSize - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            setBackgroundResource(R.drawable.scrollbar_bg);
            String letter = arrIndexList.get(idx);
            if (mListener != null) {
                mListener.onClick(letter);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setBackgroundDrawable(new ColorDrawable(0x00000000));
            if (mListener != null) {
                mListener.onClick("");
            }
        }

        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (arrIndexList.isEmpty()) {
            return;
        }
        int y;
        int intSize = arrIndexList.size();
        int itemHeight = getMeasuredHeight() / intSize;
        for (int i = 0; i < intSize; i++) {
            y = (i + 1) * itemHeight;
            float textWidth = paint.measureText(arrIndexList.get(i));
            float width = getMeasuredWidth();
            canvas.drawText(arrIndexList.get(i), textWidth <= width ? (width-textWidth) / 2 : 0, y - (itemHeight / 2), paint); // y-(itemHeight/2):让字母显示在item vertical center
        }
    }

    public interface OnLetterClickListener {
        void onClick(String letter);
    }

    public void setOnLetterClickListener(OnLetterClickListener listener) {
        this.mListener = listener;
    }
}
