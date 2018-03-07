package com.merlin.view.calendar.model;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/2.
 *
 * @author melrin
 */

public class CalendarAttrsModel {

    /**
     * 单选、多选、连选
     */
    public final static int TYPE_CHOOSE_NONE = 0;
    public final static int TYPE_CHOOSE_SINGLE = 1;
    public final static int TYPE_CHOOSE_MULTI = 2;
    public final static int TYPE_CHOOSE_SERIES = 3;

    /**
     * 日历的开始年、月
     */
    private int[] startDate;
    /**
     * 日历的结束年、月
     */
    private int[] endDate;
    /**
     * 初始显示
     */
    private int[] initDate;
    /**
     * 是否显示上个月、下个月
     */
    private boolean showLastNext = true;
    /**
     * 是否显示农历
     */
    private boolean showLunar = true;
    /**
     * 是否显示节假日(不显示农历则节假日无法显示，节假日会覆盖农历显示)
     */
    private boolean showHoliday = true;
    /**
     * 是否显示节气
     */
    private boolean showTerm = true;
    /**
     * 单选时切换月份，是否选中上次的日期
     */
    private boolean chooseSwitch = true;
    /**
     * 阳历的日期颜色
     */
    private int colorSolar = Color.BLACK;
    /**
     * 阴历的日期颜色
     */
    private int colorLunar = Color.parseColor("#666666");
    /**
     * 阴历的日期颜色
     */
    private int colorLastOrNext = Color.parseColor("#999999");
    /**
     * 节假日的颜色
     */
    private int colorSpecify = Color.parseColor("#00cd00");
    /**
     * 阳历的日期颜色
     */
    private int colorWeekend = Color.parseColor("#666666");
    /**
     * 选中的日期文字颜色
     */
    private int chooseColor = Color.WHITE;
    /**
     * 阳历日期文字尺寸
     */
    private int sizeSolar = 14;
    /**
     * 阴历日期文字尺寸
     */
    private int sizeLunar = 8;
    /**
     * 选中的背景
     */
    private int chooseBg = 0;
    /**
     * 表示日历是单选还是多选
     */
    private int chooseType = TYPE_CHOOSE_NONE;
    /**
     * 指定日期对应的文字map
     */
    private Map<String, String> specifyMap = new HashMap<>();

    /**
     * 设置起始时间
     *
     * @param startDate YYYY-MM
     */
    public void setStartDate(String startDate) {
        setDate(this.startDate = new int[2], startDate);
    }

    /**
     * 结束时间
     *
     * @param endDate YYYY-MM
     */
    public void setEndDate(String endDate) {
        setDate(this.endDate = new int[2], endDate);
    }

    /**
     * 初始时间
     *
     * @param initDate YYYY-MM
     */
    public void setInitDate(String initDate) {
        setDate(this.initDate = new int[2], initDate);
    }

    private void setDate(int[] dates, String date) {
        String[] dateArray = date.split("-");
        dates[0] = Integer.valueOf(dateArray[0]);
        dates[1] = Integer.valueOf(dateArray[1]);
    }

    /**
     * 总月数
     *
     * @return monthCount
     */
    public int getMonthCount() {
        return (endDate[0] - startDate[0]) * 12 + endDate[1] - startDate[1] + 1;
    }

    /**
     * 初始时间的位置
     *
     * @return position
     */
    public int getInitPosition() {
        if (initDate == null) {
            return 0;
        }
        return (initDate[0] - startDate[0]) * 12 + initDate[1] - startDate[1];
    }

// ********************** getter & setter **********************

    public int[] getStartDate() {
        return startDate;
    }

    public void setStartDate(int[] startDate) {
        this.startDate = startDate;
    }

    public int[] getEndDate() {
        return endDate;
    }

    public void setEndDate(int[] endDate) {
        this.endDate = endDate;
    }

    public boolean isShowLastNext() {
        return showLastNext;
    }

    public void setShowLastNext(boolean showLastNext) {
        this.showLastNext = showLastNext;
    }

    public boolean isShowLunar() {
        return showLunar;
    }

    public void setShowLunar(boolean showLunar) {
        this.showLunar = showLunar;
    }

    public boolean isShowHoliday() {
        return showHoliday;
    }

    public void setShowHoliday(boolean showHoliday) {
        this.showHoliday = showHoliday;
    }

    public boolean isShowTerm() {
        return showTerm;
    }

    public void setShowTerm(boolean showTerm) {
        this.showTerm = showTerm;
    }

    public int getColorSolar() {
        return colorSolar;
    }

    public void setColorSolar(int colorSolar) {
        this.colorSolar = colorSolar;
    }

    public int getColorLunar() {
        return colorLunar;
    }

    public void setColorLunar(int colorLunar) {
        this.colorLunar = colorLunar;
    }

    public int getSizeSolar() {
        return sizeSolar;
    }

    public void setSizeSolar(int sizeSolar) {
        this.sizeSolar = sizeSolar;
    }

    public int getSizeLunar() {
        return sizeLunar;
    }

    public void setSizeLunar(int sizeLunar) {
        this.sizeLunar = sizeLunar;
    }

    public int getChooseType() {
        return chooseType;
    }

    public void setChooseType(int chooseType) {
        this.chooseType = chooseType;
    }

    public int[] getInitDate() {
        return initDate;
    }

    public void setInitDate(int[] initDate) {
        this.initDate = initDate;
    }

    public boolean isChooseSwitch() {
        return chooseSwitch;
    }

    public void setChooseSwitch(boolean chooseSwitch) {
        this.chooseSwitch = chooseSwitch;
    }

    public int getChooseBg() {
        return chooseBg;
    }

    public void setChooseBg(int chooseBg) {
        this.chooseBg = chooseBg;
    }

    public Map<String, String> getSpecifyMap() {
        return specifyMap;
    }

    public void setSpecifyMap(Map<String, String> specifyMap) {
        this.specifyMap = specifyMap;
    }

    public int getColorSpecify() {
        return colorSpecify;
    }

    public void setColorSpecify(int colorSpecify) {
        this.colorSpecify = colorSpecify;
    }

    public int getColorWeekend() {
        return colorWeekend;
    }

    public void setColorWeekend(int colorWeekend) {
        this.colorWeekend = colorWeekend;
    }

    public int getChooseColor() {
        return chooseColor;
    }

    public void setChooseColor(int chooseColor) {
        this.chooseColor = chooseColor;
    }

    public int getColorLastOrNext() {
        return colorLastOrNext;
    }

    public void setColorLastOrNext(int colorLastOrNext) {
        this.colorLastOrNext = colorLastOrNext;
    }
}
