package com.cmpe275lab2.flightreservation.Controller;

import com.cmpe275lab2.flightreservation.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    //Make Reservation
    @RequestMapping(value = "/reservation", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> makeReservation(@RequestParam("passengerId") int passengerId,
                                             @RequestParam("flightLists") String flightLists) {
        return reservationService.makeReservation(passengerId, flightLists);
    }

    //Get Reservation Details
    @RequestMapping(value = "/reservation/{reservationNumber}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getReservationJSON(@PathVariable String reservationNumber) {

        return reservationService.getReservation(Integer.parseInt(reservationNumber), "JSON");
    }

    //Cancel Reservation
    @RequestMapping(value = "/reservation/{reservationNumber}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> cancelReservation(@PathVariable int reservationNumber) {
        return reservationService.cancelReservation(reservationNumber);
    }
}
