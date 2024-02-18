package com.example.carpoolbuddy;

/**
 * this class is for alumni to inherit the properties of user
 * add the alumni specific properties (graduateYear)
 * setter and getter methods for these properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Alumni extends User{
    private String graduateYear;

    public String getGraduateYear() {
        return graduateYear;
    }

    public void setGraduateYear(String graduateYear) {
        this.graduateYear = graduateYear;
    }
}