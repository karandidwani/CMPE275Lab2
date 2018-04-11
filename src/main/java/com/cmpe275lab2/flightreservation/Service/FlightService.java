package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Entity.Plane;
import com.cmpe275lab2.flightreservation.Repository.FlightRepository;
import com.cmpe275lab2.flightreservation.Repository.PassengerRepository;
import com.cmpe275lab2.flightreservation.Repository.ReservationRepository;
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
import java.util.List;

@Service
public class FlightService {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ResponseService responseService;

    @Autowired
    FlightResponseService flightResponseService;


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
            int seatsReserved = flight.getPlane().getCapacity() - flight.getSeatsLeft(); // 400 - 200 = 200

            if (capacity > seatsReserved) { //capacity 250
                flight.getPlane().setCapacity(capacity); //
                flight.setSeatsLeft(capacity - seatsReserved);

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
                System.out.println("No change in date, Continue creating the flight - (existingArrival.equals(formattedArrival) && existingDeparture.equals(formattedDeparture)");
            } else {
                //get passenger data and handle
                List<Passenger> flightPassengerList = flight.getPassengerList();
                if (flightPassengerList.size() != 0) {
                    for (Passenger p : flightPassengerList) {
                        List<Flight> flights = p.getFlightList();
                        if (flights.contains(flight)) {
                            flights.remove(flight);
                        } else {
                            System.out.println("Flight is not present");
                        }

                        if (flights.size() != 0 && isFlightTimingsNotValid(flights)) {
                            try {
                                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                                        "The Request cannot be completed because Flights updated schedule has Overlapping time with other flights of the passengers").toString(),
                                        HttpStatus.NOT_FOUND);
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
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

    //Compare Dates
    public boolean isFlightTimingsNotValid(List<Flight> flightL) {

        for (int i = 0; i < flightL.size() - 1; i++) {
            for (int j = i + 1; j < flightL.size(); j++) {
                //D1 and D2 are the new arrival and departure Time
                Date D1 = flightL.get(i).getArrivalTime();
                System.out.println("D1 " + D1);
                Date D2 = flightL.get(i).getDepartureTime();
                System.out.println("D2 " + D2);

                //DepTime and Arrival time should not lie between D1 and D2
                //DepTime is the departure time
                Date DepTime = flightL.get(j).getDepartureTime();

                //ArrTime is arrival time
                Date ArrTime = flightL.get(j).getArrivalTime();
                System.out.println("DepTime " + DepTime);
                System.out.println("ArrTime " + ArrTime);

                if (D1.compareTo(DepTime) >= 0 && D1.compareTo(ArrTime) <= 0 || D2.compareTo(DepTime) >= 0 && D2.compareTo(ArrTime) <= 0) {
                    System.out.println("Compare 1 " + (D1.compareTo(DepTime) >= 0 && D1.compareTo(ArrTime) <= 0));
                    System.out.println("Compare 2 " + (D2.compareTo(DepTime) >= 0 && D2.compareTo(ArrTime) <= 0));
                    return true;
                }
            }
        }
        return false;
    }



    //Get flight based on the format specified from controller
    public ResponseEntity<?> getFlight(String flightNumber, String format) {

        Flight flight = flightRepository.findByFlightNumber(flightNumber);

        HttpHeaders httpHeaders = new HttpHeaders();
        if (flight != null) {

            if (format.equals("XML")) {
                httpHeaders.setContentType(MediaType.APPLICATION_XML);
                try {
                    return new ResponseEntity<>(XML.toString(flightResponseService.getFlightJSON(flight)), httpHeaders, HttpStatus.OK);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
                }
            } else {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>(flightResponseService.getFlightJSON(flight).toString(), httpHeaders, HttpStatus.OK);
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
