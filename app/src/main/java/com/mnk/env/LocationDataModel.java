package com.mnk.env;

public class LocationDataModel {

    private String roomName;

    public LocationDataModel() {}

    public LocationDataModel(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
