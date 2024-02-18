package com.example.carpoolbuddy;

/**
 * this class is for segway to inherit the properties of vehicle
 * add the segway specific properties (range, weightCapacity, etc)
 * setter and getter methods for those properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */
public class Segway extends Vehicle {
    private int range;
    private int weightCapacity;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getWeightCapacity() {
        return weightCapacity;
    }

    public void setWeightCapacity(int weightCapacity) {
        this.weightCapacity = weightCapacity;
    }
}
