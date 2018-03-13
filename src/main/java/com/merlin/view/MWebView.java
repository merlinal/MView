package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * @author merlin
 */

public class MWebView extends WebView {

    public MWebView(Context context) {
        this(context, null);
    }

    public MWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //解决： ViewPager里非首屏WebView点击事件不响应
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
        }
        return super.onTouchEvent(ev);
    }

    public boolean existVerticalScrollbar() {
        return computeVerticalScrollExtent() < computeVerticalScrollRange();
    }

}
