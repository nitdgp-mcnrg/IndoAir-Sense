package com.mnk.env;

public class ClassRoomData {
    private String occupants;
    private String ac;
    private String fans;
    private String doors;
    private String windows;
    private String start_Time;
    private String end_Time;
    private String class_Status;

    public ClassRoomData(String class_Status, String doors, String ac, String start_Time, String end_Time, String windows, String occupants, String fans) {

        this.occupants = occupants;
        this.ac = ac;
        this.fans = fans;
        this.doors = doors;
        this.windows = windows;
        this.start_Time = start_Time;
        this.end_Time = end_Time;
        this.class_Status = class_Status;
    }
    public ClassRoomData() {}

    public String getOccupants() {
        return occupants;
    }

    public void setOccupants(String occupants) {
        this.occupants = occupants;
    }

    public String getAC() {
        return ac;
    }

    public void setAC(String AC) {
        this.ac = AC;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getDoors() {
        return doors;
    }

    public void setDoors(String doors) {
        this.doors = doors;
    }

    public String getWindows() {
        return windows;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    public String getStart_Time() {
        return start_Time;
    }

    public void setStart_Time(String start_Time) {
        this.start_Time = start_Time;
    }

    public String getEnd_Time() {
        return end_Time;
    }

    public void setEnd_Time(String end_Time) {
        this.end_Time = end_Time;
    }

    public String getClass_Status() {
        return class_Status;
    }

    public void setClass_Status(String class_Status) {
        this.class_Status = class_Status;
    }
}
