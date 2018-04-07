package com.cmpe275lab2.flightreservation.Repository;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends CrudRepository<Flight,String> {

    Flight findByFlightNumber(String flightNumber);

    //boolean findReservationListByFlightNumber(String flightNumber);

    Flight findByFlightNumberAndReservationListIsNotNull(String flightNumber);

}
