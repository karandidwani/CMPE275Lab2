package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    ResponseService responseService;

    @Autowired
    ReservationResponseService reservationResponseService;

    public ResponseEntity<?> makeReservation(int passengerId, String flightLists) {

        Passenger passenger = passengerRepository.findFirstByPassengerId(passengerId);

        if (passenger == null) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", "Passenger with Id " + passengerId + " can not be found").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException j) {
                return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
            }
        }

        List<String> fList = Arrays.asList(flightLists.split("\\s*,\\s*"));
        List<Flight> flightL = new ArrayList<>();
        double price = 0;
        for (int i = 0; i < fList.size(); i++) {
            Flight flight = flightRepository.findByFlightNumber(fList.get(i));
            System.out.println("i ---" + fList.get(i));
            System.out.println(flight.toString());
            flightL.add(flight);
            price = price + flight.getPrice();
        }

        if (fList.size() != flightL.size()) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "One of the flight or All flights in the flight list does not exists").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        Reservation reservation = new Reservation(price, flightL, passenger);

        passenger.getReservations().add(reservation);

        for (Flight f : flightL) {
            f.getPassengerList().add(passenger);
        }

        reservationRepository.save(reservation);

        Reservation reservationCreated = reservationRepository.findFirstByOrderByReservationNumberDesc();

        return getReservation(reservationCreated.getReservationNumber(), "XML");
    }


    public ResponseEntity<?> getReservation(int reservationNumber, String format) {

        Reservation reservation = reservationRepository.getReservationByReservationNumber(reservationNumber);

        if (reservation == null) {

        }

        HttpHeaders httpHeaders = new HttpHeaders();

        if (reservation != null) {

            JSONObject reservationJSON = reservationResponseService.getReservationJSON(reservation);

            if (format.equals("JSON")) {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>(reservationJSON.toString(), httpHeaders, HttpStatus.OK);
            } else {
                httpHeaders.setContentType(MediaType.APPLICATION_XML);
                try {
                    return new ResponseEntity<>(XML.toString(reservationJSON), httpHeaders, HttpStatus.OK);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else {
            try {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400", "No Reservation details are found").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
            }
        }

    }

    public ResponseEntity<?> cancelReservation(int reservationNumber) {

        Reservation reservation = reservationRepository.getReservationByReservationNumber(reservationNumber);
        HttpHeaders httpHeaders = new HttpHeaders();

        if (reservation != null) {
            reservationRepository.delete(reservation);
            httpHeaders.setContentType(MediaType.APPLICATION_XML);
            try {
                JSONObject response = responseService.getResponse("", "200", "Reservation with number " + reservationNumber + " cancelled successfully.");
                return new ResponseEntity<>(XML.toString(response), httpHeaders, HttpStatus.OK);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
            }
        } else {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            try {
                JSONObject response = responseService.getResponse("BadRequest", "404", "Reservation with number " + reservationNumber + " does not exist");
                return new ResponseEntity<>(response.toString(), httpHeaders, HttpStatus.NOT_FOUND);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
            }

        }

    }

}