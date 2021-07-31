package com.example.imhungry3.Class;

import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkShopClass implements Serializable, Comparable<WorkShopClass> {
    private StorageReference mStorageRef;
    private String wkName, wkDate, wkDescraption, wkEndTime, wkStartTime, wkLocation, wkMaxGuests,
            wkPrice, wkUsrEmail, wkPicUrl, wkId, wkIdUser, WkRegisterd, WkWhatPrepare;


    public WorkShopClass() {
    }


    public WorkShopClass(StorageReference mStorageRef, String wkName, String wkDate,
                         String wkDescraption, String wkEndTime, String wkStartTime,
                         String wkLocation, String wkMaxGuests, String wkPrice,
                         String wkUsrEmail, String wkPicUrl,
                         String wkId, String wkIdUser, String wkMinGuests, String WkWhatPrepare) {
        this.mStorageRef = mStorageRef;
        this.wkName = wkName;
        this.wkDate = wkDate;
        this.wkDescraption = wkDescraption;
        this.wkEndTime = wkEndTime;
        this.wkStartTime = wkStartTime;
        this.wkLocation = wkLocation;
        this.wkMaxGuests = wkMaxGuests;
        this.wkPrice = wkPrice;
        this.wkUsrEmail = wkUsrEmail;
        this.wkPicUrl = wkPicUrl;
        this.wkId = wkId;
        this.wkIdUser = wkIdUser;
        this.WkRegisterd = wkMinGuests;
        this.WkWhatPrepare = WkWhatPrepare;
    }

    public String getWkIdUser() {
        return wkIdUser;
    }

    public void setWkIdUser(String wkIdUser) {
        this.wkIdUser = wkIdUser;
    }

    public String getWkRegisterd() {
        return WkRegisterd;
    }

    public void setWkRegisterd(String wkMinGuests) {
        this.WkRegisterd = wkMinGuests;
    }

    public StorageReference getmStorageRef() {
        return mStorageRef;
    }

    public void setmStorageRef(StorageReference mStorageRef) {
        this.mStorageRef = mStorageRef;
    }

    public String getWkName() {
        return wkName;
    }

    public void setWkName(String wkName) {
        this.wkName = wkName;
    }

    public String getWkDate() {
        return wkDate;
    }

    public void setWkDate(String wkDate) {
        this.wkDate = wkDate;
    }

    public String getWkWhatPrepare() {
        return WkWhatPrepare;
    }

    public void setWkWhatPrepare(String wkWhatPrepare) {
        WkWhatPrepare = wkWhatPrepare;
    }

    public String getWkDescraption() {
        return wkDescraption;
    }

    public void setWkDescraption(String wkDescraption) {
        this.wkDescraption = wkDescraption;
    }

    public String getWkEndTime() {
        return wkEndTime;
    }

    public void setWkEndTime(String wkEndTime) {
        this.wkEndTime = wkEndTime;
    }

    public String getWkStartTime() {
        return wkStartTime;
    }

    public void setWkStartTime(String wkStartTime) {
        this.wkStartTime = wkStartTime;
    }

    public String getWkLocation() {
        return wkLocation.toString();
    }

    public void setWkLocation(String wkLocation) {
        this.wkLocation = wkLocation.toString();
    }

    public String getWkMaxGuests() {
        return wkMaxGuests;
    }

    public void setWkMaxGuests(String wkMaxGuests) {
        this.wkMaxGuests = wkMaxGuests;
    }

    public String getWkPrice() {
        return wkPrice;
    }

    public void setWkPrice(String wkPrice) {
        this.wkPrice = wkPrice;
    }


    public String getWkUsrEmail() {
        return wkUsrEmail;
    }

    public void setWkUsrEmail(String wkUsrEmail) {
        this.wkUsrEmail = wkUsrEmail;
    }

    public String getWkPicUrl() {
        return wkPicUrl;
    }

    public void setWkPicUrl(String wkPicUrl) {
        this.wkPicUrl = wkPicUrl;
    }

    public String getWkId() {
        return wkId;
    }

    public void setWkId(String wkId) {
        this.wkId = wkId;
    }

    public Date getDateTime() {
        SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date tmp = sdformat.parse(this.getWkDate());
            return tmp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public int compareTo(WorkShopClass o) {
        return getDateTime().compareTo(o.getDateTime());

    }
}
