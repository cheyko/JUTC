package com.jutcjm.jutc;

import java.util.Date;

/**
 * Created by ariel on 5/19/18.
 */

public class RouteTrip {

    private int busNumber,fleetNum;
    private Date startTime, endTime;

    public RouteTrip(){

    }


    public RouteTrip(int num , Date starting, Date ending, int fleet){

        this.busNumber = num;
        this.startTime = starting;
        this.endTime = ending;
        this.fleetNum = fleet;

    }

    public int getBusNumber(){
        return this.busNumber;
    }

    public void setBusNumber(int newBusNumber){
        this.busNumber = newBusNumber;
    }

    public Date getStartTime (){ return this.startTime; }

    public void setStartTime (Date newDateTime){    this.startTime = newDateTime;   }

    public Date getEndTime (){ return this.endTime; }

    public void setEndTime (Date newDateTime){    this.endTime = newDateTime;   }

    public int getFleetNumber(){
        return this.fleetNum;
    }

    public void setFleetNumber(int newFleetNumber){
        this.fleetNum = newFleetNumber;
    }


}
