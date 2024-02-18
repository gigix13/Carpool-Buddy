package com.example.carpoolbuddy;

/**
 * this class is for bicycle to inherit the properties of vehicle
 * add the bicycle specific properties (weight, weightCapacity, etc)
 * setter and getter methods for those properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Bicycle extends Vehicle{
    private String bicycleType;
    private int weight;
    private int weightCapacity;

    public String getBicycleType() {
        return bicycleType;
    }

    public void setBicycleType(String bt) {
        this.bicycleType = bt;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int w) {
        this.weight = w;
    }

    public int getWeightCapacity() {
        return weightCapacity;
    }

    public void setWeightCapacity(int wc) {
        this.weightCapacity = wc;
    }
}