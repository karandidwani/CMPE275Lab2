package com.cmpe275lab2.flightreservation.Controller;

import com.cmpe275lab2.flightreservation.Service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PassengerController {

    @Autowired
    PassengerService passengerService;

    //create a new passenger
    @RequestMapping(value = "/passenger", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPassenger(@RequestParam("firstname") String firstName,
                                             @RequestParam("lastname") String lastName,
                                             @RequestParam("age") int age,
                                             @RequestParam("gender") String gender,
                                             @RequestParam("phone") String phone) {

        return passengerService.createPassenger(firstName, lastName, age, gender, phone);

    }

    @RequestMapping(value = "/passenger/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPassenger(@PathVariable String id,
                                          @RequestParam(value = "xml", required = false) boolean xml) {

        if (xml) {
            return passengerService.getPassenger(Integer.parseInt(id), "XML");
        } else {
            return passengerService.getPassenger(Integer.parseInt(id), "JSON");
        }

    }

    @RequestMapping(value = "/passenger/{id}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updatePassenger(@PathVariable String id,
                                             @RequestParam("firstname") String firstName,
                                             @RequestParam("lastname") String lastName,
                                             @RequestParam("age") int age,
                                             @RequestParam("gender") String gender,
                                             @RequestParam("phone") String phone) {

        return passengerService.updatePassenger(Integer.parseInt(id), firstName, lastName, age, gender, phone);

    }

    @RequestMapping(value = "/passenger/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deletePassenger(@PathVariable String id) {

        return passengerService.deletePassenger(Integer.parseInt(id));

    }
}

