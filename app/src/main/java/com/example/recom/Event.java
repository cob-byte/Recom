package com.example.recom;

public class Event {
    public String name, date, startTime, endTime, description;
    public int chairs, tables, tents;
    public long timestampDate;
    public boolean basketballCourt, eventHall;

    public Event() {
    }

    public Event(String name, String date, String startTime, String endTime, String description, int chairs, int tables, int tents, long timestampDate, boolean basketballCourt, boolean eventHall) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.chairs = chairs;
        this.tables = tables;
        this.tents = tents;
        this.timestampDate = timestampDate;
        this.basketballCourt = basketballCourt;
        this.eventHall = eventHall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChairs() {
        return chairs;
    }

    public void setChairs(int chairs) {
        this.chairs = chairs;
    }

    public int getTables() {
        return tables;
    }

    public void setTables(int tables) {
        this.tables = tables;
    }

    public int getTents() {
        return tents;
    }

    public void setTents(int tents) {
        this.tents = tents;
    }

    public long getTimestampDate() {
        return timestampDate;
    }

    public void setTimestampDate(long timestampDate) {
        this.timestampDate = timestampDate;
    }

    public boolean isBasketballCourt() {
        return basketballCourt;
    }

    public void setBasketballCourt(boolean basketballCourt) {
        this.basketballCourt = basketballCourt;
    }

    public boolean isEventHall() {
        return eventHall;
    }

    public void setEventHall(boolean eventHall) {
        this.eventHall = eventHall;
    }
}
