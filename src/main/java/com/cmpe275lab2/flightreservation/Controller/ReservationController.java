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
    @RequestMapping(value = "/reservation/{number}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getReservationJSON(@PathVariable int reservationNumber) {
        return reservationService.getReservationJSON(reservationNumber);
    }

    //Cancel Reservation
    @RequestMapping(value = "/reservation/{number}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> cancelReservation(@PathVariable int reservationNumber) {
        return reservationService.cancelReservation(reservationNumber);
    }
}
