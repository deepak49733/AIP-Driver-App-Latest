package com.sc.aipdriver.activities.models;

public class TimelineEventModel {
    public enum Type {
        MEETING, EVENT, SHIFT
    }

    private String title;
    private String startTime;
    private String endTime;
    private Type type;

    public TimelineEventModel(String title, String startTime, String endTime, Type type) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Type getType() {
        return type;
    }
}
