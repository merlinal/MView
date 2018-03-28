package com.merlin.view.calendar.listener;

import com.merlin.view.calendar.model.CalendarDayModel;

/**
 * @author merlin
 */

public interface OnDayLongClickListener {
    /**
     * 长按事件
     *
     * @param dayModel CalendarDayModel
     */
    void onClick(CalendarDayModel dayModel);
}
