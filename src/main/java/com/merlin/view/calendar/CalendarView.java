package com.merlin.view.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.merlin.view.calendar.listener.OnDayLongClickListener;
import com.merlin.view.calendar.listener.OnMonthChangeListener;
import com.merlin.view.calendar.listener.OnSelectedListener;
import com.merlin.view.calendar.model.CalendarAttrsModel;
import com.merlin.view.calendar.util.CalendarUtil;
import com.merlin.view.R;
import com.merlin.view.calendar.util.SolarUtil;

import java.util.Calendar;
import java.util.Map;

/**
 * @author merlin
 */

public class CalendarView extends ViewPager {

    private CalendarAttrsModel mAttrsModel;
    private CalendarHelper mHelper;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAttrsModel = new CalendarAttrsModel();
        mHelper = new CalendarHelper();
        mHelper.setAttrsModel(mAttrsModel);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.CalendarView_show_last_next) {
                mAttrsModel.setShowLastNext(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_lunar) {
                mAttrsModel.setShowLunar(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_holiday) {
                mAttrsModel.setShowHoliday(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_term) {
                mAttrsModel.setShowTerm(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_choose_switch) {
                mAttrsModel.setChooseSwitch(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_solar_color) {
                mAttrsModel.setColorSolar(ta.getColor(attr, mAttrsModel.getColorSolar()));
            } else if (attr == R.styleable.CalendarView_solar_size) {
                mAttrsModel.setSizeSolar(CalendarUtil.getTextSize(context, ta.getDimensionPixelSize(attr, mAttrsModel.getSizeSolar())));
            } else if (attr == R.styleable.CalendarView_lunar_color) {
                mAttrsModel.setColorLunar(ta.getColor(attr, mAttrsModel.getColorLunar()));
            } else if (attr == R.styleable.CalendarView_lunar_size) {
                mAttrsModel.setSizeLunar(CalendarUtil.getTextSize(context, ta.getDimensionPixelSize(attr, mAttrsModel.getSizeLunar())));
            } else if (attr == R.styleable.CalendarView_holiday_color) {
                mAttrsModel.setColorSpecify(ta.getColor(attr, mAttrsModel.getColorSpecify()));
            } else if (attr == R.styleable.CalendarView_choose_color) {
                mAttrsModel.setChooseColor(ta.getColor(attr, mAttrsModel.getChooseColor()));
            } else if (attr == R.styleable.CalendarView_choose_bg) {
                mAttrsModel.setChooseBg(ta.getResourceId(attr, mAttrsModel.getChooseBg()));
            } else if (attr == R.styleable.CalendarView_choose_type) {
                mAttrsModel.setChooseType(ta.getInt(attr, CalendarAttrsModel.TYPE_CHOOSE_NONE));
            } else if (attr == R.styleable.CalendarView_start_date) {
                mAttrsModel.setStartDate(ta.getString(attr));
            } else if (attr == R.styleable.CalendarView_end_date) {
                mAttrsModel.setEndDate(ta.getString(attr));
            } else if (attr == R.styleable.CalendarView_init_date) {
                mAttrsModel.setInitDate(ta.getString(attr));
            }
        }

        ta.recycle();
        if (mAttrsModel.getChooseType() != CalendarAttrsModel.TYPE_CHOOSE_NONE) {
            mAttrsModel.setShowLastNext(false);
        }
        if (mAttrsModel.getStartDate() == null) {
            mAttrsModel.setStartDate("1900-01");
        }
        if (mAttrsModel.getEndDate() == null) {
            mAttrsModel.setEndDate("2049-12");
        }
        if (mAttrsModel.getInitDate() == null) {
            Calendar c = Calendar.getInstance();
            mAttrsModel.setInitDate(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1));
        }
    }

    private void init() {
        mHelper.refresh();
        setAdapter(new CalendarPagerAdapter(mHelper));
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mHelper.setCurrentPosition(position);
            }
        });
        setCurrentItem(mHelper.getCurrentPosition(), true);
    }

    public void notifyDataChanged() {
        init();
    }

    /**
     * @param startDate yyyy-MM
     * @param endDate   yyyy-MM
     * @param initDate  yyyy-MM
     */
    public void set(String startDate, String endDate, String initDate) {
        if (!TextUtils.isEmpty(startDate)) {
            mAttrsModel.setStartDate(startDate);
        }
        if (!TextUtils.isEmpty(endDate)) {
            mAttrsModel.setEndDate(endDate);
        }
        if (!TextUtils.isEmpty(initDate)) {
            mAttrsModel.setInitDate(initDate);
        }
    }

    /**
     * 计算 ViewPager 高度
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int calendarHeight;
        if (getAdapter() != null) {
            MonthView view = (MonthView) getChildAt(0);
            if (view != null) {
                calendarHeight = view.getMeasuredHeight();
                setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(calendarHeight, MeasureSpec.EXACTLY));
            }
        }
    }

    public void setMonthChangeListener(OnMonthChangeListener monthChangeListener) {
        mHelper.setMonthChangeListener(monthChangeListener);
    }

    public void setDayLongClickListener(OnDayLongClickListener dayLongClickListener) {
        mHelper.setOnDayLongClickListener(dayLongClickListener);
    }

    public void setSelectedListener(OnSelectedListener selectedListener) {
        mHelper.setOnSelectedListener(selectedListener);
    }

    /**
     * 单选时跳转到今天
     */
    public void today() {
        setCurrentItem(mHelper.getTodayPosition(), true);
    }

    /**
     * 跳转到下个月
     */
    public void nextMonth() {
        setCurrentItem(mHelper.getNextMonthPosition(), true);
    }

    /**
     * 跳转到上个月
     */
    public void lastMonth() {
        setCurrentItem(mHelper.getLastMonthPosition(), true);
    }

    /**
     * 跳转到上一年的当前月
     */
    public void lastYear() {
        setCurrentItem(mHelper.getLastYearPosition(), false);
    }

    /**
     * 跳转到下一年的当前月
     */
    public void nextYear() {
        setCurrentItem(mHelper.getNextYearPosition(), false);
    }

    /**
     * 清除选择
     */
    public void clearSelected() {
        mHelper.clearSelected();
    }

    /**
     * 特殊日期
     *
     * @param date yyyy-MM-dd
     * @param text text
     */
    public void addSpecify(String date, String text) {
        mAttrsModel.getSpecifyMap().put(date, text);
    }

    public void addSpecify(Map<String, String> map) {
        mAttrsModel.getSpecifyMap().putAll(map);
    }

    public void addSpecify(int year, int month, int day, String text) {
        mAttrsModel.getSpecifyMap().put(SolarUtil.getFormatDate(year, month, day), text);
        init();
    }

    /**
     * 特殊日期
     *
     * @param date yyyy-MM-dd
     */
    public void removeSpecify(String date) {
        mAttrsModel.getSpecifyMap().remove(date);
    }

    public void clearSpecify() {
        mAttrsModel.getSpecifyMap().clear();
    }

}
