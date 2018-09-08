package com.jutcjm.jutc;

/**
 * Created by ariel on 4/5/18.
 */

public class Coordinates {
    private String latti;
    private String longi;

    public Coordinates() {

    }

    public Coordinates(String latti, String longi) {
        this.latti = latti;
        this.longi = longi;
    }

    public String getLatti(){
        return this.latti;
    }

    public void setLatti(String newLatti){
        this.latti = newLatti;
    }
    //same for Y

    public String getLongi(){
        return this.longi;
    }

    public void setLongi(String newLongi){
        this.longi = newLongi;
    }
}


