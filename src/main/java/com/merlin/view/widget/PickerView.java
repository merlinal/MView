package com.merlin.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.merlin.core.tool.IHandler;
import com.merlin.core.tool.SafeHandle;
import com.merlin.core.util.MLog;
import com.merlin.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zal
 */
public class PickerView extends View implements IHandler{

    private Context context;
    /**
     * 新增字段 控制是否首尾相接循环显示 默认为循环显示
     */
//    private boolean loop = false;
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 2.8f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 10;
    private List<?> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint, nPaint;
    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;
    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;
    private int mViewHeight;
    private int mViewWidth;
    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private boolean canScroll = true;
    private onSelectListener mSelectListener;

    private SafeHandle mHandler = new SafeHandle<>(this, this);

    @Override
    public void onHandleMessage(Message message) {
        if (Math.abs(mMoveLen) < SPEED) {
            mMoveLen = 0;
            performSelect();
            mHandler.removeMessages(0);
        } else {
            if (Math.abs(mMoveLen) >= mMaxTextSize && mCurrentSelected > 0 && mCurrentSelected < mDataList.size() - 1) {
                int moveCount = (int) (mMoveLen / mMaxTextSize);
                if (moveCount < 0) {
                    if (mCurrentSelected - moveCount >= mDataList.size() - 1) {
                        mCurrentSelected = mDataList.size() - 1;
                        mMoveLen = mMoveLen + (mDataList.size() - 1 - mCurrentSelected) * mMaxTextSize;
                    } else {
                        mCurrentSelected = mCurrentSelected - moveCount;
                        mMoveLen = mMoveLen % mMaxTextSize;
                    }
                } else {
                    if (mCurrentSelected - moveCount <= 0) {
                        mCurrentSelected = 0;
                        mMoveLen = mMoveLen - mCurrentSelected * mMaxTextSize;
                    } else {
                        mCurrentSelected = mCurrentSelected - moveCount;
                        mMoveLen = mMoveLen % mMaxTextSize;
                    }
                }
            } else {
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            }
            mHandler.sendEmptyMessageDelayed(0, 10);
        }
        invalidate();
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null) {
            mSelectListener.onSelect(this, mCurrentSelected, mDataList.get(mCurrentSelected).toString());
        }
    }

    public void setData(List<?> datas) {
        mDataList = datas;
//        mCurrentSelected = datas.size() / 4;
        invalidate();
    }

    /**
     * 选择选中的item的index
     */
    public void setSelected(int selected) {
        mCurrentSelected = selected;
//        if (loop) {
//            int distance = mDataList.size() / 2 - mCurrentSelected;
//            if (distance < 0) {
//                for (int i = 0; i < -distance; i++) {
//                    moveHeadToTail();
//                    mCurrentSelected--;
//                }
//            } else if (distance > 0) {
//                for (int i = 0; i < distance; i++) {
//                    moveTailToHead();
//                    mCurrentSelected++;
//                }
//            }
//        }
        invalidate();
    }

    /**
     * 选择选中的内容
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                break;
            }
        }
    }

    private void moveHeadToTail() {
//        if (loop) {
//            String head = mDataList.get(0);
//            mDataList.remove(0);
//            mDataList.add(head);
//        }
    }

    private void moveTailToHead() {
//        if (loop) {
//            String tail = mDataList.get(mDataList.size() - 1);
//            mDataList.remove(mDataList.size() - 1);
//            mDataList.add(0, tail);
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        // 按照View的高度计算字体大小
        mMaxTextSize = mViewHeight / 7f;
        mMinTextSize = mMaxTextSize / 2.2f;
        isInit = true;
        invalidate();
    }

    private void init() {
        mDataList = new ArrayList<>();
        //第一个paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setColor(ContextCompat.getColor(context, R.color.primary));
        //第二个paint
        nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Style.FILL);
        nPaint.setTextAlign(Align.CENTER);
        nPaint.setColor(ContextCompat.getColor(context, R.color.label));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit && mDataList.size() > 0) {
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mDataList.get(mCurrentSelected).toString(), x, baseline, mPaint);
        // 绘制上方data
        for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen;
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        nPaint.setTextSize(size);
        nPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        FontMetricsInt fmi = nPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position).toString(),
                (float) (mViewWidth / 2.0), baseline, nPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(0);
                mLastDownY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveLen += (event.getY() - mLastDownY);
                if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
                    if (mCurrentSelected == 0) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    mCurrentSelected--;
                    // 往下滑超过离开距离
                    moveTailToHead();
                    mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
                } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
                    if (mCurrentSelected == mDataList.size() - 1) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    mCurrentSelected++;
                    // 往上滑超过离开距离
                    moveHeadToTail();
                    mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
                }
                mLastDownY = event.getY();
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
                MLog.e(mMoveLen + "/" + getScaleY());
                if (Math.abs(mMoveLen) < 0.0001) {
                    mMoveLen = 0;
                } else {
                    mHandler.sendEmptyMessage(0);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public interface onSelectListener {
        void onSelect(PickerView pickerView, int index, String text);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return canScroll && super.dispatchTouchEvent(event);
    }

    /**
     * 控制内容是否首尾相连
     */
    public void setIsLoop(boolean isLoop) {
//        loop = isLoop;
    }

}