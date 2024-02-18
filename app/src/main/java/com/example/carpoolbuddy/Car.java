package com.example.carpoolbuddy;

/**
 * this class is for car to inherit the properties of vehicle
 * add the car specific properties (range)
 * setter and getter methods for those properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Car extends Vehicle{
    private int range;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}