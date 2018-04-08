package com.cmpe275lab2.flightreservation.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int reservationNumber;

    private double price;

    //Each reservation can have one or more flights reserved
    @ManyToMany(mappedBy = "reservationList") //@JsonManagedReference
    private List<Flight> flights;

    //Each passenger can have one or more reservations
    @ManyToOne
    @JsonBackReference
    private Passenger passenger;

    public Reservation(double price, List<Flight> flights, Passenger passenger) {
        this.price = price;
        this.flights = flights;
        this.passenger = passenger;
    }

    public Reservation() {
    }

    public int getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(int reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationNumber='" + reservationNumber + '\'' +
                ", price=" + price +
                ", flights=" + flights +
                ", passenger=" + passenger +
                '}';
    }
}
