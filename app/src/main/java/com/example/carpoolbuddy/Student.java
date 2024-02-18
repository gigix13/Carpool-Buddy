package com.example.carpoolbuddy;
import java.util.ArrayList;

/**
 * this class is for student to inherit the properties of user
 * add the student specific properties (graduatingYear, etc)
 * setter and getter methods for these properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Student extends User{
    private String graduatingYear;
    private ArrayList<String> parentUIDs;

    public String getGraduatingYear() {
        return graduatingYear;
    }
    public void setGraduatingYear(String graduatingYear) {
        this.graduatingYear = graduatingYear;
    }
    public ArrayList<String> getParentUIDs() {
        return parentUIDs;
    }
    public void setParentUIDs(ArrayList<String> parentUIDs) {
        this.parentUIDs = parentUIDs;
    }
}