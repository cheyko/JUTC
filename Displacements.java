package com.jutcjm.jutc;

import java.util.Date;

/**
 * Created by ariel on 4/5/18.
 */

public class Displacements {
    private Coordinates points;
    private int busNumber,timeThus,fleetNum ;
    private Date currentTime;
    private Double distance;

    public Displacements() {

    }
    public Displacements( int busNumber,Date currentTime, Coordinates points, int timeThus, int fleetNum) {
        this.busNumber = busNumber;
        this.points = points;
        this.timeThus = timeThus;
        this.currentTime = currentTime;
        this.fleetNum = fleetNum;
    }

    public Displacements( int busNumber, Date currentTime , Double dist, int timeThus) {
        this.busNumber = busNumber;
        this.distance = dist;
        this.timeThus = timeThus;
        this.currentTime = currentTime;
    }

    public int getBusNumber(){
        return this.busNumber;
    }

    public void setBusNumber(int newBusNumber){
        this.busNumber = newBusNumber;
    }

    public int getTimeThus(){ return this.timeThus; }

    public void setTimeThus(int newTime){
        this.timeThus = newTime;
    }

    public Double getDistance() {   return this.distance;   }

    public void setDistance(Double dist){  this.distance = dist; }

    public Date getCurrentTime(){
        return this.currentTime;
    }

    public void setCurrentTime(Date newTime){
        this.currentTime = newTime;
    }

    public Coordinates getPoints(){
        return this.points;
    }

    public void setPoints(Coordinates newPoints){ this.points = newPoints; }

    public int getFleetNumber(){
        return this.fleetNum;
    }

    public void setFleetNumber(int newFleetNumber){
        this.fleetNum = newFleetNumber;
    }
}
