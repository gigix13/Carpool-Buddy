package com.example.carpoolbuddy;
import java.util.ArrayList;

/**
 * this class is for the declaration and setters/getters for the properties of user
 * is a parent class for student, teacher, parent, and alumni
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class User {
    private String uid;
    private String name;
    private String email;
    private String userType;
    private ArrayList<String> ownedVehicles;
    private double priceMultiplier;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<String> getOwnedVehicles() {
        return ownedVehicles;
    }

    public void setOwnedVehicles(ArrayList<String> ownedVehicles) {
        this.ownedVehicles = ownedVehicles;
    }
    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

}