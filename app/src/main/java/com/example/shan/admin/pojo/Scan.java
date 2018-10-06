package com.example.shan.admin.pojo;

/**
 * Created by pc on 9/9/2018.
 */

public class Scan {

    private String locationName;
    private String barcodeValue;
    private String imagePath;
    private String latitude;
    private String longitude;
    private String time;
    private String supervisor;
    private String supervisorName;
    private String user;
    private String superUser;
    private String superAdmin;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSuperUser() {
        return superUser;
    }

    public void setSuperUser(String superUser) {
        this.superUser = superUser;
    }

    public String getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(String superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Scan{" +
                "barcodeValue='" + barcodeValue + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", time='" + time + '\'' +
                ", supervisor='" + supervisor + '\'' +
                ", supervisorName='" + supervisorName + '\'' +
                ", user='" + user + '\'' +
                ", superUser='" + superUser + '\'' +
                ", superAdmin='" + superAdmin + '\'' +
                '}';
    }
}
