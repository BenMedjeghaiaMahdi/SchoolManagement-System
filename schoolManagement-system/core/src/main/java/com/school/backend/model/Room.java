package com.school.backend.model;

public class Room {
    private int id;
    private String roomNumber;
    private String type;

    public Room() {}

    public Room(String roomNumber, String type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}