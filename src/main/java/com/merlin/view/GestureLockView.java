package com.merlin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.merlin.view.R;
import com.merlin.view.widget.GestureLockCircleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 整体包含n*n个GestureLockView,每个GestureLockView间间隔mMarginBetweenLockView，
 * 最外层的GestureLockView与容器存在mMarginBetweenLockView的外边距
 * <p>
 * 关于GestureLockView的边长（n*n）： n * mGestureLockViewWidth + ( n + 1 ) *
 * mMarginBetweenLockView = mWidth ; 得：mGestureLockViewWidth = 4 * mWidth / ( 5
 * * mCount + 1 ) 注：mMarginBetweenLockView = mGestureLockViewWidth * 0.25 ;
 *
 * @author zal
 */
public class GestureLockView extends RelativeLayout {

    private static final String TAG = "GestureLockViewGroup";
    /**
     * 保存所有的GestureLockView
     */
    private GestureLockCircleView[] mGestureLockViews;
    /**
     * 每个边上的GestureLockView的个数
     */
    private int mColumns;
    /**
     * 存储答案
     */
    private String mAnswer;
    /**
     * 保存用户选中的GestureLockView的id
     */
    private List<Integer> mChooseList = new ArrayList<>();

    private Paint mPaint;
    /**
     * 每个GestureLockView中间的间距 设置为：mGestureLockViewWidth * 25%
     */
    private int mMarginBetweenLockView = 30;
    /**
     * GestureLockView的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mColumnWidth;

    private int mColorInner; //初始化内圆
    private int mColorOuter; //初始化外圆
    private int mColorTouch; //触摸时
    private int mColorSuccess; //成功时
    private int mColorFailure; //失败时

    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;

    private Path mPath;
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引下的结束位置
     */
    private Point mTmpTarget = new Point();

    /**
     * 最大尝试次数
     */
    private int mMaxTimes = Integer.MAX_VALUE;
    /**
     * 尝试次数
     */
    private int mTryTimes;
    /**
     * 回调接口
     */
    private OnGestureLockViewListener mLockListener;

