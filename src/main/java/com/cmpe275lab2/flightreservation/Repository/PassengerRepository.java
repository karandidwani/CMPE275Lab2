package com.cmpe275lab2.flightreservation.Repository;

import com.cmpe275lab2.flightreservation.Entity.Passenger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger,String> {

    Passenger findFirstByPassengerId(int id);

    Passenger findByPhone(String phone);
}
