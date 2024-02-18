package com.example.carpoolbuddy;

/**
 * this class is for teacher to inherit the properties of user
 * add the teacher specific properties (inSchoolTitle)
 * setter and getter methods for these properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Teacher extends User{
    private String inSchoolTitle;

    public String getInSchoolTitle() {
        return inSchoolTitle;
    }
    public void setInSchoolTitle(String inSchoolTitle) {
        this.inSchoolTitle = inSchoolTitle;
    }
}