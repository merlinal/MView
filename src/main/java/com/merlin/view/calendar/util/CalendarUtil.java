package com.merlin.view.calendar.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Administrator on 2018/3/5.
 */

public class CalendarUtil {

    public static int getTextSize(Context context, int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 根据ViewPager position 得到对应年月
     *
     * @param position 位置
     * @return
     */
    public static int[] positionToDate(int position, int startY, int startM) {
        int year = position / 12 + startY;
        int month = position % 12 + startM;

        if (month > 12) {
            month = month % 12;
            year = year + 1;
        }

        return new int[]{year, month};
    }

    public static int getPxSize(Context context, int size) {
        return size * context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getTextSize1(Context context, int size) {
        return (int) (size * context.getResources().getDisplayMetrics().scaledDensity);
    }

}
