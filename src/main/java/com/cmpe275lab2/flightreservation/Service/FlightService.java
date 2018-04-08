package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Plane;
import com.cmpe275lab2.flightreservation.Repository.FlightRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FlightService {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    ResponseService responseService;



    public ResponseEntity<?> createOrUpdateFlight(String flightNumber, int capacity, String model, String manufacturer, int year, double price, String fromCity, String toCity, String departureTime, String arrivalTime, String description) {

        Flight flight;
        HttpHeaders defaultHeader = new HttpHeaders();
        defaultHeader.setContentType(MediaType.APPLICATION_JSON);

        DateFormat df = new SimpleDateFormat("yy-MM-dd-hh");
        Date formattedArrival = null;
        Date formattedDeparture = null;
        try {
            formattedArrival = df.parse(arrivalTime);
            formattedDeparture = df.parse(departureTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //check dates- arrival date should come after departure date
        if (formattedDeparture.compareTo(formattedArrival) > 0) {
            //Departure date is greater than arrival date
            //throw error
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "The Request cannot be completed because the departure time should be before the arrival time").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }

        }

        //check cities- origin and destination cities should be different
        if (fromCity.equals(toCity)) {

            //source and destination cities can not be the same. Throw error
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "The Request cannot be completed because the source and destination cities are same").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        //embedded plane object
        Plane plane = new Plane(capacity, model, manufacturer, year);


        //check if flight exists
        flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight == null) {

            //if flight doesn't exist then create a new one
            flight = new Flight(flightNumber, formattedArrival, plane, price, fromCity, toCity, formattedDeparture, description);
            flightRepository.save(flight);

        } else {

            //else update existing flight

            //check if capacity is changed then update seats left
            int existingCapacity = flight.getPlane().getCapacity(); //400
            int existingSeatsLeft = flight.getSeatsLeft();  //390
            if (existingSeatsLeft <= capacity) {
                //390 < 410 (if capacity is increased) or 390 < 395 (if capacity is decreased)
                //if capacity is increased/decreased then update the number of seats left
                flight.setSeatsLeft(existingSeatsLeft + (capacity - existingCapacity));
                //(390 +  410 - 400 = 400) or (390 + 395 - 400 = 385)
                flight.setPlane(plane);
            } else {
                //return error code 400 and cancel the update
                try {
                    return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                            "The Request cannot be completed because the Seats capacity of the flight cannot be lower than the reserved number of seats").toString(),
                            HttpStatus.NOT_FOUND);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }


            //handle case if passengers have a flight with overlapping time then cancel the update
            //get flight timings and compare with new
            Date existingArrival = flight.getArrivalTime();
            Date existingDeparture = flight.getDepartureTime();
            if (existingArrival.equals(formattedArrival) && existingDeparture.equals(formattedDeparture)) {
                //No change in date, Continue creating the flight
            } else {
                //get passenger data and handle
            }

            flight.setDescription(description);
            flight.setFromCity(fromCity);
            flight.setToCity(toCity);
            flight.setPrice(price);
            flightRepository.save(flight);

        }
        //In both the cases return the newly created/updated flight object in XML format
        return getFlight(flightNumber, "XML");

    }


    //Get flight based on the format specified from controller
    public ResponseEntity<?> getFlight(String flightNumber, String format) {

        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        HttpHeaders httpHeaders = new HttpHeaders();
        if (flight != null) {

            if (format.equals("XML")) {
                httpHeaders.setContentType(MediaType.APPLICATION_XML);
                return new ResponseEntity<>(flight, httpHeaders, HttpStatus.OK);
            } else {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>(flight, httpHeaders, HttpStatus.OK);
            }
        } else {
            try {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", "No flight details are found for flight number " + flightNumber + "").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }
        }

    }


    public ResponseEntity<?> deleteFlight(String flightNumber) {

        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        HttpHeaders httpHeaders = new HttpHeaders();

        if (flight != null) {

            Flight temp = flightRepository.findByFlightNumberAndReservationListIsNotNull(flightNumber);
            //System.out.println(temp.toString());

            if (temp != null) {

                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                try {
                    JSONObject response = responseService.getResponse("BadRequest", "400", "Flight with number " + flightNumber + " can not be deleted as it has active reservations");
                    return new ResponseEntity<>(response.toString(), httpHeaders, HttpStatus.BAD_REQUEST);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
                }
            } else {

                flightRepository.delete(flight);
                try {
                    httpHeaders.setContentType(MediaType.APPLICATION_XML);
                    JSONObject response = responseService.getResponse("Response", "200", "Flight with number " + flightNumber + " is deleted successfully");
                    System.out.println(response);
                    return new ResponseEntity<>(XML.toString(response), httpHeaders, HttpStatus.OK);
                } catch (JSONException e) {
                    e.printStackTrace();
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
                }
            }
        } else {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            try {
                JSONObject response = responseService.getResponse("BadRequest", "404", "Flight with number " + flightNumber + " can not be found");
                return new ResponseEntity<>(response.toString(), httpHeaders, HttpStatus.NOT_FOUND);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
            }

        }

    }

}
