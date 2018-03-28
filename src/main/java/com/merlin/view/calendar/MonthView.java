package com.merlin.view.calendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merlin.view.calendar.model.CalendarDayModel;
import com.merlin.view.R;
import com.merlin.view.databinding.MCalendarMonthBinding;

import java.util.List;

/**
 * Created by Administrator on 2018/3/5.
 */

public class MonthView extends ViewGroup {

    private static final int ROW = 6;
    private static final int COLUMN = 7;

    private Context mContext;

    private CalendarHelper mHelper;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
    }

    public void setMonth(CalendarHelper helper, int year, int month) {
        this.mHelper = helper;
        setDayList(helper.getMonthDayList(year, month));
    }

    /**
     * @param dayList
     */
    private void setDayList(List<CalendarDayModel> dayList) {
        if (getChildCount() > 0) {
            removeAllViews();
        }

        for (int i = 0; i < dayList.size(); i++) {
            final CalendarDayModel dayModel = dayList.get(i);

            if (dayModel.getType() == CalendarDayModel.TYPE_LAST_MONTH) {
                if (!mHelper.getAttrsModel().isShowLastNext()) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            if (dayModel.getType() == CalendarDayModel.TYPE_NEXT_MONTH) {
                if (!mHelper.getAttrsModel().isShowLastNext()) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            MCalendarMonthBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.m_calendar_month, null, false);
            binding.setModel(dayModel);
            binding.setDayClickListener(mHelper.getDayClickListener());
            binding.setDayLongClickListener(mHelper.getOnDayLongClickListener());

            addView(binding.getRoot(), i);
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int itemWidth = widthSpecSize / COLUMN;

        //计算日历的最大高度
        if (heightSpecSize > itemWidth * ROW) {
            heightSpecSize = itemWidth * ROW;
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);

        int itemHeight = heightSpecSize / ROW;

        int itemSize = Math.min(itemWidth, itemHeight);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(itemSize, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        View childView = getChildAt(0);
        int itemWidth = childView.getMeasuredWidth();
        int itemHeight = childView.getMeasuredHeight();
        //计算列间距
        int dx = (getMeasuredWidth() - itemWidth * COLUMN) / (COLUMN * 2);

        //当显示五行时扩大行间距
        int dy = 0;
        if (getChildCount() == 35) {
            dy = itemHeight / 5;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int left = i % COLUMN * itemWidth + ((2 * (i % COLUMN) + 1)) * dx;
            int top = i / COLUMN * (itemHeight + dy);
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            view.layout(left, top, right, bottom);
        }
    }

}

