package com.cmpe275lab2.flightreservation.Repository;

import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation,String> {

    Reservation findFirstByPassenger(Passenger PassengerId);

    Reservation getReservationByReservationNumber(int reservationNumber);

    Reservation findFirstByOrderByReservationNumberDesc();

    Reservation getReservationByPassenger(Passenger passenger);
}
