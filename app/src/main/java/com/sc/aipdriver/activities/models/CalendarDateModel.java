package com.sc.aipdriver.activities.models;

import java.util.Date;

public class CalendarDateModel {
    private Date date;
    private boolean isSelected;
    private boolean hasMeeting;
    private boolean hasEvent;
    private boolean hasShift;

    public CalendarDateModel(Date date, boolean isSelected, boolean hasMeeting, boolean hasEvent, boolean hasShift) {
        this.date = date;
        this.isSelected = isSelected;
        this.hasMeeting = hasMeeting;
        this.hasEvent = hasEvent;
        this.hasShift = hasShift;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isHasMeeting() {
        return hasMeeting;
    }

    public void setHasMeeting(boolean hasMeeting) {
        this.hasMeeting = hasMeeting;
    }

    public boolean isHasEvent() {
        return hasEvent;
    }

    public void setHasEvent(boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public boolean isHasShift() {
        return hasShift;
    }

    public void setHasShift(boolean hasShift) {
        this.hasShift = hasShift;
    }
}
