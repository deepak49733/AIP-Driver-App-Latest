package com.sc.aipdriver.activities.models;

import java.util.List;

public class TimelineRowModel {
    private String time;
    private List<TimelineEventModel> events;

    public TimelineRowModel(String time, List<TimelineEventModel> events) {
        this.time = time;
        this.events = events;
    }

    public String getTime() {
        return time;
    }

    public List<TimelineEventModel> getEvents() {
        return events;
    }
}
