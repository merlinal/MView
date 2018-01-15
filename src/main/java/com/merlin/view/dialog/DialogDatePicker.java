package com.merlin.view.dialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.merlin.core.context.MContext;
import com.merlin.core.util.MDate;
import com.merlin.core.util.MLog;
import com.merlin.core.util.MUtil;
import com.merlin.core.util.MVerify;
import com.merlin.view.R;
import com.merlin.view.widget.PickerView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 时间日期选择
 *
 * @author merlin
 */

public class DialogDatePicker {

    public static DialogDatePicker newInstance() {
        return new DialogDatePicker();
    }

    private DialogCommon mDialog;
    private TextView mYearText, mMonthText, mDayText, mHourText, mMinuteText, mSecondText;
    private PickerView mYearPicker, mMonthPicker, mDayPicker, mHourPicker, mMinutePicker, mSecondPicker;
    private TextView mTitleText, mLeftText, mRightText;
    /**
     * 已选日期、开始日期、结束日期
     */
    private Calendar mSelectedCalender, mStartCalendar, mEndCalendar;
    /**
     * 数据集：年月日时分秒
     */
    private SparseArray<ArrayList<String>> mDataList = new SparseArray<>(6);
    /**
     * 是否显示：年月日时分秒
     */
    private boolean[] isShow = {true, true, true, true, true, true};
    /**
     * 是否首次展示dialog
     */
    private boolean isFirstShow = true;

    private IDialog.OnCancelListener mCancelListener;
    private IDialog.OnSelectListener mSelectListener;

