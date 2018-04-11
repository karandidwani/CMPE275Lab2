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
import java.util.Date;
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

        if (fList.size() == 0) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "No flight found.").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < fList.size(); i++) {
            Flight flight = flightRepository.findByFlightNumber(fList.get(i));
            if (flight == null) {
                try {
                    return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                            "One or more flights does not exist.").toString(),
                            HttpStatus.NOT_FOUND);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("i ---" + fList.get(i));
            //System.out.println(flight.toString());
            flight.setSeatsLeft(flight.getSeatsLeft() - 1);
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
        if (isFlightTimingsNotValid(flightL)) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "Flight timings overlap each other.").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
        if (!isSeatsAvailable(flightL)) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "No seats available.").toString(),
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

            Passenger passenger = reservation.getPassenger();
            for (Flight f : reservation.getFlights()) {
                f.setSeatsLeft(f.getSeatsLeft() + 1);
                f.getPassengerList().remove(passenger);
            }
            reservation.setFlights(null);
            passenger.getReservations().remove(reservation);

            reservationRepository.delete(reservation);
            httpHeaders.setContentType(MediaType.APPLICATION_XML);
            try {
                JSONObject response = responseService.getResponse("Response", "200", "Reservation with number " + reservationNumber + " cancelled successfully.");
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

    public ResponseEntity<?> updateReservation(int reservationNumber, String flightsAdded, String flightsRemoved) {

        //Passenger passenger = passengerRepository.findFirstByPassengerId(passengerId);
        System.out.println("Rovin String flightsAdded " + flightsAdded);
        System.out.println("Rovin String flightsRemoved " + flightsRemoved);
        Reservation reservation = reservationRepository.getReservationByReservationNumber(reservationNumber);
        List<Flight> f = reservation.getFlights();
        List<String> fListRem = Arrays.asList(flightsRemoved.split("\\s*,\\s*"));
        List<String> fListAdd = Arrays.asList(flightsAdded.split("\\s*,\\s*"));
        double price = reservation.getPrice();
        Passenger passenger = reservation.getPassenger();
        if (flightsRemoved.equals(null) || flightsAdded.equals(null)) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "Parameter FlightsAdded or FlightsRemoved cannot be empty.").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        if (!flightsRemoved.equals("NA")) {
            for (int i = 0; i < fListRem.size(); i++) {
                System.out.println("fListRem.get(i) " + fListRem.get(i));
                Flight flightRem = flightRepository.findByFlightNumber(fListRem.get(i));
                if (flightRem == null) {
                    try {
                        return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                                "Flight list does not exist.").toString(),
                                HttpStatus.NOT_FOUND);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
                price = price - flightRem.getPrice();
                flightRem.getPassengerList().remove(passenger);
                f.remove(flightRem);
            }
        }

        if (!flightsAdded.equals("NA")) {
            for (int i = 0; i < fListAdd.size(); i++) {
                Flight flightAdd = flightRepository.findByFlightNumber(fListAdd.get(i));
                if (flightAdd == null) {
                    try {
                        return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                                "Flight list does not exist.").toString(),
                                HttpStatus.NOT_FOUND);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
                price = price + flightAdd.getPrice();
                flightAdd.getPassengerList().add(passenger);
                f.add(flightAdd);
            }
        }


        if (isFlightTimingsNotValid(f)) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400",
                        "Flight timings overlap each other.").toString(),
                        HttpStatus.NOT_FOUND);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        reservation.setPrice(price);

        Reservation reservationUpdate = new Reservation(price, f, reservation.getPassenger());

        passenger.getReservations().add(reservation);


        reservationRepository.save(reservation);

        Reservation reservationCreated = reservationRepository.getReservationByReservationNumber(reservationNumber);

        return getReservation(reservationCreated.getReservationNumber(), "JSON");
    }

    /*public ResponseEntity<?> searchReservation(int passengerId, String origin, String to, String flightNumber){
        //Reservation reservation = reservationRepository.getReservationByReservationNumber(reservationNumber);

    }*/


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

    //Check if seats available
    public boolean isSeatsAvailable(List<Flight> flightL) {
        for (int i = 0; i < flightL.size(); i++) {
            if (flightL.get(i).getSeatsLeft() == 0) {
                return false;
            }
        }
        return true;
    }

}