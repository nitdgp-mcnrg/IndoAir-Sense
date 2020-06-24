package com.mnk.env;

public class UserData {
    private String _id;
    private String name;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String homeAddress;
    private String workAddress;

    public UserData(String _id, String name, String email, String phoneNumber, String dateOfBirth, String homeAddress, String workAddress) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.workAddress = workAddress;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public UserData() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }
}
