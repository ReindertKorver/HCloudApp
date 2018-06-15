package com.example.reind.hcloudtest;

/**
 * Created by reind on 17-5-2018.
 */

public class Medications {
    private int ID;

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private String Description;

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    private boolean HandedOut;

    public boolean getHandedOut() {
        return this.HandedOut;
    }

    public void setHandedOut(boolean HandedOut) {
        this.HandedOut = HandedOut;
    }

    private String HandedOutDate;

    public String getHandedOutDate() {
        return this.HandedOutDate;
    }

    public void setHandedOutDate(String HandedOutDate) {
        this.HandedOutDate = HandedOutDate;
    }

    private String ExpirationDate;

    public String getExpirationDate() {
        return this.ExpirationDate;
    }

    public void setExpirationDate(String ExpirationDate) {
        this.ExpirationDate = ExpirationDate;
    }

    private int BSNNumber;

    public int getBSNNumber() {
        return this.BSNNumber;
    }

    public void setBSNNumber(int BSNNumber) {
        this.BSNNumber = BSNNumber;
    }

    private int HandedOutByID;

    public int getHandedOutByID() {
        return this.HandedOutByID;
    }

    public void setHandedOutByID(int HandedOutByID) {
        this.HandedOutByID = HandedOutByID;
    }

    private int Determiner;

    public int getDeterminer() {
        return this.Determiner;
    }

    public void setDeterminer(int Determiner) {
        this.Determiner = Determiner;
    }
}
