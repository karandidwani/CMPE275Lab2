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

    @RequestMapping(value = "/reservation", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> makeReservation(@RequestParam int passengerId,
                                             @RequestParam String flightLists) {
        return reservationService.makeReservation(passengerId, flightLists);
    }

    @RequestMapping(value = "/reservation/{number}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getReservationJSON(@PathVariable int reservationNumber) {
        return reservationService.getReservationJSON(reservationNumber);
    }

    @RequestMapping(value = "/reservation/{number}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> cancelReservation(@PathVariable int reservationNumber) {
        return reservationService.cancelReservation(reservationNumber);
    }

}
