package com.example.reind.hcloudtest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
/**
 * Created by reind on 25-4-2018.
 */

public class User {

    private int ID;

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private String BsnNumber;

    public String getBsnNumber() {
        return this.BsnNumber;
    }

    public void setBsnNumber(String BsnNumber) {
        this.BsnNumber = BsnNumber;
    }

    private String PhoneNumber;

    public String getPhoneNumber() {
        return this.PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    private String FirstName;

    public String getFirstName() {
        return this.FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    private String LastName;

    public String getLastName() {
        return this.LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    private String EmailAdress;

    public String getEmailAdress() {
        return this.EmailAdress;
    }

    public void setEmailAdress(String EmailAdress) {
        this.EmailAdress = EmailAdress;
    }

    private String PasswordHash;

    public String getPasswordHash() {
        return this.PasswordHash;
    }

    public void setPasswordHash(String PasswordHash) {
        this.PasswordHash = PasswordHash;
    }

    private boolean Confirmed;

    public boolean getConfirmed() {
        return this.Confirmed;
    }

    public void setConfirmed(boolean Confirmed) {
        this.Confirmed = Confirmed;
    }

    private String UniqueID;

    public String getUniqueID() {
        return this.UniqueID;
    }

    public void setUniqueID(String UniqueID) {
        this.UniqueID = UniqueID;
    }

    private int RoleID;

    public int getRoleID() {
        return this.RoleID;
    }

    public void setRoleID(int RoleID) {
        this.RoleID = RoleID;
    }

    private int MainTherapistID;

    public int getMainTherapistID() {
        return this.MainTherapistID;
    }

    public void setMainTherapistID(int MainTherapistID) {
        this.MainTherapistID = MainTherapistID;
    }
    public List<User> JSONDeserialize(String JSON){

        Gson gson = new GsonBuilder().create();
        User[] users = gson.fromJson(JSON, User[].class);
        return java.util.Arrays.asList(users);
    }
}