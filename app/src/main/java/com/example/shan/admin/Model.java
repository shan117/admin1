package com.example.shan.admin;

/**
 * Created by Shan on 6/15/2018.
 */

public class Model {


    private String barcodeValue;
    private String imagePath;
    private long latitude;
    private long longitude;
    private long latitude1;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private long longitude1;
    private String time;

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

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude1() {
        return latitude1;
    }

    public void setLatitude1(long latitude1) {
        this.latitude1 = latitude1;
    }

    public long getLongitude1() {
        return longitude1;
    }

    public void setLongitude1(long longitude1) {
        this.longitude1 = longitude1;
    }

    public boolean isUploadAllowed() {
        return uploadAllowed;
    }

    public void setUploadAllowed(boolean uploadAllowed) {
        this.uploadAllowed = uploadAllowed;
    }

    private boolean uploadAllowed;

}
