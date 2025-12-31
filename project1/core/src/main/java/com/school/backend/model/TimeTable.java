package com.school.backend.model;

public class TimeTable {
    private int id;
    private int day;
    private int hour;
    private int teacherId;
    private int roomId;
    private int groupId;
    public TimeTable() {}
    public TimeTable(int id, int day, int hour, int teacherId, int roomId, int groupId) {
        this.id = id;
        this.day = day;
        this.hour = hour;
        this.teacherId = teacherId;
        this.roomId = roomId;
        this.groupId = groupId;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getDay() {return day;}
    public void setDay(int day) {this.day = day;}
    public int getHour() {return hour;}
    public void setHour(int hour) {this.hour = hour;}
    public int getTeacherId() {return teacherId;}
    public void setTeacherId(int teacherId) {this.teacherId = teacherId;}
    public int getRoomId() {return roomId;}
    public void setRoomId(int roomId) {this.roomId = roomId;}
    public int getGroupId() {return groupId;}
    public void setGroupId(int groupId) {this.groupId = groupId;}
}