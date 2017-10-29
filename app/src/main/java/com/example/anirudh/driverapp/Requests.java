package com.example.anirudh.driverapp;

/**
 * Created by anirudh on 21/1/17.
 */
public class Requests {
    String assigned;
    String name;
    String customerUID;
    String pushID;

    public Requests(String assigned,String name,String customerUID,String pushID){
        this.assigned=assigned;
        this.name=name;
        this.customerUID=customerUID;
        this.pushID=pushID;
    }
}
