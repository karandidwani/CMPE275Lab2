package com.cmpe275lab2.flightreservation.Entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int passengerId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    @Column(unique = true)
    private String phone;

    //Reservation - One passenger can have multiple reservations
    @OneToMany(mappedBy = "passenger")
    private List<Reservation> reservations;

    //Flights
    @ManyToMany(mappedBy = "passengerList")
    private List<Flight> flightList;


    public Passenger(String firstName, String lastName, int age, String gender, String phone, List<Reservation> reservations, List<Flight> flightList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.reservations = reservations;
        this.flightList = flightList;
    }

    public Passenger() {
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public List<Flight> getFlightList() {
        return flightList;
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id='" + passengerId + '\'' +
                ", firstname='" + firstName + '\'' +
                ", lastname='" + lastName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                // ", reservations=" + reservations +
                '}';
    }
}
