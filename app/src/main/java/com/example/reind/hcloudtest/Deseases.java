package com.example.reind.hcloudtest;

/**
 * Created by reind on 17-5-2018.
 */

public class Deseases {
    private int ID;

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private int BSNNumber;

    public int getBSNNumber() {
        return this.BSNNumber;
    }

    public void setBSNNumber(int BSNNumber) {
        this.BSNNumber = BSNNumber;
    }

    private String Description;

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    private int DeterminerID;

    public int getDeterminerID() {
        return this.DeterminerID;
    }

    public void setDeterminerID(int DeterminerID) {
        this.DeterminerID = DeterminerID;
    }

    private String Date;

    public String getDate() {
        return this.Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    private boolean DeclaredHealthy;

    public boolean getDeclaredHealthy() {
        return this.DeclaredHealthy;
    }

    public void setDeclaredHealthy(boolean DeclaredHealthy) {
        this.DeclaredHealthy = DeclaredHealthy;
    }

    private String DeclaredHealthyDate;

    public String getDeclaredHealthyDate() {
        return this.DeclaredHealthyDate;
    }

    public void setDeclaredHealthyDate(String DeclaredHealthyDate) {
        this.DeclaredHealthyDate = DeclaredHealthyDate;
    }
}