    private boolean isMatch = false;

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /**
         * 获得所有自定义的参数的值
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GestureLockView, defStyle, 0);
        mColorInner = a.getColor(R.styleable.GestureLockView_colorInitInner, 0xff939090);
        mColorOuter = a.getColor(R.styleable.GestureLockView_colorInitOuter, 0xffE0DBDB);
        mColorTouch = a.getColor(R.styleable.GestureLockView_colorTouch, 0xff378FC9);
        mColorSuccess = a.getColor(R.styleable.GestureLockView_colorSuccess, 0xff159C77);
        mColorFailure = a.getColor(R.styleable.GestureLockView_colorFailure, 0xffFF6347);
        mColumns = a.getInt(R.styleable.GestureLockView_columns, 3);
        mMaxTimes = a.getInt(R.styleable.GestureLockView_maxTime, Integer.MAX_VALUE);
        mColumnWidth = (int) a.getDimension(R.styleable.GestureLockView_columnWidth, 0);

        a.recycle();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        mHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;

        // setMeasuredDimension(mWidth, mHeight);

        // 初始化mGestureLockViews
        if (mGestureLockViews == null) {
            mGestureLockViews = new GestureLockCircleView[mColumns * mColumns];

            if (mColumnWidth < 0) {
                //计算每个GestureLockView的宽度
                mColumnWidth = (int) (4 * mWidth * 1.0f / (5 * mColumns + 1));
                //计算每个GestureLockView的间距
                mMarginBetweenLockView = (int) (mColumnWidth * 0.25);
            } else {
                mMarginBetweenLockView = (mWidth - mColumnWidth * mColumns) / (mColumns + 1);
            }

            // 设置画笔的宽度为GestureLockView的内圆直径稍微小点（不喜欢的话，随便设）
            mPaint.setStrokeWidth(mColumnWidth * 0.29f);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                //初始化每个GestureLockView
                mGestureLockViews[i] = new GestureLockCircleView(getContext(),
                        mColorInner, mColorOuter, mColorTouch, mColorSuccess, mColorFailure);
                mGestureLockViews[i].setId(i + 1);
                //设置参数，主要是定位GestureLockView间的位置
                RelativeLayout.LayoutParams lockerParams = new RelativeLayout.LayoutParams(
                        mColumnWidth, mColumnWidth);

                // 不是每行的第一个，则设置位置为前一个的右边
                if (i % mColumns != 0) {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF,
                            mGestureLockViews[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mColumns - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW,
                            mGestureLockViews[i - mColumns].getId());
                }
                //设置右下左上的边距
                int rightMargin = mMarginBetweenLockView;
                int bottomMargin = mMarginBetweenLockView;
                int leftMargin = 0;
                int topMargin = 0;
                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i >= 0 && i < mColumns) {
                    // 第一行
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mColumns == 0) {
                    // 第一列
                    leftMargin = mMarginBetweenLockView;
                }

                lockerParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                mGestureLockViews[i].setMode(GestureLockCircleView.Mode.STATUS_INIT);
                addView(mGestureLockViews[i], lockerParams);
            }

            Log.i(TAG, "mWidth = " + mWidth + " ,  "
                    + "mGestureViewWidth = " + mColumnWidth + " , "
                    + "mMarginBetweenLockView = " + mMarginBetweenLockView);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 重置
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mColorTouch);
                mPaint.setAlpha(50);
                GestureLockCircleView child = getChildIdByPos(x, y);
                if (child != null) {
                    int cId = child.getId();
                    if (!mChooseList.contains(cId)) {
                        mChooseList.add(cId);
                        child.setMode(GestureLockCircleView.Mode.STATUS_TOUCH);
                        if (mLockListener != null) {
                            mLockListener.onMoveTo(cId);
                        }
                        // 设置指引线的起点
                        mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
                        mLastPathY = child.getTop() / 2 + child.getBottom() / 2;

                        if (mChooseList.size() == 1) {
                            // 当前添加为第一个
                            mPath.moveTo(mLastPathX, mLastPathY);
                        } else {
                            // 非第一个，将两者使用线连上
                            mPath.lineTo(mLastPathX, mLastPathY);
                        }

                    }
                }
                // 指引线的终点
                mTmpTarget.x = x;
                mTmpTarget.y = y;
                break;
            case MotionEvent.ACTION_UP:
                if (mChooseList.size() > 0) {
                    mTryTimes++;
                    Log.i(TAG, "mTryTimes = " + mTryTimes);
                    if (mTryTimes > mMaxTimes) {
                        isMatch = false;
                        if (mLockListener != null) {
                            mLockListener.onResult("", isMatch, true, mTryTimes);
                        }
                        break;
                    }

                    String choose = "";
                    for (Integer id : mChooseList) {
                        choose += id;
                    }
                    isMatch = choose.equals(mAnswer);
                    mPaint.setColor(isMatch ? mColorSuccess : mColorFailure);
                    mPaint.setAlpha(80);

                    // 回调是否成功
                    if (mLockListener != null) {
                        mLockListener.onResult(choose, isMatch, false, mTryTimes);
                    }

                    Log.i(TAG, "mChoose = " + mChooseList);
                    // 将终点设置位置为起点，即取消指引线
                    mTmpTarget.x = mLastPathX;
                    mTmpTarget.y = mLastPathY;

                    // 改变子元素的状态为UP
                    changeItemMode();
                }
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    private void changeItemMode() {
        for (GestureLockCircleView gestureLockView : mGestureLockViews) {
            if (mChooseList.contains(gestureLockView.getId())) {
                gestureLockView.setMode(isMatch ? GestureLockCircleView.Mode.STATUS_SUCCESS : GestureLockCircleView.Mode.STATUS_FAILURE);
            }
        }
    }

    /**
     * 做一些必要的重置
     */
    public void reset() {
        mChooseList.clear();
        mPath.reset();
        for (GestureLockCircleView gestureLockView : mGestureLockViews) {
            gestureLockView.setMode(GestureLockCircleView.Mode.STATUS_INIT);
        }
    }

//    /**
//     * 检查用户绘制的手势是否正确
//     *
//     * @return
//     */
//    private boolean isMatched() {
//        if (mAnswer.length() != mChooseList.size()) {
//            return false;
//        }
//        String choose = "";
//        for (Integer id : mChooseList) {
//            choose += id;
//        }
//        return choose.equals(mAnswer);
//    }

    /**
     * 检查当前左边是否在child中
     *
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y) {
        //设置了内边距，即x,y必须落入下GestureLockView的内部中间的小区域中，可以通过调整padding使得x,y落入范围不变大，或者不设置padding
        int padding = (int) (mColumnWidth * 0.15);

        if (x >= child.getLeft() + padding && x <= child.getRight() - padding
                && y >= child.getTop() + padding
                && y <= child.getBottom() - padding) {
            return true;
        }
        return false;
    }

    /**
     * 通过x,y获得落入的GestureLockView
     *
     * @param x
     * @param y
     * @return
     */
    private GestureLockCircleView getChildIdByPos(int x, int y) {
        for (GestureLockCircleView gestureLockView : mGestureLockViews) {
            if (checkPositionInChild(gestureLockView, x, y)) {
                return gestureLockView;
            }
        }

        return null;

    }

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnGestureLockViewListener(OnGestureLockViewListener listener) {
        this.mLockListener = listener;
    }

    /**
     * 对外公布设置答案的方法
     *
     * @param answer
     */
    public void setAnswer(String answer) {
        this.mAnswer = answer;
    }

    /**
     * 设置最大实验次数
     *
     * @param boundary
     */
    public void setMaxTimes(int boundary) {
        this.mMaxTimes = boundary;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制GestureLockView间的连线
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChooseList.size() > 0) {
            if (mLastPathX != 0 && mLastPathY != 0) {
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x, mTmpTarget.y, mPaint);
            }
        }

    }

    public interface OnGestureLockViewListener {
        /**
         * 选中元素的Id
         *
         * @param id
         */
        void onMoveTo(int id);

        /**
         * 是否匹配
         *
         * @param isMatched
         * @param isReachMaxTime
         * @param currentTime
         */
        void onResult(String result, boolean isMatched, boolean isReachMaxTime, int currentTime);
    }

}
