package com.cmpe275lab2.flightreservation.Entity;

import javax.persistence.Embeddable;

@Embeddable
public class Plane {

    private int capacity;
    private String model;
    private String manufacturer;
    private int year;

    public Plane(int capacity, String model, String manufacturer, int year) {
        this.capacity = capacity;
        this.model = model;
        this.manufacturer = manufacturer;
        this.year = year;
    }

    public Plane() {
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /*@Override
    public String toString() {
        return "Plane{" +
                "capacity=" + capacity +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", year=" + year +
                '}';
    }*/
}
