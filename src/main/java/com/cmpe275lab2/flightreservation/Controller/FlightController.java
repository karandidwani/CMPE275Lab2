package com.cmpe275lab2.flightreservation.Controller;

import com.cmpe275lab2.flightreservation.Service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FlightController {

    @Autowired
    FlightService flightService;

    //Create or Update a Flight
    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createOrUpdateFlight(@PathVariable String flightNumber,
                                                  @RequestParam("price") double price,
                                                  @RequestParam("origin") String fromCity,
                                                  @RequestParam("to") String toCity,
                                                  @RequestParam("departureTime") String departureTime,
                                                  @RequestParam("arrivalTime") String arrivalTime,
                                                  @RequestParam("description") String description,
                                                  @RequestParam("capacity") int capacity,
                                                  @RequestParam("model") String model,
                                                  @RequestParam("manufacturer") String manufacturer,
                                                  @RequestParam("year") int year) {

        return flightService.createOrUpdateFlight(flightNumber, capacity, model, manufacturer, year, price, fromCity, toCity, departureTime, arrivalTime, description);

    }

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getFlight(@PathVariable String flightNumber,
                                       @RequestParam(value = "xml", required = false) boolean xml) {
        //check if XML parameter is true, then get flight in XML or else in JSON
        if (xml) {
            return flightService.getFlight(flightNumber, "XML");
        } else {
            return flightService.getFlight(flightNumber, "JSON");
        }

    }

    @RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteFlight(@PathVariable String flightNumber) {
        return flightService.deleteFlight(flightNumber);
    }

}
