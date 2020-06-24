package com.mnk.env;

public class AddDetails {
    private String instituteId, classroomId, acs, fans, occupants, window, door, startTime, endTime;

    public AddDetails(String instituteId, String classroomId, String acs, String fans, String occupants, String window, String door, String startTime, String endTime) {
        this.classroomId = classroomId;
        this.acs = acs;
        this.fans = fans;
        this.occupants = occupants;
        this.window = window;
        this.door = door;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public String getAcs() {
        return acs;
    }

    public String getFans() {
        return fans;
    }

    public String getOccupants() {
        return occupants;
    }

    public String getWindow() {
        return window;
    }

    public String getDoor() {
        return door;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
