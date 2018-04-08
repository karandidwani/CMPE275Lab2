package com.cmpe275lab2.flightreservation.Entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Flight {

    @Id
    private String flightNumber;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Flights_Reservations",
    joinColumns = {@JoinColumn(name = "flightNumber") },
    inverseJoinColumns = {@JoinColumn(name = "reservationNumber")})
    private List<Reservation> reservationList;

    @Embedded
    private Plane plane;

    private double price;
    private String fromCity;
    private String toCity;
    private Date departureTime;
    private Date arrivalTime;
    private int seatsLeft;
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "passengerId")
    private List<Passenger> passengerList;

    public Flight(String flightNumber, Date arrivalTime, Plane plane, double price, String fromCity, String toCity, Date departureTime, String description) {
        this.flightNumber = flightNumber;
        this.plane = plane;
        this.price = price;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.description = description;
        this.seatsLeft = plane.getCapacity();
    }

    public Flight() {
    }

    public Flight(String flightNumber, List<Reservation> reservation, Plane plane, double price, String fromCity, String toCity, Date departureTime, Date arrivalTime, int seatsLeft, String description, List<Passenger> passengerList) {
        this.flightNumber = flightNumber;
        this.reservationList = reservation;
        this.plane = plane;
        this.price = price;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatsLeft = seatsLeft;
        this.description = description;
        this.passengerList = passengerList;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public List<Reservation> getReservation() { return reservationList; }

    public void setReservation(List<Reservation> reservationList) { this.reservationList = reservationList; }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Passenger> getPassengerList() { return passengerList; }

    public void setPassengerList(List<Passenger> passengerList) { this.passengerList = passengerList; }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", reservationList=" + reservationList +
                ", plane=" + plane +
                ", price=" + price +
                ", fromCity='" + fromCity + '\'' +
                ", toCity='" + toCity + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", seatsLeft=" + seatsLeft +
                ", description='" + description + '\'' +
                ", passengerList=" + passengerList +
                '}';
    }
}
