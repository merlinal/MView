package com.merlin.view.calendar;


import android.text.TextUtils;

import com.merlin.view.calendar.listener.OnDayClickListener;
import com.merlin.view.calendar.listener.OnDayLongClickListener;
import com.merlin.view.calendar.listener.OnMonthChangeListener;
import com.merlin.view.calendar.listener.OnSelectedListener;
import com.merlin.view.calendar.model.CalendarAttrsModel;
import com.merlin.view.calendar.model.CalendarDayModel;
import com.merlin.view.calendar.util.CalendarUtil;
import com.merlin.view.calendar.util.LunarUtil;
import com.merlin.view.calendar.util.SolarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author merlin
 */

public class CalendarHelper {

    private CalendarAttrsModel attrsModel;
    private Map<String, List<CalendarDayModel>> monthMap = new HashMap<>();
    private int[] currentDate;
    private int currentPosition = -1;
    private HashSet<CalendarDayModel> selectSet = new LinkedHashSet<>();

    private CalendarDayModel todayModel;

    private OnMonthChangeListener monthChangeListener;
    private boolean isFirstClick = true;
    private CalendarDayModel selectStartModel;

    private OnDayLongClickListener onDayLongClickListener;
    private OnSelectedListener onSelectedListener;

    public void init() {
        selectSet.add(todayModel);
        if (onSelectedListener != null) {
            onSelectedListener.onSelected(new ArrayList<>(selectSet));
        }
    }

