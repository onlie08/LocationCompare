package com.sfmap.map.demo;

public class ResultBean {
    private String sfLoc;
    private String gdLoc;
    private String bdLoc;
    private String gpsLoc;
    private double sfDistance;
    private double gdDistance;
    private double bdDistance;
    private String time;

    public String getSfLoc() {
        return sfLoc;
    }

    public void setSfLoc(String sfLoc) {
        this.sfLoc = sfLoc;
    }

    public String getGdLoc() {
        return gdLoc;
    }

    public void setGdLoc(String gdLoc) {
        this.gdLoc = gdLoc;
    }

    public String getBdLoc() {
        return bdLoc;
    }

    public void setBdLoc(String bdLoc) {
        this.bdLoc = bdLoc;
    }

    public String getGpsLoc() {
        return gpsLoc;
    }

    public void setGpsLoc(String gpsLoc) {
        this.gpsLoc = gpsLoc;
    }

    public double getSfDistance() {
        return sfDistance;
    }

    public void setSfDistance(double sfDistance) {
        this.sfDistance = sfDistance;
    }

    public double getGdDistance() {
        return gdDistance;
    }

    public void setGdDistance(double gdDistance) {
        this.gdDistance = gdDistance;
    }

    public double getBdDistance() {
        return bdDistance;
    }

    public void setBdDistance(double bdDistance) {
        this.bdDistance = bdDistance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
