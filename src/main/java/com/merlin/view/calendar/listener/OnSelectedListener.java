package com.merlin.view.calendar.listener;

import com.merlin.view.calendar.model.CalendarDayModel;

import java.util.List;

/**
 * @author merlin
 */

public interface OnSelectedListener {

    /**
     * @param dayList List<CalendarDayModel>
     */
    void onSelected(List<CalendarDayModel> dayList);
}
