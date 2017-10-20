package com.adsale.ChinaPlas.ui.view;

import java.util.List;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.utils.DisplayUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 国家列表侧边的索引bar
 */
public class SideLetter extends View {
    private List<String> arrIndexList;
    private Paint paint;
    private OnLetterClickListener mListener;

    public SideLetter(Context context) {
        super(context);
        init();
    }

    public SideLetter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideLetter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setList(List<String> pIndexList) {
        arrIndexList = pIndexList;
    }

    public void refresh(){
        invalidate();
    }

    private void init() {
        // l = new char[] {'#','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
        // 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        // 'W', 'X', 'Y', 'Z'};
        int fontSize = DisplayUtil.dip2px(getContext(), 12);
        paint = new Paint();
        paint.setColor(0xff858c94);
        paint.setTextSize(fontSize);
        paint.setStyle(Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @SuppressWarnings("deprecation")
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        int intSize = arrIndexList.size();
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
        float widthCenter = getMeasuredWidth() / 2;
        if (arrIndexList != null) {
            if (arrIndexList.size() > 1) {
                int intSize = arrIndexList.size();
                int y;
                int height = getMeasuredHeight() / (intSize);
                for (int i = 0; i < intSize; i++) {
                    y = (i + 1) * height;
                    canvas.drawText(arrIndexList.get(i), widthCenter, y, paint);
                }
            } else if (arrIndexList.size() == 1) {
                int height = getMeasuredHeight();
                canvas.drawText(arrIndexList.get(0), widthCenter, height / 2, paint);
            }

        }
        this.invalidate();

    }

    public interface OnLetterClickListener {
        void onClick(String letter);
    }

    public void setOnLetterClickListener(OnLetterClickListener listener) {
        this.mListener = listener;
    }
}
