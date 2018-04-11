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

    //Update Reservation
    @RequestMapping(value = "/reservation/{reservationNumber}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateReservation(@PathVariable int reservationNumber,
                                               @RequestParam(value = "flightsAdded", required = false, defaultValue = "NA") String flightsAdded,
                                               @RequestParam(value = "flightsRemoved", required = false, defaultValue = "NA") String flightsRemoved) {
        return reservationService.updateReservation(reservationNumber, flightsAdded, flightsRemoved);
    }

    //Search for Reservation
    /*@RequestMapping(value = "/reservation", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateReservation(@RequestParam(value = "passengerId", required = false, defaultValue = "") String passengerId,
                                               @RequestParam(value = "origin", required = false, defaultValue = "") String origin,
                                               @RequestParam(value = "to", required = false, defaultValue = "") String to,
                                               @RequestParam(value = "flightNumber", required = false, defaultValue = "") String flightNumber) {
        return reservationService.searchReservation(Integer.parseInt(passengerId),origin,to,flightNumber);
    }*/
}
