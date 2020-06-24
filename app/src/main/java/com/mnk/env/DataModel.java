package com.mnk.env;

public class DataModel {

    private String id;
    private String current_Date_time;
    private String lat;
    private String lang;

    private String pm1;
    private String pm25;
    private String co2;
    private String pm10;
    private String no2;
    private String co;
    private String humidity;
    private String temperature;

    public DataModel(String _id, String current_Date_time, String lat, String lang, String pm1, String pm25, String pm10, String no2, String CO2, String co, String humidity, String temperature) {
        this.id=_id;
        this.current_Date_time = current_Date_time;
        this.lat = lat;
        this.lang = lang;
        this.co2=CO2;
        this.temperature = temperature;
        this.pm1 = pm1;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.no2 = no2;
        this.co = co;
        this.humidity = humidity;
    }
    public DataModel(){}

    public String get_id() {
        return id;
    }

    public void set_id(String _id) {
        this.id = _id;
    }

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
        this.co2 = co2;
    }

    public String getCurrent_Date_time() {
        return current_Date_time;
    }

    public void setCurrent_Date_time(String current_Date_time) {
        this.current_Date_time = current_Date_time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPm1() {
        return this.pm1;
    }

    public void setPm1(String pm1) {
        this.pm1 = pm1;
    }

    public String getPm25() {
        return this.pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPm10() {
        return this.pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getNo2() {
        return this.no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getCo() {
        return this.co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
