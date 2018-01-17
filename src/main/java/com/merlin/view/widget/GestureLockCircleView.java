package com.merlin.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * @author merlin
 */

public class GestureLockCircleView extends View {

    /**
     * GestureLockView的三种状态
     */
    public enum Mode {
        STATUS_INIT, STATUS_TOUCH, STATUS_SUCCESS, STATUS_FAILURE
    }

    /**
     * GestureLockView的当前状态
     */
    private Mode mCurrentStatus = Mode.STATUS_INIT;

    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;
    /**
     * 外圆半径
     */
    private int mRadius;
    /**
     * 画笔的宽度
     */
    private int mStrokeWidth = 2;

    /**
     * 圆心坐标
     */
    private int mCenterX;
    private int mCenterY;
    private Paint mPaint;

    /**
     * 内圆的半径 = mInnerCircleRadiusRate * mRadus
     */
    private float mInnerCircleRadiusRate = 0.3F;

    /**
     * 颜色，可由用户自定义，初始化时由GestureLockViewGroup传入
     */
    private int mColorInner; //初始化内圆
    private int mColorOuter; //初始化外圆
    private int mColorTouch; //触摸时
    private int mColorSuccess; //成功时
    private int mColorFailure; //失败时

    public GestureLockCircleView(Context context, int mColorInner, int mColorOuter, int mColorTouch, int mColorSuccess, int mColorFailure) {
        super(context);
        this.mColorInner = mColorInner;
        this.mColorOuter = mColorOuter;
        this.mColorTouch = mColorTouch;
        this.mColorSuccess = mColorSuccess;
        this.mColorFailure = mColorFailure;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 取长和宽中的小值
        mWidth = mWidth < mHeight ? mWidth : mHeight;
        mRadius = mCenterX = mCenterY = mWidth / 2;
        mRadius -= mStrokeWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mCurrentStatus) {
            case STATUS_INIT:
                // 绘制外圆
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mColorOuter);
                break;
            case STATUS_TOUCH:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mColorTouch);
                mPaint.setStrokeWidth(2);
                break;
            case STATUS_SUCCESS:
                mPaint.setColor(mColorSuccess);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(2);
                break;
            case STATUS_FAILURE:
                mPaint.setColor(mColorFailure);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(2);
                break;
            default:
                break;
        }
        //绘制外圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        //绘制内圆
        if (mCurrentStatus == Mode.STATUS_INIT) {
            mPaint.setColor(mColorInner);
        }
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);
    }

    /**
     * 设置当前模式并重绘界面
     *
     * @param mode
     */
    public void setMode(Mode mode) {
        this.mCurrentStatus = mode;
        invalidate();
    }

}
