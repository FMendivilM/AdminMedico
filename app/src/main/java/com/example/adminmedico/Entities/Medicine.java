package com.example.adminmedico.Entities;

import java.util.Calendar;

public class Medicine {
    String id;
    String name;
    int portion;
    String portionType;
    String intervals;
    String days;
    String hour;
    String via;
    String contactName;
    String contactNumber;
    int actualTakes;
    int totalTakes;
    String number;

    public Medicine(String id, String name, int portion, String portionType, String intervals, String days, String hour, String via, String contactName, String contactNumber, int actualTakes, int totalTakes, String number){
        this.id = id;
        this.name = name;
        this.portion = portion;
        this.portionType = portionType;
        this.intervals = intervals;
        this.days = days;
        this.hour = hour;
        this.via = via;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.actualTakes = actualTakes;
        this.totalTakes = totalTakes;
        this.number = number;
    }

    public Medicine(String id, String name, String hour, int actualTakes, int totalTakes){
        this.id = id;
        this.name = name;
        this.hour = hour;
        this.actualTakes = actualTakes;
        this.totalTakes = totalTakes;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }

    public String getPortionType() {
        return portionType;
    }

    public void setPortionType(String portionType) {
        this.portionType = portionType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIntervals() {
        return intervals;
    }

    public void setIntervals(String intervals) {
        this.intervals = intervals;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public int getActualTakes() { return actualTakes; }

    public void setActualTakes(int actualTakes) {
        this.actualTakes = actualTakes;
    }

    public int getTotalTakes() {
        return totalTakes;
    }

    public void setTotalTakes(int totalTakes) {
        this.totalTakes = totalTakes;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contact) {
        this.contactName = contact;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contacNumber) {
        this.contactNumber = contacNumber;
    }
}
