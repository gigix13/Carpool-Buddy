package com.example.carpoolbuddy;
import java.util.ArrayList;

/**
 * this class is for parent to inherit the properties of user
 * add the parent specific properties (childrenUIDs)
 * setter and getter methods for these properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Parent extends User{
    private ArrayList<String> childrenUIDs;

    public ArrayList<String> getChildrenUIDs() {
        return childrenUIDs;
    }
    public void setChildrenUIDs(ArrayList<String> childrenUIDs) {
        this.childrenUIDs = childrenUIDs;
    }
}