    private DialogDatePicker() {
        mDialog = new DialogCommon.Builder(MContext.app())
                .setLayout(R.layout.m_dialog_date_picker)
                .bottom()
                .build();
        //view
        mYearText = mDialog.view(R.id.m_dialog_year);
        mMonthText = mDialog.view(R.id.m_dialog_month);
        mDayText = mDialog.view(R.id.m_dialog_day);
        mHourText = mDialog.view(R.id.m_dialog_hour);
        mMinuteText = mDialog.view(R.id.m_dialog_minute);
        mSecondText = mDialog.view(R.id.m_dialog_second);

        mYearPicker = mDialog.view(R.id.m_dialog_year_picker);
        mMonthPicker = mDialog.view(R.id.m_dialog_month_picker);
        mDayPicker = mDialog.view(R.id.m_dialog_day_picker);
        mHourPicker = mDialog.view(R.id.m_dialog_hour_picker);
        mMinutePicker = mDialog.view(R.id.m_dialog_minute_picker);
        mSecondPicker = mDialog.view(R.id.m_dialog_second_picker);

        mTitleText = mDialog.view(R.id.m_dialog_title);
        mLeftText = mDialog.view(R.id.m_dialog_left);
        mRightText = mDialog.view(R.id.m_dialog_right);

        //Calendar
        mSelectedCalender = Calendar.getInstance();
        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();
        //初始化
        setStartDate("1970-01-01 00:00:00");
        setEndDate("2070-01-01 00:00:00");
        for (int i = 0; i < 6; i++) {
            mDataList.put(i, new ArrayList<String>());
        }
        mYearPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                //年发生变化，通知月
                mSelectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                notifyMonthChanged();
            }
        });
        mMonthPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                //mSelectedCalender.set有坑，故使用add增量实现
                mSelectedCalender.add(Calendar.MONTH, Integer.parseInt(text) - mSelectedCalender.get(Calendar.MONTH) - 1);
                notifyDayChanged();
            }
        });
        mDayPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                mSelectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
                notifyHourChanged();
            }
        });
        mHourPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                mSelectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text));
                notifyMinuteChanged();
            }
        });
        mMinutePicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                mSelectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
                notifySecondChanged();
            }
        });
        mSecondPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(PickerView pickerView, int index, String text) {
                mSelectedCalender.set(Calendar.SECOND, Integer.parseInt(text));
            }
        });
        mLeftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (mCancelListener != null) {
                    mCancelListener.onCancel();
                }
            }
        });
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (mSelectListener != null) {
                    mSelectListener.onSelect(getSelectDate());
                }
            }
        });
    }

    public DialogDatePicker setEnable(boolean showYear, boolean showMonth, boolean showDay, boolean showHour, boolean showMinute, boolean showSecond) {
        isShow[0] = showYear;
        isShow[1] = showMonth;
        isShow[2] = showDay;
        isShow[3] = showHour;
        isShow[4] = showMinute;
        isShow[5] = showSecond;
        setVisibility(showYear, mYearText, mYearPicker);
        setVisibility(showMonth, mMonthText, mMonthPicker);
        setVisibility(showDay, mDayText, mDayPicker);
        setVisibility(showHour, mHourText, mHourPicker);
        setVisibility(showMinute, mMinuteText, mMinutePicker);
        setVisibility(showSecond, mSecondText, mSecondPicker);
        return this;
    }

    /**
     * @param date yyyy-MM-dd HH:mm:ss
     * @return
     */
    public DialogDatePicker setStartDate(String date) {
        if (!MVerify.isBlank(date)) {
            mStartCalendar.setTime(MDate.parse(date, MDate.FORMAT_STANDARD));
        }
        return this;
    }

    /**
     * @param date yyyy-MM-dd HH:mm:ss
     * @return
     */
    public DialogDatePicker setEndDate(String date) {
        if (!MVerify.isBlank(date)) {
            mEndCalendar.setTime(MDate.parse(date, MDate.FORMAT_STANDARD));
        }
        return this;
    }

    /**
     * @param date yyyy-MM-dd HH:mm:ss
     * @return
     */
    public DialogDatePicker setSelectDate(String date) {
        if (!MVerify.isBlank(date)) {
            mSelectedCalender.setTime(MDate.parse(date, MDate.FORMAT_STANDARD));
            checkSelectedCalender();
        }
        return this;
    }

    public DialogDatePicker setTitle(String text) {
        if (text != null) {
            mTitleText.setText(text);
        }
        return this;
    }

    public DialogDatePicker setLeft(String text, final IDialog.OnCancelListener onCancelListener) {
        if (text != null) {
            mLeftText.setText(text);
        }
        mCancelListener = onCancelListener;
        return this;
    }

    public DialogDatePicker setRight(String text, final IDialog.OnSelectListener onSelectListener) {
        if (text != null) {
            mRightText.setText(text);
        }
        mSelectListener = onSelectListener;
        return this;
    }

    public DialogDatePicker setTitleText(int textColor, float textSize) {
        setTextView(mTitleText, textColor, textSize);
        return this;
    }

    public DialogDatePicker setLeftText(int textColor, float textSize) {
        setTextView(mLeftText, textColor, textSize);
        return this;
    }

    public DialogDatePicker setRightText(int textColor, float textSize) {
        setTextView(mRightText, textColor, textSize);
        return this;
    }

    private DialogDatePicker setTextView(TextView textView, int textColor, float textSize) {
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        if (textSize != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        return this;
    }

    private DialogDatePicker setVisibility(boolean isShow, View... views) {
        int visibility = isShow ? View.VISIBLE : View.GONE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
        return this;
    }

    public DialogDatePicker show(FragmentManager fragmentManager) {
        if (mDialog.isShowing()) {
            MLog.e("DialogDatePicker is showing");
            return this;
        }
        if (mStartCalendar.getTime().after(mEndCalendar.getTime())) {
            MLog.e("startDate is after endDate");
            return this;
        }
        //第一次show时，初始数据
        if (isFirstShow) {
            checkSelectedCalender();
            //数据初始化
            notifyYearChanged();
            isFirstShow = false;
        }
        mDialog.show(fragmentManager, "DialogDatePicker");
        return this;
    }


    /**
     * 年数据变化
     */
    private void notifyYearChanged() {
        //如果不显示年，则直接通知月
        if (!isShow[0]) {
            notifyMonthChanged();
            return;
        }
        //组织年数据：start <= 年 <= end
        ArrayList<String> dataList = mDataList.get(0);
        if (dataList.size() < 1) {
            for (int i = getValue(mStartCalendar, Calendar.YEAR); i <= getValue(mEndCalendar, Calendar.YEAR); i++) {
                dataList.add(i + "");
            }
            mYearPicker.setData(dataList);
            mYearPicker.setSelected(dataList.indexOf(getValue(mSelectedCalender, Calendar.YEAR) + ""));
        }
        //通知月
        notifyMonthChanged();
    }

    /**
     * 月数据变化
     */
    private void notifyMonthChanged() {
        if (!isShow[1]) {
            notifyDayChanged();
            return;
        }
        ArrayList<String> dataList = mDataList.get(1);
        //判断月数据是否变化
        if (isChanged(dataList, Calendar.MONTH, 1, 12)) {
            mMonthPicker.setData(dataList);
            int selectIndex = dataList.indexOf(formatUnit(getValue(mSelectedCalender, Calendar.MONTH)));
            if (selectIndex < 0) {
                selectIndex = 0;
                executeAnimator(mMonthPicker);
            }
            mMonthPicker.setSelected(selectIndex);
            mSelectedCalender.set(Calendar.MONTH, Integer.valueOf(dataList.get(selectIndex)) - 1);
        }
        notifyDayChanged();
    }

    /**
     * 天数据变化
     */
    private void notifyDayChanged() {
        if (!isShow[2]) {
            notifyHourChanged();
            return;
        }
        ArrayList<String> dataList = mDataList.get(2);
        if (isChanged(dataList, Calendar.DAY_OF_MONTH, 1, mSelectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH))) {
            mDayPicker.setData(dataList);
            int selectIndex = dataList.indexOf(formatUnit(getValue(mSelectedCalender, Calendar.DAY_OF_MONTH)));
            if (selectIndex < 0) {
                selectIndex = 0;
                executeAnimator(mDayPicker);
            }
            mDayPicker.setSelected(selectIndex);
            mSelectedCalender.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dataList.get(selectIndex)));
        }
        notifyHourChanged();
    }

    /**
     * 时数据变化
     */
    private void notifyHourChanged() {
        if (!isShow[3]) {
            notifyMinuteChanged();
            return;
        }
        ArrayList<String> dataList = mDataList.get(3);
        //Calendar.HOUR:12小时制; Calendar.HOUR_OF_DAY:24小时制
        if (isChanged(dataList, Calendar.HOUR_OF_DAY, 0, 23)) {
            mHourPicker.setData(dataList);
            int selectIndex = dataList.indexOf(formatUnit(getValue(mSelectedCalender, Calendar.HOUR_OF_DAY)));
            if (selectIndex < 0) {
                selectIndex = 0;
                executeAnimator(mHourPicker);
            }
            mHourPicker.setSelected(selectIndex);
            mSelectedCalender.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dataList.get(selectIndex)));
        }
        notifyMinuteChanged();
    }

    /**
     * 分数据变化
     */
    private void notifyMinuteChanged() {
        if (!isShow[4]) {
            notifySecondChanged();
            return;
        }
        ArrayList<String> dataList = mDataList.get(4);
        if (isChanged(dataList, Calendar.MINUTE, 0, 59)) {
            mMinutePicker.setData(dataList);
            int selectIndex = dataList.indexOf(formatUnit(getValue(mSelectedCalender, Calendar.MINUTE)));
            if (selectIndex < 0) {
                selectIndex = 0;
                executeAnimator(mMinutePicker);
            }
            mMinutePicker.setSelected(selectIndex);
            mSelectedCalender.set(Calendar.MINUTE, Integer.valueOf(dataList.get(selectIndex)));
        }
        notifySecondChanged();
    }

    /**
     * 秒数据变化
     */
    private void notifySecondChanged() {
        if (!isShow[5]) {
            return;
        }
        ArrayList<String> dataList = mDataList.get(5);
        if (isChanged(dataList, Calendar.SECOND, 0, 59)) {
            mSecondPicker.setData(dataList);
            int selectIndex = dataList.indexOf(formatUnit(getValue(mSelectedCalender, Calendar.SECOND)));
            if (selectIndex < 0) {
                selectIndex = 0;
                executeAnimator(mSecondPicker);
            }
            mSecondPicker.setSelected(selectIndex);
            mSelectedCalender.set(Calendar.SECOND, Integer.valueOf(dataList.get(selectIndex)));
        }
    }

    /**
     * @param dataList 当前数据集
     * @param field    当前字段，如 Calendar.MONTH，Calendar.DAY_OF_MONTH，...
     * @param start    当前字段的起始值
     * @param count    当前字段的最大值
     * @return
     */
    private boolean isChanged(ArrayList<String> dataList, int field, int start, int count) {
        boolean isChanged = false;
        //三种情况：是否是开始日期、是否是结束日期、正常值
        if (isMatchStart(field)) {
            isChanged = true;
            resetDataList(dataList, getValue(mStartCalendar, field), count);
        } else if (isMatchEnd(field)) {
            isChanged = true;
            resetDataList(dataList, start, getValue(mEndCalendar, field));
        } else {
            //正常值字段是否需要变化：如3、4月是正常值，但3、4月天数不同，从3月滚动到4月，天字段也需要变化
            if (dataList.size() != count) {
                isChanged = true;
                resetDataList(dataList, start, count);
            }
        }
        return isChanged;
    }

    private void resetDataList(ArrayList<String> dataList, int start, int end) {
        dataList.clear();
        for (int i = start; i <= end; i++) {
            dataList.add(formatUnit(i));
        }
    }

    private int getValue(Calendar calendar, int field) {
        //Calendar.MONTH字段需+1
        return field == Calendar.MONTH ? calendar.get(field) + 1 : calendar.get(field);
    }

    private boolean isMatchStart(int field) {
        int[] calendars = getParents(field);
        for (int cal : calendars) {
            if (mSelectedCalender.get(cal) != mStartCalendar.get(cal)) {
                return false;
            }
        }
        return true;
    }

    private boolean isMatchEnd(int field) {
        int[] calendars = getParents(field);
        for (int cal : calendars) {
            if (mSelectedCalender.get(cal) != mEndCalendar.get(cal)) {
                return false;
            }
        }
        return true;
    }

    private int[] getParents(int calendar) {
        switch (calendar) {
            case Calendar.YEAR:
                return new int[]{};
            case Calendar.MONTH:
                return new int[]{Calendar.YEAR};
            case Calendar.DAY_OF_MONTH:
                return new int[]{Calendar.YEAR, Calendar.MONTH};
            case Calendar.HOUR_OF_DAY:
                return new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
            case Calendar.MINUTE:
                return new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY};
            case Calendar.SECOND:
                return new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE};
            default:
                return new int[]{};
        }
    }

    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start();
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    /**
     * 获取选择的日期
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    private String getSelectDate() {
        return MDate.format(mSelectedCalender.getTime(), MDate.FORMAT_STANDARD);
        /*return getValue(mSelectedCalender, Calendar.YEAR) + "-"
                + formatUnit(getValue(mSelectedCalender, Calendar.MONTH)) + "-"
                + formatUnit(getValue(mSelectedCalender, Calendar.DAY_OF_MONTH)) + " "
                + formatUnit(getValue(mSelectedCalender, Calendar.HOUR_OF_DAY)) + ":"
                + formatUnit(getValue(mSelectedCalender, Calendar.MINUTE)) + ":"
                + formatUnit(getValue(mSelectedCalender, Calendar.SECOND));*/
    }

    /**
     * 已选日期介于开始时间和结束时间之间
     */
    private void checkSelectedCalender() {
        if (mSelectedCalender.getTime().before(mStartCalendar.getTime())
                || mSelectedCalender.getTime().after(mEndCalendar.getTime())) {
            mSelectedCalender.setTime(mStartCalendar.getTime());
        }
    }

}