    private OnDayClickListener dayClickListener = new OnDayClickListener() {
        @Override
        public void onClick(CalendarDayModel dayModel) {
            //首次点击，去掉今天的选中状态
            if (isFirstClick && attrsModel.getChooseType() != CalendarAttrsModel.TYPE_CHOOSE_NONE) {
                selectSet.add(todayModel);
                setSelectStatus(todayModel, false);
                selectSet.clear();
                isFirstClick = false;
            }
            //是否是反选
            boolean isInverse = false;
            switch (attrsModel.getChooseType()) {
                case CalendarAttrsModel.TYPE_CHOOSE_SINGLE:
                    for (CalendarDayModel temp : selectSet) {
                        setSelectStatus(temp, false);
                        if (temp.getId() == dayModel.getId()) {
                            isInverse = true;
                        }
                    }
                    selectSet.clear();
                    if (!isInverse) {
                        selectSet.add(dayModel);
                        setSelectStatus(dayModel, true);
                    }
                    break;
                case CalendarAttrsModel.TYPE_CHOOSE_MULTI:
                    for (CalendarDayModel temp : selectSet) {
                        if (temp.getId() == dayModel.getId()) {
                            setSelectStatus(temp, false);
                            isInverse = true;
                            selectSet.remove(temp);
                            break;
                        }
                    }
                    if (!isInverse) {
                        selectSet.add(dayModel);
                        setSelectStatus(dayModel, true);
                    }
                    break;
                case CalendarAttrsModel.TYPE_CHOOSE_SERIES:
                    selectSeries(dayModel);
                    break;
                default:
                    break;
            }
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(new ArrayList<>(selectSet));
            }
        }
    };

    private void selectSeries(CalendarDayModel dayModel) {
        //起始位置为空，则清空所选，记录起始位置
        //起始位置不为空，则记录为结束位置，计算所选。清空起始位置。
        if (selectStartModel == null) {
            for (CalendarDayModel temp : selectSet) {
                setSelectStatus(temp, false);
            }
            selectSet.clear();

            selectStartModel = dayModel;
            setSelectStatus(selectStartModel, true);
        } else {
            CalendarDayModel selectEndModel = dayModel;
            //起始位置 >= 结束位置，则清除起始位置；否则，计算所选
            if (selectStartModel.getId() >= selectEndModel.getId()) {
                setSelectStatus(selectStartModel, false);
            } else {
                List<CalendarDayModel> dayList = new ArrayList<>();
                for (int year = selectStartModel.getSolar()[0]; year <= selectEndModel.getSolar()[0]; year++) {
                    for (int month = 1; month <= 12; month++) {
                        List<CalendarDayModel> temp = monthMap.get(getKey(year, month));
                        if (temp != null) {
                            dayList.addAll(temp);
                        }
                    }
                }
                for (CalendarDayModel temp : dayList) {
                    if (temp.getType() != CalendarDayModel.TYPE_THIS_MONTH) {
                        continue;
                    }
                    if (temp.getId() < selectStartModel.getId()) {
                        continue;
                    }
                    if (temp.getId() <= selectEndModel.getId()) {
                        setSelectStatus(temp, true);
                        selectSet.add(temp);
                    }
                    if (temp.getId() == selectEndModel.getId()) {
                        break;
                    }
                }
            }
            selectStartModel = null;
        }
    }

    private String getKey(int year, int month) {
        return year + "-" + month;
    }

    public List<CalendarDayModel> getMonthDayList(int year, int month) {
        String key = getKey(year, month);
        if (monthMap.containsKey(key)) {
            return monthMap.get(key);
        }
        List<CalendarDayModel> dayList = new ArrayList<>();
        int week = SolarUtil.getWeek(year, month - 1, 1);
        //*** 上个月 ***
        int lastYear, lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        //上个月总天数
        int lastMonthDays = SolarUtil.getMonthDays(lastYear, lastMonth);
        for (int i = 0; i < week; i++) {
            dayList.add(getDayModel(lastYear, lastMonth, lastMonthDays - week + 1 + i, CalendarDayModel.TYPE_LAST_MONTH));
        }
        //*** 当前月 ***
        int currentMonthDays = SolarUtil.getMonthDays(year, month);
        for (int i = 0; i < currentMonthDays; i++) {
            dayList.add(getDayModel(year, month, i + 1, CalendarDayModel.TYPE_THIS_MONTH));
        }
        //*** 下个月 ***
        int nextYear, nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }
        for (int i = 0; i < 7 * getMonthRows(year, month) - currentMonthDays - week; i++) {
            dayList.add(getDayModel(nextYear, nextMonth, i + 1, CalendarDayModel.TYPE_NEXT_MONTH));
        }
        //缓存
        monthMap.put(key, dayList);
        return dayList;
    }

    private CalendarDayModel getDayModel(int year, int month, int day, int type) {
        CalendarDayModel dayModel = new CalendarDayModel();
        dayModel.setType(type);
        dayModel.setSolar(year, month, day);
        dayModel.setSolarText(day + "");
        //是否特殊日子
        boolean isSpecify = false;
        if (attrsModel.isShowLunar()) {
            //用户自定义
            String specify = attrsModel.getSpecifyMap().get(SolarUtil.getFormatDate(year, month, day));
            if (!TextUtils.isEmpty(specify)) {
                dayModel.setLunarText(specify);
                isSpecify = true;
            }
            //阳历节假日
            if (!isSpecify && attrsModel.isShowHoliday()) {
                String solarHoliday = SolarUtil.getSolarHoliday(year, month, type == CalendarDayModel.TYPE_LAST_MONTH ? day - 1 : day);
                if (!TextUtils.isEmpty(solarHoliday)) {
                    dayModel.setLunarText(solarHoliday);
                    isSpecify = true;
                }
            }
            //阳历节假日
            String[] lunars = LunarUtil.solarToLunar(year, month, day);
            if (!isSpecify && attrsModel.isShowHoliday()) {
                if (!TextUtils.isEmpty(lunars[2])) {
                    dayModel.setLunarText(lunars[2]);
                    isSpecify = true;
                }
            }
            //节气
            if (!isSpecify && attrsModel.isShowTerm()) {
                String term = LunarUtil.getTermString(year, month - 1, day);
                if (!TextUtils.isEmpty(term)) {
                    dayModel.setLunarText(term);
                    isSpecify = true;
                }
            }
            //阴历
            if (!isSpecify) {
                dayModel.setLunarText("初一".equals(lunars[1]) ? lunars[0] : lunars[1]);
            }
        }
        //是否特殊日期
        dayModel.setSpecify(isSpecify);
        //星期几
        dayModel.setWeek(SolarUtil.getWeek(year, month - 1, day));
        //是否是今天
        if (SolarUtil.isToday(year, month, day)) {
            dayModel.setToday(true);
            todayModel = dayModel;
        }
        //文字大小
        dayModel.setSolarTextSize(attrsModel.getSizeSolar());
        dayModel.setLunarTextSize(attrsModel.getSizeLunar());
        //文字颜色设置
        setSelectStatus(dayModel, false);
        return dayModel;
    }

    private boolean isSelected(CalendarDayModel dayModel) {
//        for (CalendarDayModel temp : selectSet) {
//            if (temp.getCode().equals(dayModel.getCode())) {
//                return true;
//            }
//        }
        return selectSet.contains(dayModel);
    }

    /**
     * 文字颜色&背景
     *
     * @param dayModel   model
     * @param isSelected 是否选中
     */
    private void setSelectStatus(CalendarDayModel dayModel, boolean isSelected) {
        switch (dayModel.getType()) {
            case CalendarDayModel.TYPE_LAST_MONTH:
            case CalendarDayModel.TYPE_NEXT_MONTH:
                if (isSelected) {
                    dayModel.setSolarTextColor(attrsModel.getChooseColor());
                    dayModel.setLunarTextColor(attrsModel.getChooseColor());
                    dayModel.setBgResource(attrsModel.getChooseBg());
                } else {
                    dayModel.setSolarTextColor(attrsModel.getColorLastOrNext());
                    dayModel.setLunarTextColor(attrsModel.getColorLastOrNext());
                    dayModel.setBgResource(0);
                }
                break;
            case CalendarDayModel.TYPE_THIS_MONTH:
                if (isSelected) {
                    dayModel.setSolarTextColor(attrsModel.getChooseColor());
                    dayModel.setLunarTextColor(attrsModel.getChooseColor());
                    dayModel.setBgResource(attrsModel.getChooseBg());
                } else {
                    if (dayModel.isToday() && selectSet.isEmpty()) {
                        dayModel.setSolarTextColor(attrsModel.getChooseColor());
                        dayModel.setLunarTextColor(attrsModel.getChooseColor());
                        dayModel.setBgResource(attrsModel.getChooseBg());
                    } else {
                        dayModel.setSolarTextColor(dayModel.isWeekend() ? attrsModel.getColorWeekend() : attrsModel.getColorSolar());
                        dayModel.setLunarTextColor(dayModel.isSpecify() ? attrsModel.getColorSpecify() : attrsModel.getColorLunar());
                        dayModel.setBgResource(0);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year  年
     * @param month 月
     * @return
     */
    public int getMonthRows(int year, int month) {
        int items = SolarUtil.getWeek(year, month - 1, 1) + SolarUtil.getMonthDays(year, month);
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
        if (rows == 4) {
            rows = 5;
        }
        return rows;
    }

    public void clearSelected() {
        for (CalendarDayModel temp : selectSet) {
            setSelectStatus(temp, false);
        }
    }

    public int getTodayPosition() {
        Calendar c = Calendar.getInstance();
        return getPosition(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    public int getLastMonthPosition() {
        return currentPosition > 0 ? currentPosition - 1 : 0;
    }

    public int getNextMonthPosition() {
        int count = attrsModel.getMonthCount();
        return currentPosition < count - 1 ? currentPosition + 1 : count - 1;
    }

    public int getLastYearPosition() {
        return currentPosition - 12 > 0 ? currentPosition - 12 : 0;
    }

    public int getNextYearPosition() {
        int count = attrsModel.getMonthCount();
        return currentPosition + 12 < count - 1 ? currentPosition + 12 : count - 1;
    }

    public int getPosition(int year, int month) {
        return (year - attrsModel.getStartDate()[0]) * 12 + month - attrsModel.getStartDate()[1];
    }

    public CalendarAttrsModel getAttrsModel() {
        return attrsModel;
    }

    public void setAttrsModel(CalendarAttrsModel attrsModel) {
        this.attrsModel = attrsModel;
    }

    public int[] getCurrentDate() {
        return currentDate;
    }

    public int getCurrentPosition() {
        if (currentPosition < 0) {
            currentPosition = attrsModel.getInitPosition();
        }
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        currentDate = CalendarUtil.positionToDate(currentPosition, attrsModel.getStartDate()[0], attrsModel.getStartDate()[1]);
        onMonthChanged();
    }

    public void setMonthChangeListener(OnMonthChangeListener monthChangeListener) {
        this.monthChangeListener = monthChangeListener;
        if (currentDate != null) {
            onMonthChanged();
        }
    }

    private void onMonthChanged() {
        if (monthChangeListener != null) {
            monthChangeListener.onMonthChanged(new String[]{
                    currentDate[0] + "年" + (currentDate[1] < 10 ? "0" + currentDate[1] : currentDate[1]) + "月",
                    LunarUtil.getAnimalYear(currentDate[0])
            });
        }
    }

    public OnDayClickListener getDayClickListener() {
        return dayClickListener;
    }

    public void setOnDayLongClickListener(OnDayLongClickListener onDayLongClickListener) {
        this.onDayLongClickListener = onDayLongClickListener;
    }

    public OnDayLongClickListener getOnDayLongClickListener() {
        return onDayLongClickListener;
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public OnSelectedListener getOnSelectedListener() {
        return onSelectedListener;
    }

    public void refresh() {
        monthMap.clear();
    }

    public CalendarDayModel getTodayModel() {
        return todayModel;
    }
}
