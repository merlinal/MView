package com.merlin.view.calendar;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.merlin.view.calendar.util.CalendarUtil;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/3/5.
 */

public class CalendarPagerAdapter extends PagerAdapter {

    /**
     * 缓存上一次回收的MonthView
     */
    private LinkedList<MonthView> cache = new LinkedList<>();
    private SparseArray<MonthView> mViews = new SparseArray<>();

    private CalendarHelper mHelper;

    public CalendarPagerAdapter(CalendarHelper helper) {
        this.mHelper = helper;
    }

    @Override
    public int getCount() {
        return mHelper.getAttrsModel().getMonthCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MonthView view;
        if (!cache.isEmpty()) {
            view = cache.removeFirst();
        } else {
            view = new MonthView(container.getContext());
        }
        //根据position计算对应年、月
        int[] date = CalendarUtil.positionToDate(position, mHelper.getAttrsModel().getStartDate()[0], mHelper.getAttrsModel().getStartDate()[1]);
        view.setMonth(mHelper, date[0], date[1]);
        mViews.put(position, view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((MonthView) object);
        cache.addLast((MonthView) object);
        mViews.remove(position);
    }

    /**
     * 获得ViewPager缓存的View
     *
     * @return
     */
    public SparseArray<MonthView> getViews() {
        return mViews;
    }

}
