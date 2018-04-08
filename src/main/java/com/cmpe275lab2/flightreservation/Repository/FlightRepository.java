package com.cmpe275lab2.flightreservation.Repository;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends CrudRepository<Flight,String> {

    Flight findByFlightNumber(String flightNumber);

    //boolean findReservationListByFlightNumber(String flightNumber);

    Flight findByFlightNumberAndReservationListIsNotNull(String flightNumber);

}
