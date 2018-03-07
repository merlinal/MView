package com.merlin.view.calendar.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.merlin.view.BR;

/**
 * Created by Administrator on 2018/3/2.
 */

public class CalendarDayModel extends BaseObservable {
    /**
     * 当天归属于哪个月
     */
    public final static int TYPE_THIS_MONTH = 0;
    public final static int TYPE_LAST_MONTH = 1;
    public final static int TYPE_NEXT_MONTH = 2;

    private long id;
    /**
     * 公历
     */
    @Bindable
    private int solarTextColor;
    private int solarTextSize;
    private String solarText;
    /**
     * 农历
     */
    @Bindable
    private int lunarTextColor;
    private int lunarTextSize;
    private String lunarText;
    /**
     * 背景
     */
    @Bindable
    private int bgResource;

    private int week;
    private boolean specify;
    private boolean today;
    /**
     * 阳历年、月、日
     */
    private int[] solar;
    /**
     * 农历月、日
     */
    private String[] lunar;
    /**
     * 0:上月，1:当月，2:下月
     */
    private int type;



    public void setSolar(int year, int month, int day) {
        solar = new int[]{year, month, day};
        id = year * 10000 + month * 100 + day;
    }

    public boolean isWeekend(){
        return week == 0 || week == 6;
    }


    public int[] getSolar() {
        return solar;
    }

    public void setSolar(int[] solar) {
        this.solar = solar;
    }

    public String[] getLunar() {
        return lunar;
    }

    public void setLunar(String[] lunar) {
        this.lunar = lunar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSolarTextColor() {
        return solarTextColor;
    }

    public void setSolarTextColor(int solarTextColor) {
        this.solarTextColor = solarTextColor;
        notifyPropertyChanged(BR.solarTextColor);
    }

    public int getSolarTextSize() {
        return solarTextSize;
    }

    public void setSolarTextSize(int solarTextSize) {
        this.solarTextSize = solarTextSize;
    }

    public String getSolarText() {
        return solarText;
    }

    public void setSolarText(String solarText) {
        this.solarText = solarText;
    }

    public int getLunarTextColor() {
        return lunarTextColor;
    }

    public void setLunarTextColor(int lunarTextColor) {
        this.lunarTextColor = lunarTextColor;
        notifyPropertyChanged(BR.lunarTextColor);
    }

    public int getLunarTextSize() {
        return lunarTextSize;
    }

    public void setLunarTextSize(int lunarTextSize) {
        this.lunarTextSize = lunarTextSize;
    }

    public String getLunarText() {
        return lunarText;
    }

    public void setLunarText(String lunarText) {
        this.lunarText = lunarText;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public boolean isSpecify() {
        return specify;
    }

    public void setSpecify(boolean specify) {
        this.specify = specify;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBgResource() {
        return bgResource;
    }

    public void setBgResource(int bgResource) {
        this.bgResource = bgResource;
        notifyPropertyChanged(BR.bgResource);
    }

}
