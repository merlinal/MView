package com.merlin.view.calendar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/3/6.
 */

public class DateUtil {

    public static String format(String dateStr, String srcFormat, String targetFormat) {
        return format(parse(dateStr, srcFormat), targetFormat);
    }

    public static String format(Date date, String format) {
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static Date parse(String dateStr, String format) {
        if(dateStr != null) {
            if(dateStr.trim().length() < 1) {
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);

            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException var4) {
                var4.printStackTrace();
            }
        }
        return null;
    }

}
