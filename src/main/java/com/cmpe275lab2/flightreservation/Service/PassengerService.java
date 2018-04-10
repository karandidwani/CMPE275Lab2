package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
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

import java.util.List;

@Service
public class PassengerService {

    @Autowired
    PassengerResponseService passengerResponseService;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private ReservationRepository reservationRepository;

    public List<Passenger> getAllPassengers() {
        return (List<Passenger>) passengerRepository.findAll();
    }

    public ResponseEntity<?> createPassenger(String firstName, String lastName, int age, String gender, String phone) {

        Passenger passenger = new Passenger(firstName, lastName, age, gender, phone, null, null);

        try {
            passengerRepository.save(passenger);
        } catch (Exception e) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400", "Please enter unique Number. Another passenger with same number already exists").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException j) {
                e.printStackTrace();
                return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
            }
        }

        Passenger passengerCreated = passengerRepository.findByPhone(phone);

        int id = passengerCreated.getPassengerId();

        return getPassenger(id, "JSON");

    }

    public ResponseEntity<?> getPassenger(int id, String format) {

        if (id != 0) {

            Passenger passenger = passengerRepository.findFirstByPassengerId(id);
            HttpHeaders httpHeaders = new HttpHeaders();


            if (passenger != null) {

                JSONObject passengerJSON = passengerResponseService.getPassengerJSON(passenger);

                if (format.equals("JSON")) {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    return new ResponseEntity<>(passengerJSON.toString(), httpHeaders, HttpStatus.OK);
                } else {
                    httpHeaders.setContentType(MediaType.APPLICATION_XML);
                    try {
                        return new ResponseEntity<>(XML.toString(passengerJSON), httpHeaders, HttpStatus.OK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    return new ResponseEntity<>(responseService.getResponse("BadRequest", "400", "No Passenger details are found for Id " + id + "").toString(), HttpStatus.NOT_FOUND);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(e, HttpStatus.BAD_GATEWAY);
                }
            }


        }

        try {
            return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", " Invalid data, please provide proper Id ").toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_GATEWAY);
        }

    }

    public ResponseEntity<?> updatePassenger(int id, String firstName, String lastName, int age, String gender, String phone) {

        if (id == 0) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "400", "Please enter correct Passenger Id Number, Passenger Id can not be found").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException j) {
                return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
            }
        } else {
            Passenger passenger = passengerRepository.findFirstByPassengerId(id);
            if (passenger == null) {

                try {
                    return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", "Passenger with Id " + id + " can not be found").toString(), HttpStatus.NOT_FOUND);
                } catch (JSONException j) {
                    return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
                }

            } else {
                String existingPhone = passenger.getPhone();

                if (!phone.equals(existingPhone)) {

                    Passenger p = passengerRepository.findByPhone(phone);

                    if (p != null) {
                        try {
                            return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", "Phone number cannot be updated as it is already in use. Please enter unique phone number").toString(), HttpStatus.NOT_FOUND);
                        } catch (JSONException j) {
                            return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
                        }
                    }
                }

                passenger.setAge(age);
                passenger.setFirstName(firstName);
                passenger.setLastName(lastName);
                passenger.setPhone(phone);
                passenger.setGender(gender);

                passengerRepository.save(passenger);

                return getPassenger(passenger.getPassengerId(), "JSON");

            }
        }
    }

    public ResponseEntity<?> deletePassenger(int id) {

        if (id == 0) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", "Please enter correct Passenger Id Number, Passenger Id can not be found").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException j) {
                return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
            }
        }

        Passenger passenger = passengerRepository.findFirstByPassengerId(id);
        if (passenger == null) {
            try {
                return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", "Passenger with Id " + id + " can not be found").toString(), HttpStatus.NOT_FOUND);
            } catch (JSONException j) {
                return new ResponseEntity<>(j, HttpStatus.BAD_GATEWAY);
            }
        } else {

            List<Reservation> passengerReservationList = reservationRepository.findAllByPassenger(passenger);
            if (passengerReservationList != null) {
                for (Reservation r : passengerReservationList) {
                    List<Flight> passengerFlightList = r.getFlights();
                    for (Flight f : passengerFlightList) {
                        f.setSeatsLeft(f.getSeatsLeft() + 1);
                        f.setPassengerList(null);
                    }
                    reservationRepository.delete(r);
                }
                passengerRepository.delete(passenger);
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        try {
            JSONObject response = responseService.getResponse("Response", "200", "Passenger with Id " + id + " deleted successfully.");
            return new ResponseEntity<>(XML.toString(response), httpHeaders, HttpStatus.OK);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, httpHeaders, HttpStatus.BAD_GATEWAY);
        }
    }
}
