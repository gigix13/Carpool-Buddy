package com.example.carpoolbuddy;

/**
 * this class is for helicopter to inherit the properties of vehicle
 * add the helicopter specific properties (macAltitude, etc)
 * setter and getter methods for those properties are here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class Helicopter extends Vehicle{
    private int maxAltitude;
    private int maxAirspeed;

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(int maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public int getMaxAirspeed() {
        return maxAirspeed;
    }

    public void setMaxAirspeed(int maxAirspeed) {
        this.maxAirspeed = maxAirspeed;
    }
}