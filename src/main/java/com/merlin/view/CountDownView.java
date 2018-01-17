package com.merlin.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

import com.merlin.core.tool.IHandler;
import com.merlin.core.tool.SafeHandle;

import java.lang.ref.WeakReference;

/**
 * 倒计时控件 | 计数控件
 *
 * @author 17081536
 * @date 2017/11/23.
 */

public class CountDownView extends AppCompatTextView implements IHandler {

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 计数器
     */
    private int mCount = 0;
    /**
     * 计数开始位置
     */
    private int mStart = 0;
    /**
     * 计数结束位置
     */
    private int mEnd = 0;
    /**
     * 计数步长
     */
    private int mStep = 1;
    /**
     * 倒计时结束文本
     */
    private String mEndText;
    /**
     * 倒计时格式
     */
    private String mCountFormat = "%s秒";
    private View.OnClickListener mOnClickListener;
    /**
     * 计数控制器
     */
    private Handler mHandler = new SafeHandle<>(this, this);
    /**
     * 状态：0=初始，1=计数中,2=计数结束
     */
    private int mStatus = 0;


    /**
     * @param startText   开始计数前文本
     * @param endText     计数结束文本
     * @param countFormat 计数时文本格式
     * @param start       计数开始位置
     * @param end         计数结束文职
     * @param step        计数步长
     */
    public void set(String startText, String endText, String countFormat, int start, int end, int step, final View.OnClickListener onClickListener) {
        mStart = start;
        mEnd = end;
        mEndText = endText;
        mCountFormat = countFormat;
        mOnClickListener = onClickListener;

        if (mStep != 0) {
            mStep = step;
        }
        if (startText != null) {
            setText(startText);
        }
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    /**
     * 停止计数
     */
    public void stop() {
        mStatus = 2;
        if (mHandler != null) {
            mHandler.removeMessages(1);
        }
        setEnabled(true);
        setText(mEndText);
    }

    /**
     * 开始
     */
    public void start() {
        mStatus = 1;
        mCount = mStart;
        setEnabled(false);
        if (mHandler != null) {
            mHandler.removeMessages(1);
        }
        mHandler.sendEmptyMessage(1);
        if (mOnClickListener != null) {
            mOnClickListener.onClick(CountDownView.this);
        }
    }

    /**
     * 是否点击过获取验证码按钮
     *
     * @return true=点击过，false=初始状态
     */
    public boolean isClicked() {
        return mStatus > 0;
    }

    /**
     * 是否计数中
     *
     * @return true=计数中，false=未开始或已完成
     */
    public boolean isCounting() {
        return mStatus == 1;
    }

    private void countDown() {
        if (mCount > mEnd) {
            if (mCountFormat != null) {
                setText(String.format(mCountFormat, "" + mCount));
            } else {
                setText("" + mCount);
            }

            mCount = mCount - mStep;
            mHandler.sendEmptyMessageDelayed(1, 1000);
        } else {
            stop();
        }
    }

    @Override
    public void onHandleMessage(Message msg) {
        if (msg.what == 1) {
            countDown();
        }
    }

}
